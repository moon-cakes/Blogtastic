package json;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import domain.BlogEntry;
import domain.Comment;

public class CommentJsonMarshaller implements MessageBodyWriter<Comment>  {

	 @Override
	 public long getSize(Comment comment, Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType) {
	 return -1;
	}

	 @Override
	 public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType) {
	 return clazz == Comment.class;
	}

	 @Override
	 public void writeTo(Comment comment, Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType,
	 MultivaluedMap<String, Object> valueMap, OutputStream stream) throws IOException, WebApplicationException {

	 JsonObject jsonObject = Json.createObjectBuilder()
	  .add("author", comment.get_author())
	  .add("comment", comment.get_comment()).build();

	 DataOutputStream outputStream = new DataOutputStream(stream);
	 outputStream.writeBytes(jsonObject.toString());
	}
}
