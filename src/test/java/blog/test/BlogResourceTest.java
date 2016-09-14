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
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Blog;
import domain.Category;
import domain.User;
import services.BlogResource;
import services.DatabaseUtility;

public class BlogResourceTest {

	private static String WEB_SERVICE_URI = "http://localhost:10000/services";

	private Logger _logger = LoggerFactory.getLogger(BlogResourceTest.class);
	
	private static Client _client;
	
	private static User user;
	
	private static Blog blog;
	
	private static Category category;
	
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
		
		user = new User("Lin", "Amy", "xlin504");
		Response response = _client
				.target(WEB_SERVICE_URI + "/users").request()
				.cookie("username", "xlin504")
				.post(Entity.xml(user));
		if (response.getStatus() != 201) {
			fail("Failed to create new User");
		}
		
		String location = response.getLocation().toString();
		response.close();
		_logger.info("location for newly created user: " + location);
		
		User userFromService = _client.
				target(location).request().
				accept("application/xml").
				get(User.class);
		
		user.set_id(userFromService.get_id());
		
		assertEquals(user.get_firstname(), userFromService.get_firstname());
		assertEquals(user.get_lastname(), userFromService.get_lastname());
	}
	
	/**
	 * Create some categories
	 */
	
	//@Test
	public void addCategoy(){
		
		category = new Category("Lifestyle");
		Response response = _client
				.target(WEB_SERVICE_URI + "/categories").request()
				.post(Entity.xml(category));
		if (response.getStatus() != 201) {
			fail("Failed to create new category");
		}
		
		String location = response.getLocation().toString();
		response.close();
		_logger.info("location for newly created category: " + location);
		
		Category categoryFromService = _client.
				target(location).request().
				accept("application/xml").
				get(Category.class);
		
		category.set_id(categoryFromService.get_id());
		
		assertEquals(category.get_name(), categoryFromService.get_name());
		assertEquals(category.get_name(), categoryFromService.get_name());
	}
	
	/**
	 * Create a new blog for an already existing user
	 */
	@Test 
	public void createNewBlog(){
		blog = new Blog("Amy's Life", user);
		
		Response response = _client
				.target(WEB_SERVICE_URI + "/users/" + user.get_id() + "/blog").request()
				.post(Entity.xml(blog));
		if (response.getStatus() != 201) {
			fail("Failed to create new blog for user: " + user);
		}
		
		String location = response.getLocation().toString();
		response.close();
		_logger.info("location for newly created blog: " + location + " for user: " +user);
		
		Blog blogFromService = _client.
				target(location).request().
				accept("application/xml").
				get(Blog.class);
		
		blogFromService.set_id(blogFromService.get_id());
		
		assertEquals(blog.get_blogname(), blogFromService.get_blogname());
		assertEquals(blog.get_blogname(), blogFromService.get_blogname());
	}
	
	/**
	 * Create another blog for the same existing user
	 */
	@Test 
	public void createSecondaryBlog(){
		
		blog = new Blog("Amy's Makeup Tutorials", user);
		Response response = _client
				.target(WEB_SERVICE_URI  + "/users/" + user.get_id() + "/blog").request()
				.post(Entity.xml(blog));
		if (response.getStatus() != 201) {
			fail("Failed to create new blog for user: " + user);
		}
		
		String location = response.getLocation().toString();
		response.close();
		_logger.info("location for newly created blog: " + location + " for user: " + user);
		
		/*Response responseFromRetrievingBlogs = _client.
				target(WEB_SERVICE_URI  + "/users/" + user.get_id() + "/blog" ).request().
				accept("application/xml").
				get();
		
		Set<Blog> blogsFromService = (Set<Blog>) responseFromRetrievingBlogs.getEntity();*/
		Set<Blog> blogsFromService = _client.
				target(WEB_SERVICE_URI  + "/users/" + user.get_id() + "/blog" ).request().
				accept("application/xml").
				get(new GenericType<Set<Blog>>(){});
		
		_logger.info("blogs from client: " + user.get_blogs());
		_logger.info("blogs from service: " + blogsFromService);
		assertEquals(user.get_blogs(), blogsFromService);
	
		
	}
	
}
