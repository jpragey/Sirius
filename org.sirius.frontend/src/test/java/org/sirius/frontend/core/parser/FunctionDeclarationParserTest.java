package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.empty;
//import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.Annotation;
//import org.sirius.frontend.api.Annotation;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.ClosureElement;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.parser.Sirius;
import org.sirius.frontend.symbols.QNameSetterVisitor;

public class FunctionDeclarationParserTest {

	private Reporter reporter ;
//	private Parsers parsers;
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
//		this.parsers = new Parsers(this.reporter);
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private FunctionDeclaration parseTypeDeclaration(String inputText) {
		
//		Sirius parser = ParserUtil.createParser(reporter, inputText);
		ParserBuilder parserFactory = ParserUtil.createParserBuilder(reporter, inputText);
		Sirius parser = parserFactory.create();

		ParseTree tree = parser.functionDeclaration();
				
		Parsers.FunctionDeclarationVisitor fdeclVisitor = new Parsers(reporter, parserFactory.tokenStream()).new FunctionDeclarationVisitor();
		FunctionDeclaration functionDecl = fdeclVisitor.visit(tree);
		
		functionDecl.visit(new QNameSetterVisitor());
		
		return functionDecl;
	}
	
	private FunctionDefinition parseTypeDefinition(String inputText) {
		
//		Sirius parser = ParserUtil.createParser(reporter, inputText);
		ParserBuilder parserFactory = ParserUtil.createParserBuilder(reporter, inputText);
		Sirius parser = parserFactory.create();

		FunctionDeclarationParser.FunctionDefinitionVisitor fdefinitionVisitor = 
				new FunctionDeclarationParser(reporter,parserFactory.tokenStream()).new FunctionDefinitionVisitor();
		FunctionDefinition functionDef = fdefinitionVisitor.visit(parser.functionDefinition());
		
		functionDef.visit(new QNameSetterVisitor());
		
		return functionDef;
	}
	
	@Test
	@DisplayName("Simplest function (check name)")
	public void simplestFunction() {
		FunctionDefinition fd = parseTypeDefinition("void f() {}" /*, new QName("a", "b", "c")*/);
		assertEquals(fd.getNameString(), "f");
		assertEquals(fd.getqName().dotSeparated(), "sirius.default.f");
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
		
		// Check closures
		assertThat(fd.getClosure().getClosureEntries(), empty());
		assertThat(fd.getArgs(), hasSize(2));
		
		FunctionDefinition fd1 = fd.getFirstArgAppliedFunctionDef().get();
		assertThat(fd1.getClosure().getClosureEntries(), hasSize(1));
		assertThat(fd1.getArgs(), hasSize(1));
		
		assertThat(fd1.getClosure().getClosureEntries().get(0).getName().getText(), is("a"));
		assertThat(fd1.getArgs().get(0).getName().getText(), is("b"));
		
		FunctionDefinition fd2 = fd1.getFirstArgAppliedFunctionDef().get();
		assertThat(fd2.getClosure().getClosureEntries(), hasSize(2));
		assertThat(fd2.getArgs(), empty());

		assertThat(fd2.getClosure().getClosureEntries()
				.stream()
				.map(ClosureElement::getName)
				.map(AstToken::getText).toList(),
				contains("a", "b"));

		
		
		assertThat(fd2.getFirstArgAppliedFunctionDef().isEmpty(), is(true));
		
		// Most close
		assertThat(fd.mostClosed().getClosure().getClosureEntries(), hasSize(2));
		assertThat(fd.mostClosed().getArgs(), empty());
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
		List<AstStatement> bodyStatements = fd.getAllArgsPartial().getBodyStatements();
		
		assertThat(bodyStatements.size(), is(2));
	}

	@Test
	@DisplayName("Function declaration (without body)")
	public void functionWithoutBodyStatements() {
		FunctionDeclaration fd = parseTypeDeclaration("void f()" /*, new QName()*/);
		
//		assertThat(fd.getAllArgsPartial().getBodyStatements().isPresent(), is(false));
	}

	@Test
	@DisplayName("Function declaration with no-arg annotations: check annotations are parsed")
	public void functionWithAnnotaionsAreParsed() {
		FunctionDeclaration fd = parseTypeDeclaration("anno0 anno1 void f()" /*, new QName()*/);
//		assertThat(fd.getAnnotationList().getAnnotations().toArray().length, is(2));
		assertThat(fd.getAnnotationList().getAnnotations()
				.stream()
				.map(Annotation::getName)
				.map(AstToken::getText)
				.toList(), 
				contains("anno0", "anno1"));
	}
	@Test
	@DisplayName("Function definition with no-arg annotations: check annotations are parsed")
	public void functionDefinitionWithAnnotaionsAreParsed() {
		FunctionDefinition fd = parseTypeDefinition("anno0 anno1 void f() {}");
//		assertThat(fd.getAnnotationList().getAnnotations().toArray().length, is(2));
		// -- Check AST
		assertThat(fd.getAnnotationList().getAnnotations().stream()
				.map(anno -> anno.getName().getText()).toArray(), 
				is(new String[] {"anno0", "anno1"}));

		assertThat(fd.getAnnotationList().getAnnotations().stream()
				.map( Annotation::getName)
				.map( AstToken::getText)
				.toList(), 
				contains("anno0", "anno1"));
		
		// -- Check API

		assertThat(fd.getAllArgsPartial().getAnnotationList().getAnnotations().stream()
				.map(Annotation::getName)
				.map(AstToken::getText)
				.toList(),
				contains("anno0", "anno1"));
	}
}
