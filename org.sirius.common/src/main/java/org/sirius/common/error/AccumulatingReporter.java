package org.sirius.common.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccumulatingReporter implements Reporter {
	
	private Reporter delegate;
	
	private List<String> messages = new ArrayList<>();
	private List<String> warnings = new ArrayList<>();
	private List<String> errors = new ArrayList<>();
	
	public AccumulatingReporter(Reporter delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void message(Severity severity, String message, Optional<Token> token, Optional<Exception> exception) {
		switch(severity) {
		case INFO : 
			messages.add(message);
			break;
		case WARNING: 
			warnings.add(message);
			break;
		case ERROR: 
			errors.add(message);
			break;
		}
		delegate.message(severity, message, token, exception);
	}



	@Override
	public int getErrorCount() {
		return errors.size();
	}


	public List<String> getMessages() {
		return messages;
	}


	public List<String> getWarnings() {
		return warnings;
	}


	public List<String> getErrors() {
		return errors;
	}

	
}
