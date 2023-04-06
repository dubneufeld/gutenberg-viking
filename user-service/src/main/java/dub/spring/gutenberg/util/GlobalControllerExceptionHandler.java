package dub.spring.gutenberg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebInputException;

import dub.spring.gutenberg.exceptions.DuplicateUserException;
import dub.spring.gutenberg.exceptions.InvalidInputException;
import dub.spring.gutenberg.exceptions.NotFoundException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
class GlobalControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
 
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public @ResponseBody
    HttpErrorInfo handleNotFoundException(ServerHttpRequest request, Exception ex) {

        return createHttpErrorInfo(NOT_FOUND, request, ex);
    }
    
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(TimeoutException.class)
    public @ResponseBody
    HttpErrorInfo handleTimeoutException(ServerHttpRequest request, Exception ex) {

        return createHttpErrorInfo(INTERNAL_SERVER_ERROR, request, ex);
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public @ResponseBody HttpErrorInfo handleInvalidInputException(ServerHttpRequest request, Exception ex) {
    	
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
    }
    
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ServerWebInputException.class)
    public @ResponseBody HttpErrorInfo handleServerWebInputException(ServerHttpRequest request, Exception ex) {
    	
    	System.err.println("LOREM IPSUM " + ex);
        return createHttpErrorInfo(BAD_REQUEST, request, ex);
    }
    
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DecodingException.class)
    public @ResponseBody HttpErrorInfo handleDecodingException(ServerHttpRequest request, Exception ex) {
    	
    	System.err.println("AD PATRES " + ex);
        return createHttpErrorInfo(BAD_REQUEST, request, ex);
    }
    
    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DuplicateUserException.class)
    public @ResponseBody HttpErrorInfo handleDuplicateUserException(ServerHttpRequest request, Exception ex) {
    	
    	System.err.println("MORBUS GRAVIS " + ex);
        return createHttpErrorInfo(CONFLICT, request, ex);
    }
    
    
    
    
    
    private HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, ServerHttpRequest request, Exception ex) {
        final String path = request.getPath().pathWithinApplication().value();
            
        String message = "";
      
        message = ex.getMessage();
       
        
        boolean bingo = ex.getMessage().contains("mismatch");
    	System.err.println("bingo " + bingo);
        LOG.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
        return new HttpErrorInfo(httpStatus, path, message);
    }

  
}
