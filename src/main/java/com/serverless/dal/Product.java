package com.serverless.dal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

@DynamoDBTable(tableName = "PLACEHOLDER_PRODUCTS_TABLE_NAME")
public class Product {
	private static final String PRODUCTS_TABLE_NAME = System.getenv("PRODUCTS_TABLE_NAME");
	
	private DynamoDBAdapter db_adapter;
	private AmazonDynamoDB client;
	private DynamoDBMapper mapper;
	
	private String id;
	private String name;
	private Float price;
	
	private static final Logger logger = Logger.getLogger(Product.class.getName());
	
	public Product() {
		DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
				.withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(PRODUCTS_TABLE_NAME))
				.build();
		this.db_adapter = DynamoDBAdapter.getInstance();
		this.client = this.db_adapter.getDbClient();
		this.mapper = this.db_adapter.createDbMapper(mapperConfig);
	}
	
	@DynamoDBHashKey(attributeName = "id")
	@DynamoDBAutoGeneratedKey
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@DynamoDBRangeKey(attributeName = "name")
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@DynamoDBAttribute(attributeName = "price")
	public Float getPrice() {
		return this.price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	
	public List<Product> list() throws IOException {
		DynamoDBScanExpression scanExp = new DynamoDBScanExpression();
		List<Product> results = this.mapper.scan(Product.class, scanExp);
		for (Product p: results) {
			logger.info("Products - list(): " + p.toString());
		}
		return results;
	}
	
	public Product get(String id) throws IOException {
		Product product = null;
		
		HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
		av.put(":v1", new AttributeValue().withS(id));
		
		DynamoDBQueryExpression<Product> queryExp = new DynamoDBQueryExpression<Product>()
				.withKeyConditionExpression("id = :v1")
				.withExpressionAttributeValues(av);
		
		PaginatedQueryList<Product> result = this.mapper.query(Product.class, queryExp);
		if (result.size() > 0) {
			product = result.get(0);
			logger.info("Products - get(): product - " + product.toString());
		} else {
			logger.info("Products - get(): product - Not Found.");
		}
		
		return product;
	}
	
	public void save(Product product) throws IOException {
		logger.info("Products - save(): " + product.toString());
		this.mapper.save(product);
	}
	
	public Boolean delete(String id) throws IOException {
		Product product = null;
		
		// get product if exists
		product = get(id);
		if (product != null) {
			logger.info("Products - delete(): " + product.toString());
			this.mapper.delete(product);
		} else {
			logger.info("Products - delete(): product - does not exists");
			return false;
		}
		return true;
	}
}
