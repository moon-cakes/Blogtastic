package services;

import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import domain.Category;

public class CategoryResolver implements ContextResolver<JAXBContext> {

	private JAXBContext _context;
	
	public CategoryResolver() {
		try {
			// The JAXB Context should be able to marshal and unmarshal the
			// specified classes.
			_context = JAXBContext.newInstance(Category.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public JAXBContext getContext(Class<?> type) {
		if (type.equals(Category.class)) {
			return _context;
		} else {
			return null;
		}
	}

}
