package services;

import java.net.URI;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Category;
import domain.User;

/**
 * Web service resource implementation for category specific functionality. An instance
 * of this class handles all HTTP requests related to category creation.
 * 
 * @author xlin504
 *
 */
@Path("/categories") 
public class CategoryResource {
	
	private static final Logger _logger = LoggerFactory.getLogger(BlogResource.class);
	private EntityManager _entityManager = PersistenceManager.instance().createEntityManager();
	
	@POST
	@Consumes("application/xml")
	public Response createCategory(Category category) throws ClassNotFoundException, SQLException{

		_entityManager.getTransaction().begin();
		_logger.info("Read category: " + category);
		//persist category to db
		_entityManager.persist(category);
		_logger.info("Created category: " + category);
		_entityManager.getTransaction().commit();
		
		// Return a Response that specifies a status code of 201 Created along
		// with the Location header set to URI of the newly created category.
		return Response.created(URI.create("/categories/" + category.get_id()))
				.build();
	}
	
	@GET
	@Path("{category-id}")
	@Produces("application/xml")
	public Category getCategory(@PathParam("category-id") long cat_id) throws 
		ClassNotFoundException, SQLException{

		_entityManager.getTransaction().begin();
		_logger.info("Trying to find category with id: " + String.valueOf(cat_id));
		Category cat = _entityManager.find(Category.class, cat_id);
		_logger.info("Found category: " + cat);
		_entityManager.getTransaction().commit();
		return cat;
	}
	
}
