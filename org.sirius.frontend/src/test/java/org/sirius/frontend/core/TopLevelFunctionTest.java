package org.sirius.frontend.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ArrayType;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.FunctionParameter;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.StringConstantExpression;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.TypeCastExpression;
import org.sirius.frontend.apiimpl.ScopeImpl;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.parser.Compiler;

public class TopLevelFunctionTest {

	@Test
	public void findTopLevelFunction() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(){}");
		
		// -- AST
		List<AstModuleDeclaration> astModules = session.getAstModules();
		AstModuleDeclaration astModule = astModules.get(0);
		AstPackageDeclaration astPackage = astModule.getPackageDeclarations().get(0);
				
		// -- API
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);
		
		ModuleDeclaration md = moduleDeclarations.get(0);
		PackageDeclaration pd = md.packageDeclarations().get(0);
		
		assertEquals(pd.getFunctions().size(), 1);
		AbstractFunction fd = pd.getFunctions().get(0);
		
		assertEquals(fd.qName().get().getLast(), "f");
		
		// -- As API
		assertEquals(md.packageDeclarations().size(), 1);
		PackageDeclaration apiPd = md.packageDeclarations().get(0);
		
		assertEquals(apiPd.getFunctions().size(), 1);
		AbstractFunction tlf = apiPd.getFunctions().get(0);
		
		assertEquals(tlf.qName().get().dotSeparated(), "a.b.f");
	}

	@Test
	public void checkFunctionArgumentsInAPIFunction() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(Integer i, Integer j){}");

		AbstractFunction tlf = session.getModuleDeclarations().get(0).packageDeclarations().get(0).getFunctions().get(2);
		
		assertEquals(tlf.parameters().size(), 2);
		assertEquals(tlf.parameters().get(0).getQName().dotSeparated(), "a.b.f.i");
		assertEquals(tlf.parameters().get(1).getQName().dotSeparated(), "a.b.f.j");
	}

	@Test
	public void checkFunctionBodyContainsAnExpressionStatement() {
		ScriptSession session = Compiler.compileScript(
				"#!\n "
				+ "import sirius.lang {String} "
				+ "module a.b \"1.0\" {} "
				+ "void f(){println(\"Hello World\");}");
		
		ModuleDeclaration md1 = session.getModuleDeclarations().get(0);
		PackageDeclaration pd = md1.packageDeclarations().get(0);
		
		List<AbstractFunction> funcs = pd.getFunctions();
		AbstractFunction fd = funcs.get(0);

		List<Statement> statements = fd.bodyStatements();
		assertEquals(statements.size(), 1);
		Statement statement0 = statements.get(0);
		ExpressionStatement printCallStmt = (ExpressionStatement)statement0;	// NOTE: type casting is used as an assertion
		//Expression fctCallExpr = printCallStmt.getExpression();
		FunctionCall functionCall = (FunctionCall)printCallStmt.expression();
//		assertEquals(functionCall.getFunctionName().getText(), "println");
		
		
		assertEquals(functionCall.nameToken().getText(), "println");
		assertEquals(functionCall.arguments().size(), 1);

		// -- arg 0 is a TypeCastExpression because println requires an Stringifiable
		Expression arg0TypeCastExpr = functionCall.arguments().get(0);
		assert(arg0TypeCastExpr instanceof TypeCastExpression);
		TypeCastExpression strTypeCastExpr = (TypeCastExpression)arg0TypeCastExpr;
		
		Expression arg0Expr = strTypeCastExpr.expression();
//		Expression arg0Expr = functionCall.getArguments().get(0);
		assert(arg0Expr instanceof StringConstantExpression);
		StringConstantExpression strExpr = (StringConstantExpression)arg0Expr;
		
		assertEquals(strExpr.getContent().getText(), "\"Hello World\"");
		assertEquals(strExpr.getText(), "Hello World");
		
	}

	@Test
	public void checkFunctionArgumentFoundInApi() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(String s){}");
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);

		ModuleDeclaration md = moduleDeclarations.get(0);
		PackageDeclaration pd = md.packageDeclarations().get(0);
		AbstractFunction fd = pd.getFunctions().get(1);
		
		assertThat(fd.parameters().size(), is(1));
		FunctionParameter fctArg0 = fd.parameters().get(0);
		
