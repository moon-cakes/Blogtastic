package json;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import domain.Category;

@Provider
@Produces("application/json")
public class CategoryJsonMarshaller implements MessageBodyWriter<Category> {

	 @Override
	 public long getSize(Category cat, Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType) {
	 return -1;
	}

	 @Override
	 public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType) {
	 return clazz == Category.class;
	}

	 @Override
	 public void writeTo(Category cat, Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType,
	 MultivaluedMap<String, Object> valueMap, OutputStream stream) throws IOException, WebApplicationException {

	 JsonObject jsonObject = Json.createObjectBuilder()
	  .add("name", cat.get_name()).build();

	 DataOutputStream outputStream = new DataOutputStream(stream);
	 outputStream.writeBytes(jsonObject.toString());
	}
}