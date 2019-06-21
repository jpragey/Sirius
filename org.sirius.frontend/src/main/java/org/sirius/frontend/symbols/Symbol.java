package org.sirius.frontend.symbols;

import java.util.Optional;

import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstValueDeclaration;
import org.sirius.frontend.ast.FunctionFormalArgument;
import org.sirius.frontend.ast.TypeFormalParameterDeclaration;

public class Symbol {

	private AstToken name;

	private Optional<AstClassDeclaration> classDeclaration = Optional.empty();
	private Optional<TypeFormalParameterDeclaration> formalParameterDeclaration = Optional.empty();
	private Optional<AstFunctionDeclaration> functionDeclaration = Optional.empty();
	private Optional<FunctionFormalArgument> functionArgument = Optional.empty();

	private Optional<AstValueDeclaration> valueDeclaration = Optional.empty();

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

	public Symbol(AstToken name, FunctionFormalArgument argument) {
		super();
		this.name = name;
		this.functionArgument = Optional.of(argument);
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
	
}
