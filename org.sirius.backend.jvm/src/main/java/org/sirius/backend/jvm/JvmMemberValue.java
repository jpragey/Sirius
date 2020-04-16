package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

import java.util.Optional;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;

public class JvmMemberValue {
	private MemberValue memberValue;
	private boolean isStatic = false;	// TODO
	private boolean isFinal = false;	// TODO
	private boolean isPublic = true;	// TODO
	private Reporter reporter;
	private DescriptorFactory descriptorFactory;
	
	public JvmMemberValue(MemberValue memberValue, DescriptorFactory descriptorFactory, Reporter reporter) {
		this.memberValue = memberValue;
		this.descriptorFactory = descriptorFactory;
		this.reporter = reporter;
	}
	@Override
	public String toString() {
		return memberValue.toString();
	}
	public void writeBytecode(ClassWriter classWriter/*, MemberFunction declaration*/) {

		int access = 0;
		if(isPublic)
			access |= ACC_PUBLIC;
		if(isStatic)
			access |= ACC_STATIC;
		if(isFinal)
			access |= ACC_FINAL;
		
		String name = memberValue.getName().getText();
		
		Type type = memberValue.getType();
		String descriptor = descriptorFactory.fieldDescriptor(type);
		String signature = null;	// Used for generics
		Object value = 666;
		classWriter.visitField(access, name, descriptor, signature, value);
		classWriter.visitEnd();
	}
	
	/** Write the part of &lt;init&gt; initializing this member value.
	 * 
	 * @param classWriter
	 * @param mv
	 * @param scope
	 * @param qName
	 */
	public void writeInitBytecode(ClassWriter classWriter/*, MemberFunction declaration*/, MethodVisitor mv, JvmScope scope, /*ClassDeclaration containerType, */QName qName) {
		Optional<Expression> optExpr = memberValue.getInitialValue();
		if(optExpr.isEmpty())
			return;
		Expression expression = optExpr.get(); // TODO
		
		// -- put objectref (this)
		mv.visitVarInsn(Opcodes.ALOAD, 0 /*0 = this  locvarIndex*/);

		// -- put value
		new JvmExpression(reporter, descriptorFactory).writeExpressionBytecode(mv, expression, scope);
				
		String owner = Util.classInternalName(qName); // internal name x/y/A
		String name = memberValue.getName().getText();
		String descriptor = descriptorFactory.fieldDescriptor(memberValue.getType());

		mv.visitFieldInsn(Opcodes.PUTFIELD, owner, name, descriptor);

		
	}

	
}
