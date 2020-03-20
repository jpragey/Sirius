package org.sirius.frontend.integration;

import static org.testng.Assert.assertEquals;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.TypeCastExpression;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.parser.Compiler;
import org.sirius.frontend.symbols.Symbol;
import org.testng.annotations.Test;

public class SdkCallTest {

	@Test(enabled = false) // TODO println() Doesn't take Stringifiable for now
	public void sdkCalled() {
//		ScriptSession session = Compiler.compileScript("#!\n void run() {println(\"Hello\");}");
		ScriptSession session = Compiler.compileScript("#!\n void run() {println(42);}");

		assertEquals(session.getModuleDeclarations().size(), 1);
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.getPackages().get(0);
		TopLevelFunction runFunction = pack.getFunctions().get(0);
		
		Statement callExprStatement = runFunction.getBodyStatements().get(0);
		assert(callExprStatement instanceof ExpressionStatement);

		Expression callExpression = ((ExpressionStatement)callExprStatement).getExpression();
		assert(callExpression instanceof FunctionCall);
		FunctionCall functionCall = (FunctionCall)callExpression;
		
		assertEquals(functionCall.getArguments().size(), 1);
		Expression argExpression = functionCall.getArguments().get(0);

		TypeCastExpression argTypeCast = (TypeCastExpression) argExpression;
		IntegerConstantExpression argIntConstant = (IntegerConstantExpression)argTypeCast.expression();
		
		assertEquals(argIntConstant.getValue(), 42);
		
		Symbol symbol = session.getGlobalSymbolTable().lookup(new QName("sirius", "lang", "println")).get();
		
	}
	
}