package org.sirius.frontend.core.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AntlrErrorListenerProxy;
import org.sirius.frontend.parser.SiriusLexer;
import org.sirius.frontend.parser.SiriusParser;

public class ParserUtil {

	public static SiriusParser createParser(Reporter reporter, String inputText) {
		CharStream charStream = CharStreams.fromString(inputText);
		SiriusLexer lexer = new SiriusLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		SiriusParser parser = new SiriusParser(tokenStream);
		
		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));
		
		return parser;
	}

}
