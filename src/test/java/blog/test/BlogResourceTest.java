package blog.test;

import static org.junit.Assert.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.User;

public class BlogResourceTest {

	private static String WEB_SERVICE_URI = "http://localhost:10000/services/users";

	private Logger _logger = LoggerFactory.getLogger(BlogResourceTest.class);
	
	private static Client _client;
	
	@BeforeClass
	public static void setUpClient() {
		_client = ClientBuilder.newClient();
	}
	
	@Before
	public void reloadServerData() {
		/*Response response = _client
				.target(WEB_SERVICE_URI).request()
				.put(null);
		response.close();*/

		// Pause briefly before running any tests. Test addParoleeMovement(),
		// for example, involves creating a timestamped value (a movement) and
		// having the Web service compare it with data just generated with 
		// timestamps. Joda's Datetime class has only millisecond precision, 
		// so pause so that test-generated timestamps are actually later than 
		// timestamped values held by the Web service.
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
	}
	
	@AfterClass
	public static void destroyClient() {
		_client.close();
	}
	
	@Test
	public void addUser() {
		
		User amy = new User("Lin", "Amy");
		Response response = _client
				.target(WEB_SERVICE_URI).request()
				.post(Entity.xml(amy));
		if (response.getStatus() != 201) {
			fail("Failed to create new User");
		}
		
		String location = response.getLocation().toString();
		response.close();
		
		User amyFromService = _client.target(location).request()
				.accept("application/xml").get(User.class);
		
		assertEquals(amy.get_firstname(), amyFromService.get_firstname());
		assertEquals(amy.get_lastname(), amyFromService.get_lastname());
	}
}
