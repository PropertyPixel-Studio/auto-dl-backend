package cz.pps.auto_dl_be.exception;

public class SavingCsvException extends Exception {
    public SavingCsvException(String message) {
        super(message);
    }

    public SavingCsvException(String message, Throwable cause) {
        super(message, cause);
    }
}
