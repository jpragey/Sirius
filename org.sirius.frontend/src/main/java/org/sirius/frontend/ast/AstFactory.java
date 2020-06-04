package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.symbols.DefaultSymbolTable;

import com.google.common.collect.ImmutableList;

public class AstFactory {

	private Reporter reporter;
	private DefaultSymbolTable globalSymbolTable;
	
	public AstFactory(Reporter reporter, DefaultSymbolTable globalSymbolTable) {
		super();
		this.reporter = reporter;
		this.globalSymbolTable = globalSymbolTable;
	}

	// -- Types

	public AstClassDeclaration createClassOrInterface(Token name, boolean isInterface, QName containerQName) {
		return new AstClassDeclaration(reporter,  isInterface, new AstToken(name), containerQName);
	}

	public AstInterfaceDeclaration createInterface(Token name, QName containerQName) {
		return new AstInterfaceDeclaration(reporter, new AstToken(name), Optional.of(containerQName));
	}

	public TypeParameter createTypeFormalParameter(Variance variance, Token formalName) {
		return new TypeParameter(variance, new AstToken(formalName));
	}
	
	public SimpleType createSimpleType(Token name) {
		return new SimpleType(reporter, new AstToken(name));
	}

	public ConstructorCallExpression createConstructorCall(Token name) {
		return new ConstructorCallExpression(reporter, new AstToken(name));
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
	
	
//	public AstFunctionDeclaration.Builder createFunctionDeclaration(AnnotationList annotationList, Token name, AstType returnType, boolean concrete, boolean member) {
	
	public AstFunctionDeclaration createFunctionDeclaration(AnnotationList annotationList, Token name, AstType returnType, QName containerQName) {
//		return new AstFunctionDeclaration(reporter, annotationList, new AstToken(name), returnType, concrete, member, new DefaultSymbolTable());
		return new AstFunctionDeclaration(
				reporter, 
				annotationList, 
				new AstToken(name), 
				returnType,
				ImmutableList.of(), //<TypeParameter> typeParameters,
				ImmutableList.of(), //<AstFunctionFormalArgument> formalArguments,
				containerQName,
				true, // concrete,
				true, //boolean member,
				null,	// DefaultSymbolTable symbolTable,
				new ArrayList<AstStatement>() // statements
				
//				reporter, annotationList, new AstToken(name), returnType, containerQName
				);
		
		
//		public AstFunctionDeclaration(Reporter reporter, AnnotationList annotationList, AstToken name, AstType returnType,
//				ImmutableList<TypeParameter> typeParameters,
//				ImmutableList<AstFunctionFormalArgument> formalArguments,
//				QName containerQName,
//				QName qName,
//				boolean concrete,
//				boolean member,
//				DefaultSymbolTable symbolTable,
//				List<AstStatement> statements
//				) {

		
		
	}

	public StandardCompilationUnit createStandardCompilationUnit() {
		return new StandardCompilationUnit(reporter, globalSymbolTable);
	}
	public ScriptCompilationUnit createScriptCompilationUnit(AstModuleDeclaration rootModule /* initially empty module*/) {
		return new ScriptCompilationUnit(reporter, globalSymbolTable, rootModule);
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

	public AstModuleDeclaration createModuleDeclaration(QualifiedName qualifiedName, Token version) {
		AstModuleDeclaration mod = new AstModuleDeclaration(reporter, qualifiedName.toQName(), version);
		return mod;
	}
	
	public ImportDeclaration createImportDeclaration(QualifiedName pack) {
		return new ImportDeclaration(reporter, pack);
	}
	
	
	public ImportDeclarationElement createImportDeclarationElement(Token importedTypeName, Token alias) {
		return new ImportDeclarationElement(importedTypeName, Optional.of(alias));
	}
	public ImportDeclarationElement createImportDeclarationElement(Token importedTypeName) {
		return new ImportDeclarationElement(importedTypeName, Optional.empty());
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

	
	public AstMemberAccessExpression valueAccess(AstExpression containerExpression, Token fieldName) {
		return new AstMemberAccessExpression(reporter, containerExpression, new AstToken(fieldName));
	}
	public SimpleReferenceExpression simpleReference(Token referenceName) {
		return new SimpleReferenceExpression(reporter, new AstToken(referenceName));
	}

	// -- Statements
	
	public AstLocalVariableStatement localVariableStatement(AnnotationList annotationList, AstType type, Token varName) {
		return new AstLocalVariableStatement(annotationList, type, new AstToken(varName));
	}

	public AstIfElseStatement ifElseStatement(AstExpression ifExpression, AstStatement ifBlock) {
		return new AstIfElseStatement(reporter, ifExpression, ifBlock);
	}

	// -- Values
	public AstMemberValueDeclaration valueDeclaration(AnnotationList annotationList, AstType type, Token name) {
		return new AstMemberValueDeclaration(annotationList, type, new AstToken(name));
	}
	
	// -- Annotations
	public Annotation annotation(Token name) {
		return new Annotation(new AstToken(name));
	}
	
}
