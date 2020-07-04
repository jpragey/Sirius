package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTable;

/** most simple (class or interface) type 
 * 
 * @author jpragey
 *
 */
public final class SimpleType implements AstType {
	
	private Reporter reporter;
	
	private AstToken name;

	private List<AstType> typeParameters;
	
	private DefaultSymbolTable symbolTable;
	
	private Optional<AstType> resolvedElementType = Optional.empty();

	public SimpleType(Reporter reporter, AstToken name, List<AstType> typeParameters) {
		super();
		this.reporter = reporter;
		this.name = name;
		this.typeParameters = typeParameters;
	}
	public SimpleType(Reporter reporter, AstToken name) {
		this(reporter, name, new ArrayList<>() /* TODO: immutable ??? */);
	}

	public AstToken getName() {
		return name;
	}
	public String getNameString() {
		return name.getText();
	}
	
	public DefaultSymbolTable getSymbolTable() {
		return symbolTable;
	}

	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public void appliedParameter(AstType type) {
		typeParameters.add(type);
	}
	
	public List<AstType> getTypeParameters() {
		return typeParameters;
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
		return messageStr();
	}
	
	@Override
	public Type getApiType() {
//		assert(resolvedElementType.isPresent());
//		AstType type = resolvedElementType.get();
		AstType type = resolve();
		return type.getApiType();
	}

	private boolean isExactlyAClassDeclaration(AstClassDeclaration thisClassDeclaration, AstType otherType) {
		if(otherType instanceof ClassDeclaration) {
			return thisClassDeclaration.isExactlyA(otherType);
		}
		return false;
	}

	private Optional<AstClassDeclaration> getClassDeclaration() {
		Optional<Symbol> optSymbol = symbolTable.lookup(name.getText());
		if(optSymbol.isPresent()) {
			Symbol symbol = optSymbol.get();
			Optional<AstClassDeclaration> optClassDeclaration = symbol.getClassDeclaration();
			return optClassDeclaration;
		}
		return Optional.empty();
	}
	
	@Override
	public boolean isExactlyA(AstType type) {
		Optional<Symbol> optSymbol = symbolTable.lookup(name.getText());
		if(optSymbol.isPresent()) {
			Symbol symbol = optSymbol.get();
			Optional<AstClassDeclaration> optClassDeclaration = symbol.getClassDeclaration();
			if(optClassDeclaration.isPresent()) {
				AstClassDeclaration thisClassDeclaration = optClassDeclaration.get();
				boolean match = isExactlyAClassDeclaration(thisClassDeclaration, type);
				return match;
			}
		}
		return false;
	}

	@Override
	public boolean isAncestorOrSameAs(AstType type) {
		Optional<AstClassDeclaration> optCD = getClassDeclaration();
		if(optCD.isPresent()) {
			return optCD.get().isAncestorOrSameAs(type);
		}
		
		return false;
	}
	@Override
	public boolean isStrictDescendantOf(AstType type) {
		throw new UnsupportedOperationException();
	}

	
	private Optional<AstType> fetchResolveType(SymbolTable symbolTable) {
		Optional<Symbol> optSymbol = symbolTable.lookup(name.getText());
		if(optSymbol.isPresent()) {
			Symbol symbol = optSymbol.get();
			
			Optional<AstClassDeclaration> optClassDecl = symbol.getClassDeclaration();
			if(optClassDecl.isPresent()) {
				return Optional.of(optClassDecl.get());
			}
			
			reporter.error("Symbol \"" + name.getText() + "\" : class name expected.", name);
			return Optional.of(new AstVoidType());
		} else {
			reporter.error("Symbol \"" + name.getText() + "\" not found.", name);
			return Optional.of(new AstVoidType());
		}
	}
	
	@Override
	public AstType resolve() {
		if(resolvedElementType.isEmpty()) {
			resolvedElementType = fetchResolveType(symbolTable);
		}
			
		return resolvedElementType.get();
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.start(this);
		visitor.end(this);		
	}
}
