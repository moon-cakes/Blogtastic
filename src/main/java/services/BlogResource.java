package services;

import java.net.URI;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
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
	protected static EntityManagerFactory _factory = null;
	protected static EntityManager _entityManager = null;
	
	/**
	 * Add a new user to the system
	 * @param user	a user who has signed up
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	@POST
	@Consumes("application/xml")
	public Response createUser(User user) throws ClassNotFoundException, SQLException{
		
		DatabaseUtility.openDatabase();
		
		EntityManagerFactory _factory = Persistence.createEntityManagerFactory("blogPU");
		EntityManager _entityManager = _factory.createEntityManager();
		_entityManager.getTransaction().begin();
		
		_logger.info("Read user: " + user);
		//persist user to db
		_entityManager.persist(user);
		_logger.info("Created user: " + user);
		
		_entityManager.getTransaction().commit();
		_entityManager.close();
		DatabaseUtility.closeDatabase();
		
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
	public Response createBlogForUser(@PathParam("user-id") long id,
			Blog blog){
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
	
/*	*//**
	 * Update an existing registered user.
	 * @param user
	 *//*
	@PUT
	@Path("{user-id}")
	@Consumes("application/xml")
	public void updateUser(User user){
		User user_update = findUser(user.get_id());
		user_update.set_firstname(user.get_firstname());
		user_update.set_lastname(user.get_lastname());
	}
	
	@GET
	@Path("{user-id}")
	@Produces("application/xml")
	public User getUser(@PathParam("user-id") long user_id){
		
		return user;
	}*/

	

}
