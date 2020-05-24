package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Visitor;

public class CodeTreeBuilder implements Visitor {
	private Reporter reporter;
	
	public CodeTreeBuilder(Reporter reporter) {
		super();
		this.reporter = reporter;
	}

	public static class JvmPackage {
		private Reporter reporter;
		private PackageDeclaration packageDeclaration;
		private ArrayList<JvmNodeClass> jvmClasses = new ArrayList<JvmNodeClass>();
		private JvmNodeClass packageClass;

//		public JvmPackage(Reporter reporter) {
//			this.reporter = reporter;
//		}

		public JvmPackage(Reporter reporter, PackageDeclaration packageDeclaration) {
			super();
			this.reporter = reporter;
			this.packageDeclaration = packageDeclaration;
			this.packageClass = new JvmNodeClass(reporter, packageDeclaration);
			
			jvmClasses.add(this.packageClass);
			for(ClassDeclaration cd: packageDeclaration.getClasses()) {
				jvmClasses.add(new JvmNodeClass(reporter, cd));
			}
			for(InterfaceDeclaration id: packageDeclaration.getInterfaces()) {
				jvmClasses.add(new JvmNodeClass(reporter, id));
			}
			for(AbstractFunction func: packageDeclaration.getFunctions()) {
				packageClass.addTopLevelFunction(func);
			}
		}
		public void createByteCode(List<ClassWriterListener> listeners) {
//			packageClass.toBytecode(listeners);
			for(JvmNodeClass c: jvmClasses) {
				c.toBytecode(listeners);
			}
		}		
	}
	
	public static class JvmNodeModule {
		private Reporter reporter;
		private ModuleDeclaration moduleDeclaration;
		private ArrayList<JvmPackage> jvmPackages = new ArrayList<CodeTreeBuilder.JvmPackage>();

		public JvmNodeModule(Reporter reporter, ModuleDeclaration moduleDeclaration) {
			super();
			this.reporter = reporter;
			this.moduleDeclaration = moduleDeclaration;
			for(PackageDeclaration pd: moduleDeclaration.getPackages()) {
				jvmPackages.add(new JvmPackage(reporter, pd));
			}
		}
		
		public void createByteCode(List<ClassWriterListener> listeners) {
			listeners.forEach(l -> l.start(moduleDeclaration));
			
			for(JvmPackage pd: jvmPackages) {
				pd.createByteCode(listeners);
			}
			
			listeners.forEach(l -> l.end());
		}
	}
	
	JvmNodeModule nodeModule;

	@Override
	public void start(ModuleDeclaration declaration) {
		
		this.nodeModule = new JvmNodeModule(reporter, declaration);
	}

	public void createByteCode(List<ClassWriterListener> listeners) {
		nodeModule.createByteCode(listeners);
	}

	@Override
	public void end(ModuleDeclaration declaration) {
	}

}
