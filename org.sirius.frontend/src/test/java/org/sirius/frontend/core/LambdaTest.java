package org.sirius.frontend.core;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.LambdaDeclaration;
import org.sirius.frontend.ast.LambdaDefinition;
import org.sirius.frontend.core.parser.ParserBuilder;
import org.sirius.frontend.core.parser.ParserUtil;
import org.sirius.frontend.core.parser.StatementParser;
import org.sirius.frontend.parser.Compiler;
import org.sirius.frontend.parser.SParser;

public class LambdaTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private AstStatement parseLambdaDefinition(String inputText) {
		
//		Sirius parser = ParserUtil.createParser(reporter, inputText);
		ParserBuilder parserFactory = ParserUtil.createParserBuilder(reporter, inputText);
		SParser parser = parserFactory.create();
		
		ParseTree tree = parser.localVariableStatement();
				
		StatementParser.StatementVisitor typeVisitor = new StatementParser(reporter, parserFactory.tokenStream()).new StatementVisitor();
		AstStatement myType = typeVisitor.visit(tree);
		
		return myType;
	}

	@Test
	@DisplayName("Lambda smoke test")
	public void lambdaSmokeTest() {
		AstStatement rawLambda = parseLambdaDefinition(
				"(Integer, Integer) -> Integer add = (Integer a, Integer b) : Integer {};"
				);
		
		assertThat(rawLambda, instanceOf(AstLocalVariableStatement.class));
		AstLocalVariableStatement var = (AstLocalVariableStatement)rawLambda;
		
		assertThat(var.getVarName().getText(), is("add"));
		
		assertThat(var.getType(), instanceOf(LambdaDeclaration.class));
		LambdaDeclaration varType = (LambdaDeclaration)var.getType();

		assertThat(var.getInitialValue().get(), instanceOf(LambdaDefinition.class));
		LambdaDefinition lambdaDefinition = (LambdaDefinition)var.getInitialValue().get();

//		lambdaDefinition.getType()
	}
	
	@Test
	@Disabled("Temp, Lambdas not correctly implemented")
	public void lambdaDefsAreCorrectlySet() {
		String sourceCode = "#!\n package p.k; class C(){"
				+"(Integer, Integer) -> Integer add = (Integer a, Integer b) : Integer {};"
//				+"(Integer, Integer) -> Integer sub = (Integer a, Integer b) : Integer {};"
//				+"(Integer, Integer) -> Integer mult = (Integer a, Integer b) : Integer {};"
				+ "}";
		ScriptSession session = Compiler.compileScript(sourceCode);
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		PackageDeclaration pd =  md.packageDeclarations().get(0);
		ClassType classC = pd.getClasses().get(0);
		
		MemberValue add = classC.memberValues().get(0);
		assertThat(add.initialValue().isPresent(), is(true));
//		assertThat(add.getInitialValue(), instanceOf(LambdaDefinition.class));

	}
}
