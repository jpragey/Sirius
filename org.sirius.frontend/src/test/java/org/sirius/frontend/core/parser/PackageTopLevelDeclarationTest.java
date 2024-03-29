package org.sirius.frontend.core.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.core.parser.FunctionDeclarationParser.FunctionDefinitionVisitor;
import org.sirius.frontend.parser.SParser;
import org.sirius.frontend.parser.SParser.ClassDeclarationContext;
import org.sirius.frontend.parser.SParser.FunctionDeclarationContext;
import org.sirius.frontend.parser.SParser.FunctionDefinitionContext;
import org.sirius.frontend.parser.SParser.PackageTopLevelDeclarationContext;
import org.sirius.frontend.parser.SParser.PackageTopLevelDeclarationsContext;
import org.sirius.frontend.parser.SParserBaseVisitor;

public class PackageTopLevelDeclarationTest {

	Reporter reporter;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter();
	}

	/*
	 * packageTopLevelDeclaration : 
    	  functionDeclaration
    	| functionDefinition 	
    	| classDeclaration 		
    	| interfaceDeclaration	
	 */
	public static class PackageTopLevelDeclarationVisitor /* SiriusBaseVisitor<String> packageTopLevelDeclarationVisitor =*/
		extends SParserBaseVisitor<String> 
	{
//		private Reporter reporter;
//		private CommonTokenStream tokens;
		private Parsers parsers;
		
//		public PackageTopLevelDeclarationVisitor(Reporter reporter, CommonTokenStream tokens) {
//			super();
//			this.reporter = reporter;
//			this.tokens = tokens;
//		}

		@Override
		public String visitFunctionDefinition(FunctionDefinitionContext ctx) {
			// TODO Auto-generated method stub
			return super.visitFunctionDefinition(ctx);
		}

		public PackageTopLevelDeclarationVisitor(Parsers parsers) {
	super();
	this.parsers = parsers;
}

		@Override
		public String visitPackageTopLevelDeclaration(PackageTopLevelDeclarationContext ctx) {
			FunctionDeclarationContext fdeclCtx = ctx.functionDeclaration();
			FunctionDefinitionContext fdefCtx = ctx.functionDefinition();
			ClassDeclarationContext clsDdeclCtx = ctx.classDeclaration();
//			InterfaceDeclarationContext intfCtx = ctx.interfaceDeclaration();
			
			if(fdeclCtx != null) {
				FunctionDeclaration fd = parsers.new FunctionDeclarationVisitor().visit(fdeclCtx);
			}
			if(fdefCtx != null) {
				// TODO: argh...
				FunctionDefinitionVisitor fdv = new FunctionDeclarationParser(parsers.reporter(), parsers.tokens()).new FunctionDefinitionVisitor();

				FunctionDefinition fd = fdv.visit(fdefCtx);
			}
			if(clsDdeclCtx != null) {
				// TODO: argh...
				ClassDeclarationParser.ClassDeclarationVisitor visitor = new ClassDeclarationParser(parsers.reporter(),
						parsers.tokens()).new ClassDeclarationVisitor();
				AstClassDeclaration classDeclaration = visitor.visit(clsDdeclCtx);

			}
			
			System.out.println("::: visitPackageTopLevelDeclaration ::: ");
			return super.visitPackageTopLevelDeclaration(ctx);
		}
		
	};

	@Test
	@DisplayName("Parsing a top-level functiondefinition results in a TODO")
	public void parseTopLevelDefinitionTest() {
//		FunctionDefinition fd = parseTypeDefinition("void f() {}" /*, new QName("a", "b", "c")*/);
		
		String inputText = "void f() {}";
		ParserBuilder fact = ParserUtil.createParserBuilder(reporter, inputText);
		Parsers parsers = new Parsers(reporter, fact.tokenStream());
		SParser sirius = fact.create();
		
		PackageTopLevelDeclarationsContext ctx = sirius.packageTopLevelDeclarations();
		String res = new PackageTopLevelDeclarationVisitor(parsers).visit(ctx);
		
		System.out.println("Result: " + res);
		
//		assertThat("", res);
	}
}
