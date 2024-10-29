package s05.t01.exception;

public class NoCardsAvailableException extends RuntimeException {
    public NoCardsAvailableException(String message){
        super(message);
    }

}
