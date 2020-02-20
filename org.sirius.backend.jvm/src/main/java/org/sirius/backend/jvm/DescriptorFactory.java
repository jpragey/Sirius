package org.sirius.backend.jvm;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ArrayType;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.IntegerType;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.VoidType;

/** Tools for creation or JVM descriptors (types, methods...)
 * 
 * @author jpragey
 *
 */
public class DescriptorFactory {
	private Reporter reporter;

	public DescriptorFactory(Reporter reporter) {
		super();
		this.reporter = reporter;
	}
	 // TODO: remove
	/** Map some sirius-related class name to java class names. To remove when we have a decent SDK. 
	 * 
	 */
	private String tempMapClassInternalName(String siriusName) {
		
		switch(siriusName) {
		case "String": return "java/lang/String";
		case "sirius/lang/String": return "java/lang/String";
		default: return siriusName;
		}
	}
	
	public String fieldDescriptor(Type type) {
		if(type instanceof ClassType) {
			ClassType classType = (ClassType)type;
			String internalName = classType.getQName().getStringElements().stream().collect(Collectors.joining("/"));
			internalName = tempMapClassInternalName(internalName);
			return "L" + internalName + ";";
		} else if(type instanceof VoidType) {
			return "V";
		} else if(type instanceof ArrayType) {
			ArrayType arrayType = (ArrayType)type;
			return "[" + fieldDescriptor(arrayType.getElementType());
		} else if(type instanceof IntegerType) {
			return "I";
		} else {
			reporter.error("JVM backend: internal error creating fieldDescriptor, type " + type + ":" + type.getClass() + " has no mapping to JVM type descriptor.");
			return "";
		}
	}
	
	public String methodDescriptor(AbstractFunction function  ) {
		Type returnType = function .getReturnType();
		String descr = function.getArguments().stream()
			.map((FunctionFormalArgument arg) -> fieldDescriptor(arg.getType()) )
			.collect(Collectors.joining("", "(", ")"))
			+ (returnType instanceof VoidType ? "V" : fieldDescriptor(returnType))
			;
		
		return descr;
	}
	

}
