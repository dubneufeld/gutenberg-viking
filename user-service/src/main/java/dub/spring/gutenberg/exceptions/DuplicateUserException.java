package dub.spring.gutenberg.exceptions;

public class DuplicateUserException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DuplicateUserException() {}
 
	public DuplicateUserException(String message) {
	    super(message);
	}

	public DuplicateUserException(String message, Throwable cause) {
	    super(message, cause);
	}

	public DuplicateUserException(Throwable cause) {
	    super(cause);
	}

}
