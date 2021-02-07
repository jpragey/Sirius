package org.sirius.frontend.core.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.QNameSetterVisitor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class FunctionDeclarationParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private FunctionDeclaration parseTypeDeclaration(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.functionDeclaration();
				
		FunctionDeclarationParser.FunctionDeclarationVisitor fdeclVisitor = new FunctionDeclarationParser.FunctionDeclarationVisitor(reporter /*, containerQName*/);
		FunctionDeclaration functionDecl = fdeclVisitor.visit(tree);
		
//		FunctionDeclarationParser.FunctionDefinitionVisitor fdefinitionVisitor = new FunctionDeclarationParser.FunctionDefinitionVisitor(reporter /*, containerQName*/);
//		FunctionDefinition functionDef = fdefinitionVisitor.visit(tree);
		
//		functionDef.visit(new QNameSetterVisitor());
		functionDecl.visit(new QNameSetterVisitor());
		
		return functionDecl;
	}
	
	private FunctionDefinition parseTypeDefinition(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
//		ParseTree tree = parser.functionDeclaration();
				
//		FunctionDeclarationParser.FunctionDeclarationVisitor fdeclVisitor = new FunctionDeclarationParser.FunctionDeclarationVisitor(reporter /*, containerQName*/);
//		FunctionDeclaration functionDecl = fdeclVisitor.visit(tree);
		
		FunctionDeclarationParser.FunctionDefinitionVisitor fdefinitionVisitor = new FunctionDeclarationParser.FunctionDefinitionVisitor(reporter /*, containerQName*/);
		FunctionDefinition functionDef = fdefinitionVisitor.visit(parser.functionDefinition());
		
		functionDef.visit(new QNameSetterVisitor());
//		functionDecl.visit(new QNameSetterVisitor());
		
		return functionDef;
	}
	
	@Test
	@DisplayName("Simplest function (check name)")
	public void simplestFunction() {
		FunctionDefinition fd = parseTypeDefinition("void f() {}" /*, new QName("a", "b", "c")*/);
		assertEquals(fd.getNameString(), "f");
		assertEquals(fd.getqName().dotSeparated(), "f");
	}

	@Test
	@DisplayName("Function parameters")
	public void functionWithParameters() {
		FunctionDefinition fd = parseTypeDefinition("void f(A a, B b) {}"/*, new QName()*/);
		//assertEquals(partialList.getNameString(), "f");
		assertEquals(fd.getPartials().size(), 3 /* NB: 1 more than parameters*/);
		
		assertThat(
				fd.getAllArgsPartial().getArgs().stream().map(astFuncParam -> astFuncParam.getNameString()).toArray(),
				equalTo(new String[] {"a", "b"}));
	}

	@Test
	@DisplayName("Function Simple return type")
	public void functionReturnType() {
		FunctionDefinition partialList = parseTypeDefinition("Result f() {}"/*, new QName()*/);
		AstType returnType = partialList.getAllArgsPartial().getReturnType();
		
		assertThat(returnType, instanceOf(SimpleType.class));
		
		assertEquals(((SimpleType)returnType).getNameString(), "Result");
	}
	
	@Test
	@DisplayName("Function with void return type")
	public void functionVoidReturnType() {
		FunctionDefinition partialList = parseTypeDefinition("void f() {}"/*, new QName()*/);
		AstType returnType = partialList.getAllArgsPartial().getReturnType();
		
		assertThat(returnType, instanceOf(AstVoidType.class));
	}

	@Test
	@DisplayName("Function containing statements")
	public void functionWithBodyStatements() {
		FunctionDefinition fd = parseTypeDefinition("void f() {Integer i; return 42;}" /*, new QName()*/);
		List<AstStatement> bodyStatements = fd.getAllArgsPartial().getBodyStatements().get();
		
		assertThat(bodyStatements.size(), is(2));
		assertThat(fd.isConcrete(), is(true));
	}

	@Test
	@DisplayName("Function declaration (without body)")
	public void functionWithoutBodyStatements() {
		FunctionDeclaration fd = parseTypeDeclaration("void f()" /*, new QName()*/);
		
//		assertThat(fd.getAllArgsPartial().getBodyStatements().isPresent(), is(false));
	}
}
