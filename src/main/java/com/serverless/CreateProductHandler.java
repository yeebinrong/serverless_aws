package com.serverless;
import com.serverless.dal.Product;

import java.net.http.HttpHeaders;
import java.util.Collections;

import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateProductHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger logger = LogManager.getLogger(CreateProductHandler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		try {
			// get the 'body' from input
			JsonNode body = new ObjectMapper().readTree((String) input.get("body"));
			
			// create the product object for post
			Product product = new Product();
			//product.setId(body.get("id").asText());
			product.setName(body.get("name").asText());
			product.setPrice((float) body.get("price").asDouble());
			product.setQty((int) body.get("qty").asInt());
			product.save(product);
			
			Map<String, String> m = new HashMap<String, String>();
			m.put("X-Powered-By","AWS Lambda & Serverless");
			m.put("Access-Control-Allow-Origin","true");
			m.put("Access-Control-Allow-Origin","*");

			// send the response back
				return ApiGatewayResponse.builder()
						.setStatusCode(200)
						.setObjectBody(product)
						// .setHeaders(Collections.singletonMap("X-Powered-By","AWS Lambda & Serverless"))
						.setHeaders(m)
						.build();
		} catch (Exception e) {
	          logger.error("Error in saving product: " + e);
	          
	          // send the error response back
	          	Response responseBody = new Response("Error in saving the product: ", input);
	          	return ApiGatewayResponse.builder()
	          			.setStatusCode(500)
	          			.setObjectBody(responseBody)
	          			.setHeaders(Collections.singletonMap("X-Powered-By","AWS Lambda & Serverless"))
	          			.build();
		}
	}
}
