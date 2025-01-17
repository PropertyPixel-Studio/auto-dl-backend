package cz.pps.auto_dl_be.exception;

public class NoDataException extends Exception {
    public NoDataException(String message) {
        super(message);
    }

    public NoDataException(String message, Throwable cause) {
        super(message, cause);
    }
}