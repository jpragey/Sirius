package org.sirius.frontend.apiimpl;

import java.util.ArrayList;
import java.util.List;

import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;

public class InterfaceDeclarationImpl implements ClassType {
	private QName qName;
	private MapOfList<QName, FunctionDefinition> allFctMap;
	private List<MemberValue> memberValues; 

	public InterfaceDeclarationImpl(QName qName, MapOfList<QName, FunctionDefinition> allFctMap,
			List<MemberValue> memberValues) {
		super();
		this.qName = qName;
		this.allFctMap = allFctMap;
		this.memberValues = memberValues;
	}

	@Override
	public List<MemberValue> getMemberValues() {
		return memberValues;
	}

	@Override
	public List<AbstractFunction> getFunctions() {
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
	public QName getQName() {
		return qName;
	}
	@Override
	public boolean isAncestorOrSame(Type type) {
		throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
	}
}