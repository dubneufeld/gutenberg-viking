package dub.spring.gutenberg.api.review;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/** 
 * A given user can write only one review for the same book
 * */
// POJO, not document
public class Review implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LocalDateTime date;
	private String text;
	private int rating;
	private String username;
	private int helpfulVotes;
	private int bookId;
	private long userId;
	private Set<Long> voterIds;
	
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public Set<Long> getVoterIds() {
		return voterIds;
	}
	public void setVoterIds(Set<Long> voterIds) {
		this.voterIds = voterIds;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
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
	public int getHelpfulVotes() {
		return helpfulVotes;
	}
	public void setHelpfulVotes(int helpfulVotes) {
		this.helpfulVotes = helpfulVotes;
	}
	
	
	
	
}