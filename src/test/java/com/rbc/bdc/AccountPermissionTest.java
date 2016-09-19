package com.rbc.bdc;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class AccountPermissionTest {
	
	InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("flat_service_account_permission_data.json");
	String jsonString = new Scanner(jsonInputStream, "UTF-8").useDelimiter("\\Z").next();

	@Test
	public void givenDocument_whenCalculatingArrayLength_thenSucceed(){
		 DocumentContext context= JsonPath.parse(jsonString);
		 int length = context.read("$.length()");
		 //List<Object> accountList = context.read("$[ca].[account].[read].[data]");
		 
	     assertEquals(length, 1);
	}
	
	@Test
	public void givenPathWithBrackets_whenLookingUpAccounts_thenSucceed(){
		 DocumentContext context= JsonPath.parse(jsonString);
		
		 Object accountList = context.read("$['ca'].['account'].[?(@['permission'] == 'read')].['data']");
		 
	     String dataString = accountList.toString();
		
	     assertEquals("[[\"account1\",\"account2\",\"account3\",\"account4\"]]", dataString);
	}
	
	@Test
	public void givenAccounts_whenLookingUpServices_thenSucceed(){
		 DocumentContext context= JsonPath.parse(jsonString);
		
		 Object accountList = context.read("$..[?('account1' in @['data'])]");
		 
	     String dataString = accountList.toString();
		
	     assertEquals("[[\"account1\",\"account2\",\"account3\",\"account4\"]]", dataString);
	}
	//$[?('Eva Green' in @['starring'])]")

}
