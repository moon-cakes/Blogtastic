package blog.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Blog;
import domain.BlogEntry;
import domain.Category;
import domain.User;
import services.PersistenceManager;

public class BlogResourceTest {

	private static String WEB_SERVICE_URI = "http://localhost:10000/services";

	private Logger _logger = LoggerFactory.getLogger(BlogResourceTest.class);

	private static Client _client;

	private static User user;

	private static Blog blog1;

	private static Blog blog2;

	private static Category category;
	
	private static BlogEntry entry;

	@BeforeClass
	public static void setUpClient() {
		_client = ClientBuilder.newClient();
	}

	@Before
	public void reloadServerData() {
		/*
		 * Response response = _client .target(WEB_SERVICE_URI).request()
		 * .put(null); response.close();
		 */

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
	public static void destroyClient() throws ClassNotFoundException,
			SQLException {
		_client.close();
	}

	/**
	 * Add a new user (Amy) to the system, send a request a cookie
	 */
	@Test
	public void addUser() {

		user = new User("Lin", "Amy", "xlin504");
		Response response = _client.target(WEB_SERVICE_URI + "/users")
				.request().cookie("username", "xlin504").post(Entity.xml(user));
		if (response.getStatus() != 201) {
			fail("Failed to create new User");
		}

		String location = response.getLocation().toString();
		response.close();
		_logger.info("location for newly created user: " + location);

		User userFromService = _client.target(location).request()
				.accept("application/xml").get(User.class);
		
		user.set_id(1);
		
		assertEquals(user.get_id(), userFromService.get_id());
	}

	/**
	 * Create some categories
	 */

	@Test
	public void addCategoy() {

		category = new Category("Lifestyle");
		Response response = _client.target(WEB_SERVICE_URI + "/categories")
				.request().post(Entity.xml(category));
		if (response.getStatus() != 201) {
			fail("Failed to create new category");
		}

		String location = response.getLocation().toString();
		response.close();
		_logger.info("Location for newly created category: " + location);

		Category categoryFromService = _client.target(location).request()
				.accept("application/xml").get(Category.class);

		assertEquals(Long.valueOf(1), categoryFromService.get_id());

	}

	/**
	 * Create a new blog for an already existing user
	 */
	@Test
	public void createNewBlog() {

		Category categoryFromService = _client
				.target(WEB_SERVICE_URI + "/categories/1").request()
				.accept("application/xml").get(Category.class);

		blog1 = new Blog("Amy's Life", user, categoryFromService);

		Response response = _client
				.target(WEB_SERVICE_URI + "/users/" + user.get_id() + "/blog")
				.request().post(Entity.xml(blog1));
		if (response.getStatus() != 201) {
			fail("Failed to create new blog for user: " + user);
		}

		String location = response.getLocation().toString();
		response.close();
		_logger.info("Location for newly created blog: " + location);

		Blog blogFromService = _client.target(location).request()
				.accept("application/xml").get(Blog.class);

		assertEquals(Long.valueOf(1), blogFromService.get_id());

	}

	/**
	 * Create another blog for the same existing user, test association that a user
	 * can own two blogs
	 */
	@Test
	public void createSecondaryBlog() {

		blog2 = new Blog("Amy's Makeup Tutorials", user);

		Response response = _client
				.target(WEB_SERVICE_URI + "/users/" + user.get_id() + "/blog")
				.request().post(Entity.xml(blog2));
		if (response.getStatus() != 201) {
			fail("Failed to create new blog for user: " + user);
		}

		String location = response.getLocation().toString();
		response.close();
		_logger.info("location for newly created blog: " + location
				+ " for user: " + user);

		Set<Blog> blogsFromService = _client
				.target(WEB_SERVICE_URI + "/users/" + user.get_id() + "/blog")
				.request().accept("application/xml")
				.get(new GenericType<Set<Blog>>() {
				});

		blog1.set_id(1);
		blog2.set_id(2);

		Set<Blog> blogsFromClient = new HashSet<Blog>();
		blogsFromClient.add(blog1);
		blogsFromClient.add(blog2);

		_logger.info("blogs from client: " + blogsFromClient);
		_logger.info("blogs from service: " + blogsFromService);
		assertEquals(blogsFromClient, blogsFromService);
	}

	/**
	 * Create a blog entry for "Amy's Life" blog
	 */
	@Test
	public void createBlogEntry() {

		String content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
				+ "Mauris placerat nunc vel ultrices ultrices. Quisque euismod porta urna, "
				+ "quis ullamcorper nisl ullamcorper nec. ";
		
		entry = new BlogEntry(new DateTime(), "Dealing With Stress",
				content, blog1);

		_logger.info("Trying..." + WEB_SERVICE_URI + "/users/" + user.get_id() + "/blog/"
						+ 1 + "/entry");
		
		Response response = _client
				.target(WEB_SERVICE_URI + "/users/" + user.get_id() + "/blog/"
						+ 1 + "/entry").request()
				.post(Entity.xml(entry));
		if (response.getStatus() != 201) {
			fail("Failed to create new blog entry: " + entry);
		}
		
		String location = response.getLocation().toString();
		response.close();
		_logger.info("Location for newly created blog entry: " + location);

		BlogEntry blogEntryFromService = _client.target(location).request()
				.accept("application/xml").get(BlogEntry.class);
		
		entry.set_id(1);
		
		assertEquals(entry.get_id(), blogEntryFromService.get_id());

	}

}
