package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.InterfaceDeclarationImpl;
import org.sirius.frontend.symbols.Scope;

public class AstInterfaceDeclaration implements AstType, Scoped, Visitable, AstParametric<AstInterfaceDeclaration>, AstClassOrInterface, Named, Verifiable {

	private Reporter reporter;

	private List<AstToken> ancestors = new ArrayList<>();
	
	private Scope scope = null;

	private List<TypeParameter> typeParameters;
	private AstToken name;
	
	private List<FunctionDeclaration> functionDeclarations;
	private List<FunctionDefinition> functionDefinitions;
	
	private List<AstMemberValueDeclaration> valueDeclarations = new ArrayList<>();

	private QName qName = new QName("<not_set>"); 

	public AstInterfaceDeclaration(Reporter reporter, AstToken name, 
			List<FunctionDeclaration> functionDeclarations,
			List<FunctionDefinition> functionDefinitions,
			List<TypeParameter> typeParameters,
			List<AstToken> ancestorInfos,
			List<AstMemberValueDeclaration> valueDeclarations
			) 
	{
		this.reporter = reporter;
		this.name = name;
		this.functionDeclarations = functionDeclarations;
		this.functionDefinitions= functionDefinitions;
		this.typeParameters = typeParameters;
		
		this.qName = null;
		
		this.ancestors = new ArrayList<>(ancestorInfos);
		this.valueDeclarations = new ArrayList<>(valueDeclarations);
	}

	public AstInterfaceDeclaration(Reporter reporter, AstToken name) {
		this(reporter, name,
				List.of() /*functionDeclarations*/,
				List.of() /*functionDefinitions*/,
				List.of() /*typeDeclarations*/,
				List.of() /*ancestorInfos*/,
				List.of() /*valueDeclarations*/
				);
	}

	public static AstInterfaceDeclaration newInterface(Reporter reporter, AstToken name) {
		return new AstInterfaceDeclaration (reporter, name);
	}

	
	@Override
	public AstToken getName() {
		return name;
	}
	public String getNameString() {
		return name.getText();
	}
	@Override
	public QName getQName() {
		return this.qName;
	}

	@Override
	public List<TypeParameter> getTypeParameters() {
		return typeParameters;
	}
	
