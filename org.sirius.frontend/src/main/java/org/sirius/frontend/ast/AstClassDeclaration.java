package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.ClassOrInterface;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.Symbol;

import com.google.common.collect.ImmutableList;

public class AstClassDeclaration implements AstType, Scoped, Visitable, AstParametric<AstClassDeclaration>, AstClassOrInterface, Named {

	private AstToken name;
	private QName qName; 
	
	// Formal parameters
	private ImmutableList<TypeParameter> typeParameters;
	
	private ImmutableList<AstFunctionDeclaration> functionDeclarations;
	
	private List<AstMemberValueDeclaration> valueDeclarations = new ArrayList<>();
	private List<AstFunctionFormalArgument> anonConstructorArguments = new ArrayList<>(); 

	/** Root package at first */
	private Optional<QName> packageQName;
	
	/** True for annotation classes (ConstrainedAnnotation subtypes, ie OptionalAnnotation or SequencedAnnotation) */
	private boolean annotationType = false; 
	
	private boolean interfaceType;
	
	private List<AncestorInfo> ancestors = new ArrayList<>();
	
	private List<AstInterfaceDeclaration> interfaces = new ArrayList<>();

	private DefaultSymbolTable symbolTable /*= new SymbolTable()*/; 
	

	private Reporter reporter;
	
	public AstClassDeclaration(Reporter reporter, boolean interfaceType, AstToken name/*, PackageDeclaration packageDeclaration*/, Optional<QName> packageQName,
			ImmutableList<TypeParameter> typeParameters,
			ImmutableList<AstFunctionDeclaration> functionDeclarations,
			List<AstMemberValueDeclaration> valueDeclarations,
			List<AstFunctionFormalArgument> anonConstructorArguments 
			) {
		super();
		this.reporter = reporter;
		this.interfaceType = interfaceType;
		this.name = name;
		this.packageQName = packageQName;
		
		this.qName = null;
		packageQName.ifPresent((pkgQName) -> {this.qName = pkgQName.child(name.getText());});
		
		this.typeParameters = typeParameters;
		this.functionDeclarations = functionDeclarations;
		this.valueDeclarations = valueDeclarations;
		this.anonConstructorArguments = anonConstructorArguments; 
	}

	public AstClassDeclaration(Reporter reporter, boolean interfaceType, AstToken name/*, PackageDeclaration packageDeclaration*/, Optional<QName> packageQName) {
		this(reporter,interfaceType, name, packageQName,
		ImmutableList.of(),//<TypeFormalParameterDeclaration> typeParameters,
		ImmutableList.of(), //new ArrayList<AstFunctionDeclaration>(), // functionDeclarations,
		new ArrayList<AstMemberValueDeclaration>(), //List valueDeclarations,
		new ArrayList<AstFunctionFormalArgument>() //List anonConstructorArguments
		);
	}
	public AstClassDeclaration withFormalParameter(TypeParameter param) {
		
		ImmutableList.Builder<TypeParameter> builder = ImmutableList.builderWithExpectedSize(typeParameters.size() + 1);
		ImmutableList<TypeParameter> newTypeParams = builder.addAll(typeParameters).add(param).build();

		AstClassDeclaration fd = new AstClassDeclaration(reporter,
				interfaceType,
				name, 
				packageQName, 
				newTypeParams,
				functionDeclarations,
				valueDeclarations, anonConstructorArguments);
		return fd;
	}

	public AstClassDeclaration withFunctionDeclaration(AstFunctionDeclaration fd) {
		
		// 
		if(!fd.getAnnotationList().contains("static"))
			fd.setMember(true);
		
		ImmutableList.Builder<AstFunctionDeclaration> builder = ImmutableList.builderWithExpectedSize(functionDeclarations.size() + 1);
		ImmutableList<AstFunctionDeclaration> newFunctions = builder.addAll(functionDeclarations).add(fd).build();

		AstClassDeclaration cd = new AstClassDeclaration(reporter,
				interfaceType,
				name, 
				packageQName, 
				typeParameters,
				newFunctions,
				valueDeclarations, anonConstructorArguments);
		return cd;
	}

	
	public AstClassDeclaration(Reporter reporter, boolean interfaceType, Token name/*, PackageDeclaration packageDeclaration*/, Optional<QName> packageQName) {
		this(reporter, interfaceType, new AstToken(name), packageQName);
	}

