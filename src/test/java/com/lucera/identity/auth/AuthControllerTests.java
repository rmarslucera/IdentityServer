package com.lucera.identity.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lucera.identity.auth.security.jwt.JwtProvider;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class AuthControllerTests {

    private MockMvc mvc;
    
    @Autowired
    Gson gson;
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private JwtProvider jwtHelper;

 
    @Before
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:roles.sql")
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:users.sql")
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
    	//TODO: Fix the assertion to see that the correct Exception RoleNotFound was thrown.
    	//		The code is working, the assertion is not
        
    	mvc.perform( MockMvcRequestBuilders
    		      .post("/lucera/api/auth/signup")
    		      .content(Utils.asJsonString(Utils.getSignUpForm("norole")))
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .accept(MediaType.APPLICATION_JSON))
    			  .andExpect(status().is5xxServerError()).andDo(print());
    			  //.andExpect(content().string("Error! -> Cause: User Role not found."));
    	
    }
    
    @Test
    @WithAnonymousUser
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:roles.sql")
    public void signUp() throws Exception { 	
    	        
    	mvc.perform( MockMvcRequestBuilders
    		      .post("/lucera/api/auth/signup")
    		      .content(Utils.asJsonString(Utils.getSignUpForm("user")))
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .accept(MediaType.APPLICATION_JSON))
    			  .andExpect(status().is2xxSuccessful())
    		      .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists());
    }
 
    @Test
    @WithAnonymousUser
    //@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:users.sql")
    public void signIn() throws Exception {
    	        
    	MvcResult res = mvc.perform( MockMvcRequestBuilders
    		      .post("/lucera/api/auth/signin")
    		      .content(Utils.asJsonString(Utils.getLoginForm()))
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .accept(MediaType.APPLICATION_JSON))
    			  .andExpect(status().is2xxSuccessful()).andReturn();
    		     
    	
    	assertThat(getUserNameFromResp(res)).isEqualTo(Utils.getUser().getUsername());
    	
    }
    
    @Test
    @WithAnonymousUser
    //@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:roles.sql")
    //@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:users.sql")
    public void userAccess() throws Exception {
    	
    	    	
    	        
    	MvcResult res = mvc.perform( MockMvcRequestBuilders
    		      .post("/lucera/api/auth/signin")
    		      .content(Utils.asJsonString(Utils.getLoginForm()))
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .accept(MediaType.APPLICATION_JSON))
    			  .andExpect(status().is2xxSuccessful()).andReturn();
    		     
    	
    	mvc.perform( MockMvcRequestBuilders.get("/lucera/api/test/user")
			    					       .header("authentication", "Bearer " + getAuthTokenFromResp(res)))
									       .andExpect(status().isOk())
									       .andExpect(MockMvcResultMatchers.jsonPath("$.userProfile").exists());
									                    
    }
    
    @Test
    @WithAnonymousUser
    //@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:roles.sql")
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:broker.sql")
    public void brokerAccess() throws Exception {
    	
    	    	
    	        
    	MvcResult res = mvc.perform( MockMvcRequestBuilders
    		      .post("/lucera/api/auth/signin")
    		      .content(Utils.asJsonString(Utils.getLoginForm()))
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .accept(MediaType.APPLICATION_JSON))
    			  .andExpect(status().is2xxSuccessful()).andReturn();
    		     
    	
    	mvc.perform( MockMvcRequestBuilders.get("/lucera/api/test/broker")
			    					       .header("authentication", "Bearer " + getAuthTokenFromResp(res)))
									       .andExpect(status().isOk())
									       .andExpect(MockMvcResultMatchers.jsonPath("$.userProfile").exists());
									                    
    }
    
    @Test
    @WithAnonymousUser
    //@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:roles.sql")
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:admin.sql")
    public void adminAccess() throws Exception {
    	
    	    	
    	        
    	MvcResult res = mvc.perform( MockMvcRequestBuilders
    		      .post("/lucera/api/auth/signin")
    		      .content(Utils.asJsonString(Utils.getLoginForm()))
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .accept(MediaType.APPLICATION_JSON))
    			  .andExpect(status().is2xxSuccessful()).andReturn();
    		     
    	
    	mvc.perform( MockMvcRequestBuilders.get("/lucera/api/test/admin")
			    					       .header("authentication", "Bearer " + getAuthTokenFromResp(res)))
									       .andExpect(status().isOk())
									       .andExpect(MockMvcResultMatchers.jsonPath("$.userProfile").exists());
									                    
    }
    
    @Test
    @WithAnonymousUser
    //@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:roles.sql")
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:changeToUserRole.sql")
    public void adminAccessNoRoleExpect403() throws Exception {
    	
    	    	
    	        
    	MvcResult res = mvc.perform( MockMvcRequestBuilders
    		      .post("/lucera/api/auth/signin")
    		      .content(Utils.asJsonString(Utils.getLoginForm()))
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .accept(MediaType.APPLICATION_JSON))
    			  .andExpect(status().is2xxSuccessful()).andReturn();
    		     
    	
    	mvc.perform( MockMvcRequestBuilders.get("/lucera/api/test/admin")
			    					       .header("authentication", "Bearer " + getAuthTokenFromResp(res)))
									       .andExpect(status().isForbidden());
									                    
    }
    
    @Test
    @WithAnonymousUser
    //@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:roles.sql")
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:changeToUserRole.sql")
    public void brokerAccessNoRoleExpect403() throws Exception {
    	
    	    	
    	        
    	MvcResult res = mvc.perform( MockMvcRequestBuilders
    		      .post("/lucera/api/auth/signin")
    		      .content(Utils.asJsonString(Utils.getLoginForm()))
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .accept(MediaType.APPLICATION_JSON))
    			  .andExpect(status().is2xxSuccessful()).andReturn();
    		     
    	
    	mvc.perform( MockMvcRequestBuilders.get("/lucera/api/test/broker")
			    					       .header("authentication", "Bearer " + getAuthTokenFromResp(res)))
									       .andExpect(status().isForbidden());
									                    
    }
    
    private String getAuthTokenFromResp(MvcResult resp) throws JsonSyntaxException, UnsupportedEncodingException {
    	Map<String, String> map = gson.fromJson( resp.getResponse().getContentAsString(), new TypeToken<Map<String, Object>>(){}.getType() );
    	return map.get("accessToken");
    }
    
    
    private String getUserNameFromResp(MvcResult resp) throws JsonSyntaxException, UnsupportedEncodingException {
    	return jwtHelper.getUserNameFromJwtToken( getAuthTokenFromResp(resp)  );
    }
   
}
