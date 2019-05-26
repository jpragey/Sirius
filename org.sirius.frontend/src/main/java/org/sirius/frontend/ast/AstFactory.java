package org.sirius.frontend.ast;

import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.symbols.GlobalSymbolTable;

public class AstFactory {

	private Reporter reporter;
	private GlobalSymbolTable globalSymbolTable;
	
	public AstFactory(Reporter reporter, GlobalSymbolTable globalSymbolTable) {
		super();
		this.reporter = reporter;
		this.globalSymbolTable = globalSymbolTable;
	}

	public ClassDeclaration createClassDeclaration(Token name/*, PackageDeclaration packageDeclaration*/) {
		return new ClassDeclaration(reporter, false /*asInterface*/, new AstToken(name));
	}

	public ClassDeclaration createInterfaceDeclaration(Token name /*, PackageDeclaration packageDeclaration*/) {
		return new ClassDeclaration(reporter, true /*asInterface*/, new AstToken(name));
	}

	public TypeFormalParameterDeclaration createTypeFormalParameter(Variance variance, Token formalName) {
		return new TypeFormalParameterDeclaration(variance, new AstToken(formalName));
	}
	
	public SimpleType createSimpleType(Token name) {
		return new SimpleType(new AstToken(name));
	}

	public UnionType createUnionType(Type first, Type second) {
		return new UnionType(first, second);
	}
	public IntersectionType createIntersectionType(Type first, Type second) {
		return new IntersectionType(first, second);
	}
	
	public FunctionDeclaration createFunctionDeclaration(Token name, Type returnType) {
		return new FunctionDeclaration(reporter, new AstToken(name), returnType);
	}

	public StandardCompilationUnit createStandardCompilationUnit() {
		return new StandardCompilationUnit(reporter, globalSymbolTable);
	}
	public ScriptCompilationUnit createScriptCompilationUnit() {
		return new ScriptCompilationUnit(reporter);
	}
	public ModuleDescriptor createModuleDescriptorCompilationUnit(ModuleDeclaration moduleDeclaration) {
		return new ModuleDescriptor(reporter, moduleDeclaration);
	}
	public PackageDescriptorCompilationUnit createPackageDescriptorCompilationUnit(PackageDeclaration packageDeclaration) {
		return new PackageDescriptorCompilationUnit(reporter, packageDeclaration);
	}
	

	
	public PackageDeclaration createPackageDeclaration() {
		return new PackageDeclaration(reporter);
	}

	public ModuleDeclaration createModuleDeclaration() {
		return new ModuleDeclaration(reporter);
	}
	
	public ImportDeclaration createImportDeclaration(QName pack) {
		return new ImportDeclaration(reporter, pack);
	}
	
	public ImportDeclarationElement createImportDeclarationElement(Token importedTypeName, Optional<Token> alias) {
		return new ImportDeclarationElement(importedTypeName, alias);
	}

	// -- Expressions
	public StringConstantExpression stringConstant(Token value) {
		return new StringConstantExpression(new AstToken(value));
	}
	public IntegerConstantExpression integerConstant(Token value) {
		return new IntegerConstantExpression(new AstToken(value));
	}
	public FloatConstantExpression floatConstant(Token value) {
		return new FloatConstantExpression(new AstToken(value));
	}
	public BooleanConstantExpression booleanConstant(Token value) {
		return new BooleanConstantExpression(new AstToken(value));
	}
	
//	/** {type *} */ 
//	public ClassDeclaration createIterable(Token startBracket,  Type type) {
//		ClassDeclaration cd = new ClassDeclaration(reporter, true /*is interface*/, startBracket);
//		cd.se
//		return cd;
//	}
	
}
