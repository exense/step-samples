package ch.exense.step.examples.http;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue.ValueType;

import step.functions.io.OutputBuilder;

public class JsonHelper {
	
	// TODO Add the error code to RTM transaction
	// TODO mark unknown error to RTM transaction
	
	//Error body are always of type object and contains keys errorCode and errorMessage
	protected static String getBusinessError(JsonStructure response, OutputBuilder output) {
		String errorCode = null;
		if (response.getValueType().equals(ValueType.OBJECT)) {
			errorCode = addJsonObjectField2output((JsonObject) response, "errorCode", output,false);
			addJsonObjectField2output((JsonObject) response, "errorMessage", output, false);
		}
		return errorCode;
	}
	
	public static JsonStructure checkJsonResponse(HttpResponse httpResponse, ValueType valueType, int httpStatus, OutputBuilder output) {		
		int status = httpResponse.getStatus();
		String responsePayload = httpResponse.getResponsePayload();
		JsonStructure response = toJsonStructure(responsePayload);
		
		//always add the http status code to output
		output.add("HttpStatus", httpStatus);
		
		//if httpCode and json value type are not as expected, set error messages and return null 
		if (status != httpStatus || !response.getValueType().equals(valueType)) {
			//Response might contain business error, add them to output if any
			getBusinessError(response, output);
			//TODO probably remove full response from output once dev is complete
			output.add("response", response.toString());
			return null;//failWithErrorMessage("Unexpected response", output);
		} //checks for business errors anyway
		else if (getBusinessError(response, output) != null) {
			return null;//failWithErrorMessage("Business errors returned", output);
		}	
		return response;
	}
	
	public static String addJsonArrayField2output(JsonArray responseArray, int index, String key, OutputBuilder output, boolean mandatory) {
		JsonObject responseObject = responseArray.getJsonObject(index);				
		return addJsonObjectField2output(responseObject, key , output, mandatory);
	}
	
	public static String addJsonObjectField2output(JsonObject responseObject, String key, OutputBuilder output, boolean mandatory) {
		String value = null;
		if (responseObject.containsKey(key)) {
			value = responseObject.get(key).toString();
			output.add(key, value);
		} else if (mandatory) {
			//add error info if any
			getBusinessError(responseObject,output);
			//TODO probably remove full response from output once dev is complete
			output.add("response", responseObject.toString());
			return null;//failWithErrorMessage("Json object not found: " + key, output);
		}
		return value;
	}

	public static JsonObject toJsonObject(String jsonString) {
		JsonReader jsonReader = Json.createReader(new StringReader(jsonString));	
	    JsonObject object = jsonReader.readObject();
	    jsonReader.close();

	    return object;
		
	}
	
	public static JsonArray toJsonArray(String jsonString) {
		JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
		JsonArray array = jsonReader.readArray();
		jsonReader.close();
		return array;
	}
	
	public static JsonStructure toJsonStructure(String jsonString) {
		JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
		JsonStructure structure = jsonReader.read();
		jsonReader.close();
		return structure;
	}
}
