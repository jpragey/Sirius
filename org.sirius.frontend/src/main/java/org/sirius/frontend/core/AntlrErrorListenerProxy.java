package org.sirius.frontend.core;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.sirius.common.error.Reporter;

public class AntlrErrorListenerProxy extends BaseErrorListener {
	private Reporter reporter;
	
	public AntlrErrorListenerProxy(Reporter reporter) {
		super();
		this.reporter = reporter;
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		
		reporter.error(line + ":" + charPositionInLine + ": " + msg, e);
	}

}
