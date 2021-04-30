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
	
	private class JvmModuleExport {
		private QName packageQName;
		private List<QName> toClause;
		public JvmModuleExport(QName packageQName, List<QName> toClause) {
			super();
			this.packageQName = packageQName;
			this.toClause = toClause;
		}
		public JvmModuleExport(QName packageQName) {
			this(packageQName, List.of());
		}
		public QName getPackageQName() {
			return packageQName;
		}
		public List<QName> getToClause() {
			return toClause;
		}
	}
	
	public JvmModule(Reporter reporter, ModuleDeclaration moduleDeclaration, BackendOptions backendOptions) {
		super();
		this.reporter = reporter;
		this.moduleDeclaration = moduleDeclaration;
		this.backendOptions = backendOptions;
		
		for(PackageDeclaration pd: moduleDeclaration.getPackages()) {
			jvmPackages.add(new JvmPackage(reporter, pd, backendOptions));
		}
	}
	
	private void writeModuleDeclaratorBytecode(List<ClassWriterListener> listeners) {
		QName classQname = new QName("module-info");
		String moduleQname = moduleDeclaration.getQName().dotSeparated();
		
		ClassWriter classWriter = new ClassWriter(
				ClassWriter.COMPUTE_FRAMES | // No need to 
				ClassWriter.COMPUTE_MAXS // You must still call visitMaxs(), but its args are ignored 
				/*0*/ /*flags*/
				);

		classWriter.visitSource("module-info.jar", null /*debug*/);
		
		int access = ACC_MODULE; // Always use ACC_SUPER ! 
//		classWriter.visitSource();
		classWriter.visit(Bytecode.VERSION, access, "module-info" /*classInternalName*/, null /*signature*/, null /*"java/lang/Object"*//*superName*/, null /*interfaces*/);
		
//		int access = ACC_OPEN | ACC_SYNTHETIC | ACC_MANDATED;
		int moduleAccess =  ACC_PUBLIC;
		String moduleVersion = moduleDeclaration.getVersion();
//		System.out.println("Module qname: '" + moduleQname + "', version: '" + moduleVersion + "'.");

		ModuleVisitor mv = classWriter.visitModule(moduleQname, moduleAccess, moduleVersion);

		mv.visitRequire("java.base", ACC_MANDATED, null);
		mv.visitRequire("org.sirius.runtime", ACC_TRANSITIVE, Constants.SIRIUS_RUNTIME_VERSION /* "0.0.1-SNAPSHOT"*/);
		mv.visitRequire("org.sirius.sdk", ACC_TRANSITIVE, Constants.SIRIUS_SDK_VERSION /* "0.0.1-SNAPSHOT"*/);
		
		// -- exports
		List<JvmModuleExport> jvmModuleExports = List.of(
				new JvmModuleExport(moduleDeclaration.getQName())
				);
		for(JvmModuleExport me: jvmModuleExports) {
			int flags = ACC_MANDATED;// valid values are among ACC_SYNTHETIC and ACC_MANDATED.
			String packaze = Util.classInternalName(me.getPackageQName());
			
			String[] toClause = me.getToClause().stream().map(qn -> qn.dotSeparated()).toArray(String[]::new);
			mv.visitExport(packaze, flags, toClause /*, null modules */);
		}
		
		mv.visitEnd();

		classWriter.visitEnd();

		byte[] bytes = classWriter.toByteArray();
		Bytecode bytecode = new Bytecode(bytes, classQname);
		
		listeners.forEach(l -> l.addByteCode(bytecode));
		
	}
	
	public void createByteCode(List<ClassWriterListener> listeners) {
		listeners.forEach(l -> l.start(moduleDeclaration));

		writeModuleDeclaratorBytecode(listeners);

		for(JvmPackage pd: jvmPackages) {
			pd.createByteCode(listeners);
		}

		listeners.forEach(l -> l.end());
	}
	
	
	public List<JvmPackage> getJvmPackages() {
		return jvmPackages;
	}

	public void setJvmPackages(ArrayList<JvmPackage> jvmPackages) {
		this.jvmPackages = jvmPackages;
	}

	@Override
	public String toString() {
		return this.moduleDeclaration.toString();
	}
}