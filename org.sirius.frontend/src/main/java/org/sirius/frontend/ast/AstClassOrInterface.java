package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;

import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;

public interface AstClassOrInterface extends AstType {

	public List<FunctionDeclaration> getFunctionDeclarations();
	public List<FunctionDefinition> getFunctionDefinitions();

	public List<AstToken> getAncestors();

	public default MapOfList<QName, FunctionDefinition> getAllFunctions() {
		MapOfList<QName, FunctionDefinition> map = new MapOfList<>();

		// -- Function from class itself
		for(FunctionDefinition func : getFunctionDefinitions()) {
			QName fqn = func.getqName();
			map.put(fqn, func);
		}
		
		return map;
	}
	
	public default List<AbstractFunction> getAllApiMemberFunctions() {
		MapOfList<QName, FunctionDefinition> allFctMap = getAllFunctions();
		ArrayList<AbstractFunction> memberFunctions = new ArrayList<>();

		for(QName qn: allFctMap.keySet()) {
			List<FunctionDefinition> functions = allFctMap.get(qn);
			for(FunctionDefinition func: functions) {
				for(Partial partial: func.getPartials()) {
					AbstractFunction functionImpl = partial.toAPI(); 
					memberFunctions.add(functionImpl);
				}
			}
		}
		return memberFunctions;
	}


	public void addAncestor(AstToken ancestor); // TODO: remove

}
