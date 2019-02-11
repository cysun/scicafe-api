package springrest.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public enum Position {
    	Student, 
    	Faculty,
    	Staff
    }

    @Id
    @GeneratedValue
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Position position;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String email;
    
    private String title;
    
    public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Set<Program> getPrograms() {
		return programs;
	}

	public void setPrograms(Set<Program> programs) {
		this.programs = programs;
	}

	private String unit;

    @ManyToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH},fetch=FetchType.LAZY)
    @JoinTable(name = "authorities",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles;
    
    @JsonIgnore
	@ManyToMany(mappedBy="attendees" )
    Set<Event> events;
    
    //a user may have more than one program
    @ManyToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH},fetch=FetchType.LAZY)
    @JoinTable(
        name = "users_programs", 
        joinColumns = { @JoinColumn(name = "user_id") }, 
        inverseJoinColumns = { @JoinColumn(name = "program_id") }
    )
    Set<Program> programs;

    public User()
    {
        roles = new HashSet<Role>();
    }

    public Long getId()
    {
        return this.id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }
    
    public String getFirstName() 
    {
    	return this.firstName;
    }
    
    public void setFirstName( String firstName)
    {
    	this.firstName = firstName;
    }
    
    public String getLastName() 
    {
    	return this.lastName;
    }
    
    public void setLastName( String lastName)
    {
    	this.lastName = lastName;
    }

    public Position getPosition()
    {
    	return this.position;
    }
    
    public void setPosition( Position position)
    {
    	this.position = position;
    }
    
    public String getUsername()
    {
        return this.username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getEmail() 
    {
    	return this.email;
    }
    
    public void setEmail ( String email) 
    {
    	this.email = email;
    }

    public String getTitle() 
    {
    	return this.title;
    }
    
    public void setTitle ( String title) 
    {
    	this.title = title;
    }
    
    public Set<Role> getRoles()
    {
        return roles;
    }

    public void setRoles( Set<Role> roles )
    {
        this.roles = roles;
    }

	public Set<Event> getEvents() {
		return events;
	}

	public void setEvents(Set<Event> events) {
		this.events = events;
	}

    
}