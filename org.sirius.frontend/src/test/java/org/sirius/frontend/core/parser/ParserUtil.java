package org.sirius.frontend.core.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.parser.SLexer;
import org.sirius.frontend.parser.SParser;

public class ParserUtil {
	
	public static ParserBuilder createParserBuilder(Reporter reporter, String inputText) {
		CharStream charStream = CharStreams.fromString(inputText);
		SLexer lexer = new SLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		ParserBuilder parserBuilder = new ParserBuilder(reporter, tokenStream);
		return parserBuilder;
	}

	public static SParser createParser(Reporter reporter, String inputText) {
		return createParserBuilder(reporter, inputText).create();
	}

}
