package json;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import domain.BlogEntry;

public class BlogEntryJsonMarshaller implements MessageBodyWriter<BlogEntry> {

	 @Override
	 public long getSize(BlogEntry entry, Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType) {
	 return -1;
	}

	 @Override
	 public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType) {
	 return clazz == BlogEntry.class;
	}

	 @Override
	 public void writeTo(BlogEntry entry, Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType,
	 MultivaluedMap<String, Object> valueMap, OutputStream stream) throws IOException, WebApplicationException {

	 JsonObject jsonObject = Json.createObjectBuilder()
	  .add("title", entry.get_posttitle())
	  .add("category", (JsonValue) entry.get_comments()).build();

	 DataOutputStream outputStream = new DataOutputStream(stream);
	 outputStream.writeBytes(jsonObject.toString());
	}
}
