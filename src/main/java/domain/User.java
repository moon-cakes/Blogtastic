package domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Entity class to define a registered blog user. A user has a username,
 * first name, and last name.
 * 
 * Users and blogs are related using a many-to-many association. A user can 
 * subscribe to many blogs.
 * 
 * @author Xiaohui
 */
@Entity
@Table(name = "USERS") 
@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @XmlAttribute(name="id")
    private Long _id;
    
    @Column(name="USERNAME", nullable=false, length=30)
    @XmlElement(name="username")
    private String _username;

	@Column(name="FIRSTNAME", nullable=false, length=30)
    @XmlElement(name="first-name")
    private String _firstname;
    
    @Column(name="LASTNAME", nullable=false, length=30)
    @XmlElement(name="last-name")
    private String _lastname;
	
	@XmlElementWrapper(name="following-blogs")
	@XmlElement(name="blog")
	@ManyToMany(mappedBy= "_subscribers")
	private Set<Blog> _following = new HashSet<Blog>( );
	
    protected User(){
    }
    
    public User(String lastname, String firstname, String username){
    	_firstname = firstname;
    	_lastname = lastname;
    	_username = username;
    }

    public User(String lastname, String firstname, Set<Blog> blogs, Set<Blog> following) {
    	_firstname = firstname;
    	_lastname = lastname;
    	_following = following;
    }

    public Long get_id() {
        return _id;
    }
    
	public void set_id(long id) {
		_id = id;
	}
	
    public String get_username() {
		return _username;
	}

	public void set_username(String _username) {
		this._username = _username;
	}
    
    public String get_firstname() {
    	return _firstname;
    }
    
    public void set_firstname(String firstname){
    	_lastname = firstname;
    }
    
    public String get_lastname() {
    	return _lastname;
    }
    
    public void set_lastname(String lastname){
    	_lastname = lastname;
    }

	public Set<Blog> get_following() {
		return Collections.unmodifiableSet(_following);
	}

	public void add_to_following(Blog blog) {
		_following.add(blog);
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("User: { [");
		buffer.append(_id);
		buffer.append("]; ");
		if(_lastname != null) {
			buffer.append(_lastname);
			buffer.append(", ");
		}
		if(_firstname != null) {
			buffer.append(_firstname);
		}
		buffer.append(" }");
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User))
            return false;
        if (obj == this)
            return true;

        User rhs = (User) obj;
        return new EqualsBuilder().
            append(_id, rhs.get_id()).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
				append(getClass().getName()).
	            append(_id).
	            toHashCode();
	}
}
