package services;

import java.net.URI;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Blog;
import domain.BlogEntry;
import domain.User;
/**
 * 
 * Web service resource implementation for the blog application. An instance
 * of this class handles all HTTP requests for the blogging Web service.
 * 
 * @author Xiaohui
 *
 */

@Path("/users") 
public class BlogResource {
	
	private static final Logger _logger = LoggerFactory.getLogger(BlogResource.class);
	public static EntityManagerFactory _factory = Persistence.createEntityManagerFactory("blogPU");;
	public static EntityManager _entityManager = _factory.createEntityManager();
	
	
	/*
	 *  The methods below handles cookies. 
	 *  Reference: http://memorynotfound.com/jaxrs-cookieparam-crud-example/
	 */
	
	// Get a new cookie
	@GET
	@Path("/login")
	@Produces("application/xml")
	public Response login(@CookieParam("username") String cookie) {
	    return Response.ok().build();
	}
	
	// Create a new cookie
	@POST
	public Response createCookie(){
	    return Response
	            .ok()
	            .cookie(new NewCookie("username", "value"))
	            .build();
	}
	
	// Update a cookkie
	@PUT
	public Response updateCookie(@CookieParam("name") Cookie cookie){
	    if (cookie != null){
	        return Response
	                .ok()
	                .cookie(new NewCookie("name", "new-value"))
	                .build();
	    }
	    return Response.ok().build();
	}
	
	// Delete a cookie
	@DELETE
	public Response deleteCookie(@CookieParam("name") Cookie cookie){
	    if (cookie != null){
	        NewCookie newCookie = new NewCookie(cookie, "delete cookie", 0, false);
	        return Response
	                .ok()
	                .cookie(newCookie)
	                .build();
	    }
	    return Response.ok().build();
	}
	
	
	/**
	 * Add a new user to the system
	 * @param user	a user who has signed up
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	@POST
	@Consumes("application/xml")
	public Response createUser(User user) throws ClassNotFoundException, SQLException{

		_entityManager.getTransaction().begin();
		_logger.info("Read user: " + user);
		//persist user to db
		_entityManager.persist(user);
		_logger.info("Created user: " + user);
		_entityManager.getTransaction().commit();
		
		// Return a Response that specifies a status code of 201 Created along
		// with the Location header set to URI of the newly created User.
		return Response.created(URI.create("/users/" + user.get_id()))
				.build();
	}
	
	/**
	 * Add a new blog for a user
	 * @param id	the id of a user
	 * @param blog	the blog of a user
	 */
	@POST
	@Path("{user-id}/blog")
	@Consumes("application/xml")
	public Response createBlogForUser(@CookieParam("username") long cookieUserId, 
			@PathParam("user-id") long id, Blog blog){
		
		return Response.created(URI.create("/users/" + id + "/" + blog.get_id()))
				.build();
		
	}
	
	/**
	 * Add a new blog entry for a blog
	 * @param id 	the id of the blog
	 * @param entry	a blog entry
	 */
	@POST
	@Path("{user-id}/{blog-id}/blogentry")
	@Consumes("application/xml")
	public Response createBlogEntryForBlog(@PathParam("user-id") long user_id,
			@PathParam("blog-id") long blog_id, BlogEntry entry){
		
		
		return Response.created(URI.create("/users/" + user_id + "/" + 
				blog_id + "/" + entry.get_id()))
				.build();
		
	}
	
	@POST
	@Path("{user-id}/blogs-following")
	@Consumes("application/xml")
	public void addFollowing(@PathParam("user-id") long user_id,
			Blog blog){	
	}
	
	/**
	 * Update a user's details
	 * @param user
	 */
	@PUT
	@Path("{user-id}")
	@Consumes("application/xml")
	public void updateUser(User user){
		
		_entityManager.getTransaction().begin();
		User user_to_update = _entityManager.find(User.class, user.get_id());
		_entityManager.getTransaction().commit();
		user_to_update.set_firstname(user.get_firstname());
		user_to_update.set_lastname(user.get_lastname());
		
	}

	
	/**
	 * Retrieve a user
	 * @param user_id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@GET
	@Path("{user-id}")
	@Produces("application/xml")
	public User getUser(@PathParam("user-id") long user_id) throws 
		ClassNotFoundException, SQLException{

		_entityManager.getTransaction().begin();
		_logger.info("Trying to find user with user id: " + String.valueOf(user_id));
		User user = _entityManager.find(User.class, user_id);
		_logger.info("Found user " + user);
		_entityManager.getTransaction().commit();
		
		return user;
	}

}
