package exceptions;

public class NotEnoughParamsException extends Exception {
    public NotEnoughParamsException() {
        super("Not enough params");
    }
}
