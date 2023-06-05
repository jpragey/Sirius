package org.sirius.frontend.core.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.ast.Variance;
import org.sirius.frontend.core.parser.TypeParameterParser.TypeParameterVisitor;
import org.sirius.frontend.parser.SParser;

public class TypeParameterParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private AstType parseTypeDeclaration(String inputText) {
		
		SParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.typeParameterDeclaration();
				
		TypeParameterVisitor typeVisitor = new TypeParameterParser(reporter).new TypeParameterVisitor();
		AstType myType = typeVisitor.visit(tree);
		return myType;
	}
	
	@Test
	@DisplayName("Type ")
	public void simplestTypeParameter() {
		simplestTypecheck("in SomeParam",  Variance.IN);
		simplestTypecheck("out SomeParam", Variance.OUT);
		simplestTypecheck("SomeParam",     Variance.INVARIANT);
	}
	
	public void simplestTypecheck(String inputText, Variance variance) {
		AstType myType = parseTypeDeclaration(inputText);
		assert(myType instanceof TypeParameter);
		TypeParameter typeParameter = (TypeParameter)myType;
		
		assertEquals(typeParameter.getNameString(), "SomeParam");
		assertEquals(typeParameter.getVariance(), variance);
		assertTrue  (typeParameter.getDefaultType().isEmpty());
	}

	
	@Test
	@DisplayName("Type Parameter with '= SomeDefault' default")
	public void typeParameterWithDefaultValue() {
		String inputText = "SomeParam = SomeDefault";
		
		AstType myType = parseTypeDeclaration(inputText);
		assert(myType instanceof TypeParameter);
		TypeParameter typeParameter = (TypeParameter)myType;
		
		assertEquals(typeParameter.getNameString(), "SomeParam");
		
		AstType defType = typeParameter.getDefaultType().get();
		assertEquals( ((SimpleType)defType).getNameString(), "SomeDefault");
	}
	
}
