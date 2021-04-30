package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	private DescriptorFactory descriptorFactory = new DescriptorFactory(reporter);
	
	private List<JvmMemberFunction> memberFunctions;
	private List<JvmMemberValue> memberValues;
	
	private BackendOptions backendOptions;
	
	public JvmClass(Reporter reporter, QName qName, BackendOptions backendOptions, DescriptorFactory descriptorFactory, 
			List<JvmMemberFunction> memberFunctions, List<JvmMemberValue> memberValues) {
		super();
		this.reporter = reporter;
		this.qName = qName;
		this.backendOptions = backendOptions;
		this.descriptorFactory = descriptorFactory;
		this.memberFunctions = memberFunctions;
		this.memberValues = memberValues;
	}
	
	public JvmClass(Reporter reporter, QName qName, BackendOptions backendOptions, DescriptorFactory descriptorFactory) {
		this(reporter, qName, backendOptions, descriptorFactory, List.<JvmMemberFunction>of(), List.<JvmMemberValue>of());
	}
	
	public JvmClass(Reporter reporter, ClassType cd, BackendOptions backendOptions, DescriptorFactory descriptorFactory) {
		this(reporter, cd.getQName(), backendOptions, descriptorFactory,
				cd.getFunctions().stream()
					.map(mf -> new JvmMemberFunction(reporter, backendOptions,  descriptorFactory, mf, false /*isStatic*/))
					.collect(Collectors.toUnmodifiableList()), 
				cd.getMemberValues().stream()
					.map((mv) -> new JvmMemberValue(mv, descriptorFactory, reporter))
					.collect(Collectors.toUnmodifiableList())
				);
	}
	
	public static JvmClass createPackageClass(Reporter reporter, PackageDeclaration pd, BackendOptions backendOptions, DescriptorFactory descriptorFactory,
			Collection<AbstractFunction> packageFuncs) {
		return new JvmClass(reporter, pd.getQName().child(Util.jvmPackageClassName), backendOptions, descriptorFactory,
				packageFuncs.stream()
					.map(func->new JvmMemberFunction(reporter, backendOptions,descriptorFactory,func, true /*isStatic*/))
					.collect(Collectors.toUnmodifiableList()), 
					List.<JvmMemberValue>of());
	}
	
//	public void addTopLevelFunction(AbstractFunction func) {
//		memberFunctions.add(new JvmMemberFunction(reporter, backendOptions, descriptorFactory, func, true /*isStatic*/));
//	}
	
	@Override
	public String toString() {
		return qName.toString();
	}

	/* Flags for class/interface:
	 * @See https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.1
	 * Used at least for classWriter.visit()
	 */
	public enum AsmClassFlags {
		PUBLIC(Opcodes.ACC_PUBLIC),			// 	Declared public; may be accessed from outside its package.
		FINAL(Opcodes.ACC_FINAL), 			// 	Declared final; no subclasses allowed.
		SUPER(Opcodes.ACC_SUPER), 			// 	Treat superclass methods specially when invoked by the invokespecial instruction.
		INTERFACE(Opcodes.ACC_INTERFACE), 	// 	Is an interface, not a class.
		ABSTRACT(Opcodes.ACC_ABSTRACT), 	// 	Declared abstract; must not be instantiated.
		SYNTHETIC(Opcodes.ACC_SYNTHETIC), 	// 	Declared synthetic; not present in the source code.
		ANNOTATION(Opcodes.ACC_ANNOTATION), // 	Declared as an annotation type.
		ENUM(Opcodes.ACC_ENUM), 			// 	Declared as an enum type.
		MODULE(Opcodes.ACC_MODULE); 			//	Is a module, not a class or interface.
		
		public int asmFlag; // ACC_XXX value

		private AsmClassFlags(int asmFlag) {
			this.asmFlag = asmFlag;
		}
		
	}

	public void visitBytecode(List<ClassWriterListener> listeners) {
		Bytecode bytecode = createBytecode();
		for(ClassWriterListener l: listeners) {
			l.addByteCode(bytecode);
		}
	}
	
	public Bytecode createBytecode() {

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
		return bytecode;
	}
	
	private void startClass(ClassWriter classWriter) {
		int access =  AsmClassFlags.SUPER.asmFlag
					| AsmClassFlags.PUBLIC.asmFlag;
		
		/* From doc:
		 * The internal name of a class is its fully qualified name (as returned by Class.getName(), where '.' are replaced by '/'). 
		 * This method should only be used for an object or array type.
		 */
		String classInternalName = Util.classInternalName(qName);

		classWriter.visit(Bytecode.VERSION, access, classInternalName, null /*signature*/, "java/lang/Object"/*superName*/, null /*interfaces*/);
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

	public List<JvmMemberFunction> getMemberFunctions() {
		return memberFunctions;
	}

	public List<JvmMemberValue> getMemberValues() {
		return memberValues;
	}

	
}

