package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.Symbol;

public class AstClassDeclaration implements AstType, Scoped, Visitable {

	private AstToken name;
	private QName qName; 
	
	// Formal parameters
	private List<TypeFormalParameterDeclaration> typeParameters = new ArrayList<>();
	
	private List<AstFunctionDeclaration> functionDeclarations = new ArrayList<>();
	private List<AstValueDeclaration> valueDeclarations = new ArrayList<>();
	private List<AstFunctionFormalArgument> anonConstructorArguments = new ArrayList<>(); 

	/** Root package at first */
	private AstPackageDeclaration packageDeclaration;
	
	/** True for annotation classes (ConstrainedAnnotation subtypes, ie OptionalAnnotation or SequencedAnnotation) */
	private boolean annotationType = false; 
	
//	public enum Type {Interface, Class}
	
	private boolean interfaceType;
	
	/** directly implemented interfaces or extended classes */
//	private List<AstClassQName> ancestors = new ArrayList<>();
	private List<QName> ancestors = new ArrayList<>();

	private DefaultSymbolTable symbolTable /*= new SymbolTable()*/; 
	

	private Reporter reporter;
	
	public AstClassDeclaration(Reporter reporter, boolean interfaceType, AstToken name/*, PackageDeclaration packageDeclaration*/) {
		super();
		this.reporter = reporter;
		this.interfaceType = interfaceType;
		this.name = name;
		this.packageDeclaration = new AstPackageDeclaration(reporter);
//		this.symbolTable = new DefaultSymbolTable();	// TODO ???
		
//		this.symbolTable.addClass(name, this);
		
	}
	public static AstClassDeclaration newClass(Reporter reporter, AstToken name) {
		return new AstClassDeclaration (reporter, false /*interfaceType */ , name);
	}
	public static AstClassDeclaration newInterface(Reporter reporter, AstToken name) {
		return new AstClassDeclaration (reporter, true /*interfaceType */ , name);
	}

	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		if(symbolTable == null)
			throw new NullPointerException();
		
		this.symbolTable = symbolTable;

		for(TypeFormalParameterDeclaration d: typeParameters)
			this.symbolTable.addFormalParameter(this.qName, d);
	}

	public AstClassDeclaration(Reporter reporter, boolean interfaceType, Token name/*, PackageDeclaration packageDeclaration*/) {
		this(reporter, interfaceType, new AstToken(name));
	}
	
	public AstToken getName() {
		return name;
	}
	public List<AstFunctionDeclaration> getFunctionDeclarations() {
		return functionDeclarations;
	}
	
	public void addFunctionDeclaration(AstFunctionDeclaration declaration) {
		this.functionDeclarations.add(declaration);
//		this.symbolTable.addFunction(declaration);
	}
	public void addValueDeclaration(AstValueDeclaration valueDeclaration) {
		this.valueDeclarations.add(valueDeclaration);
		// TODO: add to symbol table
	}
	
	
	public AstPackageDeclaration getPackageDeclaration() {
		assert(this.packageDeclaration != null);
		return packageDeclaration;
	}
	
	
	public void setPackageDeclaration(AstPackageDeclaration packageDeclaration) {
		this.packageDeclaration = packageDeclaration;
		this.qName = packageDeclaration.getQname().child(this.name.getText());
	}
	public void addTypeParameterDeclaration(TypeFormalParameterDeclaration d) {
		typeParameters.add(d);
//		this.symbolTable.addFormalParameter(this.qName, d);
	}

	
	public boolean isInterfaceType() {
		return interfaceType;
	}
	public String getQname() {	// TODO: remove
		String pkgQname = packageDeclaration.getQnameString();
		String classname = name.getText();
		return pkgQname.isEmpty() ? classname : pkgQname + "." + classname;
	}
	public void setqName(QName qName) {
		this.qName = qName;
	}
	public QName getQName() {
		return this.qName;
	}

	public void visit(AstVisitor visitor) {
		visitor.startClassDeclaration(this);
		functionDeclarations.stream().forEach(fd -> fd.visit(visitor));
		valueDeclarations.stream().forEach(fd -> fd.visit(visitor));
		visitor.endClassDeclaration(this);
	}
	
	@Override
	public DefaultSymbolTable getSymbolTable() {
		return symbolTable;
	}
