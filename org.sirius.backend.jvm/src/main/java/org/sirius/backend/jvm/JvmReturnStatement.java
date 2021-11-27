package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.IRETURN;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.IntegerType;
import org.sirius.frontend.api.ReturnStatement;

public class JvmReturnStatement implements JvmStatement {
	private Reporter reporter;
	private DescriptorFactory descriptorFactory;
	private ReturnStatement statement;
	private JvmScope scope;
	
	public JvmReturnStatement(Reporter reporter, DescriptorFactory descriptorFactory, ReturnStatement statement,
			JvmScope scope) {
		super();
		this.reporter = reporter;
		this.descriptorFactory = descriptorFactory;
		this.statement = statement;
		this.scope = scope;
	}

	@Override
	public void writeBytecode(ClassWriter classWriter, MethodVisitor mv) {

		// -- write return expression
		JvmExpression expr = new JvmExpression(reporter, descriptorFactory, statement.expression());
		expr.writeExpressionBytecode(mv, scope);

		// -- write return
		org.sirius.frontend.api.Type type = statement.getExpressionType();

		if(type instanceof IntegerType) {
			if(Util.mapIntsToClasses) {
				mv.visitInsn(ARETURN);	// TODO ???
			} else {
				mv.visitInsn(IRETURN);
			}
		} else if(type instanceof ClassType) {
			mv.visitInsn(ARETURN);
		} else {
			reporter.error("Currently unsupported expression type in return statement: " + type);
		}
	}
}