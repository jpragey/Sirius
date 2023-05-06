package org.sirius.backend.jvm;

import org.sirius.frontend.api.FunctionCall;

import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 */
public record JvmFunctionCall(FunctionCall apiFunctionCall) {

	/** Get the ASM opcode of the function being invoked.
	 * It can be INVOKESTATIC, INVOKEVIRTUAL (and later INVOKEINTERFACE and INVOKESPECIAL), see JVM doc.
	 * 
	 * @return {@link org.objectweb.asm.Opcodes} : INVOKESTATIC , INVOKEVIRTUAL and later INVOKEINTERFACE and INVOKESPECIAL.
	 */
	int asmOpCode() {
		if(apiFunctionCall.thisExpression().isPresent())
			return INVOKEVIRTUAL;
		else
			return INVOKESTATIC;
	}
	
	/** Owner class  - TODO: implement other than toplevel functions
	 *  
	 * From ASM visitMethodInsn : "the internal name of the method's owner class (see Type.getInternalName())."
	 * 
	 * And the internal name is (from Type.getInternalName()) : "Returns the internal name of the class corresponding to this object or array type. 
	 * 	The internal name of a class is its fully qualified name (as returned by Class.getName(), where '.' are replaced by '/'). 
	 * This method should only be used for an object or array type."
	 * 
	 * @return
	 * @see visitMethodInsn
	 */
	String getOwner() {
		String owner = Util.jvmPackageClassQName.slashSeparated();
		return owner;
	}
}
