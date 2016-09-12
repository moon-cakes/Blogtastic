package domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Entity class to represent a blog entry.
 * @author Xiaohui
 * 
 * Blog entries and category are related using a many-to-many association.
 * A blog entry can belong to many categories and and a category can have 
 * many blog entries.
 *
 */
@Entity
@Table(name = "BLOGENTRIES")
@XmlRootElement(name="blog-entry")
@XmlAccessorType(XmlAccessType.FIELD)
public class BlogEntry implements Comparable<BlogEntry>{

    @Id
    @GeneratedValue(generator="ID_GENERATOR")
    @XmlAttribute(name="id")
    private Long _id;

	// Define a many-to-one association with blog - many blog entries can be associated 
	// with a single blog. When a blog entry is loaded, its associated blog (the blog 
	// the posts to) will be loaded on demand (lazily).
	@ManyToOne(fetch=FetchType.LAZY)
	// Make the association mandatory - a BlogEntry MUST belong to a blog.
	@JoinColumn(name="BLOG_ID", nullable=false)
	@XmlElement(name="owning-blog")
	protected Blog _blog;

	@Column(name="POSTTITLE", nullable=false, length=30)
    @XmlElement(name="post-title")
    private String _posttitle;
    
	// Set up the collection of comments for a blog entry as a Set.
	@ElementCollection
	@CollectionTable(name="COMMENT")
	@XmlElementWrapper(name="comments")
	@XmlElement(name="comment")
	private Set<Comment> _comments = new HashSet<Comment>();
	
	@ManyToMany(mappedBy= "_blogentries")
	@XmlElementWrapper(name="categories")
	@XmlElement(name="category")
	private Set<Category> _categories = new HashSet<Category>( );
	
	@XmlElement(name="time")
	private DateTime _timestamp;
	
	protected BlogEntry(){
	}
	
    public BlogEntry(DateTime timestamp, Blog blog, String posttitle) {
       	_timestamp = timestamp;
    	_blog = blog;
    	_posttitle = posttitle;

    }
    
    public Long get_id() {
		return _id;
	}

    public void set_id(Long id){
    	_id = id;
    }
    
    public Set<Comment> get_comments() {
		return _comments;
	}

	public void set_comments(Set<Comment> _comments) {
		this._comments = _comments;
	}

	public DateTime get_timestamp() {
		return _timestamp;
	}

	public void set_timestamp(DateTime _timestamp) {
		this._timestamp = _timestamp;
	}

	public Set<Category> get_categories() {
		return _categories;
	}
	
	public Blog get_blog() {
		return _blog;
	}
	
	public void add_blog(Blog blog){
		_blog = blog;
	}

	public void set_blog(Blog _blog) {
		this._blog = _blog;
	}

	public String get_posttitle() {
		return _posttitle;
	}
	
	
	public void set_posttitle(String _posttitle) {
		this._posttitle = _posttitle;
	}
	
	public Set<Comment> getComments() {
		// Wrap the Set of Image objects with a wrapper that provides read-only
		// access. Clients thus can't change the state of the returned Set.
		return Collections.unmodifiableSet(_comments);
	}
	
	public void addComment(Comment comment) {
		_comments.add(comment);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BlogEntry))
            return false;
        if (obj == this)
            return true;

        BlogEntry rhs = (BlogEntry) obj;
        return new EqualsBuilder().
            append(_id, rhs.get_id()).
            append(_timestamp, rhs._timestamp).
            append(_blog, rhs.get_id()).
            append(_posttitle, rhs.get_id()).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(getClass().getName()).
				append(_id).
			    append(_timestamp).
				append(_blog).
				append(_posttitle).
				toHashCode();
	}
	
	@Override
	public int compareTo(BlogEntry entry) {
		return _timestamp.compareTo(entry._timestamp);
	}
	
	@Override
	public String toString() {
		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
		
		
		StringBuffer buffer = new StringBuffer();
		return buffer.toString();
	}
}
