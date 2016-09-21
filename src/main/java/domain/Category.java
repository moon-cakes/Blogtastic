package domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
@XmlRootElement(name="category")
@XmlAccessorType(XmlAccessType.FIELD)
public class Category {

    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @XmlAttribute(name="id")
    private Long _id;

	@Column(name="NAME", nullable=false, length=30)
	@XmlElement(name="name")
	private String _name;
	
	protected Category(){
	}
	
    public Category(String name) {
		_name = name;
	}

	private static Logger _logger = LoggerFactory.getLogger(Category.class);
    
    public Long get_id() {
		return _id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

	public String get_name() {
		return _name;
	}

	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Category: ");
		if(_name != null) {
			buffer.append(_name);
		}
		return buffer.toString();
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
