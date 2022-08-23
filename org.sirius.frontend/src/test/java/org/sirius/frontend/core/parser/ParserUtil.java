package org.sirius.frontend.core.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.AntlrErrorListenerProxy;
import org.sirius.frontend.parser.SLexer;
import org.sirius.frontend.parser.Sirius;

public class ParserUtil {
	
	public static record ParserFactory(Reporter reporter, CommonTokenStream tokenStream) {
		public Sirius create() {
			Sirius parser = new Sirius(tokenStream);
			
			parser.removeErrorListeners();
			parser.addErrorListener(new AntlrErrorListenerProxy(reporter));
			
			return parser;
		}		
	};
	
	public static ParserFactory createParserFactory(Reporter reporter, String inputText) {
		CharStream charStream = CharStreams.fromString(inputText);
		SLexer lexer = new SLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		ParserFactory parserFactory = new ParserFactory(reporter, tokenStream);
		return parserFactory;
	}

	public static Sirius createParser(Reporter reporter, String inputText) {
		return createParserFactory(reporter, inputText).create();
	}

}
