package domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entity class to define categories for blog entries. 
 * 
 * Category and Blogs are related using an optional many-to-one association.
 * A category can contain many blogs or nonne but a blog may or may not contain a category.
 *
 * @author Xiaohui
 */
@Entity
@Table(name = "CATEGORIES") 
@XmlAccessorType(XmlAccessType.FIELD)
public class Category {

    @Id 
    @GeneratedValue(generator="ID_GENERATOR")
    @XmlAttribute(name="id")
    private Long _id;

	@Column(name="NAME", nullable=false, length=30)
	@XmlElement(name="name")
	private String _name;
    
	@OneToMany(mappedBy = "_category", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@XmlElementWrapper(name="blogs")
	@XmlElement(name="blog")
	private Set<Blog> _blogs = new HashSet<Blog>();
	
	protected Category(){
	}
	
    public Category(String name) {
		_name = name;
	}

	private static Logger _logger = LoggerFactory.getLogger(Category.class);
	
	public void addBlog(Blog blog) {
		_logger.info("Attempting to add: " + blog.toString());
		_blogs.add(blog);
	}
    
    public Long get_id() {
		return _id;
	}

	public String get_name() {
		return _name;
	}

	public Set<Blog> get_blogs() {
		return Collections.unmodifiableSet(_blogs);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BlogEntry))
			return false;
		if (obj == this)
			return true;

		Category rhs = (Category) obj;
		return new EqualsBuilder().
				append(_id, rhs.get_id()).
				append(_name, rhs.get_name()).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(getClass().getName()).
				append(_id).
				append(_name).
				toHashCode();
	}
}
