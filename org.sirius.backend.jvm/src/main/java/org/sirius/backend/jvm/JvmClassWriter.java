package org.sirius.backend.jvm;

import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassDeclaration;

/** Convert a class declaration in equivalent bytecode.
 * 
 * @author jpragey
 *
 */
public class JvmClassWriter {

	private List<ClassWriterListener> listeners;
	private Reporter reporter;
	private boolean verboseAst;
	
	public JvmClassWriter(Reporter reporter, List<ClassWriterListener> listeners, boolean verboseAst) {
		super();
		this.reporter = reporter;
		this.listeners = listeners;
		this.verboseAst = verboseAst;
	}

	
	public Bytecode createByteCode(ClassDeclaration classDeclaration) {
		CodeCreationVisitor visitor = new CodeCreationVisitor(reporter);
		classDeclaration.visitMe(visitor);
		
		byte[] bytes = visitor.toByteCode();
		Bytecode bytecode = new Bytecode(bytes, classDeclaration.getQName());
		
		listeners.forEach(l -> l.addByteCode(bytecode, classDeclaration.getQName()));
		
		return bytecode;
	}

	public static String classInternalName(ClassDeclaration classDeclaration) {
		/* From doc:
		 * Returns the internal name of the class corresponding to this object or array type. 
		 * The internal name of a class is its fully qualified name (as returned by Class.getName(), where '.' are replaced by '/'). 
		 * This method should only be used for an object or array type.
		 */
		String in = classDeclaration.getQName().getStringElements().stream().collect(Collectors.joining("/"));
		return in;
	}
}
