package org.sirius.frontend.core.parser;

import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AntlrErrorListenerProxy;
import org.sirius.frontend.parser.SParser;

public record ParserBuilder(Reporter reporter, CommonTokenStream tokenStream) {
	public SParser create() {
		SParser parser = new SParser(tokenStream);
		
		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));
		
		return parser;
	}		
}