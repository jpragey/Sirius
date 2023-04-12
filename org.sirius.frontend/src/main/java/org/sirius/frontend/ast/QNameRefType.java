package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.SymbolTableImpl;
import org.sirius.frontend.symbols.Symbol;

/** most simple (class or interface) type 
 * 
 * @author jpragey
 *
 */
public final class QNameRefType implements AstType {
	private QName qName ;

	private List<AstType> appliedParameters = new ArrayList<>();
	
	private SymbolTableImpl symbolTable = null;
	
	public QNameRefType(QName name) {
		super();
		this.qName = name;
	}

	public QNameRefType(String dotSeparated, Reporter reporter) {
		super();
//		this.qName = QName.parseDotSeparated(dotSeparated);
		this.qName = QName.parseAndValidate(dotSeparated, reporter).get() /* TODO: check */;
		
	}

	public QName getqName() {
		return qName;
	}

	public SymbolTableImpl getSymbolTable() {
		return symbolTable;
	}

	public void setSymbolTable(SymbolTableImpl symbolTable) {
		this.symbolTable = symbolTable;
	}

	public void appliedParameter(AstType type) {
		appliedParameters.add(type);
	}

	
	
	
	@Override
	public String messageStr() {
		
		List<String> typeParams = appliedParameters.stream().map(p -> p.messageStr()).collect(Collectors.toList());
		
		return "class " + 
				qName.dotSeparated() + 
				"<" + 
				String.join(",", typeParams) + 
				">";
	}
	
	@Override
	public String toString() {
		return messageStr();
	}
	
	@Override
	public Type getApiType() { // TODO: check/refactor
		assert(symbolTable != null);
		Optional<Symbol> optSymbol = symbolTable.lookupByQName(qName);
		assert(optSymbol.isPresent());
		
		Optional<AstClassDeclaration> optCd = optSymbol.get().getClassDeclaration();
		assert(optCd.isPresent());
		Type apiType = optCd.get().getApiType();
		return apiType;
		
//		throw new UnsupportedOperationException();
//		return new ClassType() {
//			QName qName = new QName(name.getText());	// TODO : must be a full class name
//			@Override
//			public QName getQName() {
//				return qName;
//			}
//			
//		};
	}

	private boolean isExactlyAClassDeclaration(AstClassDeclaration thisClassDeclaration, AstType otherType) {
		throw new UnsupportedOperationException();
//		if(otherType instanceof ClassDeclaration) {
//			return thisClassDeclaration.isExactlyA(otherType);
//		}
//		return false;
	}

	private Optional<AstClassDeclaration> getClassDeclaration() {
		Optional<Symbol> optSymbol = symbolTable.lookupByQName(qName);
		
//		symbolTable.dump();
		
		if(optSymbol.isPresent()) {
			Symbol symbol = optSymbol.get();
			Optional<AstClassDeclaration> optClassDeclaration = symbol.getClassDeclaration();
			return optClassDeclaration;
		}
//		return Optional.empty();
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isExactlyA(AstType type) {
		throw new UnsupportedOperationException();
//		Optional<Symbol> optSymbol = symbolTable.lookup(name.getText());
//		if(optSymbol.isPresent()) {
//			Symbol symbol = optSymbol.get();
//			Optional<AstClassDeclaration> optClassDeclaration = symbol.getClassDeclaration();
//			if(optClassDeclaration.isPresent()) {
//				AstClassDeclaration thisClassDeclaration = optClassDeclaration.get();
//				boolean match = isExactlyAClassDeclaration(thisClassDeclaration, type);
//				return match;
//			}
//		}
//		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AstType resolve() {
		throw new UnsupportedOperationException("QNameRefType.resolve(not supported)");
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.start(this);
		appliedParameters.stream().forEach(type -> type.visit(visitor));
		visitor.end(this);		
	}

	@Override
	public void verify(int featureFlags) {
		verifyList(appliedParameters, featureFlags);
		
		verifyNotNull(symbolTable, "SimpleType.symbolTable");
	}

}
