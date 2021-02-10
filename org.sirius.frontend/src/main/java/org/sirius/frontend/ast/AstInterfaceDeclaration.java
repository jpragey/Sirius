package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.InterfaceDeclarationImpl;
import org.sirius.frontend.symbols.DefaultSymbolTable;

import com.google.common.collect.ImmutableList;

public class AstInterfaceDeclaration implements AstType, Scoped, Visitable, AstParametric<AstInterfaceDeclaration>, AstClassOrInterface, Named, Verifiable {

	private Reporter reporter;

	private List<AncestorInfo> ancestors = new ArrayList<>();
	
	private DefaultSymbolTable symbolTable /*= new SymbolTable()*/; 

	private ImmutableList<TypeParameter> typeParameters;
	private AstToken name;
	
	private ImmutableList<FunctionDeclaration> functionDeclarations;
	private ImmutableList<FunctionDefinition> functionDefinitions;
	
	private List<AstMemberValueDeclaration> valueDeclarations = new ArrayList<>();

//	private Optional<QName> packageQName;
	private QName qName = new QName("<not_set>"); 

	public AstInterfaceDeclaration(Reporter reporter, AstToken name, //Optional<QName> packageQName,
			ImmutableList<FunctionDeclaration> functionDeclarations,
			ImmutableList<FunctionDefinition> functionDefinitions,
			ImmutableList<TypeParameter> typeParameters,
			ImmutableList<AncestorInfo> ancestorInfos,
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

	public AstInterfaceDeclaration(Reporter reporter, AstToken name /*, Optional<QName> packageQName*/) {
		this(reporter, name,
				ImmutableList.of() /*functionDeclarations*/,
				ImmutableList.of() /*functionDefinitions*/,
				ImmutableList.of() /*typeDeclarations*/,
				ImmutableList.of() /*ancestorInfos*/,
				ImmutableList.of() /*valueDeclarations*/
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

	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		if(symbolTable == null)
			throw new NullPointerException();
		
		this.symbolTable = symbolTable;

		for(TypeParameter d: typeParameters)
			this.symbolTable.addFormalParameter(this.qName, d);
	}

	@Override
	public ImmutableList<TypeParameter> getTypeParameters() {
		return typeParameters;
	}
	
	@Override
	public Optional<AstInterfaceDeclaration> apply(AstType parameter) {
		throw new UnsupportedOperationException("AstInterfaceDeclaration...");
	}

	@Override
	public DefaultSymbolTable getSymbolTable() {
		return symbolTable;
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
//		this.packageQName = Optional.of(packageQName);
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
	public List<AncestorInfo> getAncestors() {
		return ancestors;
	}

	private List<AstInterfaceDeclaration> cachedDirectInterfaces = null;
	
	@Override
	public List<AstInterfaceDeclaration> getInterfaces() {
		if(cachedDirectInterfaces != null)
			return cachedDirectInterfaces;
		
		cachedDirectInterfaces = new ArrayList<>(ancestors.size());

		for(AncestorInfo ai : ancestors) {
			Optional<AstInterfaceDeclaration> opt = ai.getAstClassDecl(symbolTable, reporter);
			if(opt.isPresent()) {
				AstInterfaceDeclaration interf = opt.get();
				cachedDirectInterfaces.add(interf);
			}
		}
		return cachedDirectInterfaces;
	}

	public AstInterfaceDeclaration withFormalParameter(TypeParameter param) {
		ImmutableList.Builder<TypeParameter> builder = ImmutableList.builderWithExpectedSize(typeParameters.size() + 1);
		ImmutableList<TypeParameter> newTypeParams = builder.addAll(typeParameters).add(param).build();
		return new AstInterfaceDeclaration(reporter, name, // packageQName,
				functionDeclarations,
				functionDefinitions,
				newTypeParams,
				ImmutableList.of(),	// TODO
				valueDeclarations
				);
	}
	
	public void addAncestor(Token ancestor) {// TODO: remove
		addAncestor(new AstToken(ancestor)) ;
	}
	
	@Override 
	public void addAncestor(AstToken ancestor) {	// TODO: remove
		this.ancestors.add(new AncestorInfo(ancestor));	
	}
	
	public void addValueDeclaration(AstMemberValueDeclaration valueDeclaration) {
		this.valueDeclarations.add(valueDeclaration);
		// TODO: add to symbol table
	}

	
	public AstInterfaceDeclaration withFunctionDeclaration(FunctionDefinition fd) {
		
//		if(!fd.getAnnotationList().contains("static"))
//			fd.setMember(true);

		return new AstInterfaceDeclaration(reporter, name, //packageQName,
				functionDeclarations,
//				ImmutableList.<FunctionDeclaration>builder()
//					.addAll(functionDeclarations)
//					.build(),
					
				ImmutableList.<FunctionDefinition>builder()
					.addAll(functionDefinitions)
					.add(fd)
					.build(),
					
				typeParameters,
				ImmutableList.copyOf(ancestors),
				valueDeclarations
				);
	}

	private InterfaceDeclarationImpl impl = null;
	
	public InterfaceDeclaration getInterfaceDeclaration() {
		if(impl == null) {
//			private List<AstMemberValueDeclaration> valueDeclarations = new ArrayList<>();
			List<MemberValue> memberValues = valueDeclarations.stream()
					.map(vd ->vd.getMemberValue())
					.collect(Collectors.toList());

//			public default MapOfList<QName, FunctionDefinition> getAllFunctions() {
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
				
//				return map;
//			}

			impl = new InterfaceDeclarationImpl(qName, allFctMap,
				memberValues);
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
		
		verifyList(ancestors, featureFlags);
		
//		verifyList(interfaces, featureFlags);
	}
	
}
