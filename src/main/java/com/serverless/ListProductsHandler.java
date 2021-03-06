package com.serverless;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.dal.Product;

public class ListProductsHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger logger = LogManager.getLogger(ListProductsHandler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("X-Powered-By","AWS Lambda & Serverless");
		m.put("Access-Control-Allow-Origin","true");
		m.put("Access-Control-Allow-Origin","*");
		try {
			List<Product> products = new Product().list();

			// send the response back
			return ApiGatewayResponse.builder()
					.setStatusCode(200)
					.setObjectBody(products)
					// .setHeaders(Collections.singletonMap("X-Powered-By","AWS Lambda & Serverless"))
					.setHeaders(m)
					.build();
		} catch (Exception e) {
			logger.error("Error in listing products: " + e);
			
	          // send the error response back
          	Response responseBody = new Response("Error in getting the product list: ", input);
          	return ApiGatewayResponse.builder()
          			.setStatusCode(500)
          			.setObjectBody(responseBody)
					// .setHeaders(Collections.singletonMap("X-Powered-By","AWS Lambda & Serverless"))
					.setHeaders(m)
          			.build();
		}
	}
}
