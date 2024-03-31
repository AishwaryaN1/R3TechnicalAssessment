package assignmentFirst;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import  org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.time.Duration;


public class HTTPRequestsDemo {
	String url="https://open.er-api.com/v6/latest/USD";
	
	@Test(priority=0)
	//API call is successful and returns valid price.
	void getResponse() 
	{
		given()
		
		.when()
		.get(url)
		
		
		.then()
		.statusCode(200)
		.log().all();
	}
	
	
	
	
	@Test(priority=1)
	/*Check the status code and status retuned by the API response.
	o API could return multiple statuses like SUCCESS, FAILURE etc. Make sure this
			is catered for.*/
	
	void checkStatus()
	{
		Response res = given()
                .contentType(ContentType.JSON) 
                
              
                .when()
                .get(url);

        
        int statusCode = res.getStatusCode();
        JSONObject jo = new JSONObject(res.getBody().asString());
        String status = jo.has("status") ? jo.getString("status") : "UNKNOWN";
        
        assertEquals( 200, statusCode,"API response status code is not 200");
        String expectedStatus = "SUCCESS";
        assertEquals(expectedStatus, status,"API response status is not as expected" );
	}
	
	
	
	@Test(priority=2)
	/*Fetch the USD price against the AED and make sure the prices are in range on 3.6 –
3.7.*/
	void checkAEDRate()
	{
		Response res=given()
			.contentType(ContentType.JSON)
				
				
		.when()
		.get(url);
	
		Assert.assertEquals(res.getStatusCode(),200);
		JSONObject Responsebody=new JSONObject(res.getBody().asString());    // Extracting response body as JSON object
		
		double calcRateofAED =Responsebody.getJSONObject("rates").getDouble("AED");
		
        assertTrue(calcRateofAED >= 3.6 && calcRateofAED <= 3.7,"AED price is not in the range of 3.6 to 3.7");
		System.out.println("Current rate of AED is: "+calcRateofAED);
				
	}
	
	
	
	@Test(priority=3)
	/*Make sure API response time is not less then 3 seconds then current time in second.
	o Timestamp is returned in the API response.*/
	void timeStamp()
	{
		long currentTime = System.currentTimeMillis();
		System.out.println("Response time in milliseconds is "+currentTime);
	
		Response res= given()
				.contentType(ContentType.JSON)
				
				.when()
				.get(url);
		
		 JSONObject jo=new JSONObject(res.getBody().asString());
		long responseTimestampinms = System.currentTimeMillis();
		
		if(jo.has("timestamp"))
		{
			responseTimestampinms=jo.getLong("timestamp")*1000;
		}
		else
		{
		
		long APIResponseTime=responseTimestampinms-currentTime; 
		//Time taken for API Reponse
		System.out.println("API Repsonse time is "+APIResponseTime+"in milliseconds");
		assertTrue(APIResponseTime >= 3000,"API response time is less than 3 seconds");
		}
		
	
	}
	

	
	
	@Test(priority=4)
	//Verify that 162 currency pairs are retuned by the API.
	
	void totalCurrenciesFromAPI()
	{
		Response res =given()
		.contentType(ContentType.JSON)
		
		.when()
		.get(url);
		
		 JSONObject jo = new JSONObject(res.getBody().asString()); // Converting response to JsonObject
	     JSONObject rateobj = jo.getJSONObject("rates");

	        JSONArray currencyArray = rateobj.names();

	        if (currencyArray != null && currencyArray.length() == 162) {
	            System.out.println("162 currency pairs are returned by the API.");
	        } else {
	            System.out.println("API did not return 162 currency pairs.");
	        }
		
	        System.out.println(currencyArray);
		
	}
	
	
	
	
	
	@Test(priority=5)
	/*Make sure API response matches the Json schema.
	Generate a schema from the API response.*/
	void APIResponseMatchesSchema() {
		
		
		Response res=given()
				.contentType(ContentType.JSON)
				
				.when()
				.get(url);
		
		
		JSONObject jo=new JSONObject(res.getBody().asString());//Extracting response body as JSONOBject
		
		String schema=jo.toString();//generating JSONSChema from response
		
		System.out.println("Schema"+ schema);
		res.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schema));
		
		
	}
	

}
