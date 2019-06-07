package parser.actions;

import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.col;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;
import org.json.JSONObject;

import it.unimib.disco.asia.ASIA4J;
import it.unimib.disco.asia.ASIA4JFactory;
import it.unimib.disco.asia.ASIAHashtableClient;
import parser.actions.BaseAction.ActionType;
import parser.actions.enums.EnumActionField;

public class ASIA4J_Mapping extends BaseAction {
	
	private static class ColTarget{
		public int id;
		public String value;
		
		private ColTarget(JSONObject js) throws ActionException{
			
			if(js == null) throw new ActionException("ColTarget - js in constructor is null");
			try {
				// 0. ID
				if(js.isNull(MappingjsonKey.ID.getVal())) throw new ActionException("ColTarget - ID in json is null");
				this.id = js.getInt(MappingjsonKey.ID.val);
				
				// 1. value
				if(js.isNull(MappingjsonKey.VALUE.getVal())) throw new ActionException("ColTarget - VALUE in json is null");
				
				this.value = js.getString(MappingjsonKey.VALUE.val);
				
			}catch (Exception e) {
				throw new ActionException(e.getMessage());
			}
		}
			
		
	
	private enum MappingjsonKey{

			ID("id"), VALUE("value");

			private String val;

			MappingjsonKey(String val) {
				this.val = val;
			}

			public String getVal() {
				return this.val;
			}
		}
	}

	private String asia4jEndpointUrl;
	private String newColName;
	private ColTarget colToApply;

	public ASIA4J_Mapping(JSONObject js, int sequenceNumber, String asia4jEndpointUrl) throws ActionException {
		super(js, sequenceNumber, ActionType.CUSTOM);
		
		try {
		
			// 0. Assign endpoint if is not null
			if(asia4jEndpointUrl == null || asia4jEndpointUrl.trim().equals("")) {
				throw new ActionException("ASIA4J_Mapping -  init - asia4jEndpointUrl is  null");
			}
			this.asia4jEndpointUrl = asia4jEndpointUrl;
			
			// 1. Extract new col name
			if (js.isNull(EnumActionField.NEW_COLUMN_NAME.getVal()))
	        	throw new ActionException("Error while creating ASIA4J_Mapping function. NEW_COLUMN_NAME is not present");
	        this.newColName = js.getString(EnumActionField.NEW_COLUMN_NAME.getVal());
	        
	        // 2. Extract col target 
 			if (js.isNull(EnumActionField.COL_TARGET.getVal()))
	        	throw new ActionException("Error while creating ASIA4J_Mapping function. COL_TARGET is not present");
 	        this.colToApply = new ColTarget(js.getJSONObject(EnumActionField.COL_TARGET.getVal()));
    
	        
		}catch(Exception e) {
			throw new ActionException(e.getMessage());
		}
		
	}

	@Override
	public Dataset<Row> actionToExecute(Dataset<Row> input) {

		// Create client
		ASIA4J hClient = ASIA4JFactory.getClient(asia4jEndpointUrl, ASIAHashtableClient.class);

		UDF1<String, String> udf = row -> {

			
			String result = hClient.reconcile("Berlin", "A.ADM1", 0.1, "geonames");
			System.out.println("***********\n"+result);
			return result;
		};

		input.sqlContext().udf().register("asia4mapping", udf, DataTypes.StringType);
		// input = input.withColumn(newColName, callUDF("titleize", col(colToApplyName)));
		input = input.withColumn(newColName, callUDF("asia4mapping", col(colToApply.value)));
		return input;
	}

}