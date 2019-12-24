package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.sirius.frontend.api.ArrayType;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.StringConstantExpression;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.TypeCastExpression;
import org.sirius.frontend.ast.AstExpressionStatement;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstFunctionFormalArgument;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstStatement;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.parser.Compiler;
import org.testng.annotations.Test;

public class TopLevelFunctionTest {

	@Test
	public void findTopLevelFunction() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(){}");

		assertEquals(session.getModuleContents().size(), 1);
		
		AstModuleDeclaration md = session.getModuleContents().get(0).getModuleDeclaration();
		AstPackageDeclaration pd = md.getPackageDeclarations().get(0);
		
		assertEquals(pd.getFunctionDeclarations().size(), 1);
		AstFunctionDeclaration fd = pd.getFunctionDeclarations().get(0);
		
		assertEquals(fd.getName().getText(), "f");
		
		// -- As API
		List<ModuleDeclaration> mds = session.getModuleDeclarations();
		assertEquals(mds.size(), 1);
		assertEquals(mds.get(0).getPackages().size(), 1);
		PackageDeclaration apiPd = mds.get(0).getPackages().get(0);
		
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

		TopLevelFunction tlf = session.getModuleDeclarations().get(0).getPackages().get(0).getFunctions().get(0);
		
		assertEquals(tlf.getArguments().size(), 2);
		assertEquals(tlf.getArguments().get(0).getQName().dotSeparated(), "a.b.f.i");
		assertEquals(tlf.getArguments().get(1).getQName().dotSeparated(), "a.b.f.j");
	}

	@Test(description = "")
	public void checkFunctionBodyContainsAnExpressionStatement() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(){println(\"Hello World\");}");
		
		AstModuleDeclaration md = session.getModuleContents().get(0).getModuleDeclaration();
		AstPackageDeclaration pd = md.getPackageDeclarations().get(0);
		AstFunctionDeclaration fd = pd.getFunctionDeclarations().get(0);

		List<AstStatement> statements = fd.getStatements();
		assertEquals(statements.size(), 1);
		
		AstExpressionStatement printCallStmt = (AstExpressionStatement)statements.get(0);	// NOTE: type casting is used as an assertion
		AstFunctionCallExpression callExpression = (AstFunctionCallExpression)printCallStmt.getExpression();
		assertEquals(callExpression.getName().getText(), "println");
		
		
		
		// -- API
		TopLevelFunction tlf = session.getModuleDeclarations().get(0).getPackages().get(0).getFunctions().get(0);
		assertEquals(tlf.getQName().dotSeparated(), "a.b.f");
		
		List<Statement> apiStatements = tlf.getBodyStatements();
		assertEquals(apiStatements.size(), 1);
		ExpressionStatement functionCallst = (ExpressionStatement)apiStatements.get(0);
		
		FunctionCall functionCall = (FunctionCall)functionCallst.getExpression();
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
		
		AstModuleDeclaration md = session.getModuleContents().get(0).getModuleDeclaration();
		AstPackageDeclaration pd = md.getPackageDeclarations().get(0);
		AstFunctionDeclaration fd = pd.getFunctionDeclarations().get(0);
		
		assertEquals(fd.getFormalArguments().size(), 1);
		AstFunctionFormalArgument fctArg0 = fd.getFormalArguments().get(0);
		
		System.out.println("Arg: type=" + fctArg0.getType().getClass() + " : " + fctArg0.getType() + ", name=" + fctArg0.getName());
		
		// -- API
		List<FunctionFormalArgument> apiArgs = session.getModuleDeclarations().get(0).getPackages().get(0).getFunctions().get(0).getArguments();
		assertEquals(apiArgs.size(), 1);
		FunctionFormalArgument apiArg0 = apiArgs.get(0);
		
		System.out.println("Arg: type=" + apiArg0.getType().getClass() + " : " + apiArg0.getType() + ", name=" + apiArg0.getQName());
		assert (apiArg0.getType() instanceof ClassType);
		ClassType argType = (ClassType)apiArg0.getType();
		
		System.out.println("API arg type qname: " + argType.getQName());
		
		
	}
	
	@Test
	public void checkArrayFunctionArgumentFoundInApi() {
		ScriptSession session = Compiler.compileScript("#!\n module a.b \"1.0\" {}  void f(String[] s){}");
		
		AstModuleDeclaration md = session.getModuleContents().get(0).getModuleDeclaration();
		AstPackageDeclaration pd = md.getPackageDeclarations().get(0);
		AstFunctionDeclaration fd = pd.getFunctionDeclarations().get(0);
		
		assertEquals(fd.getFormalArguments().size(), 1);
		AstFunctionFormalArgument fctArg0 = fd.getFormalArguments().get(0);
		
		System.out.println("Arg: type=" + fctArg0.getType().getClass() + " : " + fctArg0.getType() + ", name=" + fctArg0.getName());
		
		// -- API
		List<FunctionFormalArgument> apiArgs = session.getModuleDeclarations().get(0).getPackages().get(0).getFunctions().get(0).getArguments();
		assertEquals(apiArgs.size(), 1);
		FunctionFormalArgument apiArg0 = apiArgs.get(0);
		
		System.out.println("Arg: type=" + apiArg0.getType().getClass() + " : " + apiArg0.getType() + ", name=" + apiArg0.getQName());
		assert (apiArg0.getType() instanceof ArrayType);
		ArrayType argType = (ArrayType)apiArg0.getType();
		
		System.out.println("API arg type qname: " + argType.getElementType());
		
		assert (argType.getElementType() instanceof ClassType);
		ClassType classType = (ClassType)argType.getElementType();
		System.out.println("API element type: " + classType.getQName());
	}
	
}
