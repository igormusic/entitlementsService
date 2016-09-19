package com.rbc.bdc;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

@Ignore
public class ArrayAccountPermissionTest {
	
	InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("flat_array_service_account_permission_data.json");
	String jsonString = new Scanner(jsonInputStream, "UTF-8").useDelimiter("\\Z").next();

	@Test
	public void givenDocument_whenCalculatingArrayLength_thenSucceed(){
		 DocumentContext context= JsonPath.parse(jsonString);
		 int length = context.read("$.length()");
		 //List<Object> accountList = context.read("$[ca].[account].[read].[data]");
		 
	     assertEquals(length, 2);
	}
	
	@Test
	public void givenPathWithBrackets_whenLookingUpAccounts_thenSucceed(){
		
		Configuration config = Configuration
        .builder()
        .mappingProvider(new JacksonMappingProvider())
        .jsonProvider(new JacksonJsonProvider())
        .options(Option.ALWAYS_RETURN_LIST)
        .build();
		
		
		 DocumentContext context= JsonPath.using(config).parse(jsonString);
		
		 Object accountList = context.read("$[?(@.reporting)].[?(@.account)]");
		 
	     String dataString = accountList.toString();
		
	     assertEquals("[[\"account1\",\"account2\",\"account3\",\"account4\"]]", dataString);
	}
	
	

}
