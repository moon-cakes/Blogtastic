package blog.test;

import static org.junit.Assert.*;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Blog;
import domain.User;
import services.BlogResource;
import services.DatabaseUtility;

public class BlogResourceTest {

	private static String WEB_SERVICE_URI = "http://localhost:10000/services/users";

	private Logger _logger = LoggerFactory.getLogger(BlogResourceTest.class);
	
	private static Client _client;
	
	@BeforeClass
	public static void setUpClient() {
		_client = ClientBuilder.newClient();
	}
	
	//@Before
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
	public static void destroyClient() throws ClassNotFoundException, SQLException {
		_client.close();
	}
	
	/**
	 * Add a new user (Amy) to the system, send a request a cookie
	 */
	@Test
	public void addUser() {
		
		User amy = new User("Lin", "Amy", "xlin504");
		Response response = _client
				.target(WEB_SERVICE_URI).request()
				.cookie("username", "xlin504")
				.post(Entity.xml(amy));
		if (response.getStatus() != 201) {
			fail("Failed to create new User");
		}
		
		String location = response.getLocation().toString();
		response.close();
		_logger.info("location: " + location);
		
		User amyFromService = _client.
				target(location).request().
				accept("application/xml").
				get(User.class);
		
		assertEquals(amy.get_firstname(), amyFromService.get_firstname());
		assertEquals(amy.get_lastname(), amyFromService.get_lastname());
	}
	
	/**
	 * Create a new blog for an already existing user
	 */
	@Test 
	public void createNewBlog(){
		 CookieManager manager = new CookieManager();
		 try {
	        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
	        CookieHandler.setDefault(manager);

	        // get content from URLConnection;
	        // cookies are set by web site
	        URL url = new URL("http://localhost:10000");
	        URLConnection connection = url.openConnection();
	        connection.getContent();

	        // get cookies from underlying
	        // CookieStore
	        CookieStore cookieJar =  manager.getCookieStore();
	        List <HttpCookie> cookies =
	            cookieJar.getCookies();
	        for (HttpCookie cookie: cookies) {
	          _logger.info("CookieHandler retrieved cookie: " + cookie);
	        }
	    } catch(Exception e) {
	    	 _logger.info("Unable to get cookie using CookieHandler");
	        e.printStackTrace();
	    }
		
/*		User user = _client.
				target(location).request().
				accept("application/xml").
				get(User.class);
		Blog blog = new Blog("Amy's Life", user);*/
		
	}
	
}
