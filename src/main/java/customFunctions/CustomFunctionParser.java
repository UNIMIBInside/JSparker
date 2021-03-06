package customFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomFunctionParser {
	/*
	{
		"funct": {
			"id": 33,
			"clojureCode": "",
			"group": "STRING",
			"name": "upper-case"
		},
		"functParams": [],
		"__type": "FunctionWithArgs"
	}*/
	
	private String functionName;
	private String[] params;

	public CustomFunctionParser(JSONObject function) {
		try {
			this.functionName = function.getJSONObject("funct").getString("name");
			if(function.getJSONArray("functParams") != null) {
				JSONArray functParams = function.getJSONArray("functParams");
				if(functParams.length() > 0) {
					this.params = new String[functParams.length()];
					for (int i = 0; i < functParams.length(); i++) {
						String param = functParams.getString(i);
						this.params[i] = param;
					}
				}
			}
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public BaseCustomFunction parseCustomFunction() {
		switch(this.functionName) {
			case CustomFunctionEnumKey.BOOLEAN:
				return new BooleanCustomF();
			case CustomFunctionEnumKey.UPPERCASE:
				return new UppercaseCustomF();
			case CustomFunctionEnumKey.LOWERCASE:
				return new LowercaseCustomF();
			case CustomFunctionEnumKey.CAPITALIZE:
				return new CapitalizeCustomF();
			case CustomFunctionEnumKey.FLOAT:
				return new FloatCustomF();
			case CustomFunctionEnumKey.LONG:
				return new LongCustomF();
			case CustomFunctionEnumKey.DOUBLE_LITERAL:
				return new DoubleLiteralCustomF();
			case CustomFunctionEnumKey.INTEGER_LITERAL:
				return new IntegerLiteralCustomF();
			case CustomFunctionEnumKey.STRING_TO_NUMERIC:
				return new StringToNumeriCustomF();
			case CustomFunctionEnumKey.TITLEIZE:
				return new TitleizeCustomF();
			case CustomFunctionEnumKey.TRIM:
				return new TrimCustomF();
			case CustomFunctionEnumKey.TRIM_L:
				return new LTrimCustomF();
			case CustomFunctionEnumKey.TRIM_R:
				return new RTrimCustomF();
			case CustomFunctionEnumKey.TRIM_NL:
				return new TrimNLCustomF();
			case CustomFunctionEnumKey.REMOVE_BLANK:
				return new RemoveBlankCustomF();
				
		}
		
		return null;
	}
}
