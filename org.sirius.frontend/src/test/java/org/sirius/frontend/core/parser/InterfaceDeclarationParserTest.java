package org.sirius.frontend.core.parser;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.ast.Variance;
import org.sirius.frontend.core.parser.TypeParameterParser.TypeParameterVisitor;
import org.sirius.frontend.parser.SiriusParser;

public class InterfaceDeclarationParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private AstInterfaceDeclaration parseInterfaceDeclaration(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.interfaceDeclaration();
				
		InterfaceDeclarationParser.InterfaceDeclarationVisitor visitor = new InterfaceDeclarationParser.InterfaceDeclarationVisitor(reporter);
		AstInterfaceDeclaration interfaceDeclaration = visitor.visit(tree);
		return interfaceDeclaration;
	}
	
	@Test
	@DisplayName("Simplest interface declarations")
	public void simplestInterfaceDeclarations() {
		simplestTypecheck("interface I{}");
//		simplestTypecheck("out SomeParam");
//		simplestTypecheck("SomeParam");
	}
	
	public void simplestTypecheck(String inputText) {
//		QName containerQName = new QName ("a", "b", "c");
		AstInterfaceDeclaration myInterface = parseInterfaceDeclaration(inputText);
		//TypeParameter typeParameter = (TypeParameter)myType;
		
		assertEquals(myInterface.getName().getText(), "I");
//		assertEquals(typeParameter.getVariance(), variance);
//		assertTrue  (typeParameter.getDefaultType().isEmpty());
	}

	@Test
	@DisplayName("Interface declarations with type parameters")
	public void interfaceDeclarationsWithTypeParameters() {
		AstInterfaceDeclaration myInterface = parseInterfaceDeclaration("interface I<T0,T1,T2>{}");

		assertEquals(myInterface.getTypeParameters().size(), 3);
		assertEquals(myInterface.getTypeParameters().stream()
				.map(typeParam -> typeParam.getNameString())
				.toArray(), 
				new String[]{"T0", "T1", "T2"});
	}
	
	@Test
	@DisplayName("Interface declarations satisfying interfaces")
	public void interfaceDeclarationsImplementingInterfaces() {
		AstInterfaceDeclaration myInterface = parseInterfaceDeclaration("interface I implements I0 {}");

		assertEquals(myInterface.getAncestors().size(), 1);
		
		assertEquals(myInterface.getAncestors().stream()
				.map(interf -> interf.getSimpleName().getText())
				.toArray(), 
				new String[]{"I0"});
	}

	@Test
	@DisplayName("Interface with methods")
	public void interfaceDeclarationsHavingMethods() {
		AstInterfaceDeclaration myInterface = parseInterfaceDeclaration("interface I {void f(){} void g(){} }");

		assertEquals(myInterface.getFunctionDeclarations().size(), 2);
	}
	@Test
	@DisplayName("Interface with values")
	public void interfaceHavingValues() {
		AstInterfaceDeclaration myInterface = parseInterfaceDeclaration("interface I {Integer v0; Integer v1;}");

		assertEquals(myInterface.getValueDeclarations().size(), 2);
	}

}
