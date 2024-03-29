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
import org.sirius.frontend.api.FunctionClass;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;

/** Class in JVM sense.
 * Translates to a new {@link Bytecode} using a new ASM {@link ClassWriter}, with "<init>()" method.
 * 
 * @author jpragey
 *
 */
public class JvmClass {
	
	private Reporter reporter;
	private QName qName;

	private DescriptorFactory descriptorFactory = new DescriptorFactory(reporter);
	
	private List<JvmMemberFunction> memberFunctions;
	private List<JvmMemberValue> memberValues;
	private List<ClassExecutorFunction> classExecutorFunctions;
	
	private BackendOptions backendOptions;
	
	public JvmClass(Reporter reporter, QName qName, BackendOptions backendOptions, DescriptorFactory descriptorFactory, 
			List<JvmMemberFunction> memberFunctions, List<JvmMemberValue> memberValues,
//			List<ClassExecutorFunction> classExecutorFunctions
			List<FunctionClass> classExecutorFunctions
			) {
		super();
		this.reporter = reporter;
		this.qName = qName;
		this.backendOptions = backendOptions;
		this.descriptorFactory = descriptorFactory;
		this.memberFunctions = memberFunctions;
		this.memberValues = memberValues;
		this.classExecutorFunctions = classExecutorFunctions.stream()
				.map((FunctionClass fc)-> {
//					QName fctQName = fc.qName();
					List<Statement> body = fc.bodyStatements();
					Type returnType = fc.returnType();
//					List<Statement> body = List.<Statement>of();
					ClassExecutorFunction cef = new ClassExecutorFunction(qName/*class QName*/, body, returnType);
					return cef;
				})
				.toList();
	}
	
	public JvmClass(Reporter reporter, ClassType cd, BackendOptions backendOptions, DescriptorFactory descriptorFactory) {
		this(reporter, cd.qName(), backendOptions, descriptorFactory,
				cd.memberFunctions().stream()
					.map(mf -> new JvmMemberFunction(reporter, backendOptions,  descriptorFactory, mf, cd.qName(), false /*isStatic*/))
					.collect(Collectors.toUnmodifiableList()), 
				cd.memberValues().stream()
					.map((mv) -> new JvmMemberValue(mv, descriptorFactory, reporter))
					.collect(Collectors.toUnmodifiableList())
				,
				List.of() // class executor functions
				);
	}
	/** Create class for package top-level stuff (eg top-level functions <b>inside</b> a package)
	 * 
	 * @param reporter
	 * @param pd
	 * @param backendOptions
	 * @param descriptorFactory
	 * @param packageFuncs
	 * @return
	 */
	public static JvmClass createPackageClass(Reporter reporter, PackageDeclaration pd, BackendOptions backendOptions, DescriptorFactory descriptorFactory,
			Collection<AbstractFunction> packageFuncs) 
	{
		QName topLevelQName;
		Optional<QName> pdQName = pd.qName();
		if(pdQName.isPresent()) {
			topLevelQName = pdQName.get().child(Util.topLevelClassName); // currently <custom_package>.Global
		} else {
			topLevelQName = Util.jvmPackageClassQName; /* Root package top-level function (must be currently sirius.default.Global)*/
		}
		
		List<JvmMemberFunction> memberFuns = packageFuncs.stream()
				
				.map( (func) ->	{
					return new JvmMemberFunction(reporter, backendOptions,descriptorFactory,func, topLevelQName, true /*isStatic*/);
				})
				.collect(Collectors.toUnmodifiableList());
		
		return new JvmClass(reporter,
				topLevelQName,
				backendOptions, descriptorFactory,
				memberFuns, 
					List.<JvmMemberValue>of(),
					List.<FunctionClass>of() // class executor functions
					);
	}
	
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

		for(ClassExecutorFunction cef: this.classExecutorFunctions) {
			cef.writeBytecode(classWriter, reporter, descriptorFactory);
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

