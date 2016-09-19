package domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Embeddable class to represent comments. Comment instances form part of the state
 * of blog entries.
 * @author Xiaohui
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="comment")
public class Comment {

	@Column(name="AUTHOR", nullable=false)
    @XmlElement(name="author")
    private String _author;
    
    @Column(name="COMMENT", nullable=false)
    @XmlElement(name="comment-content")
    private String _comment;
    
	@XmlElement(name="time")
	//@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	//@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime _timestamp;
	
    protected Comment(){
    }
    
    public Comment(DateTime time, String author, String comment) {
    	_author = author;
    	_comment = comment;
    	_timestamp = time;
    }

	public String get_author() {
		return _author;
	}

	public String get_comment() {
		return _comment;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(_author + " said: ");
		buffer.append(_comment);
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Comment))
            return false;
        if (obj == this)
            return true;

        Comment rhs = (Comment) obj;
        return new EqualsBuilder().
            append(_author, rhs._author).
            append(_comment, rhs._comment).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_author).
	            append(_comment).
	            toHashCode();
	}
	
}
