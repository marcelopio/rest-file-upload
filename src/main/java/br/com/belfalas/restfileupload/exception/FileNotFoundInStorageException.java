package br.com.belfalas.restfileupload.exception;

public class FileNotFoundInStorageException extends Exception{

    public FileNotFoundInStorageException() {
    }

    public FileNotFoundInStorageException(String s) {
        super(s);
    }

    public FileNotFoundInStorageException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FileNotFoundInStorageException(Throwable throwable) {
        super(throwable);
    }

    public FileNotFoundInStorageException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
