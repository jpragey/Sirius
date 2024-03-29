package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ExecutionEnvironment;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.ClassDeclarationImpl;
import org.sirius.frontend.sdk.SdkContent;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class AstClassDeclaration implements AstType, Scoped, Visitable, AstParametric<AstClassDeclaration>, 
	AstClassOrInterface, Named, Verifiable {

	private AstToken name;
	public static final String undefQName = "<not_set>";
	private QName qName = new QName(undefQName); 
	
	// Formal parameters
	private List<TypeParameter> typeParameters;
	
	private List<FunctionDefinition> functionDefinitions;
	
	private List<AstMemberValueDeclaration> valueDeclarations = new ArrayList<>();
	private List<AstFunctionParameter> anonConstructorArguments = new ArrayList<>(); 

	private Scope scope = null;
	
	
	private List<AstToken> ancestors = new ArrayList<>();
	
	private SymbolTableImpl symbolTable; 
	
	private ClassDeclarationImpl classDeclarationImpl = null;

	private Reporter reporter;
	
	public AstClassDeclaration(Reporter reporter, AstToken name, 
			List<TypeParameter> typeParameters,
			List<FunctionDefinition> functionDeclarations,
			List<AstMemberValueDeclaration> valueDeclarations,
			List<AstFunctionParameter> anonConstructorArguments,
			List<AstToken> ancestorInfos) 
	{
		super();
		this.reporter = reporter;
		this.name = name;
		
		this.typeParameters = typeParameters;
		this.functionDefinitions = functionDeclarations;
		this.valueDeclarations = valueDeclarations;
		this.anonConstructorArguments = anonConstructorArguments; 
		this.ancestors = ancestorInfos;
	}

	// TODO: remove ?
	public AstClassDeclaration(Reporter reporter, AstToken name)
	{
		this(reporter, name, 
			List.of(),					//<TypeFormalParameterDeclaration> typeParameters,
			List.of(), 					//<AstFunctionDeclaration>(), // functionDeclarations,
			new ArrayList<AstMemberValueDeclaration>(), //List valueDeclarations,
			new ArrayList<AstFunctionParameter>() 		//List anonConstructorArguments
			, new ArrayList<AstToken>()
		);
	}
	public AstClassDeclaration withFormalParameter(TypeParameter param) {

		List<TypeParameter> newTypeParams = new ArrayList<>(typeParameters.size() + 1);
		newTypeParams.addAll(typeParameters);
		newTypeParams.add(param);

		AstClassDeclaration fd = new AstClassDeclaration(reporter,
				name, 
				newTypeParams,
				functionDefinitions,
				valueDeclarations, anonConstructorArguments, ancestors);
		return fd;
	}

	public AstClassDeclaration withFunctionDeclaration(FunctionDefinition fd) {
		
		List<FunctionDefinition> newFunctions = new ArrayList<>(functionDefinitions.size() + 1);
		newFunctions.addAll(functionDefinitions);
		newFunctions.add(fd);

		AstClassDeclaration cd = new AstClassDeclaration(reporter, name, typeParameters, newFunctions, 
				valueDeclarations, anonConstructorArguments, ancestors);
		return cd;
	}
	
	public void setSymbolTable(SymbolTableImpl symbolTable) {
		if(symbolTable == null)
			throw new NullPointerException();
		
		this.symbolTable = symbolTable;

		assert(this.qName != null);
		for(TypeParameter d: typeParameters)
			this.symbolTable.addFormalParameter(this.qName, d);
	}

	
	public void assignScope(Scope scope) {
		assert(this.scope == null);	// No duplicate assignment
		this.scope = scope;

		// -- 
		for(TypeParameter typeParameter : this.typeParameters) {
			
		}
		
		for(FunctionDefinition fd : this.functionDefinitions) {
			scope.addFunction(fd);
		}
		for(AstMemberValueDeclaration memberValueDeclaration: this.valueDeclarations) {
			scope.addMemberValue(memberValueDeclaration);
		}
		for(AstFunctionParameter constructorArg : this.anonConstructorArguments) {
			
		}
	}
	
	
	public Scope getScope() {
		return scope;
	}

	@Override
	public AstToken getName() {
		return name;
	}
	public String getNameText() {
		return name.getText();
	}
	
	@Override
	public List<FunctionDeclaration> getFunctionDeclarations() {
		return Collections.emptyList();	// TODO
	}
	@Override
	public List<FunctionDefinition> getFunctionDefinitions() {
		return functionDefinitions;
	}
	public void addValueDeclaration(AstMemberValueDeclaration valueDeclaration) {
		this.valueDeclarations.add(valueDeclaration);
		// TODO: add to symbol table
	}
	
	private Optional<Type> sdkType = Optional.empty();
	private static Map<QName, Type> sdkTypeByQName = new HashMap<>() {{
		put(SdkContent.siriusLangIntegerQName, Type.integerType);
		put(SdkContent.siriusLangBooleanQName, Type.booleanType);
		put(SdkContent.siriusLangFloatQName, Type.floatType);
	}};
	
	public void setPackageQName(Optional<QName> packageQName) {
		String simpleName = this.name.getText();
		this.qName = packageQName
				.map(pqn -> pqn.child(simpleName))
				.orElse(new QName(simpleName));
//		this.qName = packageQName.child();
		
		this.sdkType = Optional.ofNullable(sdkTypeByQName.get(this.qName));
	}
	
	@Override
	public QName getQName() {
		assert(this.qName != null);
		return this.qName;
	}

	public void visit(AstVisitor visitor) {
		visitor.startClassDeclaration(this);
		functionDefinitions.stream().forEach(fd -> fd.visit(visitor));
		valueDeclarations.stream().forEach(fd -> fd.visit(visitor));
		visitor.endClassDeclaration(this);
	}
	
	@Override
	public SymbolTableImpl getSymbolTable() {
		return symbolTable;
	}
	@Override
	public List<TypeParameter> getTypeParameters() {
		return typeParameters;
	}
	
	
	public List<AstMemberValueDeclaration> getValueDeclarations() {
		return valueDeclarations;
	}
	public void addAnonConstructorArgument(AstFunctionParameter argument) {
		this.anonConstructorArguments.add(argument);
	}

	public List<AstFunctionParameter> getAnonConstructorArguments() {
		return anonConstructorArguments;
	}
	public List<AstToken> getAncestors() {
		return ancestors;
	}
	public void addAncestor(Token ancestor) {// TODO: remove
//		this.ancestors.add(new AncestorInfo(new AstToken(ancestor)));	
		this.ancestors.add(new AstToken(ancestor));	
	}
	public void addAncestor(AstToken ancestor) {// TODO: remove
//		this.ancestors.add(new AncestorInfo(ancestor));	
		this.ancestors.add(ancestor);	
	}

	@Override
	public Optional<AstClassDeclaration> apply(AstType parameter) {
		TypeParameter formalParam = typeParameters.get(0);
		if(formalParam == null) {
			reporter.error("Can't apply type " + parameter.messageStr() + " to class/interface " + messageStr() + ", it has no formal parameter." );
			return Optional.empty();
		}
		
		AstClassDeclaration cd = new AstClassDeclaration(reporter, /*interfaceType, */name, //packageQName,
				typeParameters.subList(1, typeParameters.size()),
				functionDefinitions,
				valueDeclarations,
				anonConstructorArguments, ancestors
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
//			if(this.interfaceType != other.interfaceType)
//				return false;
			
			assert(this.qName != null);

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
		assert(this.qName != null);
		return "class " + qName;
	}

	public ClassType getClassDeclaration() {
		if(classDeclarationImpl == null) {
			List<MemberValue> memberValues =  valueDeclarations.stream()
					.map(v->v.getMemberValue())
					.collect(Collectors.toList());
			Optional<ExecutionEnvironment> execEnv = Optional.empty();	// TODO ???
			
			classDeclarationImpl =  new ClassDeclarationImpl(qName, 
					getAllApiMemberFunctions() /* getAllFunctions()*/, 
					memberValues, 
					execEnv);
		}
		return classDeclarationImpl;
	}

	@Override
	public boolean isAncestorOrSameAs(AstType descType) {
		descType = descType.resolve();	// TODO: it's for SimpleType ref -> refactor ???

		if(isExactlyA(descType))
			return true;
		
		
		// Check descendant is a class
		if(! (descType instanceof AstClassDeclaration)) {
			return false;
		}
		AstClassDeclaration descDecl = (AstClassDeclaration)descType;

		for(AstToken ancTk: descDecl.ancestors) {
//			for(AncestorInfo ai: descDecl.ancestors) {
//			AstToken ancTk = ai.getSimpleName();
			Optional<Symbol> optSymbol = symbolTable.lookupBySimpleName(ancTk.getText());
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
		throw new UnsupportedOperationException("Temp. removed yet");
//		return this.ancestors.stream()
//				.filter(ancestorInfo -> ancestorInfo.getAstClassDecl().isPresent())
//				.map(ancestorInfo -> symbolTable.lookupBySimpleName(ancestorInfo.getSimpleName().getText()))
//				.filter(optSymbol -> optSymbol.isPresent())
//				.map(optSymbol -> optSymbol.get())
//				.map(symbol -> symbol.getClassDeclaration())
//				.filter(optCD -> optCD.isPresent())
//				.map(optCD -> optCD.get())
//				.collect(Collectors.toList());

	}
	@Override
	public AstType resolve() {
//		reporter.error("Symbol \"" + name.getText() + "\" not found.", name);
		return this; 
		// TODO
	}

	private Type type = null;
	
	@Override
	public Type getApiType() {
		if(type == null) {
			type = this.sdkType.orElse(getClassDeclaration());
//			if(this.sdkType.isPresent()) {
//				type = this.sdkType.get();
//				
//			} else {
//				type = getClassDeclaration();
//				
//			}
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
		assert(this.qName != null);
		int h = qName.hashCode();
		for(TypeParameter formalParam : typeParameters)
			h = 31 * h + formalParam.hashCode();

		return h;
	}

	@Override
	public void verify(int featureFlags) {
		
		verifyList(typeParameters, featureFlags);
		
		verifyList(functionDefinitions, featureFlags);
		
		verifyList(valueDeclarations, featureFlags);
		verifyList(anonConstructorArguments, featureFlags); 


	}
	
	@Override
	public void setScope2(Scope scope) {
		assert(this.scope == null);
		assert(scope != null);
		this.scope = scope;
	}

}
