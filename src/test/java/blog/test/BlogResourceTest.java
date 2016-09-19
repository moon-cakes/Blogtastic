package blog.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
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
import domain.Comment;
import domain.User;

public class BlogResourceTest {

	private static String WEB_SERVICE_URI = "http://localhost:10000/services";
	private Logger _logger = LoggerFactory.getLogger(BlogResourceTest.class);
	// private EntityManager _entityManager =
	// PersistenceManager.instance().createEntityManager();
	private static Client _client;

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
	public static void destroyClient() throws ClassNotFoundException, SQLException {
		_client.close();
	}

	/**
	 * Create some categories
	 */

	@Test
	public void addCategoy() {

		Category category = new Category("Beauty");
		Response response = _client.target(WEB_SERVICE_URI + "/categories").request()
				.post(Entity.xml(category));
		if (response.getStatus() != 201) {
			fail("Failed to create new category");
		}

		String location = response.getLocation().toString();
		response.close();
		_logger.info("Location for newly created category: " + location);

		Category categoryFromService = _client.target(location).request()
				.accept("application/xml").get(Category.class);

		assertEquals(Long.valueOf(4), categoryFromService.get_id());

	}
	
	/**
	 * Add a new user (Amy) to the system, send a request a cookie
	 */
	@Test
	public void addUser() {

		User user = new User("Lin", "Amy", "xlin504");
		Response response = _client.target(WEB_SERVICE_URI + "/users").request()
				.post(Entity.xml(user));
		if (response.getStatus() != 201) {
			fail("Failed to create new User");
		}

		String location = response.getLocation().toString();
		response.close();
		_logger.info("location for newly created user: " + location);

		User userFromService = _client.target(location).request().accept("application/xml")
				.get(User.class);

		assertEquals(Long.valueOf(6), userFromService.get_id());
	}
	
	/**
	 * Update user Simon Cowell
	 */
	@Test
	public void updateUserDetails() {
		
		User simon = _client.target(WEB_SERVICE_URI + "/users/2").request().accept("application/xml")
				.get(User.class);
		
		simon.set_lastname("Phillip Cowell");
		
		Response response = _client.target(WEB_SERVICE_URI + "/users/2").request()
				.put(Entity.xml(simon));
		if (response.getStatus() != 204)
			fail("Failed to update CriminalProfile");
		response.close();
		
		User simonUpdated = _client.target(WEB_SERVICE_URI + "/users/2").request()
				.accept("application/xml").get(User.class);
		
		assertEquals(simon, simonUpdated);
	}

	/**
	 * Create a new blog for an already existing user, Betty Boo who already
	 * owns a blog. Also check that a user can own more than one blog, i.e.
	 * Betty should have two blogs
	 */
	@Test
	public void createNewBlog() {

		User user = _client.target(WEB_SERVICE_URI + "/users/1").request()
				.accept("application/xml").get(User.class);

		Category cat = _client.target(WEB_SERVICE_URI + "/categories/2").request()
				.accept("application/xml").get(Category.class);

		Blog blog = new Blog("Betty's Personal Life", user, cat);

		Response response = _client.target(WEB_SERVICE_URI + "/users/1/blog").request()
				.post(Entity.xml(blog));

		if (response.getStatus() != 201) {
			fail("Failed to create new blog for user: " + user);
		}

		String location = response.getLocation().toString();
		response.close();
		_logger.info("Location for newly created blog: " + location);

		Blog blogFromService = _client.target(location).request().accept("application/xml")
				.get(Blog.class);

		assertEquals(Long.valueOf(5), blogFromService.get_id());

		Set<Blog> blogsFromService = _client.target(WEB_SERVICE_URI + "/users/1/blog")
				.request().accept("application/xml").get(new GenericType<Set<Blog>>() {
				});

		assertEquals(Long.valueOf(2), Long.valueOf(blogsFromService.size()));

	}

	/**
	 * Create a blog entry for "Kelvin's Gaming" blog
	 */
	@Test
	public void createBlogEntry() {

		String content = "B/c it just is";

		Blog blog = _client.target(WEB_SERVICE_URI + "/users/3/blog/2").request()
				.accept("application/xml").get(Blog.class);

		BlogEntry entry = new BlogEntry(new DateTime(), "Why Battlefield is Fun",
				content, blog);

		Response response = _client.target(WEB_SERVICE_URI + "/users/3/blog/2/entry").request()
				.post(Entity.xml(entry));

		if (response.getStatus() != 201) {
			fail("Failed to create new blog entry: " + entry);
		}

		String location = response.getLocation().toString();
		response.close();
		_logger.info("Location for newly created blog entry: " + location);

		BlogEntry blogEntryFromService = _client.target(location).request()
				.accept("application/xml").get(BlogEntry.class);

		assertEquals(Long.valueOf(4), blogEntryFromService.get_id());

	}

