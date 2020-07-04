package org.sirius.frontend.core.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstFactory;
import org.sirius.frontend.parser.SiriusLexer;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class ParserUtil {

	public static SiriusParser createParser(Reporter reporter, String inputText) {
		CharStream charStream = CharStreams.fromString(inputText);
		SiriusLexer lexer = new SiriusLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		SiriusParser parser = new SiriusParser(tokenStream);
		DefaultSymbolTable globalSymbolTable = new DefaultSymbolTable("");
		parser.factory = new AstFactory(reporter, globalSymbolTable);
		
		return parser;
	}

}