	@Override
	public Optional<AstInterfaceDeclaration> apply(AstType parameter) {
		throw new UnsupportedOperationException("AstInterfaceDeclaration...");
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
	public void visit(AstVisitor visitor) {
		visitor.startInterfaceDeclaration(this);
		functionDeclarations.stream().forEach(fd -> fd.visit(visitor));
		functionDefinitions.stream().forEach(fd -> fd.visit(visitor));
		valueDeclarations.stream().forEach(fd -> fd.visit(visitor));
		visitor.endInterfaceDeclaration(this);
	}

	@Override
	public AstType resolve() {
		throw new UnsupportedOperationException("AstInterfaceDeclaration...");
	}

	@Override
	public boolean isExactlyA(AstType type) {
		if(type instanceof AstInterfaceDeclaration) {
			AstInterfaceDeclaration other = (AstInterfaceDeclaration)type;
			
			if(!this.qName.equals(other.qName))
				return false;
			
			if(typeParameters != other.typeParameters)
				return false;
			
			return true;
		}
		return false;
	}

	@Override
	public boolean isStrictDescendantOf(AstType type) {
		if(type instanceof AstInterfaceDeclaration) {
			return descendStrictlyFrom( (AstInterfaceDeclaration) type);
		}
		return false;
	}

	@Override
	public boolean isAncestorOrSameAs(AstType descType) {
		if(descType instanceof AstClassOrInterface) {
			return ((AstClassOrInterface)descType).descendOrIsSameAs(this);
		}
		return false;
	}

	public void setPackageQName(QName packageQName) {
		this.qName = packageQName.child(this.name.getText());
	}

	@Override
	public List<FunctionDeclaration> getFunctionDeclarations() {
		return this.functionDeclarations;
	}
	@Override
	public List<FunctionDefinition> getFunctionDefinitions() {
		return this.functionDefinitions;
	}

	@Override
	public List<AstToken> getAncestors() {
		return ancestors;
	}

	private List<AstInterfaceDeclaration> cachedDirectInterfaces = new ArrayList<>();
	
	@Override
	public List<AstInterfaceDeclaration> getInterfaces() {
		return cachedDirectInterfaces;
	}

	public AstInterfaceDeclaration withFormalParameter(TypeParameter param) {
		List<TypeParameter> newTypeParams = new ArrayList<>(typeParameters.size() + 1);
		newTypeParams.addAll(typeParameters);
		newTypeParams.add(param);
		
		return new AstInterfaceDeclaration(reporter, name,
				functionDeclarations,
				functionDefinitions,
				newTypeParams,
				List.of(),	// TODO
				valueDeclarations
				);
	}

	public void resolveAncestors(HashMap<String, AstInterfaceDeclaration> interfacesByName) {
		for(AstToken ai: ancestors) {
			String name = ai.getText();
			AstInterfaceDeclaration intDecl = interfacesByName.get(name);
			if(intDecl == null) {
				reporter.error("Class " + getQName() + " implements an undefined interface: " + name, ai);
			} else {
				this.cachedDirectInterfaces.add(intDecl);
			}
		}
	}

	public void addAncestor(Token ancestor) {// TODO: remove
		addAncestor(new AstToken(ancestor)) ;
	}
	
	@Override 
	public void addAncestor(AstToken ancestor) {	// TODO: remove
		this.ancestors.add(ancestor);	
	}
	
	public void addValueDeclaration(AstMemberValueDeclaration valueDeclaration) {
		this.valueDeclarations.add(valueDeclaration);
		// TODO: add to symbol table
	}

	
	public AstInterfaceDeclaration withFunctionDeclaration(FunctionDefinition fd) {
		List<FunctionDefinition> functionDefs = new ArrayList<>(functionDefinitions.size() + 1);
		functionDefs.addAll(functionDefs);
		functionDefs.add(fd);
		
		return new AstInterfaceDeclaration(reporter, name, 
				functionDeclarations,
				functionDefs,
				typeParameters,
				List.copyOf(ancestors),
				valueDeclarations
				);
	}

	private InterfaceDeclarationImpl impl = null;
	
	public ClassType getInterfaceDeclaration() {
		if(impl == null) {
			List<MemberValue> memberValues = valueDeclarations.stream()
					.map(vd ->vd.getMemberValue())
					.collect(Collectors.toList());

			MapOfList<QName, FunctionDefinition> allFctMap = new MapOfList<>();

			// -- 
			for(FunctionDefinition func : getFunctionDefinitions()) {
				QName fqn = func.getqName();
				allFctMap.put(fqn, func);
			}

			for(AstInterfaceDeclaration acd: this.getInterfaces()) {
				MapOfList<QName, FunctionDefinition> amap  = acd.getAllFunctions();
				allFctMap.insert(amap);
			}

			impl = new InterfaceDeclarationImpl(qName, allFctMap, memberValues);
		}
		return impl;
		
	}

	@Override
	public String toString() {
		return "interface " + qName;
	}

	// TODO: implement yet
	@Override
	public Type getApiType() {
		throw new UnsupportedOperationException("Class " + getClass() + " has no getApiType() method (yet).");
	}

	public List<AstMemberValueDeclaration> getValueDeclarations() {
		return valueDeclarations;
	}

	@Override
	public void verify(int featureFlags) {
		
		verifyList(typeParameters, featureFlags);
		
		verifyList(functionDeclarations, featureFlags);
		verifyList(functionDefinitions, featureFlags);
		
		verifyList(valueDeclarations, featureFlags);
	}

	@Override
	public Scope getScope() {
		assert(this.scope != null);
		return this.scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
		
		for(TypeParameter d: typeParameters)
			this.scope.getSymbolTable() .addFormalParameter(this.qName, d);
	}
	@Override
	public void setScope2(Scope scope) {
		assert(this.scope == null);
		assert(scope != null);
		this.scope = scope;
	}

}
