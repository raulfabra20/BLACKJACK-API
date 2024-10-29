package s05.t01.exception;

public class NotUpdatedPlayerException extends RuntimeException {
    public NotUpdatedPlayerException(String message){
        super(message);
    }
}
