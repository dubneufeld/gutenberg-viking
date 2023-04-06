package dub.spring.gutenberg.exceptions;

public class ReviewNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ReviewNotFoundException() {}
 
	public ReviewNotFoundException(String message) {
	    super(message);
	}

	public ReviewNotFoundException(String message, Throwable cause) {
	    super(message, cause);
	}

	public ReviewNotFoundException(Throwable cause) {
	    super(cause);
	}

}
