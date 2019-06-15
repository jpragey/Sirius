package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.symbols.LocalSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

public class ClassDeclaration implements Type, Scoped, Visitable {

	private AstToken name;
	
	// Formal parameters
	private List<TypeFormalParameterDeclaration> typeParameters = new ArrayList<>();
	
	private List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
	private List<ValueDeclaration> valueDeclarations = new ArrayList<>();

	/** Root package at first */
	private PackageDeclaration packageDeclaration;
	
	/** True for annotation classes (ConstrainedAnnotation subtypes, ie OptionalAnnotation or SequencedAnnotation) */
	private boolean annotationType = false; 
	
//	public enum Type {Interface, Class}
	
	private boolean interfaceType;
	
	private LocalSymbolTable symbolTable /*= new SymbolTable()*/; 
	
	private Reporter reporter;
	
	public ClassDeclaration(Reporter reporter, boolean interfaceType, AstToken name/*, PackageDeclaration packageDeclaration*/) {
		super();
		this.reporter = reporter;
		this.interfaceType = interfaceType;
		this.name = name;
		this.packageDeclaration = new PackageDeclaration(reporter);
		this.symbolTable = new LocalSymbolTable(reporter);
		
//		this.symbolTable.addClass(name, this);
		
	}
	public static ClassDeclaration newClass(Reporter reporter, AstToken name) {
		return new ClassDeclaration (reporter, false /*interfaceType */ , name);
	}
	public static ClassDeclaration newInterface(Reporter reporter, AstToken name) {
		return new ClassDeclaration (reporter, true /*interfaceType */ , name);
	}
	
	public ClassDeclaration(Reporter reporter, boolean interfaceType, Token name/*, PackageDeclaration packageDeclaration*/) {
		this(reporter, interfaceType, new AstToken(name));
	}
	
	public AstToken getName() {
		return name;
	}
	public List<FunctionDeclaration> getFunctionDeclarations() {
		return functionDeclarations;
	}
	
	public void addFunctionDeclaration(FunctionDeclaration declaration) {
		this.functionDeclarations.add(declaration);
		this.symbolTable.addFunction(declaration.getName(), declaration);
	}
	public void addValueDeclaration(ValueDeclaration valueDeclaration) {
		this.valueDeclarations.add(valueDeclaration);
		// TODO: add to symbol table
	}
	
	
	public PackageDeclaration getPackageDeclaration() {
		assert(this.packageDeclaration != null);
		return packageDeclaration;
	}
	
	
	public void setPackageDeclaration(PackageDeclaration packageDeclaration) {
		this.packageDeclaration = packageDeclaration;
	}
	public void addTypeParameterDeclaration(TypeFormalParameterDeclaration d) {
		typeParameters.add(d);
		this.symbolTable.addFormalParameter(d.getFormalName(), d);
	}

	
	public boolean isInterfaceType() {
		return interfaceType;
	}
	public String getQname() {
		String pkgQname = packageDeclaration.getQnameString();
		String classname = name.getText();
		return pkgQname.isEmpty() ? classname : pkgQname + "." + classname;
	}

	public void visit(AstVisitor visitor) {
		visitor.startClassDeclaration(this);
		functionDeclarations.stream().forEach(fd -> fd.visit(visitor));
		visitor.endClassDeclaration(this);
	}
	
	@Override
	public LocalSymbolTable getSymbolTable() {
		return symbolTable;
	}
	public void setSymbolTableParent(SymbolTable newParent) {
		this.symbolTable.setParentSymbolTable(newParent);
	}
	public List<TypeFormalParameterDeclaration> getTypeParameters() {
		return typeParameters;
	}
	
	private List<FunctionFormalArgument> anonConstructorArguments = new ArrayList<>(); 
	
	public void addAnonConstructorArgument(FunctionFormalArgument argument) {
		this.anonConstructorArguments.add(argument);
	}

	public List<FunctionFormalArgument> getAnonConstructorArguments() {
		return anonConstructorArguments;
	}

	public Optional<Type> apply(Type parameter) {
		TypeFormalParameterDeclaration formalParam = typeParameters.get(0);
		if(formalParam == null) {
			reporter.error("Can't apply type " + parameter.messageStr() + " to class/interface " + messageStr() + ", it has no formal parameter." );
			return Optional.empty();
		}
		
		ClassDeclaration cd = new ClassDeclaration(reporter, interfaceType, name);
		
		cd.typeParameters.addAll(typeParameters.subList(1, typeParameters.size()));
	
//		for(FunctionDeclaration fd: functionDeclarations) {
//			cd.addFunctionDeclaration(fd.apply(parameter));
//		}
		
		return Optional.ofNullable(cd);
	}
	
	@Override
	public String messageStr() {
		
		List<String> typeParams = typeParameters.stream().map(p -> p.messageStr()).collect(Collectors.toList());
		
		return "class " + 
				name.getText() + 
				"<" + 
				String.join(",", typeParams) + 
				">";
	}
	
	@Override
	public String toString() {
		return "class " + name.getText();
	}
	public boolean isAnnotationType() {
		return annotationType;
	}
	public void setAnnotationType(boolean annotationType) {
		this.annotationType = annotationType;
	}

}
