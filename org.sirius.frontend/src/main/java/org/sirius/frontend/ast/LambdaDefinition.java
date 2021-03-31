package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.core.MapOfList;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.ClassDeclarationImpl;
import org.sirius.frontend.apiimpl.FunctionImpl;
import org.sirius.frontend.symbols.SymbolTable;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class LambdaDefinition implements AstExpression, Verifiable, Visitable, Scoped {

	private AstType returnType; 
	private List<AstFunctionParameter> args;

	private FunctionBody body;
	
	private SymbolTableImpl symbolTable = null;
	
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
//	private FunctionImpl functionImpl = null;
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
		MapOfList<QName, FunctionDefinition> allFctMap = new MapOfList<>(); 	// TODO
		List<MemberValue> valueDeclarations = List.of();	// TODO
		List<AstInterfaceDeclaration> interfaces = List.of();	// TODO
		ClassDeclarationImpl classImpl = new ClassDeclarationImpl(lambdaClassQName, allFctMap, valueDeclarations, interfaces);

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
		FunctionImpl functionImpl = new FunctionImpl(lambdaQName, args, resolvedReturnType, apiStatements, member);
		assert(functionImpl.getArguments().size() == args.size());

		return functionImpl;
	}

	public APIFunctionInfo toAPI(QName lambdaQName) {
//		APIFunctionInfo functionInfoImpl
//		List<AstFunctionParameter> args = getArgs();
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
		
//		functionInfo.g
		
//		return Optional.empty();	// TODO
		throw new UnsupportedOperationException();	
	}

	@Override
	public String asString() {
		return "<lambda>" + qName.orElse(QName.empty).toString() + "{}";

//		throw new UnsupportedOperationException();	// TODO
	}
	

	@Override
	public SymbolTable getSymbolTable() {
		assert(this.symbolTable != null);
		return this.symbolTable;
	}

	public void setSymbolTable(SymbolTableImpl symbolTable) {
		this.symbolTable = symbolTable;
	}

	@Override
	public String toString() {
		String s = asString();
		return s;
	}
	
}
