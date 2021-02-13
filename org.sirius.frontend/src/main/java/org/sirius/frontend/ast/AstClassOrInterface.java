package org.sirius.frontend.ast;

import java.util.List;

import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;

public interface AstClassOrInterface extends AstType {

	public List<FunctionDeclaration> getFunctionDeclarations();
	public List<FunctionDefinition> getFunctionDefinitions();

	public List<AstToken> getAncestors();
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
	
	public default MapOfList<QName, FunctionDefinition> getAllFunctions() {
		MapOfList<QName, FunctionDefinition> map = new MapOfList<>();

		// -- 
		for(FunctionDefinition func : getFunctionDefinitions()) {
			QName fqn = func.getqName();
			map.put(fqn, func);
		}
		
		for(AstInterfaceDeclaration acd: this.getInterfaces()) {
			MapOfList<QName, FunctionDefinition> amap  = acd.getAllFunctions();
			map.insert(amap);
		}
		
		return map;
	}

	public void addAncestor(AstToken ancestor); // TODO: remove

}
