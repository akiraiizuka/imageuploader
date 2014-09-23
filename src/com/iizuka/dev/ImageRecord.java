package com.iizuka.dev;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;

@PersistenceCapable
public class ImageRecord {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String imageId;
	@Persistent
	private String authDomain;
	@Persistent
	private String nickName;
	@Persistent
	private String userId;
	@Persistent
	private String eMail;
	
	public ImageRecord() {
	}
	public ImageRecord(String imageId, User user) {
		this.imageId = imageId;
		this.authDomain = user.getAuthDomain();
		this.nickName = user.getNickname();
		this.userId = user.getUserId();
		this.eMail = user.getEmail();
	}
	
	public String getImageId() {
		return imageId;
	}
	public String getAuthDomain() {
		return authDomain;
	}
	public String getNickName() {
		return nickName;
	}
	public String getUserId() {
		return userId;
	}
	public String getEmail() {
		return eMail;
	}
}
