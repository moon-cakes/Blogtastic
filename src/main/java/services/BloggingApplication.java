package services;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/services")
public class BloggingApplication extends Application {
	
	 private Set<Object> singletons = new HashSet<Object>();
	 private Set<Class<?>> classes = new HashSet<Class<?>>();
	 
	   public BloggingApplication() throws ClassNotFoundException, SQLException
	   {
	      singletons.add(new BlogResource());
	      singletons.add(new CategoryResource());
	      classes.add(BlogResolver.class);
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
