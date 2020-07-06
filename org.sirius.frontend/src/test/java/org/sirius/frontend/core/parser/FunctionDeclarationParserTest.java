package org.sirius.frontend.core.parser;

import static org.testng.Assert.assertEquals;

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
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.symbols.DefaultSymbolTable;
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
	
	
	private PartialList parseTypeDeclaration(String inputText, QName containerQName) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.functionDeclaration(containerQName);
				
		FunctionDeclarationParser.FunctionDeclarationVisitor typeVisitor = new FunctionDeclarationParser.FunctionDeclarationVisitor(reporter, containerQName);
		PartialList myType = typeVisitor.visit(tree);
		return myType;
	}
	
	@Test
	@DisplayName("Simplest function (check name)")
	public void simplestFunction() {
		PartialList partialList = parseTypeDeclaration("void f() {}", new QName("a", "b", "c"));
		assertEquals(partialList.getNameString(), "f");
		assertEquals(partialList.getqName().dotSeparated(), "a.b.c.f");
	}

	@Test
	@DisplayName("Function parameters")
	public void functionWithParameters() {
		PartialList partialList = parseTypeDeclaration("void f(A a, B b) {}", new QName());
		//assertEquals(partialList.getNameString(), "f");
		assertEquals(partialList.getPartials().size(), 3 /* NB: 1 more than parameters*/);
		
		assertEquals(
				partialList.getAllArgsPartial().getArgs().stream().map(astFuncParam -> astFuncParam.getNameString()).toArray(),
				new String[] {"a", "b"});
	}

	@Test
	@DisplayName("Function Simple return type")
	public void functionReturnType() {
		PartialList partialList = parseTypeDeclaration("Result f() {}", new QName());
		AstType returnType = partialList.getAllArgsPartial().getReturnType();
		
		assertThat(returnType, instanceOf(SimpleType.class));
		
		assertEquals(((SimpleType)returnType).getNameString(), "Result");
	}
	
	@Test
	@DisplayName("Function with void return type")
	public void functionVoidReturnType() {
		PartialList partialList = parseTypeDeclaration("void f() {}", new QName());
		AstType returnType = partialList.getAllArgsPartial().getReturnType();
		
		assertThat(returnType, instanceOf(AstVoidType.class));
	}

	@Test
	@DisplayName("Function containing statements")
	public void functionWithBodyStatements() {
		PartialList partialList = parseTypeDeclaration("void f() {Integer i; return 42;}", new QName());
		List<AstStatement> bodyStatements = partialList.getAllArgsPartial().getBodyStatements().get();
		
		assertThat(bodyStatements.size(), is(2));
		assertThat(partialList.isConcrete(), is(true));
	}

	@Test
//	@Disabled("Doesn't pass, pure declaration is not correctly handled by the grammar.")
	@DisplayName("Function declaration (without body)")
	public void functionWithoutBodyStatements() {
		PartialList partialList = parseTypeDeclaration("void f()", new QName());
		
		assertThat(partialList.getAllArgsPartial().getBodyStatements().isPresent(), is(false));
	}
}
