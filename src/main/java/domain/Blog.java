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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Entity class to represent a blog.
 * 
 * Blog and users are related using a many-to-many assocation. A blog can have many 
 * users subscribed to it, "subscribers", and users can subscribe to many blogs.
 * 
 * @author Xiaohui
 */
@Entity
@Table(name = "BLOGS") 
@XmlRootElement(name="blog")
@XmlAccessorType(XmlAccessType.FIELD)
public class Blog {
	
    @Id 
    //@GeneratedValue(generator="ID_GENERATOR")
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @XmlAttribute(name="id")
    private Long _id;
    
    @Column(name="BLOGNAME", nullable=false)
    @XmlElement(name="blog-name")
    private String _blogname;
	
	// Define a many-to-one association with User - many blogs can be associated 
	// with a single User. When a blog is loaded, its associated user/blog owner 
	// will be loaded on demand (lazily).
	@ManyToOne(fetch=FetchType.LAZY)
	// Make the association mandatory - a Blog MUST be owned by a user.
	@JoinColumn(name="USER_ID", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@XmlElement(name="blog-owner")
	protected User _blogowner;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CATEGORY_ID")
	private Category _category;

	// Define the many-to-many association with user. The association is implemented
	// using an intermediary join table. Cascading persistence is set so that
	// whenever a Blog instance is persisted, so are its subscribers.
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "BLOG_SUBSCRIBERS", joinColumns = @JoinColumn(name = "BLOG_ID"), 
	inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	@XmlElementWrapper(name="subscribers")
	@XmlElement(name="subscriber")
	private Set<User> _subscribers = new HashSet<User>();
	
	protected Blog(){
	}
	
	public Blog(String blogname, User blogowner){
	 	_blogname = blogname;
	 	_blogowner = blogowner;
    }
    
	public Blog(String blogname, User blogowner, Category cat){
	 	_blogname = blogname;
	 	_blogowner = blogowner;
	 	_category = cat;
    }
	
    public Blog(String blogname, Set<BlogEntry> blogentries, 
    		Category category, Set<User> subscribers ) {
    	_blogname = blogname;
    	//_blogentries = blogentries;
    	_category = category;
    	_subscribers = subscribers;
    }

	public Long get_id() {
		return _id;
	}
	
	public void set_id(long id) {
		_id = id;
	}

	public User get_blogowner() {
		return _blogowner;
	}

	public void set_blogowner(User owner){
		_blogowner = owner;
	}
	
	public String get_blogname() {
		return _blogname;
	}
	
	public Category get_category() {
		return _category;
	}

	public void set_category(Category _category) {
		this._category = _category;
	}
	
	public Set<User> get_subscribers() {
		return Collections.unmodifiableSet(_subscribers);
	}
	
	public void add_subscribers(User user) {
		_subscribers.add(user);
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{ [");
		buffer.append(_id);
		buffer.append("]; \"");
		if(_blogname != null) {
			buffer.append(_blogname + "\" ");
			buffer.append(", ");
		}
		if(_blogowner != null) {
			buffer.append("owned by " + _blogowner);
			buffer.append(", ");
		}
		if(_category != null) {
			buffer.append(/*"category: " +*/_category);
		} 
		if(_category == null) {
			buffer.append("none");
		} 
		buffer.append(" }");
		return buffer.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Blog))
            return false;
        if (obj == this)
            return true;

        Blog rhs = (Blog) obj;
        return new EqualsBuilder().
            append(_id, rhs.get_id()).
        /*    append(_blogowner, rhs.get_id()).
            append(_blogname, rhs.get_id()).*/
            isEquals();
	}
    
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(getClass().getName()).
				append(_id).
			/*	append(_blogowner).
				append(_blogname).*/
				toHashCode();
	}


}
