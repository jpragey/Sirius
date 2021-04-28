package org.sirius.backend.jvm;

import java.util.Arrays;
import java.util.Optional;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.IfElseStatement;
import org.sirius.frontend.api.Statement;

public class JvmIfElseStatement implements JvmStatement {
	private Reporter reporter;
	private DescriptorFactory descriptorFactory;
	private ScopeManager scopeManager;
	private IfElseStatement statement;
	private JvmScope scope;
	
	public JvmIfElseStatement(Reporter reporter, DescriptorFactory descriptorFactory, ScopeManager scopeManager, IfElseStatement statement,
			JvmScope scope) {
		super();
		this.reporter = reporter;
		this.descriptorFactory = descriptorFactory;
		this.scopeManager = scopeManager;
		this.statement = statement;
		this.scope = scope;
	}
	@Override
	public void writeBytecode(ClassWriter classWriter, MethodVisitor mv) {
		/*
		 * if-then :
		 *   evaluate expr
		 *   IFEQ goto endifLabel
		 *   ifBlock
		 * endifLabel:
		 * 
		 * if-then-else :
		 *   evaluate expr
		 *   IFEQ goto endifLabel
		 *   <ifBlock>
		 * endifLabel:
		 * 	 GOTO endElseBlock
		 *   <elseBlock>
		 * endElseBlock
		 */


		Expression expr = statement.getExpression();
		Statement ifStmt = statement.getIfStatement();
		Optional<Statement> elseStmt = statement.getElseStatement();

		new JvmExpression(reporter, descriptorFactory).writeExpressionBytecode(mv, expr, scope);
		// -- IFEQ

		Label endifLabel = new Label();
		mv.visitJumpInsn(Opcodes.IFEQ, endifLabel);

		JvmStatementBlock ifBlock = new JvmStatementBlock(reporter, descriptorFactory, scopeManager, Arrays.asList(ifStmt));
		ifBlock.writeBytecode(classWriter, mv);


		if(elseStmt.isEmpty()) {
			mv.visitLabel(endifLabel);
		} else {
			Label endElseLabel = new Label();
			mv.visitJumpInsn(Opcodes.GOTO, endElseLabel);

			mv.visitLabel(endifLabel);

			JvmStatementBlock elseBlock = new JvmStatementBlock(reporter, descriptorFactory, scopeManager, Arrays.asList(elseStmt.get()));
			elseBlock.writeBytecode(classWriter, mv);

			mv.visitLabel(endElseLabel);
		}
	}
}