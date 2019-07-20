package org.sirius.frontend.ast;

import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.GlobalSymbolTable;

public class AstFactory {

	private Reporter reporter;
	private DefaultSymbolTable globalSymbolTable;
	
	public AstFactory(Reporter reporter, DefaultSymbolTable globalSymbolTable) {
		super();
		this.reporter = reporter;
		this.globalSymbolTable = globalSymbolTable;
	}

	// -- Types
	public AstClassDeclaration createClassDeclaration(Token name/*, PackageDeclaration packageDeclaration*/) {
		return new AstClassDeclaration(reporter, false /*asInterface*/, new AstToken(name));
	}

	public AstClassDeclaration createInterfaceDeclaration(Token name /*, PackageDeclaration packageDeclaration*/) {
		return new AstClassDeclaration(reporter, true /*asInterface*/, new AstToken(name));
	}

	public TypeFormalParameterDeclaration createTypeFormalParameter(Variance variance, Token formalName) {
		return new TypeFormalParameterDeclaration(variance, new AstToken(formalName));
	}
	
	public SimpleType createSimpleType(Token name) {
		return new SimpleType(new AstToken(name));
	}

	public UnionType createUnionType(AstType first, AstType second) {
		return new UnionType(first, second);
	}
	public IntersectionType createIntersectionType(AstType first, AstType second) {
		return new IntersectionType(first, second);
	}
	
	public AstArrayType createArray(AstType element) {
		return new AstArrayType(element);
	}
	
	
	public AstFunctionDeclaration createFunctionDeclaration(AnnotationList annotationList, Token name, AstType returnType) {
		return new AstFunctionDeclaration(reporter, annotationList, new AstToken(name), returnType);
	}

	public StandardCompilationUnit createStandardCompilationUnit() {
		return new StandardCompilationUnit(reporter, globalSymbolTable);
	}
	public ScriptCompilationUnit createScriptCompilationUnit() {
		return new ScriptCompilationUnit(reporter, globalSymbolTable);
	}
	public ModuleDescriptor createModuleDescriptorCompilationUnit(AstModuleDeclaration moduleDeclaration) {
		return new ModuleDescriptor(reporter, moduleDeclaration);
	}
	public PackageDescriptorCompilationUnit createPackageDescriptorCompilationUnit(AstPackageDeclaration packageDeclaration) {
		return new PackageDescriptorCompilationUnit(reporter, packageDeclaration);
	}
	

	
	public AstPackageDeclaration createPackageDeclaration(QualifiedName qname) {
		return new AstPackageDeclaration(reporter, qname.toQName());
	}
	public AstPackageDeclaration createPackageDeclaration() {
		return new AstPackageDeclaration(reporter);
	}

	public AstModuleDeclaration createModuleDeclaration() {
		return new AstModuleDeclaration(reporter);
	}
	
	public ImportDeclaration createImportDeclaration(QualifiedName pack) {
		return new ImportDeclaration(reporter, pack);
	}
	
	public ImportDeclarationElement createImportDeclarationElement(Token importedTypeName, Optional<Token> alias) {
		return new ImportDeclarationElement(importedTypeName, alias);
	}

	// -- Expressions
	public AstStringConstantExpression stringConstant(Token value) {
		return new AstStringConstantExpression(new AstToken(value));
	}
	public AstIntegerConstantExpression integerConstant(Token value) {
		return new AstIntegerConstantExpression(new AstToken(value), reporter);
	}
	public AstFloatConstantExpression floatConstant(Token value) {
		return new AstFloatConstantExpression(new AstToken(value));
	}
	public AstBooleanConstantExpression booleanConstant(Token value) {
		return new AstBooleanConstantExpression(new AstToken(value));
	}
	public AstFunctionCallExpression functionCall(Token funcName) {
		return new AstFunctionCallExpression(reporter, funcName);
	}

	// -- Values
	public AstValueDeclaration valueDeclaration(AnnotationList annotationList, AstType type, Token name) {
		return new AstValueDeclaration(annotationList, type, new AstToken(name));
	}
	
	// -- Annotations
	public Annotation annotation(Token name) {
		return new Annotation(new AstToken(name));
	}
	
}
