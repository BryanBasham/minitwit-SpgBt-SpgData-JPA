package com.minitwit.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.minitwit.util.GravatarUtil;

@Entity
public class Message {

	//
	// Constants
	//

	private static final String GRAVATAR_DEFAULT_IMAGE_TYPE = "monsterid";
	private static final int GRAVATAR_SIZE = 48;

	//
	// Attributes
	//

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String text;

	@Column(name = "pub_date", nullable = false)
	private Date pubDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	//
	// Constructors
	//

	protected Message() {
	}

	public Message(final String text, final User user) {
		this.text = text;
		this.user = user;
		this.pubDate = new Date();
	}

	//
	// Accessors
	//

	public Long getId() {
		return id;
	}
	
	public User getUser() {
		return user;
	}

	public String getText() {
		return text;
	}

	public Date getPubDate() {
		return pubDate;
	}

	@Transient
	public String getPubDateStr() {
		if(pubDate != null) {
			return new SimpleDateFormat("yyyy-MM-dd @ HH:mm").format(pubDate);
		}
		return "";
	}

	@Transient
	public String getGravatar() {
		return GravatarUtil.gravatarURL(user.getEmail(), GRAVATAR_DEFAULT_IMAGE_TYPE, GRAVATAR_SIZE);
	}
}
