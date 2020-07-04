package org.sirius.common.error;

import java.util.Optional;

import org.sirius.common.core.Token;

/** Proxy Reporter that throws a RuntimeException for ERROR and FATAL messages.
 * 
 * @author jpragey
 *
 */
public class FailFastReporter implements Reporter {
	private Reporter delegate;
	
	
	public FailFastReporter(Reporter delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void message(Severity severity, String message, Optional<Token> token, Optional<Exception> exception) {
		delegate.message(severity, message, token, exception);
		if(severity == Severity.ERROR || severity == Severity.FATAL)
			throw new RuntimeException(message);
	}

	@Override
	public int getErrorCount() {
		return delegate.getErrorCount();
	}

}
