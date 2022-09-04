package org.sirius.frontend.core.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.core.parser.Parsers.CompilationUnit;
import org.sirius.frontend.core.parser.Parsers.ModuleHeader;
import org.sirius.frontend.core.parser.ShebangDeclarationParser.ShebangVisitor;
import org.sirius.frontend.parser.SLexer;
import org.sirius.frontend.parser.Sirius;
import org.sirius.frontend.parser.Sirius.ModuleHeaderContext;
import org.sirius.frontend.parser.Sirius.NewCompilationUnitContext;
import org.sirius.frontend.parser.SiriusBaseVisitor;

public class NewCompilationUnitTest {

	Reporter reporter;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter();
	}
	
//	public static record CompilationUnit(Optional<ShebangDeclaration> shebangDeclaration, List<ModuleHeader> modules) {}
//	public static record ModuleHeader(QName qname) {}
	
//	public static SiriusBaseVisitor<ModuleHeader> moduleHeaderVisitor = new SiriusBaseVisitor<ModuleHeader>() {
//
//		@Override
//		public ModuleHeader visitModuleHeader(ModuleHeaderContext ctx) {
//			Parsers.QNameVisitor qnameVisitor = new Parsers.QNameVisitor();
//			QName qname = qnameVisitor.visitQname(ctx.qname());
//			
//			ModuleHeader mh = new ModuleHeader(qname);
//			return mh;
//		}
//		public ModuleHeader visitNewModuleDeclaration(org.sirius.frontend.parser.Sirius.NewModuleDeclarationContext ctx) {
//			return visitModuleHeader(ctx.moduleHeader());
//		};
//		
//	};
//	public static SiriusBaseVisitor<CompilationUnit> compilationUnitVisitor = new SiriusBaseVisitor<CompilationUnit>() {
//		public CompilationUnit visitNewCompilationUnit(NewCompilationUnitContext ctx) {
//
//			SiriusBaseVisitor<ShebangDeclaration> sbv = new ShebangDeclarationParser.ShebangVisitor();
//			
//			List<ModuleHeader> mods  = ctx.newModuleDeclaration().stream()
//					.map(nmdCtx -> moduleHeaderVisitor.visitNewModuleDeclaration(nmdCtx))
//					.collect(Collectors.toList());
//			
//			Optional<ShebangDeclaration> shebangDeclaration = Optional.ofNullable(
//					ctx.shebangDeclaration()).map(sbv::visitShebangDeclaration);
//			
//			return new CompilationUnit(shebangDeclaration, mods);
//			
//		};
//	};
	
	@Test
	@DisplayName("Parsing a Compilation Unit made of 3 modules results in a 3 modules AST")
	public void parseASimpleCompilationUnitTest() {
		
		String inputText = "#!/bin/sirius azerty\n module mod1 {} ; module mod2 {} {}";
		ParserBuilder fact = ParserUtil.createParserBuilder(reporter, inputText);
		Sirius sirius = fact.create();
		
		NewCompilationUnitContext ctx = sirius.newCompilationUnit();
		CompilationUnit res = Parsers.compilationUnitVisitor.visit(ctx);
		
		System.out.println("Result: " + res);
		
		assertThat("", res.shebangDeclaration().isPresent());
		assertThat(res.shebangDeclaration().get().getTrimmedText(), is("/bin/sirius azerty"));

		assertThat(res.modules().stream().map(mh -> mh.qname().dotSeparated()).toList(), is(List.of("mod1", "mod2")));
	}
	
}