//		System.out.println("Arg: type=" + fctArg0.getType().getClass() + " : " + fctArg0.getType() + ", name=" + fctArg0.getQName().getLast());
		
		// -- API
		List<FunctionParameter> apiArgs = moduleDeclarations.get(0).packageDeclarations().get(0).getFunctions().get(1).parameters();
		assertEquals(apiArgs.size(), 1);
		FunctionParameter apiArg0 = apiArgs.get(0);
		
//		System.out.println("Arg: type=" + apiArg0.getType().getClass() + " : " + apiArg0.getType() + ", name=" + apiArg0.getQName());
		assertThat(apiArg0.getType(),  instanceOf(ClassType.class));
//		assert (apiArg0.getType() instanceof ClassType);
		ClassType argType = (ClassType)apiArg0.getType();
		
//		System.out.println("API arg type qname: " + argType.getQName());
		
		
	}
	
	@Test
	public void checkArrayFunctionArgumentFoundInApi() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(String[] s){}");
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);
		ModuleDeclaration md = moduleDeclarations.get(0);
		PackageDeclaration pd = md.packageDeclarations().get(0);
		AbstractFunction fd = pd.getFunctions().get(1);
		
		assertEquals(fd.parameters().size(), 1);
		FunctionParameter fctArg0 = fd.parameters().get(0);
		
		
//		System.out.println("Arg: type=" + fctArg0.getType().getClass() + " : " + fctArg0.getType() + ", name=" + fctArg0.getQName().getLast());
		
		// -- API
		List<AbstractFunction> apiFunc = moduleDeclarations.get(0).packageDeclarations().get(0).getFunctions();

		assertEquals(apiFunc.get(0).parameters().size(), 0);
		assertEquals(apiFunc.get(1).parameters().size(), 1);
		
		List<FunctionParameter> apiArgs = apiFunc.get(1).parameters();
		assertEquals(apiArgs.size(), 1);
		FunctionParameter apiArg0 = apiArgs.get(0);
		
//		System.out.println("Arg: type=" + apiArg0.getType().getClass() + " : " + apiArg0.getType() + ", name=" + apiArg0.getQName());
		assert (apiArg0.getType() instanceof ArrayType);
		ArrayType argType = (ArrayType)apiArg0.getType();
		
//		System.out.println("API arg type qname: " + argType.getElementType());
		
		assert (argType.elementType() instanceof ClassType);
		ClassType classType = (ClassType)argType.elementType();
//		System.out.println("API element type: " + classType.getQName());
	}
	
	@Test
	public void checkFunctionLocalArgument() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(){String s ;}");
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		PackageDeclaration pd = md.packageDeclarations().get(0);
		AbstractFunction fd = pd.getFunctions().get(0);

		assertEquals(fd.bodyStatements().size(), 1);
		Statement st0 = fd.bodyStatements().get(0);
		assert(st0 instanceof LocalVariableStatement);
	}
	
	@Test
	@DisplayName("Check that a function returning a String return in fact a sirius.lang.String")
	public void checkFunctionReturnsConvertsStringIntoSiriusLangString() {
		ScriptSession session = Compiler.compileScript("#!\n String f(){ return \"\";}");
		
		// -- AST
		assertThat(session.getAstModules().size(), equalTo(1));
		AstModuleDeclaration astModule = session.getAstModules().get(0);
		
		assertThat(astModule.getPackageDeclarations().size(), equalTo(1));
		AstPackageDeclaration astPackage = astModule.getPackageDeclarations().get(0);
		
		// -- API
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		PackageDeclaration pd = md.packageDeclarations().get(0);
		AbstractFunction fd = pd.getFunctions().get(0);
		Type type = fd.returnType();
		
		assert(type instanceof ClassType);
		ClassType classDeclaration = (ClassType)type;
		assertEquals(classDeclaration.qName().dotSeparated(), "sirius.lang.String");
	}
	
	@Test
	@DisplayName("Check that a String function parameter takes in fact a sirius.lang.String")
	public void checkStringFunctionParameterIsASiriusLangString() {
		ScriptSession session = Compiler.compileScript("#!\n void f(String s){}");
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		PackageDeclaration pd = md.packageDeclarations().get(0);
		assertEquals(pd.getFunctions().size(), 2);
		AbstractFunction fd = pd.getFunctions().get(1);
		
		List<FunctionParameter> args = fd.parameters();
		assertEquals(args.size(), 1);
		FunctionParameter arg = args.get(0);
		
		ClassType argType = (ClassType)arg.getType();
		assertEquals(argType.qName().dotSeparated(), "sirius.lang.String");
	}
	
}
