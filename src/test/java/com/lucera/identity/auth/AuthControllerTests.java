package com.lucera.identity.auth;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucera.identity.auth.model.User;
import com.lucera.identity.auth.repository.UserRepository;

@RunWith(SpringRunner.class)
//@WebMvcTest(AuthController.class)
//@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTests {

    private MockMvc mvc;
 
    @MockBean
    private UserRepository userRepository;
    
    @Autowired
    private WebApplicationContext context;
 
    // This object will be magically initialized by the initFields method below.
    private JacksonTester<User> jsonUser;
 
    @Before
    public void setup() {
    	JacksonTester.initFields(this, new ObjectMapper());
    	
        mvc = MockMvcBuilders
          .webAppContextSetup(context)
          .apply(SecurityMockMvcConfigurers.springSecurity())
          .build();
    }
    
    @Test
    @WithAnonymousUser
    public void signUpTestNoRoles() throws Exception {
    	
        
    	mvc.perform( MockMvcRequestBuilders
    		      .post("/lucera/api/auth/signup")
    		      .content(Constants.asJsonString(Constants.getSignUpForm("user")))
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .accept(MediaType.APPLICATION_JSON))
    			  .andExpect(status().is5xxServerError()).andDo(print());
    			  //.andExpect(content().string("Error! -> Cause: User Role not found."));
    	
    }
    
    @Test
    @WithAnonymousUser
    public void signUpTest() throws Exception {
    	
        
    	mvc.perform( MockMvcRequestBuilders
    		      .post("/lucera/api/auth/signup")
    		      .content(Constants.asJsonString(Constants.getSignUpForm("user")))
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .accept(MediaType.APPLICATION_JSON))
    			  .andExpect(status().isCreated())
    		      .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists());
    }
 
   
}
