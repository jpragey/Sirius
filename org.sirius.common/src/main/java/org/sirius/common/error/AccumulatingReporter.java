package org.sirius.common.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.core.Token;

public class AccumulatingReporter implements Reporter {
	
	private Reporter delegate;
	
	private List<String> messages = new ArrayList<>();
	private List<String> warnings = new ArrayList<>();
	private List<String> errors = new ArrayList<>();
	private List<String> fatals = new ArrayList<>();
	
	private List<Exception> exceptions = new ArrayList<>();
	
	public AccumulatingReporter(Reporter delegate) {
		super();
		this.delegate = delegate;
	}
	public AccumulatingReporter() {
		super();
//		this.delegate = new SilentReporter();
		this.delegate = new ShellReporter();
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
		case FATAL: 
			fatals.add(message);
			break;
		}
		
		exception.ifPresent(exceptions::add);
		
		delegate.message(severity, message, token, exception);
	}



	@Override
	public int getErrorCount() {
		return errors.size() + fatals.size();
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
	public List<String> getFatals() {
		return fatals;
	}
	
	@Override
	public String toString() {
		return "AccumulatingReporter: " + 
				fatals.size() + " fatals, " + 
				errors.size() + " errors, " + 
				warnings.size() + " warnings, " + 
				messages.size() + " messages.";
	}
	public List<Exception> getExceptions() {
		return exceptions;
	}
	
	public void rethrowFirst() throws Exception {
		if(this.exceptions.isEmpty())
			return;
		
		Exception e = this.exceptions.get(0);
		throw e;
	}
}
