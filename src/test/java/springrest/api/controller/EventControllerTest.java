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
class EventControllerTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeClass
    void setup()
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup( wac ).build();
    }
    
    @Test
    void getEvent() throws Exception
    {
        this.mockMvc.perform( get( "/event/101" ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "name" ).value( "ACM Student Chapter events") );
    }
    
    @Test
    void getEvents() throws Exception
    {
        this.mockMvc.perform( get( "/events" ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$[3].name" ).value( "Test event" ) );
    }
    
    @Test
    void getEvents2() throws Exception
    {
        this.mockMvc.perform( get( "/events" ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.length()" )
                .value( Matchers.greaterThanOrEqualTo( 4 ) ) );
    }
    
    @Test
    @Rollback(false)
    void createEvent() throws Exception
    {
        this.mockMvc
            .perform( post( "/programs" ).contentType( "application/json" )
                .content("{\r\n" + 
                		"        \"name\": \"MCS\",\r\n" + 
                		"        \"fullName\": \"Master of Computer Science\",\r\n" + 
                		"        \"description\": \"Master program of Computer Science\"\r\n" + 
                		"}"))
            .andExpect( status().is2xxSuccessful() )
            .andExpect(jsonPath( "name")
            		.value("MCS"));
    }
    
    @Test
    @Rollback(false)
    void approveEvent() throws Exception
    {
        this.mockMvc
            .perform( put( "/event/approve/101" ).contentType( "application/json" ))
            .andExpect( status().is2xxSuccessful() )
            .andExpect(jsonPath( "status")
            		.value("approved"));
    }
    
    @Test
    @Rollback(false)
    void rejectEvent() throws Exception
    {
        this.mockMvc
            .perform( put( "/event/reject/101" ).contentType( "application/json" ))
            .andExpect( status().is2xxSuccessful() )
            .andExpect(jsonPath( "status")
            		.value("rejected"));
    }
    
    @Test
    @Rollback(false)
    void getAttendees() throws Exception
    {
        this.mockMvc
            .perform( get( "/event/102/attendees" ).contentType( "application/json" ))
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.length()" )
            .value( Matchers.greaterThanOrEqualTo( 2 ) ) );;
    }
    
    @Test
    @Rollback(false)
    void addAttendee() throws Exception
    {
        this.mockMvc
            .perform( post( "/event/101/attendee" ).contentType( "application/json" )
            .content("{\r\n" + 
            		"        \"id\":101\r\n" + 
            		"}"))
            .andExpect( status().is2xxSuccessful() )
            .andExpect( jsonPath( "$.length()" )
                    .value( Matchers.greaterThanOrEqualTo( 1 ) ) );
    }

}