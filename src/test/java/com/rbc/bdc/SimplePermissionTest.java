package com.rbc.bdc;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class SimplePermissionTest {
	
	InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("simple_permissions.json");
	String jsonString = new Scanner(jsonInputStream, "UTF-8").useDelimiter("\\Z").next();

	@Test
	public void givenDocument_whenCalculatingArrayLength_thenSucceed(){
		 DocumentContext context= JsonPath.parse(jsonString);
		 int length = context.read("$.length()");
		 //List<Object> accountList = context.read("$[ca].[account].[read].[data]");
		 
	     assertEquals(length, 4);
	}
	
	@Test
	public void givenPathWithBrackets_whenLookingUpAccounts_thenSucceed(){
		 DocumentContext context= JsonPath.parse(jsonString);
		
		 Object accountList = context.read("$.[?(@.operation == 'ca.transfer.account' && @.permission == 'read')].['data']");
		 
	     String dataString = accountList.toString();
		
	     assertEquals("[[\"account1\",\"account2\",\"account3\",\"account4\"]]", dataString);
	}
	
	@Test
	public void givenRegEx_whenLookingUpAccounts_thenSucceed(){
		 DocumentContext context= JsonPath.parse(jsonString);
		
		 Object accountList = context.read("$.[?(@.operation =~ /ca.\\w*.account/i && @.permission == 'read')].['data']");
		 
	     String dataString = accountList.toString();
		
	     assertEquals("[[\"account1\",\"account2\",\"account3\",\"account4\"],[\"account4\",\"account5\",\"account6\",\"account7\"]]", dataString);
	}
	

	@Test
	public void givenAccountList_whenLookingUppermissions_thenSucceed(){
		 DocumentContext context= JsonPath.parse(jsonString);
		
		 Object accountList = context.read("$[?('account4' in @['data'])]");
		 
	     String dataString = accountList.toString();
		
	     assertEquals("[{\"operation\":\"ca.transfer.account\",\"permission\":\"read\",\"data\":[\"account1\",\"account2\",\"account3\",\"account4\"]},{\"operation\":\"ca.transfer.account\",\"permission\":\"write\",\"data\":[\"account2\",\"account4\"]},{\"operation\":\"ca.reporting.account\",\"permission\":\"read\",\"data\":[\"account4\",\"account5\",\"account6\",\"account7\"]},{\"operation\":\"ca.reporting.account\",\"permission\":\"write\",\"data\":[\"account4\",\"account6\"]}]", dataString);
	}
	
	
	//$[?('Eva Green' in @['starring'])]")

}
