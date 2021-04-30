package org.sirius.backend.jvm;

import java.util.List;
import java.util.Optional;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.IfElseStatement;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;

public class JvmStatementBlock implements JvmStatement {
	private Reporter reporter;
	private DescriptorFactory descriptorFactory;
	private List<Statement> statements;
	private ScopeManager scopeManager;
	
	public JvmStatementBlock( Reporter reporter, DescriptorFactory descriptorFactory, ScopeManager scopeManager, List<Statement> statements) {
		super();
		this.reporter = reporter;
		this.descriptorFactory = descriptorFactory;
		this.scopeManager = scopeManager;
		this.statements = statements;
	}

	// -- Write var init code at the start of function/block bytecode
	private void writeLocalVarsInitCode(JvmScope.JvmLocalVariable h, MethodVisitor mv, JvmScope scope) {
		int locvarIndex = h.getIndex();
		Optional<Expression> optInitExp = h.getInitExp();
		if(optInitExp.isPresent()) {
			Expression initExpr = optInitExp.get();
			JvmExpression jvmExpr = new JvmExpression(reporter, descriptorFactory, initExpr);
			jvmExpr.writeExpressionBytecode(mv, scope);

			mv.visitVarInsn(Opcodes.ASTORE, locvarIndex);

		}
	}

	@Override
	public void writeBytecode(ClassWriter classWriter, MethodVisitor mv) {

		JvmScope scope = scopeManager.enterNewScope("{block}" /*for debugging*/);


		// Collect local variables to generate initialization code
		for(Statement st: statements ) {
			if(st instanceof LocalVariableStatement) { 
				LocalVariableStatement locVarsStmt = (LocalVariableStatement)st;
				scope.addLocalVariable(locVarsStmt);
			}
		}
		// Write local var init code
		for(JvmScope.JvmLocalVariable h: scope.getLocVarsStmts()) {
			writeLocalVarsInitCode(h, mv, scope);
		}

		for(Statement st: statements ) {
			JvmStatement jvmStatement = null;
			if(st instanceof ReturnStatement) {
				jvmStatement = new JvmReturnStatement(reporter, descriptorFactory, (ReturnStatement)st, scope); 
			} else if(st instanceof IfElseStatement) {
				jvmStatement = new JvmIfElseStatement(reporter, descriptorFactory, scopeManager, (IfElseStatement)st, scope);
			} else if(st instanceof ExpressionStatement) {
				jvmStatement = new JvmExpressionStatement(reporter, descriptorFactory, (ExpressionStatement)st, scope);
			} else if(st instanceof LocalVariableStatement) { 
				// Ignore
			} else {
				throw new UnsupportedOperationException("No bytecode to write for statement " + st.getClass().getCanonicalName());
			}

			if(jvmStatement != null) {// TODO
				jvmStatement.writeBytecode(classWriter, mv);
			}
		}

		scope.markEnd();
		scopeManager.leaveScope();
	}
}