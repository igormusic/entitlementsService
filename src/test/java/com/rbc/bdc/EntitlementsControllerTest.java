package com.rbc.bdc;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Igor Music
 */


@RunWith(SpringRunner.class)
@SpringBootTest
public class EntitlementsControllerTest {


    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

 
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    
    @Autowired
    private  CacheProvider entitlementCacheProvider;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        
        InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("simple_permissions.json");
    	String jsonString = new Scanner(jsonInputStream, "UTF-8").useDelimiter("\\Z").next();
    	
    	entitlementCacheProvider.put("user1", jsonString);
    }


    @Test
    public void getAllEntitlements() throws Exception {
        mockMvc.perform(get("/entitlements/?userId=user1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].operation", is("ca.transfer.account")))
		        .andExpect(jsonPath("$[1].operation", is("ca.transfer.account")))
		        .andExpect(jsonPath("$[2].operation", is("ca.reporting.account")))
		        .andExpect(jsonPath("$[3].operation", is("ca.reporting.account")));
                            
    }
    
    @Test
    public void getEntitlementsForOperationAndPermission() throws Exception {
        mockMvc.perform(get("/entitlements")
        		.param("userId","user1")
        		.param("q", "$.[?(@.operation == 'ca.transfer.account' && @.permission == 'read')].['data']"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().string("[[\"account1\",\"account2\",\"account3\",\"account4\"]]"));
                             
    }
    
    @Test
    public void getEntitlementsForOperationRegExAndPermission() throws Exception {
        mockMvc.perform(get("/entitlements")
        		.param("userId","user1")
        		.param("q", "$.[?(@.operation  =~ /ca.\\w*.account/ && @.permission == 'read')].['data']"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string("[[\"account1\",\"account2\",\"account3\",\"account4\"],[\"account4\",\"account5\",\"account6\",\"account7\"]]"));
                             
    }


    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}