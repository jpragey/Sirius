package org.sirius.backend.jvm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.*;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;


public class JvmModule {
	private Reporter reporter;
	private ModuleDeclaration moduleDeclaration;
	private ArrayList<JvmPackage> jvmPackages = new ArrayList<JvmPackage>();
	private BackendOptions backendOptions;
	
	public JvmModule(Reporter reporter, ModuleDeclaration moduleDeclaration, BackendOptions backendOptions) {
		super();
		this.reporter = reporter;
		this.moduleDeclaration = moduleDeclaration;
		this.backendOptions = backendOptions;
		
		for(PackageDeclaration pd: moduleDeclaration.getPackages()) {
			jvmPackages.add(new JvmPackage(reporter, pd, backendOptions));
		}
	}
	
	private void addModuleDeclaration(List<ClassWriterListener> listeners) {
		QName classQname = moduleDeclaration.getQName().child("module-info");
		String moduleQname = moduleDeclaration.getQName().dotSeparated();
		
		ClassWriter classWriter = new ClassWriter(
				ClassWriter.COMPUTE_FRAMES | // No need to 
				ClassWriter.COMPUTE_MAXS // You must still call visitMaxs(), but its args are ignored 
				/*0*/ /*flags*/
				);

		classWriter.visitSource("module-info.jar", null /*debug*/);
		
//		String moduleFQName = classQname.dotSeparated();
		System.out.println("Module qname: " + moduleQname);
		
//		String classInternalName = Util.classInternalName(classQname);
		int access0 = ACC_MODULE; // Always use ACC_SUPER ! 
//		classWriter.visitSource();
		classWriter.visit(Bytecode.VERSION, access0, "module-info" /*classInternalName*//*"Hello"*/, null /*signature*/, null /*"java/lang/Object"*//*superName*/, null /*interfaces*/);
//		String classInternalName = Util.classInternalName(classQname);
//		int access = ACC_SUPER; // Always use ACC_SUPER ! 
//		////		if(classDeclaration.getVisibility() == Visibility.PUBLIC)
//		access |= ACC_PUBLIC;
		
//		int access = ACC_OPEN | ACC_SYNTHETIC | ACC_MANDATED;
		int moduleAccess =  ACC_PUBLIC;
		String moduleVersion = moduleDeclaration.getVersion();
		moduleVersion = "0.0.1-SNAPSHOT";
//		String moduleVersion = moduleDeclaration.g;
		
		ModuleVisitor mv = classWriter.visitModule(moduleQname, moduleAccess, moduleVersion);
////		mv.visitMainClass("$package$"/*Main class*/);
//		mv.visitMainClass("MyMainClass"/*Main class*/);
		mv.visitRequire("sirius.lang", ACC_TRANSITIVE, "0.0.1-SNAPSHOT");
		mv.visitRequire("org.sirius.sdk", ACC_TRANSITIVE, "0.0.1-SNAPSHOT");
		
		
//		mv.visitExport("myPackage", 0 /*ACC_SYNTHETIC, ACC_MANDATED*/, "a.b.module0", "a.b.module1", "a.b.module2");
		
		mv.visitEnd();
//		classWriter.visit(Bytecode.VERSION, access, classInternalName/*"Hello"*/, null /*signature*/, "java/lang/Object"/*superName*/, null /*interfaces*/);

		classWriter.visitEnd();

		byte[] bytes = classWriter.toByteArray();
		Bytecode bytecode = new Bytecode(bytes, classQname);
		
		listeners.forEach(l -> l.addByteCode(bytecode, classQname));
		
	}
	
	public void createByteCode(List<ClassWriterListener> listeners) {
		listeners.forEach(l -> l.start(moduleDeclaration));

		addModuleDeclaration(listeners);

		for(JvmPackage pd: jvmPackages) {
			pd.createByteCode(listeners);
		}

		listeners.forEach(l -> l.end());
	}
}