package org.sirius.common.error;

import java.util.Optional;

import org.sirius.common.core.Token;

public interface Reporter {
	
//	class Message {
//		private Optional<Token> token = Optional.empty();
//		public Message at(Token token) {
//			this.token = Optional.of(token);
//			return this;
//		}
//		
//	}
	
	public enum Severity {
		INFO("info"), WARNING("warning"), ERROR("error"), FATAL("fatal");
		public String name;

		private Severity(String name) {
			this.name = name;
		}
		
	}
	
	void message(Severity severity, String message, Optional<Token> token, Optional<Exception> exception);
	
	default void info(String message) {
		message(Severity.INFO, message, Optional.empty(), Optional.empty());
	}
	default void info(String message, Token token) {
		message(Severity.INFO, message, Optional.of(token), Optional.empty());
	}
	
	default void warning(String message) {
		message(Severity.WARNING, message, Optional.empty(), Optional.empty());
	}
	default void warning(String message, Token token) {
		message(Severity.WARNING, message, Optional.of(token), Optional.empty());
	}

	
	default void fatal(String message) {
		message(Severity.FATAL, message, Optional.empty(), Optional.empty());
	}
	default void fatal(String message, Token token) {
		message(Severity.FATAL, message, Optional.of(token), Optional.empty());
	}
	default void fatal(String message, Exception exception) {
		message(Severity.FATAL, message, Optional.empty(), Optional.ofNullable(exception));
	}
	default void fatal(String message, Token token, Exception exception) {
		message(Severity.FATAL, message, Optional.of(token), Optional.ofNullable(exception));
	}

	
	default void error(String message) {
		message(Severity.ERROR, message, Optional.empty(), Optional.empty());
	}
	default void error(String message, Token token) {
		message(Severity.ERROR, message, Optional.of(token), Optional.empty());
	}
	default void error(String message, Exception exception) {
		message(Severity.ERROR, message, Optional.empty(), Optional.ofNullable(exception));
	}
	default void error(String message, Token token, Exception exception) {
		message(Severity.ERROR, message, Optional.of(token), Optional.ofNullable(exception));
	}

	
	int getErrorCount();
	
	default boolean hasErrors() {
		return getErrorCount() > 0;
	}

	default boolean ok() {
		return getErrorCount() == 0;
	}
	
}
