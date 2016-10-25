package com.minitwit.web;

/**
 * A custom web exception when a resource is not found.
 * 
 * @author <a href='mailto:basham47@gmail.com'>Bryan Basham</a> 
 */
@SuppressWarnings("serial")
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}
	
}
