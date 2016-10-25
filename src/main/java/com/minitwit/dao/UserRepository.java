package com.minitwit.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.minitwit.domain.User;

/**
 * The repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Finds a {@linkplain User user} with an exact match on the username.
	 */
	Optional<User> findByUsername(String username);

	/**
	 * Queries whether the {@code follower} is friends with the {@code followee}.
	 */
	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u"
			+ " WHERE u = :follower AND :followee MEMBER OF u.friends")
	boolean isUserFollower(
			@Param("follower") User follower,
			@Param("followee") User followee);
	
}
