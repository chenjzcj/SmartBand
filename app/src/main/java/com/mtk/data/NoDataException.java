package com.mtk.data;

public class NoDataException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoDataException() {
        super();
    }

    public NoDataException(String msg) {
        super(msg);
    }

    public NoDataException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NoDataException(Throwable cause) {
        super(cause);
    }

}
