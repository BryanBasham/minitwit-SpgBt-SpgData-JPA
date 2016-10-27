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

package com.minitwit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.minitwit.service.MiniTwitService;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {
	
	@Autowired
	private MiniTwitService twitSvc;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
	
}

/*
@Component
class TheCommand implements CommandLineRunner {

	@Autowired
	private MiniTwitService twitSvc;

	
	@Override
	public void run(String... args) throws Exception {
		final List<User> users = twitSvc.getAllUsers();
		System.out.format("users count is %d%n", users.size());
		users.stream().forEach(user -> {
			final Set<User> friends = user.getFriends();
			System.out.format("%s has %d friends: %s%n",
					user.getUsername(), friends.size(),
					// display the usernames of the friends
					friends.stream().map(User::getUsername).collect(Collectors.toSet()));
			final List<Message> posts = twitSvc.getUserTimelineMessages(user);
			System.out.format("%s has %d posts.%n",
					user.getUsername(), posts.size());
			final List<Message> twits = twitSvc.getUserFullTimelineMessages(user);
			System.out.format("%s has %d twits on their timeline.%n",
					user.getUsername(), twits.size());
		});
	}
}
*/