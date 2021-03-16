package org.sirius.frontend.apiimpl;

import java.util.ArrayList;
import java.util.List;

import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;

public class ClassDeclarationImpl implements ClassType {
	private QName qName;
	private MapOfList<QName, FunctionDefinition> allFctMap;
	private List<MemberValue> valueDeclarations;
	private List<AstInterfaceDeclaration> interfaces;

	public ClassDeclarationImpl(QName qName, MapOfList<QName, FunctionDefinition> allFctMap, List<MemberValue> valueDeclarations, //List<AncestorInfo> ancestors,
			List<AstInterfaceDeclaration> interfaces) {
		this.qName = qName;
		this.allFctMap = allFctMap;
		this.valueDeclarations = valueDeclarations;
//		this.ancestors = ancestors;
		this.interfaces = interfaces;
	}

	@Override
	public List<MemberValue> getMemberValues() {
		return valueDeclarations;
	}

	@Override
	public List<AbstractFunction> getFunctions() {

		ArrayList<AbstractFunction> memberFunctions = new ArrayList<>();

		for(QName qn: allFctMap.keySet()) {
			List<FunctionDefinition> functions = allFctMap.get(qn);
			for(FunctionDefinition func: functions) {
				for(Partial partial: func.getPartials()) {
					memberFunctions.add(partial.toAPI());
				}
			}
		}
		return memberFunctions;
	}

	@Override
	public QName getQName() {
		assert(qName != null);
		return qName;
	}
	@Override
	public boolean isAncestorOrSame(Type type) {
		throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
	}

	@Override
	public String toString() {
		assert(qName != null);
		return "API class " + qName;
	}

}
