package springrest.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class ProgramControllerTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeClass
    void setup()
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup( wac ).build();
    }
    
    @Test
    void getProgram() throws Exception
    {
        this.mockMvc.perform( get( "/program/101" ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "name" ).value( "FYrE@ECST" ) );
    }

    @Test
    void getPrograms() throws Exception
    {
        this.mockMvc.perform( get( "/programs" ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$[0].name" ).value( "FYrE@ECST" ) );
    }

    @Test
    void getPrograms2() throws Exception
    {
        this.mockMvc.perform( get( "/programs" ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.length()" )
                .value( Matchers.greaterThanOrEqualTo( 2 ) ) );
    }

    @Test
    @Rollback(false)
    void createProgram() throws Exception
    {
        this.mockMvc
            .perform( post( "/programs" ).contentType( "application/json" )
                .content("{\"name\": \"MCS\"," + 
                		"        \"fullName\": \"Master of Computer Science\"," + 
                		"        \"description\": \"Master program of Computer Science\"" +
                		"    }" ) )
            .andExpect( status().is2xxSuccessful() )
            .andExpect( jsonPath( "fullName" ).value( "Master of Computer Science" ) );
    }
    
    @Test
    @Rollback(false)
    void updateProgram() throws Exception
    {
        this.mockMvc
            .perform( put( "/program/103" ).contentType( "application/json" )
                .content("{\"name\": \"new MCS\"," + 
                		"        \"fullName\": \"new Master of Computer Science\"," + 
                		"        \"description\": \"new Master program of Computer Science\"" +
                		"    }" ) )
            .andExpect( status().is2xxSuccessful() )
            .andExpect( jsonPath( "name" ).value( "new MCS" ) );
    }
    
    @Test
    @Rollback(false)
    void deleteProgram() throws Exception
    {
        this.mockMvc
            .perform( delete( "/program/104" ))
            .andExpect( status().is2xxSuccessful() );
    }

}