package com.github.unknownstudio.unknowndomain.engineapi.event;

public class EventException extends RuntimeException {
	
	public EventException(String message, Throwable throwable) {
		super(message, throwable);
	}

}