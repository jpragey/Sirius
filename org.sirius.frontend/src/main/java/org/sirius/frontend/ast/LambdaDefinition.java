package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.Annotation;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ExecutionEnvironment;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FunctionParameter;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.ClassDeclarationImpl;
import org.sirius.frontend.apiimpl.FunctionImpl;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.SymbolTable;

public class LambdaDefinition implements AstExpression, Verifiable, Visitable, Scoped {

	private AstType returnType; 
	private List<AstFunctionParameter> args;

	private FunctionBody body;
	
	private Scope scope = null;
	
	public static class APIFunctionInfo {
		private FunctionImpl functionType = null;
		private ClassType classType = null;
		
		public APIFunctionInfo(FunctionImpl functionType, ClassType classType) {
			super();
			this.functionType = functionType;
			this.classType = classType;
		}
		public FunctionImpl getFunctionType() {
			return functionType;
		}
		public ClassType getClassType() {
			return classType;
		}
		
	}
	private APIFunctionInfo functionInfoImpl = null;

	private Optional<QName> qName = Optional.empty();

	public LambdaDefinition(List<AstFunctionParameter> args, AstType returnType, FunctionBody body) {
		this.args = args;
		this.returnType = returnType;
		this.body = body;
	}
	
	public QName getqName() {
		return qName.get();
	}

	public void setqName(QName qName) {
		assert(qName != null);
		this.qName = Optional.of(qName);
	}

	public AstType getReturnType() {
		return returnType;
	}

	public List<AstFunctionParameter> getArgs() {
		return args;
	}

	public FunctionBody getBody() {
		return body;
	}

	@Override
	public void verify(int featureFlags) {
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startLambdaDefinition(this);
		visitor.endLambdaDefinition(this);
	}

	private ClassType toFunctionObjectAPI(QName lambdaQName) {
		QName lambdaClassQName = lambdaQName;
//		MapOfList<QName, FunctionDefinition> allFctMap = new MapOfList<>(); 	// TODO
		List<AbstractFunction> allFct = List.of(); // TODO
		List<MemberValue> valueDeclarations = List.of();	// TODO
		Optional<ExecutionEnvironment> execEnv = Optional.empty();	// TODO ???
		ClassDeclarationImpl classImpl = new ClassDeclarationImpl(lambdaClassQName, allFct, valueDeclarations, execEnv);

		return classImpl;
	}
	private FunctionImpl toFunctionAPI(QName lambdaQName) {

		List<AstFunctionParameter> args = getArgs();
		Type resolvedReturnType = returnType.getApiType();

		List<AstStatement> bodyStmts = body.getStatements();


		List<Statement> apiStatements = new ArrayList<>(bodyStmts.size());
		for(AstStatement stmt:  bodyStmts) {
			Optional<Statement> optSt = stmt.toAPI();
			assert(optSt.isPresent());	// TODO
			Statement st = optSt.get();
			apiStatements.add(st);
		}
		boolean member = false;	// TODO: ???
//		FunctionImpl functionImpl = new FunctionImpl(lambdaQName, args, resolvedReturnType, apiStatements, member);
		List<FunctionParameter> fctApiParams = args.stream().map(astFctParam -> astFctParam.toAPI(lambdaQName)).toList();
		List<Annotation> apiAnnos = List.of();	// TODO: ???
		FunctionImpl functionImpl = new FunctionImpl(apiAnnos, Optional.of(lambdaQName),fctApiParams, resolvedReturnType, apiStatements, member);
		assert(functionImpl.parameters().size() == args.size());

		return functionImpl;
	}

	public APIFunctionInfo toAPI(QName lambdaQName) {
		if(this.functionInfoImpl == null) {
			this.functionInfoImpl = new APIFunctionInfo(toFunctionAPI(lambdaQName), toFunctionObjectAPI(lambdaQName));
		}

		return this.functionInfoImpl;
	}

	@Override
	public AstExpression linkToParentST(SymbolTable parentSymbolTable) {
		throw new UnsupportedOperationException();	// TODO
	}

	@Override
	public AstType getType() {
		throw new UnsupportedOperationException();	// TODO
	}

	@Override
	public Optional<Expression> getExpression() {
		assert(this.qName.isPresent());
		APIFunctionInfo functionInfo =  toAPI(this.qName.get());
		
//		return Optional.empty();	// TODO
		throw new UnsupportedOperationException();	
	}

	@Override
	public String asString() {
//		return "<lambda>" + qName.orElse(QName.empty).toString() + "{}";
		return "<lambda>" + qName.map(QName::toString).orElse("") + "{}";
	}
	
	public Scope createScope(Scope parent) {
		this.scope = new Scope(parent, ":" + getqName().dotSeparated());
		return this.scope;
	}


	@Override
	public String toString() {
		String s = asString();
		return s;
	}

	@Override
	public Scope getScope() {
		assert(this.scope != null);
		return this.scope;
	}

	@Override
	public void setScope2(Scope scope) {
		assert(this.scope == null);
		assert(scope != null);
		this.scope = scope;
	}

}
