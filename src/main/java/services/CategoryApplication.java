package services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
/**
 * Application subclass for the Parolee Web service.
 * @author Xiaohui
 *
 */
@ApplicationPath("/services")
public class CategoryApplication extends Application {
	 private Set<Object> singletons = new HashSet<Object>();
	   private Set<Class<?>> classes = new HashSet<Class<?>>();

	   public CategoryApplication()
	   {
		  // Register the CategoryResource singleton to handle HTTP requests.
		  CategoryResource resource = new CategoryResource();
	      singletons.add(resource);
	      
	      // Register the ContextResolver class for JAXB.
	      classes.add(CategoryResolver.class);
	   }

	   @Override
	   public Set<Object> getSingletons()
	   {
	      return singletons;
	   }
	   
	   @Override
	   public Set<Class<?>> getClasses()
	   {
	      return classes;
	   }

}
