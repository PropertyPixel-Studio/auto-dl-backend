package cz.pps.auto_dl_be.exception;

public class CsvDownloadException extends Exception {
    public CsvDownloadException(String message) {
        super(message);
    }

    public CsvDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}