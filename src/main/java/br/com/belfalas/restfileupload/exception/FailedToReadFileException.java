package br.com.belfalas.restfileupload.exception;

public class FailedToReadFileException extends Exception {

    public FailedToReadFileException() {
    }

    public FailedToReadFileException(String s) {
        super(s);
    }

    public FailedToReadFileException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FailedToReadFileException(Throwable throwable) {
        super(throwable);
    }

    public FailedToReadFileException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
