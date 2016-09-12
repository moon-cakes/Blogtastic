package domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
 * Category and BlogEntry are related using a many-to-many association. A blog entry 
 * can belong to many categories, and one category can contain many blog entries.
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
    
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "CATEGORY_ITEM", joinColumns = @JoinColumn(name = "CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "ITEM_ID"))
	@XmlElementWrapper(name="entries")
	@XmlElement(name="entry")
	private Set<BlogEntry> _blogentries = new HashSet<BlogEntry>();
	
	protected Category(){
	}
	
    public Category(String name) {
		_name = name;
	}

	private static Logger _logger = LoggerFactory.getLogger(Category.class);
	
	public void addBlogEntries(BlogEntry blogentry) {
		_logger.info("Attempting to add: " + blogentry.toString());
		_blogentries.add(blogentry);
	}
    
    public Long get_id() {
		return _id;
	}

	public String get_name() {
		return _name;
	}

	public Set<BlogEntry> get_blogentries() {
		return Collections.unmodifiableSet(_blogentries);
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
