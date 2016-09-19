package com.rbc.bdc;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;


@RestController
@RequestMapping("/entitlements")
public class EntitlementsController {
	
	private final CacheProvider entitlementCacheProvider;
	
	@Autowired
	 EntitlementsController(CacheProvider cacheProvider){
		entitlementCacheProvider = cacheProvider;
	}

	@RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> greeting(@RequestParam(value="userId") String userId, @RequestParam(value="q", required=false) String query) {
		HttpHeaders httpHeaders = new HttpHeaders();
		
		if(!entitlementCacheProvider.containsKey(userId))
        {
        	return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
        }
		
        String entitlements = entitlementCacheProvider.get(userId);
        
        if(query==null)
        {
        	return new ResponseEntity<>(entitlements, httpHeaders, HttpStatus.OK);
        }
        
        Configuration pathConfiguration = Configuration
									        .builder()
									        .mappingProvider(new JacksonMappingProvider())
									        .jsonProvider(new JacksonJsonProvider())
									        .build();
        
        DocumentContext jsonContext = JsonPath.using(pathConfiguration).parse(entitlements);
        
    	Object queryResult = jsonContext.read(query);
    	
    	String dataString = queryResult.toString();
        
    	return new ResponseEntity<>(dataString, httpHeaders, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> add(@Valid @RequestBody EntitlementSet input) {
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
    	String json = null;
    	
    	try {
			json= mapper.writeValueAsString(input.getEntitlements());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	entitlementCacheProvider.put(input.getUserId(), json);
    	
    	HttpHeaders httpHeaders = new HttpHeaders();
		
    	httpHeaders.setLocation(ServletUriComponentsBuilder
				.fromCurrentRequest().path("?userId={id}")
				.buildAndExpand(input.getUserId()).toUri());
    	
		return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
	}
}
