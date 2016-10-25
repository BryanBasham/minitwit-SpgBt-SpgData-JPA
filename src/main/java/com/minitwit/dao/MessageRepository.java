package com.minitwit.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.minitwit.domain.Message;
import com.minitwit.domain.User;

/**
 * The repository for the {@link Message} entity.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

	/**
	 * Retrieves a list of {@linkplain Message tweets} for a specific user.
	 */
	@Query("SELECT m FROM Message m WHERE m.user = :user ORDER BY m.pubDate DESC")
	List<Message> getUserTimelineMessages(@Param("user") User user);

	/**
	 * Retrieves a list of {@linkplain Message tweets} for a specific user
	 * and all of that user's friends.
	 */
	@Query("SELECT DISTINCT m FROM Message m JOIN FETCH m.user "
			+ "WHERE m.user = :user OR m.user IN :followers ORDER BY m.pubDate DESC")
	List<Message> getUserFullTimelineMessages(
			@Param("user") User user,
			@Param("followers") Collection<User> followers);

	/**
	 * Retrieves a complete list of {@linkplain Message tweets} for all users.
	 */
	@Query("SELECT m FROM Message m JOIN FETCH m.user ORDER BY m.pubDate DESC")
	List<Message> getPublicTimelineMessages();

}
