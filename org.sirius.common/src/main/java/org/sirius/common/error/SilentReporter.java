package org.sirius.common.error;

import java.util.Optional;

import org.sirius.common.core.Token;

/** Reporter that doesn't print anything, but count errors. Used for (silent) tests.
 * 
 * @author jpragey
 *
 */
public class SilentReporter implements Reporter {
	
	int errorCount;
	public SilentReporter() {
		super();
		this.errorCount = 0;
	}

	@Override
	public void message(Severity severity, String message, Optional<Token> token, Optional<Exception> exception) {
		if(severity == Severity.ERROR || severity == Severity.FATAL) {
			this.errorCount ++;
		}
	}

	@Override
	public int getErrorCount() {
		return this.errorCount;
	}

}
