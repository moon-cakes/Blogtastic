package domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Entity class to represent a blog entry. A blog entry has a title, a timestamp,
 * and may or may not have any comments.
 * @author Xiaohui
 *
 */
@Entity
@Table(name = "BLOGENTRIES")
@XmlRootElement(name="blog-entry")
@XmlAccessorType(XmlAccessType.FIELD)
public class BlogEntry /*implements Comparable<BlogEntry>*/{

    @Id
    //@GeneratedValue(generator="ID_GENERATOR")
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @XmlAttribute(name="id")
    private Long _id;

    @Column(length=277) 
	@XmlElement(name="time")
	private DateTime _timestamp;
	
	// Define a many-to-one association with blog - many blog entries can be associated 
	// with a single blog. When a blog entry is loaded, its associated blog (the blog 
	// the posts to) will be loaded on demand (lazily).
	@ManyToOne(fetch=FetchType.LAZY)
	// Make the association mandatory - a BlogEntry MUST belong to a blog.
	@JoinColumn(name="BLOG_ID", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@XmlElement(name="owning-blog")
	protected Blog _blog;

	@Column(name="POSTTITLE", nullable=false)
    @XmlElement(name="post-title")
    private String _posttitle;
    
	// Set up the collection of comments for a blog entry as a Set.
	@ElementCollection
	@CollectionTable(name="COMMENT", joinColumns = @JoinColumn(name = "BLOGENTRY_ID"))
	@XmlElementWrapper(name="comments")
	@XmlElement(name="comment")
	private Set<Comment> _comments = new HashSet<Comment>();
	
	@Column(name="CONTENT", nullable=false)
	@XmlElement(name="content")
	private String _content;
	
	protected BlogEntry(){
	}
	
    public BlogEntry(DateTime timestamp, String posttitle, String content, 
    		Blog blog) {
       	_timestamp = timestamp;
    	_posttitle = posttitle;
    	_content = content;
       	_blog = blog;
    }
    
    public Long get_id() {
		return _id;
	}

    public void set_id(long id){
    	_id = id;
    }
    
    public Set<Comment> get_comments() {
		return _comments;
	}

	public void set_comments(Set<Comment> _comments) {
		this._comments = _comments;
	}

/*	public DateTime get_timestamp() {
		return _timestamp;
	}

	public void set_timestamp(DateTime _timestamp) {
		this._timestamp = _timestamp;
	}*/
	
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
		// Wrap the Set of Comment objects with a wrapper that provides read-only
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
            //append(_timestamp, rhs._timestamp).
            append(_blog, rhs.get_id()).
            append(_posttitle, rhs.get_id()).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(getClass().getName()).
				append(_id).
			   // append(_timestamp).
				append(_blog).
				append(_posttitle).
				toHashCode();
	}
	
	/*@Override
	public int compareTo(BlogEntry entry) {
		return _timestamp.compareTo(entry._timestamp);
	}*/
	
	@Override
	public String toString() {
		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("BlogEntry: { ");
		buffer.append("\"" + _posttitle);
		/*buffer.append("\" was posted at");
		buffer.append(timeFormatter.print(_timestamp));
		buffer.append(" on ");
		buffer.append(dateFormatter.print(_timestamp));*/
		buffer.append("\", Content: ");
		buffer.append(_content);
		buffer.append(" Comments: ");
		if (!_comments.isEmpty()){
		buffer.append(_comments);
		} else {
			buffer.append(" None");
		}
		buffer.append(" }");
		
		return buffer.toString();
	}
}
