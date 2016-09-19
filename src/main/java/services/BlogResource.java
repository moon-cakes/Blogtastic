package services;
    
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.hibernate.ObjectNotFoundException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Blog;
import domain.BlogEntry;
import domain.Category;
import domain.Comment;
import domain.User;

/**
 * 
 * Web service resource implementation for blogging specific functionality. An
 * instance of this class handles all HTTP requests related to user creation,
 * blog creation, and blog entries creation.
 * 
 * @author Xiaohui
 *
 */

@Path("/users")
public class BlogResource {

	private static final Logger _logger = LoggerFactory.getLogger(BlogResource.class);
	/*
	 * public static EntityManagerFactory _factory =
	 * Persistence.createEntityManagerFactory("blogPU");; public static
	 * EntityManager _entityManager = _factory.createEntityManager();
	 */

	private EntityManager _entityManager = PersistenceManager.instance().createEntityManager();
	protected Map<Long, AsyncResponse> _responses = new HashMap<Long, AsyncResponse>();

	public BlogResource() {
		reloadDatabase();
	}

	@PUT
	public void reloadData() {
		reloadDatabase();
	}

	/**
	 * Initiatise some data
	 */
	protected void reloadDatabase() {

		User user = new User("Boo", "Betty", "bboo123");
		User user2 = new User("Cowell", "Simon", "simoncowell");
		User user3 = new User("Lau", "Kelvin", "hotazn_klevin_potato");
		User user4 = new User("Alesund", "Christopher", "get_right");
		User user5 = new User("Jarzabkowski", "Jaroslaw", "pasha_biceps");

		Category category = new Category("Music");
		Category category2 = new Category("Lifestyle");
		Category category3 = new Category("Gaming");

		Blog blog = new Blog("Betty's Music Posts", user, category);
		Blog blog2 = new Blog("Kelvin's Gaming Blog", user3, category3);
		Blog blog3 = new Blog("GET_RIGHT's Game Guide to CS:GO", user4, category3);
		Blog blog4 = new Blog("PASHA_BICEPS's Game Guide to CS:GO", user5, category3);

		BlogEntry entry = new BlogEntry(new DateTime(), "Inspiration Behind My Latest Song",
				"It was my mom...", blog);
		Comment comment = new Comment( new DateTime(), "Anonymous", "Cool");
		entry.addComment(comment);

		BlogEntry entry2 = new BlogEntry(new DateTime(), "CS:GO smokes",
				"All videos come with a timecode for all grenade throws. "
						+ "See end of guide for basic grenade throw types and settings used.",
				blog3);
		BlogEntry entry3 = new BlogEntry(new DateTime(), "DE_DUST II Strats", "Rush B", blog4);

		_entityManager.getTransaction().begin();
		_entityManager.persist(user);
		_entityManager.persist(user2);
		_entityManager.persist(user3);
		_entityManager.persist(user4);
		_entityManager.persist(user5);
		_entityManager.persist(blog);
		_entityManager.persist(blog2);
		_entityManager.persist(blog3);
		_entityManager.persist(blog4);
		_entityManager.persist(entry);
		_entityManager.persist(entry2);
		_entityManager.persist(entry3);
		_entityManager.persist(category);
		_entityManager.persist(category2);
		_entityManager.persist(category3);
		_entityManager.getTransaction().commit();

	}

	/*
	 * The methods below handles cookies. Reference:
	 * http://memorynotfound.com/jaxrs-cookieparam-crud-example/
	 */

	/**
	 * Get a cookie
	 * 
	 * @param cookie
	 * @return
	 */
	@GET
	@Path("/login")
	@Produces("application/xml")
	public Response login(@CookieParam("username") String username) {

		// look up user in db and see if cookie matches
		_entityManager.getTransaction().begin();
		_logger.info("Trying to find user with username " + username);
		try {
			User user = _entityManager
					.createQuery("SELECT user FROM User user WHERE user._username = :username",
							User.class)
					.setParameter("username", username).getSingleResult();
			_logger.info("Found user " + user);
			_entityManager.getTransaction().commit();

			return Response.ok().build();

		} catch (ObjectNotFoundException e) {

			_logger.info("User doesn't exist in the database");
			return null;

		}

	}

