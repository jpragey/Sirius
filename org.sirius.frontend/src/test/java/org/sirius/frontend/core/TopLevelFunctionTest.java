package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.sirius.frontend.api.ArrayType;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.StringConstantExpression;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.TypeCastExpression;
import org.sirius.frontend.ast.AstExpressionStatement;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.parser.Compiler;
import org.testng.annotations.Test;

public class TopLevelFunctionTest {

	@Test
	public void findTopLevelFunction() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(){}");

		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 2);
		
		ModuleDeclaration md = moduleDeclarations.get(1);
		PackageDeclaration pd = md.getPackages().get(0);
		
		assertEquals(pd.getFunctions().size(), 1);
		TopLevelFunction fd = pd.getFunctions().get(0);
		
		assertEquals(fd.getQName().getLast(), "f");
		
		// -- As API
		assertEquals(md.getPackages().size(), 1);
		PackageDeclaration apiPd = md.getPackages().get(0);
		
		assertEquals(apiPd.getFunctions().size(), 1);
		TopLevelFunction tlf = apiPd.getFunctions().get(0);
		
		assertEquals(tlf.getQName().dotSeparated(), "a.b.f");
	}

	@Test(description = "")
	public void checkFunctionArgumentsInAPIFunction() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(Integer i, Integer j){}");
		
//		AstModuleDeclaration md = session.getModuleContents().get(0).getModuleDeclaration();
//		AstPackageDeclaration pd = md.getPackageDeclarations().get(0);
//		AstFunctionDeclaration fd = pd.getFunctionDeclarations().get(0);

		TopLevelFunction tlf = session.getModuleDeclarations().get(1).getPackages().get(0).getFunctions().get(0);
		
		assertEquals(tlf.getArguments().size(), 2);
		assertEquals(tlf.getArguments().get(0).getQName().dotSeparated(), "a.b.f.i");
		assertEquals(tlf.getArguments().get(1).getQName().dotSeparated(), "a.b.f.j");
	}

	
//	@Test(description = "", enabled = true)
//	public void checkPartialParsingCausesAnError() {
//		ScriptSession session = Compiler.compileScript(
//				"#!\n module a.b \"1.0\" {} "
//				//+ "import sirius.lang {String} "
//				+ "void f(){println(\"Hello World\");}"
//				//+ "impor t sirius.lang1 {String1 "
//				);
//		assertEquals(session.getReporter().getErrorCount(), 1);
//	}
	
	@Test(description = "", enabled = true)
	public void checkFunctionBodyContainsAnExpressionStatement() {
		ScriptSession session = Compiler.compileScript(
				"#!\n "
				+ "import sirius.lang {String} "
				+ "module a.b \"1.0\" {} "
				+ "void f(){println(\"Hello World\");}");
		
		ModuleDeclaration md0 = session.getModuleDeclarations().get(0);
		
		ModuleDeclaration md1 = session.getModuleDeclarations().get(1);
		PackageDeclaration pd = md1.getPackages().get(0);
		
		List<TopLevelFunction> funcs = pd.getFunctions();
		TopLevelFunction fd = funcs.get(0);

		List<Statement> statements = fd.getBodyStatements();
		assertEquals(statements.size(), 1);
		Statement statement0 = statements.get(0);
		ExpressionStatement printCallStmt = (ExpressionStatement)statement0;	// NOTE: type casting is used as an assertion
		//Expression fctCallExpr = printCallStmt.getExpression();
		FunctionCall functionCall = (FunctionCall)printCallStmt.getExpression();
//		assertEquals(functionCall.getFunctionName().getText(), "println");
		
		
		
//		// -- API
//		TopLevelFunction tlf = session.getModuleDeclarations().get(0).getPackages().get(0).getFunctions().get(0);
//		assertEquals(tlf.getQName().dotSeparated(), "a.b.f");
//		
//		List<Statement> apiStatements = tlf.getBodyStatements();
//		assertEquals(apiStatements.size(), 1);
//		ExpressionStatement functionCallst = (ExpressionStatement)apiStatements.get(0);
//		
//		FunctionCall functionCall = (FunctionCall)functionCallst.getExpression();
		assertEquals(functionCall.getFunctionName().getText(), "println");
		assertEquals(functionCall.getArguments().size(), 1);

		// -- arg 0 is a TypeCastExpression because println requires an Stringifiable
		Expression arg0TypeCastExpr = functionCall.getArguments().get(0);
		assert(arg0TypeCastExpr instanceof TypeCastExpression);
		TypeCastExpression strTypeCastExpr = (TypeCastExpression)arg0TypeCastExpr;
		
		Expression arg0Expr = strTypeCastExpr.expression();
		assert(arg0Expr instanceof StringConstantExpression);
		StringConstantExpression strExpr = (StringConstantExpression)arg0Expr;
		
		assertEquals(strExpr.getContent().getText(), "\"Hello World\"");
		assertEquals(strExpr.getText(), "Hello World");
		
	}

	@Test
	public void checkFunctionArgumentFoundInApi() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(String s){}");
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 2);

		ModuleDeclaration md = moduleDeclarations.get(1);
		PackageDeclaration pd = md.getPackages().get(0);
		TopLevelFunction fd = pd.getFunctions().get(0);
		
		assertEquals(fd.getArguments().size(), 1);
		FunctionFormalArgument fctArg0 = fd.getArguments().get(0);
		
