package com.gslab.oidc.servlet3;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.gslab.oidc.Config.SpringWebConfig;
/**
 * 
 * @author GS-1547
 * Class for mapping the request to the jsp page.
 */
public class MyWebIntializer extends
	AbstractAnnotationConfigDispatcherServletInitializer {
	
	@Override
	protected Class<?>[] getServletConfigClasses() {
	return new Class[] { SpringWebConfig.class };
	}
	
	@Override
	protected String[] getServletMappings() {
	return new String[] { "/" };
	}
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
	return null;
	}
}
