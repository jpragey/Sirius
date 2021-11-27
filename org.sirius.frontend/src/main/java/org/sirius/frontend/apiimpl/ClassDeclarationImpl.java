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
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;

public class ClassDeclarationImpl implements ClassType {
	private QName qName;

	private MapOfList<QName, AbstractFunction /*FunctionDefinition*/> allFctMap;
	private List<AbstractFunction> memberFunctions;

	private List<MemberValue> valueDeclarations;
	private List<AstInterfaceDeclaration> interfaces;
	public Optional<ExecutionEnvironment> executionEnvironment;

	public ClassDeclarationImpl(QName qName, 
//			MapOfList<QName, AbstractFunction /* FunctionDefinition*/> allFctMap,
			List<AbstractFunction> memberFunctions,
			List<MemberValue> valueDeclarations, //List<AncestorInfo> ancestors,
			List<AstInterfaceDeclaration> interfaces, 
			Optional<ExecutionEnvironment> executionEnvironment) {
		this.qName = qName;
//		this.allFctMap = allFctMap;
		this.memberFunctions = memberFunctions;
		this.valueDeclarations = valueDeclarations;
		this.interfaces = interfaces;
		this.executionEnvironment = executionEnvironment;
	}


	private static ArrayList<AbstractFunction> toMemberFunctionss(MapOfList<QName, FunctionDefinition> allFctMap) {
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

	@Override
	public List<MemberValue> getMemberValues() {
		return valueDeclarations;
	}

	@Override
	public List<AbstractFunction> getFunctions() {

//		ArrayList<AbstractFunction> memberFunctions = new ArrayList<>();

//		ArrayList<AbstractFunction> memberFunctions = toMemberFunctionss(allFctMap);

//		for(QName qn: allFctMap.keySet()) {
//			List<FunctionDefinition> functions = allFctMap.get(qn);
//			for(FunctionDefinition func: functions) {
//				for(Partial partial: func.getPartials()) {
//					memberFunctions.add(partial.toAPI());
//				}
//			}
//		}
		assert(memberFunctions != null);
		return memberFunctions;
	}

	@Override
	public QName getQName() {
		assert(qName != null);
		return qName;
	}

	@Override
	public String toString() {
		assert(qName != null);
		return "API class " + qName;
	}

	@Override
	public Optional<ExecutionEnvironment> getExecutionEnvironment() {
		return this.executionEnvironment;
	}

}
