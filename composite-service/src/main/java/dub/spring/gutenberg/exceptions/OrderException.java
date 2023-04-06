package dub.spring.gutenberg.exceptions;

public class OrderException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public OrderException() {}
 
	public OrderException(String message) {
	    super(message);
	}

	public OrderException(String message, Throwable cause) {
	    super(message, cause);
	}

	public OrderException(Throwable cause) {
	    super(cause);
	}

}
