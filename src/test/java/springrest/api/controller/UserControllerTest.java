package springrest.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:springrest-servlet.xml",
    "classpath:applicationContext.xml" })
class UserControllerTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeClass
    void setup()
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup( wac ).build();
    }
    
    @Test
    void getUser() throws Exception
    {
        this.mockMvc.perform( get( "/user/101" ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "username" ).value( "admin" ) );
    }

    @Test
    void getUsers() throws Exception
    {
        this.mockMvc.perform( get( "/users" ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$[0].username" ).value( "admin" ) );
    }

    @Test
    void getUsers2() throws Exception
    {
        this.mockMvc.perform( get( "/users" ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.length()" )
                .value( Matchers.greaterThanOrEqualTo( 5 ) ) );
    }

    @Test
    @Rollback(false)
    void createUserFail() throws Exception
    {
        this.mockMvc
            .perform( post( "/users" ).contentType( "application/json" )
                .content("{\"position\": \"Staff\"," + 
                		"        \"username\": \"admin\"," + 
                		"        \"email\": \"peterparker@gmail.com\"," + 
                		"        \"title\": \"Admin of Sci-CAFE\"," + 
                		"        \"unit\": \"CSULA\"" + 
                		"    }" ) )
            .andExpect( status().is4xxClientError());
    }

    @Test
    @Rollback(false)
    void createUserPass() throws Exception
    {
        this.mockMvc
            .perform( post( "/users" ).contentType( "application/json" )
                .content("{\"firstName\": \"Peter\"," + 
                		"        \"lastName\": \"Parker\"," + 
                		"        \"position\": \"Staff\"," + 
                		"        \"username\": \"pp\"," + 
                		"        \"password\": \"1234\"," + 
                		"        \"email\": \"peterparker@gmail.com\"," + 
                		"        \"title\": \"Admin of Sci-CAFE\"," + 
                		"        \"unit\": \"CSULA\"" + 
                		"    }" ) )
            .andExpect( status().is2xxSuccessful() )
            .andExpect( jsonPath( "username" ).value( "pp" ) );
    }

}