//		System.out.println("Arg: type=" + fctArg0.getType().getClass() + " : " + fctArg0.getType() + ", name=" + fctArg0.getQName().getLast());
		
		// -- API
		List<FunctionFormalArgument> apiArgs = moduleDeclarations.get(1).getPackages().get(0).getFunctions().get(0).getArguments();
		assertEquals(apiArgs.size(), 1);
		FunctionFormalArgument apiArg0 = apiArgs.get(0);
		
//		System.out.println("Arg: type=" + apiArg0.getType().getClass() + " : " + apiArg0.getType() + ", name=" + apiArg0.getQName());
		assert (apiArg0.getType() instanceof ClassType);
		ClassType argType = (ClassType)apiArg0.getType();
		
//		System.out.println("API arg type qname: " + argType.getQName());
		
		
	}
	
	@Test
	public void checkArrayFunctionArgumentFoundInApi() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(String[] s){}");
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 2);
		ModuleDeclaration md = moduleDeclarations.get(1);
		PackageDeclaration pd = md.getPackages().get(0);
		TopLevelFunction fd = pd.getFunctions().get(0);
		
		assertEquals(fd.getArguments().size(), 1);
		FunctionFormalArgument fctArg0 = fd.getArguments().get(0);
		
//		System.out.println("Arg: type=" + fctArg0.getType().getClass() + " : " + fctArg0.getType() + ", name=" + fctArg0.getQName().getLast());
		
		// -- API
		List<FunctionFormalArgument> apiArgs = moduleDeclarations.get(1).getPackages().get(0).getFunctions().get(0).getArguments();
		assertEquals(apiArgs.size(), 1);
		FunctionFormalArgument apiArg0 = apiArgs.get(0);
		
//		System.out.println("Arg: type=" + apiArg0.getType().getClass() + " : " + apiArg0.getType() + ", name=" + apiArg0.getQName());
		assert (apiArg0.getType() instanceof ArrayType);
		ArrayType argType = (ArrayType)apiArg0.getType();
		
//		System.out.println("API arg type qname: " + argType.getElementType());
		
		assert (argType.getElementType() instanceof ClassType);
		ClassType classType = (ClassType)argType.getElementType();
//		System.out.println("API element type: " + classType.getQName());
	}
	
	@Test
	public void checkFunctionLocalArgument() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(){String s ;}");
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 2);
		
		ModuleDeclaration md = session.getModuleDeclarations().get(1);
		PackageDeclaration pd = md.getPackages().get(0);
		TopLevelFunction fd = pd.getFunctions().get(0);

		assertEquals(fd.getBodyStatements().size(), 1);
		Statement st0 = fd.getBodyStatements().get(0);
		assert(st0 instanceof LocalVariableStatement);
	}
	
	@Test(description = "Check that a function returning a String return in fact a sirius.lang.String")
	public void checkFunctionReturnsConvertsStringIntoSiriusLangString() {
		ScriptSession session = Compiler.compileScript("#!\n String f(){ return \"\";}");
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		PackageDeclaration pd = md.getPackages().get(0);
		TopLevelFunction fd = pd.getFunctions().get(0);
		Type type = fd.getReturnType();
		
		assert(type instanceof ClassDeclaration);
		ClassDeclaration classDeclaration = (ClassDeclaration)type;
		assertEquals(classDeclaration.getQName().dotSeparated(), "sirius.lang.String");
	}
	
	@Test(description = "Check that a String function parameter takes in fact a sirius.lang.String")
	public void checkStringFunctionParameterIsASiriusLangString() {
		ScriptSession session = Compiler.compileScript("#!\n void f(String s){}");
		
		List<ModuleDeclaration> moduleDeclarations = session.getModuleDeclarations();
		assertEquals(moduleDeclarations.size(), 1);
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		PackageDeclaration pd = md.getPackages().get(0);
		TopLevelFunction fd = pd.getFunctions().get(0);
		
		List<FunctionFormalArgument> args = fd.getArguments();
		assertEquals(args.size(), 1);
		FunctionFormalArgument arg = args.get(0);
		
		ClassDeclaration argType = (ClassDeclaration)arg.getType();
		assertEquals(argType.getQName().dotSeparated(), "sirius.lang.String");
	}
	
	
	
	
}
