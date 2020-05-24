package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;

import com.google.common.collect.ImmutableList;

public class AstInterfaceDeclaration implements AstType, Scoped, Visitable, AstParametric<AstClassDeclaration>, AstClassOrInterface, Named {

	private Reporter reporter;

	private List<AncestorInfo> ancestors = new ArrayList<>();
	
	private DefaultSymbolTable symbolTable /*= new SymbolTable()*/; 

	private ImmutableList<TypeParameter> typeParameters;
	private AstToken name;
	
	private ImmutableList<AstFunctionDeclaration> functionDeclarations;
	
	private List<AstMemberValueDeclaration> valueDeclarations = new ArrayList<>();

	private Optional<QName> packageQName;
	private QName qName; 

	public AstInterfaceDeclaration(Reporter reporter, AstToken name, Optional<QName> packageQName,
			ImmutableList<AstFunctionDeclaration> functionDeclarations,
			ImmutableList<TypeParameter> typeParameters
			
			) {
		this.reporter = reporter;
		this.name = name;
		this.functionDeclarations = functionDeclarations;
		this.typeParameters = typeParameters;
		
		this.qName = null;
		this.packageQName = packageQName;
		
		packageQName.ifPresent((pkgQName) -> {this.qName = pkgQName.child(name.getText());});
		
	}

	public AstInterfaceDeclaration(Reporter reporter, AstToken name, Optional<QName> packageQName) {
		this(reporter, name,
				packageQName,
				ImmutableList.of() /*functionDeclarations*/,
				ImmutableList.of() /*typeDeclarations*/
				);
	}

	
	@Override
	public AstToken getName() {
		return name;
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
	public Optional<AstClassDeclaration> apply(AstType parameter) {
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
		this.packageQName = Optional.of(packageQName);
		this.qName = packageQName.child(this.name.getText());
	}

	@Override
	public List<AstFunctionDeclaration> getFunctionDeclarations() {
		return this.functionDeclarations;
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
		return new AstInterfaceDeclaration(reporter, name, packageQName,
				functionDeclarations,
				newTypeParams);
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

	
	public AstInterfaceDeclaration withFunctionDeclaration(AstFunctionDeclaration fd) {
		return new AstInterfaceDeclaration(reporter, name, packageQName,
				ImmutableList.<AstFunctionDeclaration>builder()
					.addAll(functionDeclarations)
					.add(fd)
					.build(),
				typeParameters);
	}

	public InterfaceDeclaration getInterfaceDeclaration() {
		QName containerQName = packageQName.get();
		return new InterfaceDeclaration() {
			QName qName = containerQName.child(name.getText());
			@Override
			public List<MemberValue> getMemberValues() {
				return valueDeclarations.stream()
						.map(v->v.getMemberValue())
						.collect(Collectors.toList());
			}

			@Override
			public List<AbstractFunction> getFunctions() {
				return functionDeclarations.stream()
//						.map(AstFunctionDeclaration::getMemberFunction)
						.map(AstFunctionDeclaration::toAPI)
//						.filter(fd -> fd.isPresent())
//						.map(fd -> fd.get())
						.collect(Collectors.toList());
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
	}

	@Override
	public String toString() {
		return "interface " + qName;
	}

}
