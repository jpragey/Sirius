package org.sirius.frontend.apiimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.ast.AstClassOrInterface.AncestorInfo;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstMemberValueDeclaration;

public class ClassDeclarationImpl implements ClassDeclaration {
	private QName qName;
	private MapOfList<QName, FunctionDefinition> allFctMap;
	private List<MemberValue> valueDeclarations;
	private List<AncestorInfo> ancestors;

	public ClassDeclarationImpl(QName qName, MapOfList<QName, FunctionDefinition> allFctMap, List<MemberValue> valueDeclarations, List<AncestorInfo> ancestors) {
		this.qName = qName;
		this.allFctMap = allFctMap;
		this.valueDeclarations = valueDeclarations;
		this.ancestors = ancestors;
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

	private List<InterfaceDeclaration> createDirectInterfaces() {

		List<InterfaceDeclaration> interfaces = new ArrayList<>(ancestors.size());
		for(AncestorInfo ai: ancestors) {
			Optional<AstInterfaceDeclaration> opt = ai.getAstClassDecl();
			AstInterfaceDeclaration ancestorCD = opt.get();
			InterfaceDeclaration interf = ancestorCD.getInterfaceDeclaration();
			interfaces.add(interf);
		}
		return interfaces;
	}

	@Override
	public String toString() {
		assert(qName != null);
		return "API class " + qName;
	}

}
