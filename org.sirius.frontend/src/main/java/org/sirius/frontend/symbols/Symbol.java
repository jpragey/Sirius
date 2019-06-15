package org.sirius.frontend.symbols;

import java.util.Optional;

import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.ClassDeclaration;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.FunctionFormalArgument;
import org.sirius.frontend.ast.TypeFormalParameterDeclaration;
import org.sirius.frontend.ast.ValueDeclaration;

public class Symbol {

	private AstToken name;

	private Optional<ClassDeclaration> classDeclaration = Optional.empty();
	private Optional<TypeFormalParameterDeclaration> formalParameterDeclaration = Optional.empty();
	private Optional<FunctionDeclaration> functionDeclaration = Optional.empty();
	private Optional<FunctionFormalArgument> functionArgument = Optional.empty();

	private Optional<ValueDeclaration> valueDeclaration = Optional.empty();

	public Symbol(AstToken name, ClassDeclaration classDeclaration) {
		super();
		this.name = name;
		this.classDeclaration = Optional.of(classDeclaration);
	}
	
	public Symbol(AstToken name, TypeFormalParameterDeclaration declaration) {
		super();
		this.name = name;
		this.formalParameterDeclaration = Optional.of(declaration);
	}
	
	public Symbol(AstToken name, FunctionDeclaration declaration) {
		super();
		this.name = name;
		this.functionDeclaration = Optional.of(declaration);
	}

	public Symbol(AstToken name, ValueDeclaration argument) {
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

	public Optional<ClassDeclaration> getClassDeclaration() {
		return classDeclaration;
	}

	public Optional<TypeFormalParameterDeclaration> getFormalParameterDeclaration() {
		return formalParameterDeclaration;
	}
	
	public Optional<FunctionDeclaration> getFunctionDeclaration() {
		return functionDeclaration;
	}
	
	public Optional<ValueDeclaration> getValueDeclaration() {
		return valueDeclaration;
	}
	
}
