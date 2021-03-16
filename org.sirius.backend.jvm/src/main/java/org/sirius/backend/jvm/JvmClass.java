package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.PackageDeclaration;

public class JvmClass {
	
	private Reporter reporter;
	private QName qName;

	private List<String> definedClasses = new ArrayList<>();
	private DescriptorFactory descriptorFactory = new DescriptorFactory(reporter);
	
	private ArrayList<JvmMemberFunction> memberFunctions = new ArrayList<>();
	
	private ArrayList<JvmMemberValue> memberValues = new ArrayList<>();
	private BackendOptions backendOptions;
	
	public JvmClass(Reporter reporter, QName qName, BackendOptions backendOptions) {
		super();
		this.reporter = reporter;
		this.qName = qName;
		this.backendOptions = backendOptions;
	}
	
	public JvmClass(Reporter reporter, ClassType cd, BackendOptions backendOptions) {
		this(reporter, cd.getQName(), backendOptions);
		addMemberFunctions(cd);
		addMemberValues(cd);
	}
//	public JvmClass(Reporter reporter, InterfaceDeclaration cd, BackendOptions backendOptions) {
//		this(reporter, cd.getQName(), backendOptions);
//		addMemberFunctions(cd);
//		addMemberValues(cd);
//	}
	// For Package class
	public JvmClass(Reporter reporter, PackageDeclaration pd, BackendOptions backendOptions) {
		this(reporter, pd.getQName().child(Util.jvmPackageClassName), backendOptions);
	}

	private void addMemberValues(ClassType cd) {
		for(MemberValue mv: cd.getMemberValues()) {
			JvmMemberValue jvmMv = new JvmMemberValue(mv, descriptorFactory, reporter);
			this.memberValues.add(jvmMv);
		}
	}
	
	private void addMemberFunctions(ClassType cd) {
		for(AbstractFunction mf: cd.getFunctions())
			memberFunctions.add(new JvmMemberFunction(reporter, backendOptions,  descriptorFactory, mf, false /*isStatic*/));
	}
	
	public void addTopLevelFunction(AbstractFunction func) {
		memberFunctions.add(new JvmMemberFunction(reporter, backendOptions, descriptorFactory, func, true /*isStatic*/));
		
	}
	
	@Override
	public String toString() {
		return qName.toString();
	}

	/* Flags for class/interface:
	 * @See https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.1
	 * Used at least for classWriter.visit()
	 * 
	 *  Flag Name 		Interpretation
	 *  
	ACC_PUBLIC 		Declared public; may be accessed from outside its package.
	ACC_FINAL 		Declared final; no subclasses allowed.
	ACC_SUPER 		Treat superclass methods specially when invoked by the invokespecial instruction.
	ACC_INTERFACE 	Is an interface, not a class.
	ACC_ABSTRACT 	Declared abstract; must not be instantiated.
	ACC_SYNTHETIC 	Declared synthetic; not present in the source code.
	ACC_ANNOTATION 	Declared as an annotation type.
	ACC_ENUM 		Declared as an enum type.
	ACC_MODULE		Is a module, not a class or interface.
	 */

	public enum AsmClassFlags {
		PUBLIC(Opcodes.ACC_PUBLIC), // 		Declared public; may be accessed from outside its package.
		FINAL(Opcodes.ACC_FINAL), // 		Declared final; no subclasses allowed.
		SUPER(Opcodes.ACC_SUPER), // 		Treat superclass methods specially when invoked by the invokespecial instruction.
		INTERFACE(Opcodes.ACC_INTERFACE), // 	Is an interface, not a class.
		ABSTRACT(Opcodes.ACC_ABSTRACT), // 	Declared abstract; must not be instantiated.
		SYNTHETIC(Opcodes.ACC_SYNTHETIC), // 	Declared synthetic; not present in the source code.
		ANNOTATION(Opcodes.ACC_ANNOTATION), // 	Declared as an annotation type.
		ENUM(Opcodes.ACC_ENUM), // 		Declared as an enum type.
		MODULE(Opcodes.ACC_MODULE) //		Is a module, not a class or interface.
		;
		public int asmFlag; // ACC_XXX value

		private AsmClassFlags(int asmFlag) {
			this.asmFlag = asmFlag;
		}
		
	}
	
	public void /*Bytecode*/ toBytecode(List<ClassWriterListener> listeners) {

		//			System.out.println(" -- Starting ClassDeclaration " + classDeclaration.getQName());

		String clssQname = qName.dotSeparated();
		this.definedClasses.add(clssQname);

		ClassWriter classWriter = new ClassWriter(
				ClassWriter.COMPUTE_FRAMES | // No need to 
				ClassWriter.COMPUTE_MAXS // You must still call visitMaxs(), but its args are ignored 
				/*0*/ /*flags*/
				);

		String fileName = qName.getLast() + ".java";
		classWriter.visitSource(fileName, null /*debug*/);

		startClass(classWriter);

		writeInitMethod(classWriter);

		// -- Member functions
		for(JvmMemberFunction mf: this.memberFunctions) {
			mf.writeBytecode(classWriter);
		}
		
		for(JvmMemberValue mf: this.memberValues) {
			mf.writeBytecode(classWriter);
		}

		// -- Terminate class
		classWriter.visitEnd();

		byte[] bytes = classWriter.toByteArray();
		Bytecode bytecode = new Bytecode(bytes, qName);
		
		for(ClassWriterListener l: listeners)
			l.addByteCode(bytecode);
		
//		return bytecode;
	}
	
	private void startClass(ClassWriter classWriter) {

		/* Flags for class/interface:
		 * @See https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.1
		 * 
		 *  Flag Name 		Interpretation
		 *  
		ACC_PUBLIC 		Declared public; may be accessed from outside its package.
		ACC_FINAL 		Declared final; no subclasses allowed.
		ACC_SUPER 		Treat superclass methods specially when invoked by the invokespecial instruction.
		ACC_INTERFACE 	Is an interface, not a class.
		ACC_ABSTRACT 	Declared abstract; must not be instantiated.
		ACC_SYNTHETIC 	Declared synthetic; not present in the source code.
		ACC_ANNOTATION 	Declared as an annotation type.
		ACC_ENUM 		Declared as an enum type.
		ACC_MODULE		Is a module, not a class or interface.
		 */
//		int access = ACC_SUPER; // Always use ACC_SUPER ! 
//		////		if(classDeclaration.getVisibility() == Visibility.PUBLIC)
//		access |= ACC_PUBLIC;

		int access = AsmClassFlags.SUPER.asmFlag;
//		if(classd)
		access |= AsmClassFlags.PUBLIC.asmFlag;
		
//		String classInternalName = JvmClassWriter.classInternalName(classDeclaration);
//		/* From doc:
//		 * The internal name of a class is its fully qualified name (as returned by Class.getName(), where '.' are replaced by '/'). 
//		 * This method should only be used for an object or array type.
//		 */
		String classInternalName = Util.classInternalName(qName);

		classWriter.visit(Bytecode.VERSION, access, classInternalName/*"Hello"*/, null /*signature*/, "java/lang/Object"/*superName*/, null /*interfaces*/);
	}

	/** 
	 * 
	 */
	private void writeInitMethod(ClassWriter classWriter) {
		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);

		JvmScope scope = new JvmScope(descriptorFactory, Optional.empty(), "<init(...) root>");
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL,
				"java/lang/Object",
				"<init>",
				"()V", 
				false /*isInterface*/);

		for(JvmMemberValue mf: this.memberValues) {
			mf.writeInitBytecode(classWriter, mv, scope, qName);
		}
		
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

}

