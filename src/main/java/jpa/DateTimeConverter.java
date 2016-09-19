package jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Use a custom converter to store DateTime objects into the database
 * @author Xiaohui
 *
 */
@Converter(autoApply = true)
public class DateTimeConverter implements AttributeConverter<DateTime, String>{
	
	@Override
	public String convertToDatabaseColumn(DateTime dateTime) {
		return dateTime.toString(DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));
	}

	@Override
	public DateTime convertToEntityAttribute(String stringDateTime) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
		return dtf.parseDateTime(stringDateTime);
	}
	
	
}
