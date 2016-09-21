package json.test;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.User;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Use the same tests from BlogResourceTest, except post/get using Json this time
 * @author Xiaohui
 *
 */
public class JsonMarshallerTest {

	private static String WEB_SERVICE_URI = "http://localhost:10000/services";
	private Logger _logger = LoggerFactory.getLogger(JsonMarshallerTest.class);
	private static Client _client;

	@BeforeClass
	public static void setUpClient() {
		_client = ClientBuilder.newClient();
	}

	@AfterClass
	public static void destroyClient() throws ClassNotFoundException, SQLException{
			_client.close();
	}

	@Test
	public void addUser() {

		User user = new User("Jones", "Bob", "BJon123");
		Response response = _client.target(WEB_SERVICE_URI + "/users").request()
				.post(Entity.json(user));
		if (response.getStatus() != 201) {
			fail("Failed to create new User");
		}

		String location = response.getLocation().toString();
		response.close();
		_logger.info("location for newly created user: " + location);

		User userFromService = _client.target(location).request().accept("application/json")
				.get(User.class);

		assertEquals(Long.valueOf(7), userFromService.get_id());
	}
}
