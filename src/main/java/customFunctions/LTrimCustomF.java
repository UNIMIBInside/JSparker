package customFunctions;

import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.col;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;

public class LTrimCustomF extends BaseCustomFunction {

	public LTrimCustomF() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Dataset<Row> execute(Dataset<Row> input) {
		UDF1<String, String> udf = row -> {
			String ltrim = row.replaceAll("^\\s+","");
			return ltrim;
        };

        //this is not mandatory
        //input.sparkSession().sparkContext().addJar("/Users/davideceresola/Downloads/clojure/clojure.jar");
        //input.sparkSession().sparkContext().addJar("/Users/davideceresola/Downloads/clojure/spec.alpha-0.2.176.jar");
        input.sqlContext().udf().register("ltrim", udf, DataTypes.StringType);
        input = input.withColumn(super.newColName, callUDF("ltrim", col(super.colsToApply[0])));
		
		return input;
	}

}
