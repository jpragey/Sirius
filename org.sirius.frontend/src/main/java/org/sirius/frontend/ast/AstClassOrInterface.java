package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.ast.AstClassOrInterface.AncestorInfo;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTable;

public interface AstClassOrInterface extends AstType {

	/** directly implemented interfaces or extended classes */
	public static class AncestorInfo {
		/** Name in declaration ( extends/implements NAME clause)*/
		private AstToken simpleName;
		private Optional<AstInterfaceDeclaration> astClassDecl = Optional.empty();
		public AncestorInfo(AstToken simpleName) {
			super();
			this.simpleName = simpleName;
		}
		public AstToken getSimpleName() {
			return simpleName;
		}
		public Optional<AstInterfaceDeclaration> getAstClassDecl() {
			return astClassDecl;
		}
		public Optional<AstInterfaceDeclaration> getAstClassDecl(DefaultSymbolTable symbolTable, Reporter reporter) {
			String nameText = simpleName.getText();
			Optional<Symbol> optSymbol = symbolTable.lookup(nameText);
			if(! optSymbol.isPresent()) {
				reporter.error("Symbol " + nameText + " not found.", simpleName);
				return Optional.empty();
			}
			
			Symbol symbol = optSymbol.get();
			Optional<AstInterfaceDeclaration> optClassDecl = symbol.getInterfaceDeclaration();
			if(optClassDecl.isPresent()) {
				AstInterfaceDeclaration ancestorCD = optClassDecl.get();
				return Optional.of(ancestorCD);
			} else {
				reporter.error("Symbol " + nameText + " is not an interface.", simpleName);
				return Optional.empty();
			}
		}
	}

	public List<PartialList> getFunctionDeclarations();

	public List<AncestorInfo> getAncestors();
	public List<AstInterfaceDeclaration> getInterfaces();

	public default boolean descendOrIsSameAs(AstInterfaceDeclaration ancestor) {
		if(isExactlyA(ancestor))
			return true;
		
		return getInterfaces().stream().anyMatch(intf -> {
			return intf.descendOrIsSameAs(ancestor); 
		});
	}
	
	public default boolean descendStrictlyFrom(AstInterfaceDeclaration ancestor) {
		return getInterfaces().stream().anyMatch(intf -> {
			return descendOrIsSameAs(intf); 
		});
	}
	
	public default MapOfList<QName, PartialList> getAllFunctions() {
		MapOfList<QName, PartialList> map = new MapOfList<>();

		// -- 
		for(PartialList func : getFunctionDeclarations()) {
			QName fqn = func.getqName();
			map.put(fqn, func);
		}
		
		for(AstInterfaceDeclaration acd: this.getInterfaces()) {
			MapOfList<QName, PartialList> amap  = acd.getAllFunctions();
			map.insert(amap);
		}
		
		return map;
	}
	public default List<InterfaceDeclaration> createDirectInterfaces() {
		List<AncestorInfo> ancestors = getAncestors();
		
		List<InterfaceDeclaration> interfaces = new ArrayList<>(ancestors.size());
		for(AncestorInfo ai: ancestors) {
			Optional<AstInterfaceDeclaration> opt = ai.getAstClassDecl();
			AstInterfaceDeclaration ancestorCD = opt.get();
			InterfaceDeclaration interf = ancestorCD.getInterfaceDeclaration();
			interfaces.add(interf);
		}
		return interfaces;
	}

	public void addAncestor(AstToken ancestor); // TODO: remove

}