	public static AstClassDeclaration newClass(Reporter reporter, AstToken name, Optional<QName> packageQName) {
		return new AstClassDeclaration (reporter, false /*interfaceType */ , name, packageQName);
	}
	public static AstClassDeclaration newClass(Reporter reporter, AstToken name, QName packageQName) {
		return newClass(reporter, name, Optional.of(packageQName));
	}
	
	public static AstClassDeclaration newInterface(Reporter reporter, AstToken name, Optional<QName> packageQName) {
		return new AstClassDeclaration (reporter, true /*interfaceType */ , name, packageQName);
	}
	public static AstClassDeclaration newInterface(Reporter reporter, AstToken name, QName packageQName) {
		return newInterface(reporter, name, Optional.of(packageQName));
	}

	
	
	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		if(symbolTable == null)
			throw new NullPointerException();
		
		this.symbolTable = symbolTable;

		for(TypeParameter d: typeParameters)
			this.symbolTable.addFormalParameter(this.qName, d);
	}

	
	
	
	@Override
	public AstToken getName() {
		return name;
	}
	@Override
	public List<AstFunctionDeclaration> getFunctionDeclarations() {
		return functionDeclarations;
	}
	
	public void addValueDeclaration(AstMemberValueDeclaration valueDeclaration) {
		this.valueDeclarations.add(valueDeclaration);
		// TODO: add to symbol table
	}
	
	public void setPackageQName(QName packageQName) {
		this.packageQName = Optional.of(packageQName);
		this.qName = packageQName.child(this.name.getText());
	}
	
	@Override
	public List<AstInterfaceDeclaration> getInterfaces() {
		return interfaces;
	}

	public boolean isInterfaceType() {
		return interfaceType;
	}
	@Override
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
	@Override
	public List<TypeParameter> getTypeParameters() {
		return typeParameters;
	}
	
	
	public List<AstMemberValueDeclaration> getValueDeclarations() {
		return valueDeclarations;
	}
	public void addAnonConstructorArgument(AstFunctionFormalArgument argument) {
		this.anonConstructorArguments.add(argument);
	}

	public List<AstFunctionFormalArgument> getAnonConstructorArguments() {
		return anonConstructorArguments;
	}
	public List<AncestorInfo> getAncestors() {
		return ancestors;
	}
	public void addAncestor(Token ancestor) {// TODO: remove
		this.ancestors.add(new AncestorInfo(new AstToken(ancestor)));	
	}
	public void addAncestor(AstToken ancestor) {// TODO: remove
		this.ancestors.add(new AncestorInfo(ancestor));	
	}

	@Override
	public Optional<AstClassDeclaration> apply(AstType parameter) {
		TypeParameter formalParam = typeParameters.get(0);
		if(formalParam == null) {
			reporter.error("Can't apply type " + parameter.messageStr() + " to class/interface " + messageStr() + ", it has no formal parameter." );
			return Optional.empty();
		}
		
		AstClassDeclaration cd = new AstClassDeclaration(reporter, interfaceType, name, packageQName,
				typeParameters.subList(1, typeParameters.size()),
				functionDeclarations,
				valueDeclarations,
				anonConstructorArguments
				);
		
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
			
			if(typeParameters != other.typeParameters)
				return false;
			
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "class " + qName;
	}
	public boolean isAnnotationType() {
		return annotationType;
	}
	public void setAnnotationType(boolean annotationType) {
		this.annotationType = annotationType;
	}

	private ClassDeclaration classDeclarationImpl = null;

	
	public ClassDeclaration getClassDeclaration() {
		QName containerQName = packageQName.get();
		if(classDeclarationImpl == null)
			classDeclarationImpl =  new ClassDeclaration() {
			QName qName = containerQName.child(name.getText());
			@Override
			public List<MemberValue> getMemberValues() {
				return valueDeclarations.stream()
					.map(v->v.getMemberValue())
					.collect(Collectors.toList());
			}

			@Override
			public List<AbstractFunction> getFunctions() {
				
				MapOfList<QName, AstFunctionDeclaration> allFctMap = getAllFunctions();
				
				ArrayList<AbstractFunction> memberFunctions = new ArrayList<>();
				
				for(QName qn: allFctMap.keySet()) {
					List<AstFunctionDeclaration> functions = allFctMap.get(qn);
					for(AstFunctionDeclaration func: functions) {
						if(func.isConcrete()) {
//							func.getMemberFunction().ifPresent((MemberFunction mf) -> {
//								memberFunctions.add(mf);
//							} );
							memberFunctions.add(func.toAPI());
						}
					}
					
				}
				return memberFunctions;
			}

			@Override
			public QName getQName() {
				return qName;
			}
			@Override
			public boolean isAncestorOrSame(Type type) {
				throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
			}

			List<InterfaceDeclaration> interfaces = null;
			@Override
			public List<InterfaceDeclaration> getDirectInterfaces() {
				if(interfaces == null) {
					interfaces = createDirectInterfaces();
				}
				return interfaces;
			}

		};
		return classDeclarationImpl;
	}

	@Override
	public boolean isAncestorOrSameAs(AstType descType) {
		if(isExactlyA(descType))
			return true;
		
		descType = descType.resolve();	// TODO: it's for SimpleType ref -> refactor ???
		
		// Check descendant is a class
		if(! (descType instanceof AstClassDeclaration)) {
			return false;
		}
		AstClassDeclaration descDecl = (AstClassDeclaration)descType;

		for(AncestorInfo ai: descDecl.ancestors) {
			AstToken ancTk = ai.getSimpleName();
			Optional<Symbol> optSymbol = symbolTable.lookup(ancTk.getText());
			if(! optSymbol.isPresent())
				continue;
			
			Symbol symbol = optSymbol.get();
			Optional<AstClassDeclaration> optClassDecl = symbol.getClassDeclaration();
			if(optClassDecl.isPresent()) {
				AstClassDeclaration ancestorCD = optClassDecl.get();
				if(isAncestorOrSameAs(ancestorCD)) {
					return true;
				}

			};
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
				.filter(ancestorInfo -> ancestorInfo.getAstClassDecl().isPresent())
				.map(ancestorInfo -> symbolTable.lookup(ancestorInfo.getSimpleName().getText()))
				.filter(optSymbol -> optSymbol.isPresent())
				.map(optSymbol -> optSymbol.get())
				.map(symbol -> symbol.getClassDeclaration())
				.filter(optCD -> optCD.isPresent())
				.map(optCD -> optCD.get())
				.collect(Collectors.toList());

	}
	@Override
	public AstType resolve() {
//		reporter.error("Symbol \"" + name.getText() + "\" not found.", name);
		return this; // TODO
	}

	
	
	public void resolveAncestors() {
		for(AncestorInfo ai: ancestors) {
			ai.getAstClassDecl(symbolTable, reporter).ifPresent((AstInterfaceDeclaration id) -> {
				this.interfaces.add(id);
			});
		}
	}

	private ClassOrInterface type = null;
	
	@Override
	public ClassOrInterface getApiType() {
		if(type == null) {
			type = getClassDeclaration();
		}
		
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AstClassDeclaration) {
			return isExactlyA( (AstClassDeclaration)obj );
		}
		return false;
	}
	@Override
	public int hashCode() {
		int h = qName.hashCode();
		for(TypeParameter formalParam : typeParameters)
			h = 31 * h + formalParam.hashCode();

		return h;
	}
}
