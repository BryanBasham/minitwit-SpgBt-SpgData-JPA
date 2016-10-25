package com.minitwit.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minitwit.dao.MessageRepository;
import com.minitwit.dao.UserRepository;
import com.minitwit.domain.Message;
import com.minitwit.domain.User;
import com.minitwit.util.PasswordUtil;

@Service
public class MiniTwitService {
	
	@Autowired
	private UserRepository userDao;
	
	@Autowired
	private MessageRepository messageDao;

	public List<User> getAllUsers() {
		return userDao.findAll();
	}
	
	public Optional<User> getUserbyUsername(String username) {
		return userDao.findByUsername(username);
	}

	public List<Message> getUserTimelineMessages(User user) {
		return messageDao.getUserTimelineMessages(user);
	}

	@Transactional(readOnly = false)
	public List<Message> getUserFullTimelineMessages(User user) {
		final User userRefresh = userDao.findOne(user.getId());
		final Set<User> friends = userRefresh.getFriends();
		if (friends.isEmpty()) {
			return messageDao.getUserTimelineMessages(user);
		} else {
			return messageDao.getUserFullTimelineMessages(userRefresh, friends);
		}
	}
	
	public List<Message> getPublicTimelineMessages() {
		return messageDao.getPublicTimelineMessages();
	}
	
	@Transactional(readOnly = false)
	public void followUser(User follower, User followee) {
		final User followerRefresh = userDao.getOne(follower.getId());
		final boolean addSuccess = followerRefresh.getFriends().add(followee);
		System.out.format("Adding %s from %s was successful? %s%n",
				followee.getUsername(), follower.getUsername(), addSuccess);
		userDao.saveAndFlush(followerRefresh);
	}

	@Transactional(readOnly = false)
	public void unfollowUser(User follower, User followee) {
		final User followerRefresh = userDao.getOne(follower.getId());
		final boolean removeSuccess = followerRefresh.getFriends().remove(followee);
		System.out.format("Removing %s from %s was successful? %s%n",
				followee.getUsername(), follower.getUsername(), removeSuccess);
		userDao.saveAndFlush(followerRefresh);
	}
	
	public boolean isUserFollower(User follower, User followee) {
		return userDao.isUserFollower(follower, followee);
	}

	@Transactional(readOnly = false)
	public void addMessage(Message message) {
		messageDao.saveAndFlush(message);
	}
	
	@Transactional(readOnly = false)
	public void registerUser(User user) {
		user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
		userDao.saveAndFlush(user);
	}
}
