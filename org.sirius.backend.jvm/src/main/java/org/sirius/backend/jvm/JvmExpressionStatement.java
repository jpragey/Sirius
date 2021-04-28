package org.sirius.backend.jvm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;

public class JvmExpressionStatement implements JvmStatement {
	private Reporter reporter;
	private DescriptorFactory descriptorFactory;
	private ExpressionStatement statement;
	private JvmScope scope;
	public JvmExpressionStatement(Reporter reporter, DescriptorFactory descriptorFactory, ExpressionStatement statement,
			JvmScope scope) {
		super();
		this.reporter = reporter;
		this.descriptorFactory = descriptorFactory;
		this.statement = statement;
		this.scope = scope;
	}
	
	@Override
	public void writeBytecode(ClassWriter classWriter, MethodVisitor mv) {
		JvmExpression jvmExpression = new JvmExpression(reporter, descriptorFactory);
		Expression expression = statement.getExpression();
		jvmExpression.writeExpressionBytecode(mv, expression, scope);
	}
}