package com.serverless;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.dal.Product;

public class DeleteProductHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger logger = LogManager.getLogger(DeleteProductHandler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		try {
			// get the 'pathParamters' from input
	        @SuppressWarnings("unchecked")
			Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
	        String productId = pathParameters.get("id");

	        // get the Product by id
	        Boolean success = new Product().delete(productId);
			
			Map<String, String> m = new HashMap<String, String>();
			m.put("X-Powered-By","AWS Lambda & Serverless");
			m.put("Access-Control-Allow-Origin","true");
			m.put("Access-Control-Allow-Origin","*");

			// send response back
	        if (success) {
	            return ApiGatewayResponse.builder()
	        				.setStatusCode(204)
							// .setHeaders(Collections.singletonMap("X-Powered-By","AWS Lambda & Serverless"))
							.setHeaders(m)
	        				.build();
	          } else {
	            return ApiGatewayResponse.builder()
	        				.setStatusCode(404)
	        				.setObjectBody("Product with id: '" + productId + "' not found.")
							// .setHeaders(Collections.singletonMap("X-Powered-By","AWS Lambda & Serverless"))
							.setHeaders(m)
	        				.build();
	          }
		} catch (Exception e) {
			logger.error("Error in listing products: " + e);
			
	          // send the error response back
        	Response responseBody = new Response("Error in deleting the product: ", input);
        	return ApiGatewayResponse.builder()
        			.setStatusCode(500)
        			.setObjectBody(responseBody)
        			.setHeaders(Collections.singletonMap("X-Powered-By","AWS Lambda & Serverless"))
        			.build();
		}
	}
}
