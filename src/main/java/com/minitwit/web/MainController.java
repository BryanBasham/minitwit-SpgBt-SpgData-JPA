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

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.minitwit.domain.Message;
import com.minitwit.domain.User;

/**
 * The MiniTwit main web controller.  It handles the Home page and the
 * ability to post a tweets.
 */
@Controller
public class MainController extends BaseController {
	
	/**
	 * Request the Home page.  If the user has authenticated, then then
	 * see their full timeline; otherwise the user is redirected to the
	 * Public Home page.
	 * 
	 * @param request
	 *   The HTTP request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/", method = GET)
	public ModelAndView userHome(final WebRequest request) {
		final Optional<User> authenticatedUser = getAuthenticatedUser(request);
		if (authenticatedUser.isPresent()) {
	        final ModelAndView nav = new ModelAndView("timeline");
	        nav.addObject("title", "Timeline");
	        nav.addObject("pageTitle", "Timeline");
	        nav.addObject("user", authenticatedUser.get());
			List<Message> messages =
					twitSvc.getUserFullTimelineMessages(authenticatedUser.get());
			nav.addObject("messages", messages);
			return nav;
		} else {
			return new ModelAndView(new RedirectView("/public"));
		}
	}

	/**
	 * Request the Public Home page.  This URI shows a complete timeline
	 * of tweets.
	 * 
	 * @param request
	 *   The HTTP request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/public", method = GET)
	public ModelAndView publicHome(final WebRequest request) {
        final ModelAndView nav = new ModelAndView("timeline");
		final Optional<User> authenticatedUser = getAuthenticatedUser(request);
		nav.addObject("title", "Timeline");
		nav.addObject("pageTitle", "Public Timeline");
		if (authenticatedUser.isPresent()) {
			nav.addObject("user", authenticatedUser.get());
		}
		List<Message> messages =
				twitSvc.getPublicTimelineMessages();
		nav.addObject("messages", messages);
		return nav;
	}

	/**
	 * Post a tweet.
	 * 
	 * @param text
	 *   The body of the tweet.
	 * @param request
	 *   The HTTP request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/message", method = POST)
	public ModelAndView addTweet(
			@RequestParam(value = "text", required = true) final String text,
			final WebRequest request) {
		final User user = getAuthenticatedUser(request)
				.orElseThrow(() -> new AuthenticationException());
		//
		final Message message = new Message(text, user);
		twitSvc.addMessage(message);
		//
		return new ModelAndView(new RedirectView("/"));
	}

}
