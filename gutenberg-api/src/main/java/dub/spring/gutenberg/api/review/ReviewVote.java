package dub.spring.gutenberg.api.review;

//wrapper
public class ReviewVote {

	// review unique key
	private long userId;
	private int bookId;
	
	// voter id
	private long voterId;
	
	
	private boolean helpful;
	
	
	
	
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	public long getVoterId() {
		return voterId;
	}
	public void setVoterId(long voterId) {
		this.voterId = voterId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public boolean isHelpful() {
		return helpful;
	}
	public void setHelpful(boolean helpful) {
		this.helpful = helpful;
	}	
}
