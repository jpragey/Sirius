package org.sirius.frontend.core.parser.temp;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.parser.ParserBuilder;
import org.sirius.frontend.core.parser.ParserUtil;
import org.sirius.frontend.core.parser.Parsers;
import org.sirius.frontend.core.parser.Parsers.CompilationUnit;
import org.sirius.frontend.parser.SParser;
import org.sirius.frontend.parser.SParser.NewCompilationUnitContext;

public class NewCompilationUnitTest {

	Reporter reporter;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter();
	}

	
	@Test
	@DisplayName("Parsing a Compilation Unit made of 3 modules results in a 3 modules AST")
	public void parseASimpleCompilationUnitTest() {
		
		String inputText = "#!/bin/sirius azerty\n module mod1 {} ; module mod2 {} {}";
		ParserBuilder fact = ParserUtil.createParserBuilder(reporter, inputText);
		SParser sirius = fact.create();
		
		NewCompilationUnitContext ctx = sirius.newCompilationUnit();
		CompilationUnit res = new Parsers.CompilationUnitVisitor().visit(ctx);
		
		System.out.println("Result: " + res);
		
		assertThat("", res.shebangDeclaration().isPresent());
		assertThat(res.shebangDeclaration().get().getTrimmedText(), is("/bin/sirius azerty"));

		assertThat(res.modules().stream().map(mh -> mh.qname().dotSeparated()).toList(), is(List.of("mod1", "mod2")));
	}


	@Test
	@DisplayName("Parsing Compilation Unit modules, nested form (  {}{} )")
	public void parseModuleDeclarations_NestedForm_Test() {
		
		String inputText = "module mod1 {} {} module mod2 {} {}";
		ParserBuilder fact = ParserUtil.createParserBuilder(reporter, inputText);
		SParser sirius = fact.create();
		
		NewCompilationUnitContext ctx = sirius.newCompilationUnit();
		CompilationUnit res = new Parsers.CompilationUnitVisitor().visit(ctx);
		
		assertThat("", res.shebangDeclaration().isEmpty());

		assertThat(res.modules().stream().map(mh -> mh.qname().dotSeparated()).toList(), is(List.of("mod1", "mod2")));
	}

	@Test
	@DisplayName("Parsing Compilation Unit modules, short form (  {}; )")
	public void parseModuleDeclarations_ShortForm_Test() {
		
		String inputText = "module mod1 {}; module mod2 {};";
		ParserBuilder fact = ParserUtil.createParserBuilder(reporter, inputText);
		SParser sirius = fact.create();
		
		NewCompilationUnitContext ctx = sirius.newCompilationUnit();
		CompilationUnit res = new Parsers.CompilationUnitVisitor().visit(ctx);
		
		assertThat("", res.shebangDeclaration().isEmpty());

		assertThat(res.modules().stream().map(mh -> mh.qname().dotSeparated()).toList(), is(List.of("mod1", "mod2")));
	}


	
	@Test
	@DisplayName("Parsing an empty Compilation Unit is OK")
	public void parseAnEmptyCompilationUnitTest() {
		
		String inputText = "";
		ParserBuilder fact = ParserUtil.createParserBuilder(reporter, inputText);
		SParser sirius = fact.create();
		
		NewCompilationUnitContext ctx = sirius.newCompilationUnit();
		CompilationUnit res = new Parsers.CompilationUnitVisitor().visit(ctx);
		
		assertThat("", res.shebangDeclaration().isEmpty());
		assertThat(res.modules(), empty());
	}

	@Test
	@DisplayName("Parsing Packages in  Compilation Unit modules")
	public void parseModuleContainingPackages_ShortForm_Test() {
		
		String inputText = "module mod1 {}; module mod2 {};";
		ParserBuilder fact = ParserUtil.createParserBuilder(reporter, inputText);
		SParser sirius = fact.create();
		
		NewCompilationUnitContext ctx = sirius.newCompilationUnit();
		CompilationUnit res = new Parsers.CompilationUnitVisitor().visit(ctx);
		
		assertThat("", res.shebangDeclaration().isEmpty());

		assertThat(res.modules().stream().map(mh -> mh.qname().dotSeparated()).toList(), is(List.of("mod1", "mod2")));
	}

}