//	public void setSymbolTableParent(SymbolTable newParent) {
//		this.symbolTable.setParentSymbolTable(newParent);
//	}
	public List<TypeFormalParameterDeclaration> getTypeParameters() {
		return typeParameters;
	}
	
	
	public void addAnonConstructorArgument(AstFunctionFormalArgument argument) {
		this.anonConstructorArguments.add(argument);
	}

	public List<AstFunctionFormalArgument> getAnonConstructorArguments() {
		return anonConstructorArguments;
	}
	public List<QName> getAncestors() {
		return ancestors;
	}
	public void addAncestor(QName ancestor) {
		this.ancestors.add(ancestor);
	}
	public void addAncestor(QName packageQName, String className /* class or interface */ ) {
//		this.ancestors.add(new AstClassQName(packageQName, className));
		this.ancestors.add(packageQName.child(className));
	}

	public Optional<AstType> apply(AstType parameter) {
		TypeFormalParameterDeclaration formalParam = typeParameters.get(0);
		if(formalParam == null) {
			reporter.error("Can't apply type " + parameter.messageStr() + " to class/interface " + messageStr() + ", it has no formal parameter." );
			return Optional.empty();
		}
		
		AstClassDeclaration cd = new AstClassDeclaration(reporter, interfaceType, name);
		
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
	public boolean isExactlyA(AstType type) {
		if(type instanceof AstClassDeclaration) {
			AstClassDeclaration other = (AstClassDeclaration)type;
			if(this.interfaceType != other.interfaceType)
				return false;
			if(!this.qName.equals(other.qName))
				return false;
			
			return true;
		}
		return false;
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

	public ClassDeclaration getClassDeclaration(/* Containing package/class/interface qname */QName containerQName) {
		return new ClassDeclaration() {
			QName qName = containerQName.child(name.getText());
			@Override
			public List<MemberValue> getValues() {
				return valueDeclarations.stream()
					.map(v->v.getMemberValue())
					.filter(v ->v.isPresent())
					.map(v->v.get())
					.collect(Collectors.toList());
			}

			@Override
			public List<MemberFunction> getFunctions() {
				return functionDeclarations.stream()
						.map(AstFunctionDeclaration::getMemberFunction)
						.filter(fd -> fd.isPresent())
						.map(fd -> fd.get())
						.collect(Collectors.toList());
			}

			@Override
			public QName getQName() {
				return qName;
			}
		};
	}

	public InterfaceDeclaration getInterfaceDeclaration(/* Containing package/class/interface qname */QName containerQName) {
		return new InterfaceDeclaration() {
			QName qName = containerQName.child(name.getText());
			@Override
			public List<MemberValue> getValues() {
				return valueDeclarations.stream()
						.map(v->v.getMemberValue())
						.filter(v -> v.isPresent())
						.map(v->v.get())
						.collect(Collectors.toList());
			}

			@Override
			public List<MemberFunction> getFunctions() {
				return functionDeclarations.stream()
						.map(AstFunctionDeclaration::getMemberFunction)
						.filter(fd -> fd.isPresent())
						.map(fd -> fd.get())
						.collect(Collectors.toList());
			}
			@Override
			public QName getQName() {
				return qName;
			}
		};
	}

	@Override
	public boolean isAncestorOrSameAs(AstType type) {
		if(isExactlyA(type))
			return true;
		
		if(type instanceof AstClassDeclaration) {
			AstClassDeclaration otherClassDecl = (AstClassDeclaration)type;

			for(QName ancestorQName: otherClassDecl.ancestors) {
				Optional<Symbol> optSymbol = symbolTable.lookup(ancestorQName);

				if(optSymbol.isPresent()) {
					Symbol symbol = optSymbol.get();
					Optional<AstClassDeclaration> optClassDecl = symbol.getClassDeclaration();
					if(optClassDecl.isPresent()) {
						AstClassDeclaration ancestorCD = optClassDecl.get();
						if(isAncestorOrSameAs(ancestorCD)) {
							return true;
						}
					}

				};
			}
		}
		
		return false;
	}
	@Override
	public boolean isStrictDescendantOf(AstType type) {
		for(AstClassDeclaration ancestor: getAncestorClasses()) {
			if(ancestor.isExactlyA(type))
				return true;
			
			if(ancestor.isStrictDescendantOf(type))
				return true;
		}
		return false;
	}
	
	List<AstClassDeclaration> getAncestorClasses() {
		return this.ancestors.stream()
				.map(qname -> symbolTable.lookup(qname))
				.filter(optSymbol -> optSymbol.isPresent())
				.map(optSymbol -> optSymbol.get())
				.map(symbol -> symbol.getClassDeclaration())
				.filter(optCD -> optCD.isPresent())
				.map(optCD -> optCD.get())
				.collect(Collectors.toList());
				
	}
}