	/**
	 * Create a new cookie
	 * 
	 * @return
	 */
	@POST
	public Response createCookie() {

		return Response.ok().cookie(new NewCookie("username", "value")).build();

	}

	/*
	 * * // Update a cookkie
	 * 
	 * @PUT public Response updateCookie(@CookieParam("name") Cookie cookie){ if
	 * (cookie != null){ return Response .ok() .cookie(new NewCookie("name",
	 * "new-value")) .build(); } return Response.ok().build(); }
	 * 
	 * // Delete a cookie
	 * 
	 * @DELETE public Response deleteCookie(@CookieParam("name") Cookie cookie){
	 * if (cookie != null){ NewCookie newCookie = new NewCookie(cookie,
	 * "delete cookie", 0, false); return Response .ok() .cookie(newCookie)
	 * .build(); } return Response.ok().build(); }
	 */

	/**
	 * Subscribe to a blog
	 * 
	 * @param blogId
	 * @param response
	 */
	@GET
	public synchronized void subscribeToBlog(Long blogId, @Suspended AsyncResponse response) {

		// Add the AsyncResponse to the collection associated with the Blog ID.
		_responses.put(blogId, response);

	}
	
	  /**
	   * Notify subscribers every time a new blog entry is posted for a blog
	   * @POST
	   * @param blogId
	   * @param blogEntryId
	   */
	@Consumes("application/xml")
	public synchronized void postBlogEntry(Long blogId, BlogEntry blogEntryId) {

		// Get the AsyncResponses for clients that have subscribed to the blog
		// identified by blogId.
		List<AsyncResponse> asyncResponses = _responses.remove("blogId");

		for (AsyncResponse clientHandle : asyncResponses) {
			clientHandle.resume(blogEntryId);
		}
	}

	/**
	 * Add a new user to the system
	 * 
	 * @param user
	 *            a user who has signed up
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@POST
	@Consumes("application/xml")
	public Response createUser(User user) throws ClassNotFoundException, SQLException {

		_entityManager.getTransaction().begin();
		_logger.info("Read user: " + user);
		// persist user to db
		_entityManager.persist(user);
		_logger.info("Created user: " + user);
		_entityManager.getTransaction().commit();

		// Return a Response that specifies a status code of 201 Created along
		// with the Location header set to URI of the newly created User.
		return Response.created(URI.create("/users/" + user.get_id())).build();
	}

	/**
	 * Add a new blog for a user
	 * 
	 * @param id
	 *            the id of a user
	 * @param blog
	 *            the blog of a user
	 */
	@POST
	@Path("{user-id}/blog")
	@Consumes("application/xml")
	public Response createBlogForUser(@CookieParam("username") long cookieUserId,
			@PathParam("user-id") long id, Blog blog) {

		_entityManager.getTransaction().begin();
		_logger.info("Read blog: " + blog);
		_entityManager.persist(blog);
		_logger.info("Created blog: " + blog);
		_entityManager.getTransaction().commit();

		return Response.created(URI.create("/users/" + id + "/blog/" + blog.get_id())).build();

	}

	/**
	 * Add a new blog entry for a blog
	 * 
	 * @param user_id
	 * @param blog_id
	 * @param entry
	 * @return
	 */
	@POST
	@Path("{user-id}/blog/{blog-id}/entry")
	@Consumes("application/xml")
	public Response createBlogEntryForBlog(@PathParam("user-id") long user_id,
			@PathParam("blog-id") long blog_id, BlogEntry entry) {

		_entityManager.getTransaction().begin();
		_logger.info("Read blog entry: " + entry);
		_entityManager.persist(entry);
		_logger.info("Created blog entry: " + entry);
		_entityManager.getTransaction().commit();

		return Response
				.created(URI.create(
						"/users/" + user_id + "/blog/" + blog_id + "/entry/" + entry.get_id()))
				.build();

	}

	/**
	 * Add a new comment to a blog entry
	 * 
	 * @param user_id
	 * @param blog_id
	 * @param entry_id
	 * @param comment
	 * @return
	 */
	@POST
	@Path("{user-id}/blog/{blog-id}/entry/{entry-id}/comments")
	@Consumes("application/xml")
	public Response createComment(@PathParam("user-id") long user_id,
			@PathParam("blog-id") long blog_id, @PathParam("entry-id") long entry_id,
			Comment comment) {

		_logger.info("Read comment " + comment);
		_entityManager.getTransaction().begin();
		BlogEntry entry = _entityManager.find(BlogEntry.class, entry_id);
		entry.addComment(comment);
		_entityManager.persist(entry);
		_logger.info("Created comment: " + comment);
		_entityManager.getTransaction().commit();

		return Response.created(URI.create(
				"/users/" + user_id + "/blog/" + blog_id + "/entry/" + entry_id + "/comments"))
				.build();

	}

