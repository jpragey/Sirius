package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.ClassDeclarationImpl;
import org.sirius.frontend.apiimpl.FunctionDeclarationImpl;
import org.sirius.frontend.apiimpl.FunctionImpl;

public class LambdaDeclaration implements AstType, Verifiable, Visitable {

	private LambdaClosure closure;
	private AstType returnType; 
	private List<AstType> args;
	

	public LambdaDeclaration(LambdaClosure closure, List<AstType> args, AstType returnType) {
		this.closure = closure;
		this.args = args;
		this.returnType = returnType;
	}

	public LambdaDeclaration(List<AstType> args, AstType returnType) {
		this(new LambdaClosure(), args, returnType);
	}

	public AstType getReturnType() {
		return returnType;
	}

	public List<AstType> getArgTypes() {
		return args;
	}

	@Override
	public void verify(int featureFlags) {
		returnType.verify(featureFlags);
		verifyList(args, featureFlags);
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startLambdaDeclaration(this);
//		closure.v
		returnType.visit(visitor);
		args.forEach(paramType -> {
			paramType.visit(visitor);
		});
		visitor.endLambdaDeclaration(this);
	}

//	private Optional<QName> qName = Optional.empty();
	
	
//	public QName getqName() {
//		return qName.get();
//	}

//	public void setqName(QName qName) {
//		assert(qName != null);
//		this.qName = Optional.of(qName);
//	}

	@Override
	public String messageStr() {
		return "<lambda>";
//		return "<lambda>" + qName.orElse(QName.empty).toString();
	}

	private org.sirius.frontend.api.FunctionDeclaration impl = null;
	@Override
	public Type getApiType() {
//		QName qName = this.qName.get();
//		MapOfList<QName, FunctionDefinition> allFctMap = new MapOfList<QName, FunctionDefinition>();	// TODO 
//		List<MemberValue> valueDeclarations = new ArrayList<MemberValue>() ;	// TODO
//		List<AstInterfaceDeclaration> interfaces = new ArrayList<AstInterfaceDeclaration>(); // TODO
//		
//		ClassType cd = new ClassDeclarationImpl(qName, allFctMap, valueDeclarations, interfaces);
//		return cd;
		if(this.impl == null) {
			Type retType = returnType.getApiType();
			List<Type> paramTypes = args.stream().map(AstType::getApiType).collect(Collectors.toUnmodifiableList());
			this.impl = new FunctionDeclarationImpl(retType, paramTypes);
//			this.impl = new FunctionImpl(QName functionQName, List<AstFunctionParameter> formalArguments, Type returnType, boolean member);
		}

		return this.impl;
//		throw new UnsupportedOperationException("");// TODO
	}

	@Override
	public AstType resolve() {
		throw new UnsupportedOperationException("");// TODO
	}

	@Override
	public boolean isExactlyA(AstType type) {
		throw new UnsupportedOperationException("");// TODO
	}

	@Override
	public boolean isStrictDescendantOf(AstType type) {
		throw new UnsupportedOperationException("");// TODO
	}

	@Override
	public boolean isAncestorOrSameAs(AstType type) {
		throw new UnsupportedOperationException("");// TODO
	}

	/** Move the first functionparameter to end of closures, in a new LambdaDeclaration  
	 * 
	 * @return
	 */
//	public LambdaDeclaration applyParameter() {
//		assert(!args.isEmpty());
//		
//		AstType firstArg = args.get(0);
//		LambdaClosure newClosure = closure.appendEntry(firstArg, firstArg.getName(), Optional.empty() /*Init expression*/);
//		List<AstType> newArgs = args.subList(1, args.size()-1);
//		
//		LambdaDeclaration newLambda = new LambdaDeclaration(newClosure, newArgs, this.returnType);
//		return newLambda;
//	}

}
