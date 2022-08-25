package org.sirius.frontend.core.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.ParseTree;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.FunctionDeclaration;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.ast.Variance;
import org.sirius.frontend.core.parser.TypeParameterParser.TypeParameterVisitor;
import org.sirius.frontend.parser.Sirius;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.*;

public class ClassDeclarationParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter();
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private AstClassDeclaration parseClassDeclaration(String inputText/*, QName containerQName*/) {
		
		ParserBuilder parserFactory = ParserUtil.createParserBuilder(reporter, inputText);
		Sirius parser = parserFactory.create();
		ParseTree tree = parser.classDeclaration();
				
		ClassDeclarationParser.ClassDeclarationVisitor visitor = new ClassDeclarationParser(reporter, parserFactory.tokenStream()).new ClassDeclarationVisitor();
		AstClassDeclaration classDeclaration = visitor.visit(tree);
		return classDeclaration;
	}
	
	@Test
	@DisplayName("Simplest class declarations")
	public void simplestClassDeclarations() {
		AstClassDeclaration myClass = parseClassDeclaration("class I(){}");
		
		assertEquals(myClass.getNameText(), "I");

	}

	@Test
	@DisplayName("Class declarations with type parameters")
	public void classDeclarationsWithTypeParameters() {
		AstClassDeclaration myClass = parseClassDeclaration("class C()<T0,T1,T2>{}");

		assertThat(myClass.getTypeParameters().stream()
				.map(TypeParameter::getNameString)
				.toList(), 
				contains("T0", "T1", "T2"));
	}
	
	@Test
	@DisplayName("Class declarations satisfying interfaces")
	public void classDeclarationsImplementingInterfaces() {
		AstClassDeclaration myClass = parseClassDeclaration("class I() implements I0, I1, I2 {}" /*, new QName ()*/);

		assertThat(myClass.getAncestors().stream()
				.map(AstToken::getText)
				.toList(), 
				contains("I0", "I1", "I2"));
	}

	@Test
	@DisplayName("Class with methods")
	public void classDeclarationsContainingMethods() {
		AstClassDeclaration myInterface = parseClassDeclaration("class C() {void f(){} void g(){} }" /*, new QName ()*/);

		assertThat(myInterface.getFunctionDeclarations(), empty());
		
		assertThat(myInterface.getFunctionDefinitions().stream()
				.map(FunctionDefinition::getName)
				.map(AstToken::getText)
				.toList(),
				contains("f", "g"));
	}
	@Test
	@DisplayName("Class with values")
	public void classHavingValues() {
		AstClassDeclaration myClass = parseClassDeclaration("class C() {Integer v0; Integer v1;}" /*, new QName ()*/);

		assertThat(myClass.getValueDeclarations(), hasSize(2));
	}

}