	/**
	 * Make a user follow a blog
	 * 
	 * @param user_id
	 * @param blog
	 * @return
	 */
	@POST
	@Path("{user-id}/blog/{blog-id}/followers")
	@Consumes("application/xml")
	public Response addFollowing(@PathParam("user-id") Long user_id,
			@PathParam("blog-id") Long blog_id) {

		_entityManager.getTransaction().begin();

		// find user, find blog
		Blog blog = _entityManager.find(Blog.class, blog_id);
		User user = _entityManager.find(User.class, user_id);
		_logger.info("Read " + user + "is trying to follow blog " + blog);
		blog.add_subscribers(user);
		user.add_to_following(blog);
		_entityManager.persist(user);
		_entityManager.getTransaction().commit();
		return Response.created(URI.create("/users/" + user + "/blog/" + blog_id)).build();
	}

	/**
	 * Update a user's details
	 * 
	 * @param user
	 */
	@PUT
	@Path("{user-id}")
	@Consumes("application/xml")
	public void updateUser(User user) {

		_entityManager.getTransaction().begin();
		User user_to_update = _entityManager.find(User.class, user.get_id());
		_entityManager.getTransaction().commit();
		user_to_update.set_firstname(user.get_firstname());
		user_to_update.set_lastname(user.get_lastname());

	}

	/**
	 * Retrieve a user
	 * 
	 * @param user_id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@GET
	@Path("{user-id}")
	@Produces("application/xml")
	public User getUser(@PathParam("user-id") long user_id)
			throws ClassNotFoundException, SQLException {

		_entityManager.getTransaction().begin();
		_logger.info("Trying to find user with user id: " + String.valueOf(user_id));
		User user = _entityManager.find(User.class, user_id);
		_logger.info("Found user " + user);
		_entityManager.getTransaction().commit();

		return user;
	}

	/**
	 * Retrieve a specific blog for a user
	 * 
	 * @param user_id
	 * @param blog_id
	 * @return
	 */
	@GET
	@Path("{user-id}/blog/{blog-id}")
	@Produces("application/xml")
	public Blog getUsersBlog(@PathParam("user-id") long user_id,
			@PathParam("blog-id") long blog_id) {

		_entityManager.getTransaction().begin();
		_logger.info("Trying to find blog with id: " + String.valueOf(blog_id));
		Blog blog = _entityManager.find(Blog.class, blog_id);
		_logger.info("Found blog: " + blog);
		_entityManager.getTransaction().commit();

		return blog;
	}

	/**
	 * Retrieve a list of blogs owned by a user
	 * 
	 * @param user_id
	 * @return
	 */
	@GET
	@Path("{user-id}/blog")
	@Produces("application/xml")
	public Response getUsersBlogs(@PathParam("user-id") long user_id) {

		_entityManager.getTransaction().begin();
		_logger.info(
				"Trying to find blogs owned by user with user id: " + String.valueOf(user_id));
		List<Blog> usersBlogsResults = _entityManager
				.createQuery("SELECT blogs FROM Blog blogs WHERE USER_ID = :user_id",
						Blog.class)
				.setParameter("user_id", user_id).getResultList();
		for (Blog blog : usersBlogsResults) {
			_logger.info("Found blogs: " + blog);
		}
		_entityManager.getTransaction().commit();
		Set<Blog> usersBlogs = new HashSet<Blog>(usersBlogsResults);
		GenericEntity<Set<Blog>> usersBlogsGE = new GenericEntity<Set<Blog>>(usersBlogs) {
		};
		return Response.ok(usersBlogsGE).build();
	}

