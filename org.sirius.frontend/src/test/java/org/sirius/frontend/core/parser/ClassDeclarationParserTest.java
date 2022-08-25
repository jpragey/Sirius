package org.sirius.frontend.core.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;

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
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.ast.Variance;
import org.sirius.frontend.core.parser.TypeParameterParser.TypeParameterVisitor;
import org.sirius.frontend.parser.Sirius;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClassDeclarationParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private AstClassDeclaration parseClassDeclaration(String inputText/*, QName containerQName*/) {
		
		ParserUtil.ParserFactory parserFactory = ParserUtil.createParserFactory(reporter, inputText);
		Sirius parser = parserFactory.create();
		ParseTree tree = parser.classDeclaration();
				
		ClassDeclarationParser.ClassDeclarationVisitor visitor = new ClassDeclarationParser(reporter, parserFactory.tokenStream()).new ClassDeclarationVisitor();
		AstClassDeclaration classDeclaration = visitor.visit(tree);
		return classDeclaration;
	}
	
	@Test
	@DisplayName("Simplest class declarations")
	public void simplestClassDeclarations() {
		simplestTypecheck("class I(){}");
	}
	
	public void simplestTypecheck(String inputText) {
//		QName containerQName = new QName ("a", "b", "c");
		AstClassDeclaration myClass = parseClassDeclaration(inputText/*, containerQName*/);
		//TypeParameter typeParameter = (TypeParameter)myType;
		
		assertEquals(myClass.getName().getText(), "I");
//		assertEquals(typeParameter.getVariance(), variance);
//		assertTrue  (typeParameter.getDefaultType().isEmpty());
	}

	@Test
	@DisplayName("Class declarations with type parameters")
	public void classDeclarationsWithTypeParameters() {
		AstClassDeclaration myClass = parseClassDeclaration("class C()<T0,T1,T2>{}");

		assertEquals(myClass.getTypeParameters().size(), 3);
		assertThat(myClass.getTypeParameters().stream()
				.map(typeParam -> typeParam.getNameString())
				.toArray(), 
				is(new String[]{"T0", "T1", "T2"}));
	}
	
	@Test
	@DisplayName("Class declarations satisfying interfaces")
	public void classDeclarationsImplementingInterfaces() {
		AstClassDeclaration myClass = parseClassDeclaration("class I() implements I0, I1, I2 {}" /*, new QName ()*/);

//		assertEquals(myClass.getAncestors().size(), 3);
		
		assertThat(myClass.getAncestors().stream()
				.map(interf -> interf.getText())
				.toArray(), 
				is(new String[]{"I0", "I1", "I2"}));
	}

	@Test
	@DisplayName("Class with methods")
	public void classDeclarationsContainingMethods() {
		AstClassDeclaration myInterface = parseClassDeclaration("class C() {void f(){} void g(){} }" /*, new QName ()*/);

		assertEquals(myInterface.getFunctionDeclarations().size(), 0);
		assertEquals(myInterface.getFunctionDefinitions().size(), 2);
	}
	@Test
	@DisplayName("Class with values")
	public void classHavingValues() {
		AstClassDeclaration myClass = parseClassDeclaration("class C() {Integer v0; Integer v1;}" /*, new QName ()*/);

		assertEquals(myClass.getValueDeclarations().size(), 2);
	}

}
