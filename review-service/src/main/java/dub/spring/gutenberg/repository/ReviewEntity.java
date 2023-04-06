package dub.spring.gutenberg.repository;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="reviews")

@CompoundIndexes({
    @CompoundIndex(name = "book_user", 
    					def = "{'bookId' : 1, 'userId': 1}", 
    					unique = true)
})
public class ReviewEntity {

	
	/**
	 * Note that id is internal to MongoDB, not accessible 
	 * */
    @Id
	private String id;
    
	private LocalDateTime date;
	private String text;
	private int rating;
	private String username;
	private int helpfulVotes;
	private int bookId;
	private int userId;
	private Set<Integer> voterIds;
	
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Set<Integer> getVoterIds() {
		return voterIds;
	}
	public void setVoterIds(Set<Integer> voterIds) {
		this.voterIds = voterIds;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
