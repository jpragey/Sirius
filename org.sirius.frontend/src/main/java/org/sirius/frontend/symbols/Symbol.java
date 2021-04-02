package org.sirius.frontend.symbols;

import java.util.Optional;

import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionArgument;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.TypeParameter;

public class Symbol {

	private AstToken name;

	private Optional<AstClassDeclaration> classDeclaration = Optional.empty();
	private Optional<AstInterfaceDeclaration> interfaceDeclaration = Optional.empty();
	private Optional<TypeParameter> formalParameterDeclaration = Optional.empty();
	private Optional<FunctionDefinition> functionDeclaration = Optional.empty();
	private Optional<AstFunctionArgument> functionArgument = Optional.empty();

	private Optional<AstMemberValueDeclaration> valueDeclaration = Optional.empty();
	private Optional<ImportedSymbol> importDeclaration = Optional.empty();
	private Optional<AstLocalVariableStatement> localVariableDeclaration = Optional.empty();
	
	
	public Symbol(AstToken name, AstClassDeclaration classDeclaration) {
		super();
		assert(name != null);
		this.name = name;
		this.classDeclaration = Optional.of(classDeclaration);
	}
	
	public Symbol(AstToken name, AstInterfaceDeclaration interfaceDeclaration) {
		super();
		assert(name != null);
		this.name = name;
		this.interfaceDeclaration = Optional.of(interfaceDeclaration);
	}
	
	public Symbol(AstToken name, TypeParameter declaration) {
		super();
		assert(name != null);
		this.name = name;
		this.formalParameterDeclaration = Optional.of(declaration);
	}
	
	public Symbol(AstToken name, FunctionDefinition declaration) {
		super();
		assert(name != null);
		this.name = name;
		this.functionDeclaration = Optional.of(declaration);
	}

	public Symbol(AstToken name, AstMemberValueDeclaration argument) {
		super();
		assert(name != null);
		this.name = name;
		this.valueDeclaration = Optional.of(argument);
	}

	public Symbol(AstToken name, AstFunctionArgument argument) {
		super();
		assert(name != null);
		this.name = name;
		this.functionArgument = Optional.of(argument);
	}

	public Symbol(AstToken name, ImportedSymbol argument) {
		super();
		assert(name != null);
		this.name = name;
		this.importDeclaration = Optional.of(argument);
	}
	public Symbol(AstToken name, AstLocalVariableStatement argument) {
		super();
		assert(name != null);
		this.name = name;
		this.localVariableDeclaration = Optional.of(argument);
	}
	
	public AstToken getName() {
		return name;
	}

	public Optional<AstClassDeclaration> getClassDeclaration() {
		return classDeclaration;
	}

	public Optional<AstInterfaceDeclaration> getInterfaceDeclaration() {
		return interfaceDeclaration;
	}

	public Optional<TypeParameter> getFormalParameterDeclaration() {
		return formalParameterDeclaration;
	}
	
	public Optional<FunctionDefinition> getFunctionDeclaration() {
		return functionDeclaration;
	}
	
	public Optional<AstMemberValueDeclaration> getValueDeclaration() {
		return valueDeclaration;
	}
	
	public Optional<ImportedSymbol> getImportDeclaration() {
		return importDeclaration;
	}

	public Optional<AstLocalVariableStatement> getLocalVariableStatement() {
		return localVariableDeclaration;
	}

	public Optional<AstFunctionArgument> getFunctionArgument() {
		return functionArgument;
	}
	
	@Override
	public String toString() {
		
		return name.getText() + ":" + 
				(classDeclaration.isPresent() 			? classDeclaration.get().toString() : "") +
				(interfaceDeclaration.isPresent() 		? interfaceDeclaration.get().toString() : "") +
				(formalParameterDeclaration.isPresent() ? formalParameterDeclaration.get().toString() : "") +
				(functionDeclaration.isPresent() 		? functionDeclaration.get().getName().getText() : "") +
				(functionArgument.isPresent() 			? functionArgument.toString() : "") +
				(valueDeclaration.isPresent() 			? valueDeclaration.toString() : "")+
				(importDeclaration.isPresent() 			? importDeclaration.toString() : "")
				;
	}
}
