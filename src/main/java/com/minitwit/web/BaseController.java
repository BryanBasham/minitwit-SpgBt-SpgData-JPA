package com.minitwit.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.minitwit.domain.User;
import com.minitwit.service.MiniTwitService;

/**
 * The base controller for all MiniTwit web services.  It includes authentication
 * logic and APIs.
 * 
 * @author <a href='mailto:basham47@gmail.com'>Bryan Basham</a> 
 */
public class BaseController {

	@Autowired
	protected MiniTwitService twitSvc;

	//
	// Global exception handlers
	//

	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ModelAndView handleAuthenticationException(final AuthenticationException e, final WebRequest request) {
		return new ModelAndView(new RedirectView("/login"));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ModelAndView handleResourceNotFoundException(final ResourceNotFoundException e, final WebRequest request) {
		final ModelAndView nav = new ModelAndView("my-error");
		getAuthenticatedUser(request).ifPresent(user -> nav.addObject("user", user));
		nav.addObject("pageTitle", "Error: resource not found");
		nav.addObject("message", e.getMessage());
		return nav;
	}

	//
	// Web authentication APIs
	//

	protected Optional<User> getAuthenticatedUser(final WebRequest request) {
		return Optional
				.ofNullable((String) request.getAttribute(USER_SESSION_ID, WebRequest.SCOPE_SESSION))
				.flatMap(username -> twitSvc.getUserbyUsername(username));
//		return Optional.ofNullable((User) request.getAttribute(USER_SESSION_ID, WebRequest.SCOPE_SESSION));
	}

	protected void addAuthenticatedUser(final WebRequest request, final User user) {
		request.setAttribute(USER_SESSION_ID, user.getUsername(), WebRequest.SCOPE_SESSION);
//		request.setAttribute(USER_SESSION_ID, user, WebRequest.SCOPE_SESSION);
	}

	protected void removeAuthenticatedUser(final WebRequest request) {
		request.removeAttribute(USER_SESSION_ID, WebRequest.SCOPE_SESSION);
	}

	private static final String USER_SESSION_ID = "username";

}
