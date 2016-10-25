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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.minitwit.domain.User;

@Controller
public class UserController extends BaseController {

	/**
	 * Show the set of friends for the current {@linkplain User user}.
	 * 
	 * @param request
	 *   The HTTP request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/t/friends", method = GET)
	public ModelAndView friends(final WebRequest request) {
		final ModelAndView nav = new ModelAndView("friends");
		nav.addObject("title", "Followers");
		getAuthenticatedUser(request)
		.ifPresent(user -> {
			nav.addObject("user", user);
			nav.addObject("friends", user.getFriends());
		});
		return nav;
	}

	/**
	 * Request the Timeline of a specific {@linkplain User user}.
	 * 
	 * @param followeeUsername
	 *   The username of the person this user wants to follow.
	 * @param request
	 *   The HTTP request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/t/{username}", method = GET)
	public ModelAndView userTimeline(
			@PathVariable("username") String profileUsername,
			WebRequest request) {
		//
		final User profileUser = findByUsername(profileUsername);
		final ModelAndView nav = new ModelAndView("timeline");
		nav.addObject("title", "Timeline");
		nav.addObject("pageTitle", profileUsername + "'s Timeline");
		// special data when a user is logged in
		getAuthenticatedUser(request)
		.ifPresent(user -> {
			nav.addObject("user", user);
			nav.addObject("followed", twitSvc.isUserFollower(user, profileUser));
		});
		//
		nav.addObject("profileUser", profileUser);
		nav.addObject("messages", twitSvc.getUserTimelineMessages(profileUser));
		return nav;
	}

	/**
	 * Follow a specific {@linkplain User user}.  After adding the profile to the
	 * current user's list of friends, the system redirects that that profile's
	 * timeline.
	 * 
	 * @param followeeUsername
	 *   The username of the person this user wants to un-follow.
	 * @param request
	 *   The HTTP request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/t/{username}/follow", method = GET)
	public ModelAndView follow(
			@PathVariable("username") final String followeeUsername,
			final WebRequest request) {
		// determine the user to follow (aka the 'followee')
		final User followee = findByUsername(followeeUsername);
		// the current user is the follower
		final User follower =
				getAuthenticatedUser(request)
				.orElseThrow(() -> new AuthenticationException());
		// update the user's friend list
		twitSvc.followUser(follower, followee);
		// redirect to the followee
		return new ModelAndView(new RedirectView("/t/" + followeeUsername));
	}

	/**
	 * Unfollow a specific {@linkplain User user}.  After removing the profile to the
	 * current user's list of friends, the system redirects that that profile's
	 * timeline.
	 * 
	 * @param followeeUsername
	 *   The username of the person this user wants to follow.
	 * @param request
	 *   The HTTP request.
	 * 
	 * @return
	 *   The View (with Model) to render in response.
	 */
	@RequestMapping(value = "/t/{username}/unfollow", method = GET)
	public ModelAndView unfollow(
			@PathVariable("username") final String followeeUsername,
			final WebRequest request) {
		// determine the user to follow (aka the 'followee')
		final User followee = findByUsername(followeeUsername);
		// the current user is the follower
		final User follower =
				getAuthenticatedUser(request)
				.orElseThrow(() -> new AuthenticationException());
		// update the user's friend list
		twitSvc.unfollowUser(follower, followee);
		// redirect to the followee
		return new ModelAndView(new RedirectView("/t/" + followeeUsername));
	}

	//
	// Private methods
	//
	
	/**
	 * Find a {@link User} but if not found then throw a
	 *  {@link ResourceNotFoundException}.
	 *  
	 * @param username
	 *   The username of the {@link User} to lookup
	 * 
	 * @return
	 *   The {@link User} entity if found
	 *   
	 *@throws ResourceNotFoundException
	 *   If not found
	 */
	private User findByUsername(final String username) {
		return twitSvc.getUserbyUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException(
						String.format("Profile user '%s' not found.", username)));
	}
}
