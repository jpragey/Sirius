package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.ClassOrInterfaceDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.Type;

public class JvmNodeClass {
	
	private final static int VERSION = 52; // Java SE 8
	private Reporter reporter;
	private QName qName;

	private List<String> definedClasses = new ArrayList<>();
	private DescriptorFactory descriptorFactory = new DescriptorFactory(reporter);
	
	private ArrayList<JvmMemberFunction> memberFunctions = new ArrayList<>();
	
	public class JvmMemberValue {
		private MemberValue memberValue;
		private boolean isStatic = false;	// TODO
		private boolean isFinal = false;	// TODO
		private boolean isPublic = true;	// TODO
		public JvmMemberValue(MemberValue memberValue) {
			this.memberValue = memberValue;
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
	}
	private ArrayList<JvmMemberValue> memberValues = new ArrayList<>();
	
	public JvmNodeClass(Reporter reporter, QName qName) {
		super();
		this.reporter = reporter;
		this.qName = qName;
	}
	
	public JvmNodeClass(ClassDeclaration cd) {
		super();
		this.qName = cd.getQName();
		addMemberFunctions(cd);
		addMemberValues(cd);
	}
	public JvmNodeClass(InterfaceDeclaration cd) {
		super();
		this.qName = cd.getQName();
		addMemberFunctions(cd);
		addMemberValues(cd);
	}
	// For Package class
	public JvmNodeClass(PackageDeclaration pd) {
		super();
		this.qName = pd.getQName().child("$package$");	// TODO
	}

	private void addMemberValues(ClassOrInterfaceDeclaration cd) {
		for(MemberValue mv: cd.getValues()) {
//			System.out.println(" MemberValue: " + mv);
			JvmMemberValue jvmMv = new JvmMemberValue(mv);
			this.memberValues.add(jvmMv);
		}
	}
	
	private void addMemberFunctions(ClassOrInterfaceDeclaration cd) {
		for(MemberFunction mf: cd.getFunctions())
			memberFunctions.add(new JvmMemberFunction(reporter, descriptorFactory, mf, false /*isStatic*/));
	}
	
	public void addTopLevelFunction(TopLevelFunction  func) {
		memberFunctions.add(new JvmMemberFunction(reporter, descriptorFactory, func, true /*isStatic*/));
		
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

	public Bytecode toBytecode(List<ClassWriterListener> listeners) {

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
		//			this.classWriter.visitSource("Hello.java", null /*debug*/);

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

//			byte[] bytes = visitor.toByteCode();
		byte[] bytes = classWriter.toByteArray();
		Bytecode bytecode = new Bytecode(bytes, qName);
		
		for(ClassWriterListener l: listeners)
			l.addByteCode(bytecode, qName);
		
		return bytecode;
	}
	
	private void startClass(ClassWriter classWriter /*ClassDeclaration declaration*/) {

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
		int access = ACC_SUPER; // Always use ACC_SUPER ! 
		////		if(classDeclaration.getVisibility() == Visibility.PUBLIC)
		access |= ACC_PUBLIC;

//		String classInternalName = JvmClassWriter.classInternalName(classDeclaration);
//		/* From doc:
//		 * The internal name of a class is its fully qualified name (as returned by Class.getName(), where '.' are replaced by '/'). 
//		 * This method should only be used for an object or array type.
//		 */
		String classInternalName = qName.getStringElements().stream().collect(Collectors.joining("/"));

		classWriter.visit(VERSION, access, classInternalName/*"Hello"*/, null /*signature*/, "java/lang/Object"/*superName*/, null /*interfaces*/);
	}

	/** 
	 * 
	 */
	private void writeInitMethod(ClassWriter classWriter) {
		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);

		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL,
				"java/lang/Object",
				"<init>",
				"()V", 
				false /*isInterface*/);

		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

}