	/**
	 * Add a comment to blog entry Betty Boo's music blog for entry "Inspiration
	 * Behind My Latest Song"
	 */
	@Test
	public void postComment() {

		Comment comment = new Comment(new DateTime(), "Simon Cowell",
				"Your mother also makes the best cookies!");

		Response response = _client
				.target(WEB_SERVICE_URI + "/users/1/blog/1/entry/1/comments").request()
				.post(Entity.xml(comment));

		String location = response.getLocation().toString();
		response.close();
		_logger.info("Location for newly created comment: " + location);

		Set<Comment> commentFromService = _client
				.target(WEB_SERVICE_URI + "/users/1/blog/1/entry/1/comments").request()
				.accept("application/xml").get(new GenericType<Set<Comment>>() {
				});

		// There should now be two comments
		assertEquals(Long.valueOf(2), Long.valueOf(commentFromService.size()));

	}

	/**
	 * Test that the Web service processes requests for blogs in the "gaming"
	 * category using header links for HATEOAS
	 */
	@Test
	public void getBlogsForCategoryUsingHATEOAS() {

		Response response = _client.target(WEB_SERVICE_URI + "/users/blog?category=3")
				.request().get();

		// Extract links and entity data from the response.
		Link previous = response.getLink("previous");
		Link next = response.getLink("next");
		List<Blog> blogs = response.readEntity(new GenericType<List<Blog>>() {
		});
		response.close();

		// Get only the first blog under the gaming category (by default), the
		// web service should return with a Next link, but not a previous link.
		// The first blog in the blogs table with the gaming category has an id of #2
		assertEquals(Long.valueOf(1), Long.valueOf(blogs.size()));
		assertEquals(Long.valueOf(2), Long.valueOf(blogs.get(0).get_id()));
		assertNull(previous);
		assertNotNull(next);

		// Invoke next link and extract response data
		response = _client.target(next).request().get();
		previous = response.getLink("previous");
		next = response.getLink("next");
		blogs = response.readEntity(new GenericType<List<Blog>>() {
		});
		response.close();

		_logger.info("Previous to string: ");
		_logger.info(previous.toString());
		_logger.info("Next to string: ");
		_logger.info(next.toString());
		// The second blog with category id of 3 should be returned along with Previous and 
		// next links to the adjacent blogs.
		assertEquals(Long.valueOf(1), Long.valueOf(blogs.size()));
		assertEquals(Long.valueOf(3), Long.valueOf(blogs.get(0).get_id()));
		assertEquals("<" + WEB_SERVICE_URI + "/users/blog?category=3&start=0&size=1>; rel=\"previous\"",
				previous.toString());
		assertNotNull("<" + WEB_SERVICE_URI + "/users/blog?category=3&start=0&size=1>; rel=\"previous\"",
				next.toString());
	}

	/**
	 * Make user Simon Cowell follow Betty Boo's music blog
	 */
	// @Test
	public void followBlog() {

		User user = _client.target(WEB_SERVICE_URI + "/users/2").request()
				.accept("application/xml").get(User.class);

		Response response = _client.target(WEB_SERVICE_URI + "/users/1/blog/1").request()
				.post(Entity.xml(user));

	}

	/**
	 * Test cookies, i.e. Betty Boo is logging into the website, if successful
	 * store a cookie of her username
	 */
	// @Test
	public void testCookies() {

		Response response = _client.target(WEB_SERVICE_URI + "/users/login").request()
				.cookie("username", "bboo123").accept("application/xml").get();

		if (response.getStatus() == 200) {
			System.out.println("Response: " + response.readEntity(String.class));
		}

	}

	/**
	 * Delete user Betty Boo
	 */
	// @Test
	public void deleteUser() {

		Response response = _client.target(WEB_SERVICE_URI + "/users/1").request().delete();
		int status = response.getStatus();
		if (status != 404) {
			_logger.error("Failed to delete User; Web service responsed with: " + status);
			fail();
		}

		// check to see if Betty Boo is deleted
		response = _client.target(WEB_SERVICE_URI + "/users/1").request().get();
		status = response.getStatus();
		if (status != 404) {
			_logger.error("Expecting a status code of 404 for querying a non-existent User; "
					+ "Web service responded with: " + status);
			fail();
		}

	}

}
