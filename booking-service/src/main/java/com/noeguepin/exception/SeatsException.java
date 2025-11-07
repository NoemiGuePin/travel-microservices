package com.noeguepin.exception;

public class SeatsException extends RuntimeException {
	
    public enum SeatsErrorCode {
        SEATS_UNAVAILABLE,
        RELEASE_EXCEEDS_RESERVED
    }
    
    private final SeatsErrorCode errorCode;
    
	public SeatsException(String message, SeatsErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
	
	public SeatsErrorCode getErrorCode() {
		return errorCode;
	}
}
