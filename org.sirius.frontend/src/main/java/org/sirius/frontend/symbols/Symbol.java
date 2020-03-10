package org.sirius.frontend.symbols;

import java.util.Optional;

import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstValueDeclaration;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.AstFunctionFormalArgument;
import org.sirius.frontend.ast.TypeFormalParameterDeclaration;

public class Symbol {

	private AstToken name;

	private Optional<AstClassDeclaration> classDeclaration = Optional.empty();
	private Optional<TypeFormalParameterDeclaration> formalParameterDeclaration = Optional.empty();
	private Optional<AstFunctionDeclaration> functionDeclaration = Optional.empty();
	private Optional<AstFunctionFormalArgument> functionArgument = Optional.empty();

	private Optional<AstValueDeclaration> valueDeclaration = Optional.empty();
	private Optional<ImportedSymbol> importDeclaration = Optional.empty();

	public Symbol(AstToken name, AstClassDeclaration classDeclaration) {
		super();
		this.name = name;
		this.classDeclaration = Optional.of(classDeclaration);
	}
	
	public Symbol(AstToken name, TypeFormalParameterDeclaration declaration) {
		super();
		this.name = name;
		this.formalParameterDeclaration = Optional.of(declaration);
	}
	
	public Symbol(AstToken name, AstFunctionDeclaration declaration) {
		super();
		this.name = name;
		this.functionDeclaration = Optional.of(declaration);
	}

	public Symbol(AstToken name, AstValueDeclaration argument) {
		super();
		this.name = name;
		this.valueDeclaration = Optional.of(argument);
	}

	public Symbol(AstToken name, AstFunctionFormalArgument argument) {
		super();
		this.name = name;
		this.functionArgument = Optional.of(argument);
	}

	public Symbol(AstToken name, ImportedSymbol argument) {
		super();
		this.name = name;
		this.importDeclaration = Optional.of(argument);
	}

	public AstToken getName() {
		return name;
	}

	public Optional<AstClassDeclaration> getClassDeclaration() {
		return classDeclaration;
	}

	public Optional<TypeFormalParameterDeclaration> getFormalParameterDeclaration() {
		return formalParameterDeclaration;
	}
	
	public Optional<AstFunctionDeclaration> getFunctionDeclaration() {
		return functionDeclaration;
	}
	
	public Optional<AstValueDeclaration> getValueDeclaration() {
		return valueDeclaration;
	}
	
	public Optional<ImportedSymbol> getImportDeclaration() {
		return importDeclaration;
	}

	@Override
	public String toString() {
		
		return name.getText() + ":" + 
				(classDeclaration.isPresent() 			? classDeclaration.get().toString() : "") +
				(formalParameterDeclaration.isPresent() ? formalParameterDeclaration.get().toString() : "") +
				(functionDeclaration.isPresent() ? functionDeclaration.toString() : "") +
				(functionArgument.isPresent() ? functionArgument.toString() : "") +
				(valueDeclaration.isPresent() ? valueDeclaration.toString() : "")+
				(importDeclaration.isPresent() ? importDeclaration.toString() : "")
				;
	}
}
