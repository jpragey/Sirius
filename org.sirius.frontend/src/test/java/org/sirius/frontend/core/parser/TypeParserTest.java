package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstArrayType;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.IntersectionType;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.ast.UnionType;
import org.sirius.frontend.core.parser.TypeParser.TypeVisitor;
import org.sirius.frontend.parser.Sirius;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TypeParserTest {

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
		
		Sirius parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.type();
				
		TypeVisitor typeVisitor = new TypeVisitor(reporter);
		AstType myType = typeVisitor.visit(tree);
		return myType;
	}
	
	@Test
	@DisplayName("Type made of a simple ID, without type parameter")
	public void simplestType() {
		AstType myType = parseTypeDeclaration("SomeType");
		assert(myType instanceof SimpleType);
		assertEquals(((SimpleType)myType).getName().getText(), "SomeType");
	}
	
	@Test
	@DisplayName("Type made of an ID with 3 type parameters")
	public void simpleTypeWith3Parameters() {
		AstType myAbstractType = parseTypeDeclaration("SomeType<Type0,Type1,Type2>");
		
		assert(myAbstractType instanceof SimpleType);
		SimpleType myType =(SimpleType)myAbstractType;
		
		assertEquals(myType.getName().getText(), "SomeType");
		
		assertThat(myType.getTypeParameters().stream()
				.map(myt -> ((SimpleType)myt).getNameString())
				.toArray(), 
				is(new String[]{"Type0","Type1","Type2"}));
	}

	@Test
	@DisplayName("Simple union of 3 types")
	public void simpleUnionType() {
		AstType myAbstractType = parseTypeDeclaration("Type0 | Type1 | Type2");
		
		assert(myAbstractType instanceof UnionType);
		UnionType myType =(UnionType)myAbstractType;
		
		UnionType leftUnionType = (UnionType)myType.getFirst();

		assertEquals( ((SimpleType)leftUnionType.getFirst()).getNameString(), "Type0");
		assertEquals( ((SimpleType)leftUnionType.getSecond()).getNameString(), "Type1");

		assertEquals( ((SimpleType)myType.getSecond()).getNameString(), "Type2");
	}

	@Test
	@DisplayName("Simple intersection of 3 types")
	public void simpleIntersectionType() {
		AstType myAbstractType = parseTypeDeclaration("Type0 & Type1 & Type2");
		
		assertThat(myAbstractType, instanceOf(IntersectionType.class));
//		assert(myAbstractType instanceof IntersectionType);
		IntersectionType myType =(IntersectionType)myAbstractType;
		
		IntersectionType leftIntersectionType = (IntersectionType)myType.getFirst();

		assertEquals( ((SimpleType)leftIntersectionType.getFirst()).getNameString(), "Type0");
		assertEquals( ((SimpleType)leftIntersectionType.getSecond()).getNameString(), "Type1");

		assertEquals( ((SimpleType)myType.getSecond()).getNameString(), "Type2");
	}

	@Test
	@DisplayName("Simple bracketed types ( '<SomeType>' ")
	public void simpleBracketedType() {
		AstType myAbstractType = parseTypeDeclaration("<Type0>");
		
		assert(myAbstractType instanceof SimpleType);
		SimpleType myType =(SimpleType)myAbstractType;
		assertEquals(myType.getNameString(), "Type0");
	}
	
	//@Test
	@ParameterizedTest
	@DisplayName("Simple array types ( 'SomeType []' ")
	@ValueSource(strings = {"SomeType []", "<SomeType> []", "<<SomeType>> []" })
	public void simpleArrayType(String inputText) {
		AstType myAbstractType = parseTypeDeclaration(inputText);
		
		assert(myAbstractType instanceof AstArrayType);
		assert( ((AstArrayType)myAbstractType).getElementType() instanceof SimpleType);
		SimpleType myType = (SimpleType)(((AstArrayType)myAbstractType).getElementType());
		
		assertEquals(myType.getNameString(), "SomeType");
	}

}
