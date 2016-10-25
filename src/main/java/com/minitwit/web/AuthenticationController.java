/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.minitwit.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.minitwit.domain.User;
import com.minitwit.util.PasswordUtil;

@Controller
public class AuthenticationController extends BaseController {

	/**
	 * Request the Login page.
	 * 
	 * @param request
	 *   The HTTP GET request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/login", method = GET)
	public ModelAndView loginForm(WebRequest request) {
        final ModelAndView nav = new ModelAndView("login");
        nav.addObject("user", new User());
		return nav;
	}

	/**
	 * Process the Login page.  If the user's credentials check out
	 * then store the user in the HTTP session and redirect to the
	 * {@linkplain MainController#userHome(WebRequest) Home page}.
	 * If not then send the user back to the Login page with an
	 * error message.
	 * 
	 * @param user
	 *   The {@link User} object bound to the login form fields.
	 * @param request
	 *   The HTTP POST request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/login", method = POST)
	public ModelAndView loginSubmit(@ModelAttribute("user") User user, WebRequest request) {
		final LoginResult result = checkUser(user);
		if (result.hasUser()) {
			addAuthenticatedUser(request, result.getUser());
			return new ModelAndView(new RedirectView("/"));
		} else {
	        final ModelAndView nav = new ModelAndView("login");
	        nav.addObject("user", user);
	        nav.addObject("error", result.getError());
			return nav;
		}
	}

	/**
	 * Log out of the application.  This action removes the user from
	 * the HTTP session and then redirects the user to the
	 * {@linkplain MainController#publicHome(WebRequest) Public Home page}.
	 * 
	 * @param request
	 *   The HTTP GET request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/logout", method = {GET, POST})
	public ModelAndView logout(WebRequest request) {
		removeAuthenticatedUser(request);
		return new ModelAndView(new RedirectView("/public"));
	}

	/**
	 * Request the Registration page.
	 * 
	 * @param request
	 *   The HTTP GET request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/register", method = GET)
	public ModelAndView registrationForm(WebRequest request) {
        final ModelAndView nav = new ModelAndView("register");
        nav.addObject("user", new User());
		return nav;
	}

	/**
	 * Process the Registration page.
	 * 
	 * @param user
	 *   The {@link User} object bound to the registration form fields.
	 * @param request
	 *   The HTTP POST request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/register", method = POST)
	public ModelAndView registrationSubmit(
			@ModelAttribute("user") final User user,
			final WebRequest request) {
		Optional<String> error = user.validate();
		if (!error.isPresent()) {
			Optional<User> userExists =
					twitSvc.getUserbyUsername(user.getUsername());
			if (!userExists.isPresent()) {
				twitSvc.registerUser(user);
				return new ModelAndView(new RedirectView("/"));
			}
			error = Optional.of("The username is already taken.");
		}
		final ModelAndView nav = new ModelAndView("register");
		nav.addObject("user", user);
		nav.addObject("error", error.get());
		return nav;
	}

	//
	// Private methods
	//

	private LoginResult checkUser(User user) {
		LoginResult result = new LoginResult();
		Optional<User> userFound = twitSvc.getUserbyUsername(user.getUsername());
		if(!userFound.isPresent()) {
			result.setError("Invalid username");
		} else if(!PasswordUtil.verifyPassword(user.getPassword(), userFound.get().getPassword())) {
			result.setError("Invalid password");
		} else {
			result.setUser(userFound.get());
		}
		
		return result;
	}

}
