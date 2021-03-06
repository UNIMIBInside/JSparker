package customFunctions;

import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.col;

import java.io.StringReader;
import utility.LogManager;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;

import com.esotericsoftware.kryo.serializers.ClosureSerializer.Closure;

import clojure.java.*;
import clojure.java.api.Clojure;
import clojure.lang.IFn;

public class DoubleLiteralCustomF extends BaseCustomFunction {
	
	private String clojureCode = "(defn double-literal [s] (if (nil? (re-matches #\"[0-9.]+\" s)) 0 (Double/parseDouble s)))";

	public DoubleLiteralCustomF() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Dataset<Row> execute(Dataset<Row> input) {
		LogManager.getShared().logInfo(
                "CALLED double literal EXECUTE");
		final String clCode = "(ns user) " + this.clojureCode;

		UDF1<String, Double> udf = row -> {
			//needed to load clojure.core
			clojure.java.api.Clojure.var("clojure.core", "+"); // plus is a random function 
			//
			clojure.lang.Compiler.load(new java.io.StringReader(clCode));
			clojure.lang.IFn foo1 = clojure.java.api.Clojure.var("user", "double-literal");
			Object result1 = foo1.invoke(row);
			if (result1 instanceof Number) {
			    return Double.valueOf(((Number) result1).doubleValue());
			}
            return (Double) result1;
        };

        //this is not mandatory
        //input.sparkSession().sparkContext().addJar("/Users/davideceresola/Downloads/clojure/clojure.jar");
        //input.sparkSession().sparkContext().addJar("/Users/davideceresola/Downloads/clojure/spec.alpha-0.2.176.jar");
        input.sqlContext().udf().register("double-literal", udf, DataTypes.DoubleType);
        input = input.withColumn(super.newColName, callUDF("double-literal", col(super.colsToApply[0])));
		
		
		return input;
	}

}
