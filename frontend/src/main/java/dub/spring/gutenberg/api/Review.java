package dub.spring.gutenberg.api;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/** 
 * A given user can write only one review for the same book
 * */

// POJO not document
public class Review {
	
	private long bookId;
	private LocalDateTime date;
	private String title;
	private String text;
	private int rating;
	private long userId;
	private String username;
	private int helpfulVotes;
	private Set<Long> voterIds;
	
	public Review() {
		this.voterIds = new HashSet<>();
	}


	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	

	public long getBookId() {
		return bookId;
	}


	public long getUserId() {
		return userId;
	}


	public int getHelpfulVotes() {
		return helpfulVotes;
	}

	public void setHelpfulVotes(int helpfulVotes) {
		this.helpfulVotes = helpfulVotes;
	}


	public Set<Long> getVoterIds() {
		return voterIds;
	}


	public void setVoterIds(Set<Long> voterIds) {
		this.voterIds = voterIds;
	}


	public void setBookId(long bookId) {
		this.bookId = bookId;
	}


	public void setUserId(long userId) {
		this.userId = userId;
	}

	
}
