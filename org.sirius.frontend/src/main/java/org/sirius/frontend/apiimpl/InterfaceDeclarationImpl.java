package org.sirius.frontend.apiimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ExecutionEnvironment;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;

public record InterfaceDeclarationImpl(
		QName qName, 
		MapOfList<QName, FunctionDefinition> allFctMap,
		List<MemberValue> memberValues
) implements ClassType 
{

	@Override
	public List<AbstractFunction> memberFunctions() {
		List<AbstractFunction> functions = new ArrayList<>();
		for(QName qn: allFctMap.keySet()) {
			List<FunctionDefinition> functionDefs = allFctMap.get(qn);
			for(FunctionDefinition func: functionDefs) {
				for(Partial partial: func.getPartials()) {
					functions.add(partial.toAPI());
				}
			}
		}
		return functions;
	}
	@Override
	public QName qName() {
		return qName;
	}
	
	@Override
	public Optional<ExecutionEnvironment> executionEnvironment() {
		return Optional.empty();// TODO ???
	}
}