package services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/services")
public class BlogApplication extends Application {
	
	 private Set<Object> singletons = new HashSet<Object>();
	 private Set<Class<?>> classes = new HashSet<Class<?>>();
	 
	   public BlogApplication()
	   {
	      singletons.add(new BlogResource());
	      classes.add(BlogResolver.class);
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