	/**
	 * Retrieve a specific blog entry
	 * 
	 * @param user_id
	 * @param blog_id
	 * @param entry_id
	 * @return
	 */
	@GET
	@Path("{user-id}/blog/{blog-id}/entry/{entry-id}")
	@Produces("application/xml")
	public BlogEntry getBlogEntry(@PathParam("user-id") long user_id,
			@PathParam("blog-id") long blog_id, @PathParam("entry-id") long entry_id) {

		_entityManager.getTransaction().begin();
		_logger.info("Trying to find blog entry with id: " + String.valueOf(blog_id));
		BlogEntry entry = _entityManager.find(BlogEntry.class, entry_id);
		_logger.info("Found blog entry: " + entry);
		_entityManager.getTransaction().commit();

		return entry;
	}

	/**
	 * Retrieve comments for a blog entry
	 * 
	 * @param user_id
	 * @param blog_id
	 * @param entry_id
	 * @return
	 */
	@GET
	@Path("{user-id}/blog/{blog-id}/entry/{entry-id}/comments")
	@Produces("application/xml")
	public Set<Comment> getCommentsForEntry(@PathParam("user-id") long user_id,
			@PathParam("blog-id") long blog_id, @PathParam("entry-id") long entry_id) {

		_entityManager.getTransaction().begin();
		_logger.info(
				"Trying to find comments for blog entry with id: " + String.valueOf(entry_id));

		BlogEntry entry = _entityManager.find(BlogEntry.class, entry_id);
		for (Comment c : entry.get_comments()) {
			_logger.info("Found comments: " + c);
		}
		_entityManager.getTransaction().commit();
		return entry.get_comments();

	}

	/**
	 * Retrieve a specified number of blogs for a certain category
	 * 
	 * @param category_id
	 * @param start
	 * @param size
	 * @return
	 */
	@GET
	@Path("/blog")
	@Produces("application/xml")
	public Response filterBlogsCategory(@QueryParam("category") long category_id,
			@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("1") @QueryParam("size") int size, @Context UriInfo uriInfo) {

		URI uri = uriInfo.getAbsolutePath();

		Link previous = null;
		Link next = null;

		_entityManager.getTransaction().begin();
		_logger.info("Trying to find blogs with category id: " + category_id);

		Long dbSize = _entityManager.createQuery(
				"select count(*) from Blog b RIGHT OUTER JOIN b._category c WHERE c._id = :category_id",
				Long.class).setParameter("category_id", category_id).getSingleResult();

		if (start > 0) {
			// There are previous blogs - create a previous link.
			previous = Link.fromUri(uri + "?category={category}&start={start}&size={size}")
					.rel("previous").build(category_id, start - 1, size);
			_logger.info("Previous link" + previous.toString());
		}

		if (start + size <= dbSize) {
			// There are successive blogs - create a next link.
			_logger.info("Making next link");
			next = Link.fromUri(uri + "?category={category}&start={start}&size={size}")
					.rel("next").build(category_id, start + 1, size);
			_logger.info("Next link" + next.toString());
		}

		List<Blog> categoryBlogsResults = _entityManager
				.createQuery(
						"SELECT b FROM Blog b RIGHT OUTER JOIN b._category c WHERE c._id = :category_id",
						Blog.class)
				.setParameter("category_id", category_id).setFirstResult(start)
				.setMaxResults(size).getResultList();

		_logger.info("Found blogs with category id: " + category_id);
		for (Blog blog : categoryBlogsResults) {
			_logger.info(blog.toString());
		}
		_entityManager.getTransaction().commit();
		Set<Blog> categoryBlogs = new HashSet<Blog>(categoryBlogsResults);
		GenericEntity<Set<Blog>> categoryBlogsGE = new GenericEntity<Set<Blog>>(
				categoryBlogs) {
		};

		// Build a response that contains a list of Blogs plus the link headers
		ResponseBuilder builder = Response.ok(categoryBlogsGE);
		if (previous != null) {
			builder.links(previous);
		}
		if (next != null) {
			builder.links(next);
		}
		Response response = builder.build();
		return response;

	}

	/**
	 * Delete a user
	 * 
	 * @param id
	 *            the user to delete's id
	 */
	@DELETE
	@Path("{id}")
	@Consumes("application/xml")
	public void deleteUser(@PathParam("id") Long id) {

		_logger.info("Trying to remove user with id: " + id);
		User user = _entityManager.find(User.class, id);
		_entityManager.getTransaction().begin();
		_entityManager.remove(user);
		_logger.info("Deleted user with id: " + id);
		_entityManager.getTransaction().commit();
	}

}
