package domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

/**
 * Embeddable class to represent comments. Comment instances form part of the state
 * of blog entries.
 * @author Xiaohui
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class Comment {
	
    @GeneratedValue(generator="ID_GENERATOR")
    @XmlAttribute(name="id")
    private Long _id;
    
    @Column(name="AUTHOR", nullable=false, length=30)
    @XmlElement(name="author")
    private String _author;
    
    @Column(name="COMMENT", nullable=false, length=30)
    @XmlElement(name="comment-content")
    private String _comment;
    
	@XmlElement(name="time")
	private DateTime _timestamp;
	
    protected Comment(){
    }
    
    public Comment(String author, String comment, DateTime time) {
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
