package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.List;

import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.Visitor;

public class CodeTreeBuilder implements Visitor {

	
	public static class JvmPackage {
		private PackageDeclaration packageDeclaration;
		private ArrayList<JvmNodeClass> jvmClasses = new ArrayList<JvmNodeClass>();
		private JvmNodeClass packageClass;

		public JvmPackage(PackageDeclaration packageDeclaration) {
			super();
			this.packageDeclaration = packageDeclaration;
			this.packageClass = new JvmNodeClass(packageDeclaration);
			
			jvmClasses.add(this.packageClass);
			for(ClassDeclaration cd: packageDeclaration.getClasses()) {
				jvmClasses.add(new JvmNodeClass(cd));
			}
			for(InterfaceDeclaration id: packageDeclaration.getInterfaces()) {
				jvmClasses.add(new JvmNodeClass(id));
			}
			for(TopLevelFunction func: packageDeclaration.getFunctions()) {
				packageClass.addTopLevelFunction(func);
			}
		}
		public void createByteCode(List<ClassWriterListener> listeners) {
			packageClass.toBytecode(listeners);
			for(JvmNodeClass c: jvmClasses) {
				c.toBytecode(listeners);
			}
		}		
	}
	
	public static class JvmNodeModule {
		ModuleDeclaration moduleDeclaration;
		private ArrayList<JvmPackage> jvmPackages = new ArrayList<CodeTreeBuilder.JvmPackage>();

		public JvmNodeModule(ModuleDeclaration moduleDeclaration) {
			super();
			this.moduleDeclaration = moduleDeclaration;
			for(PackageDeclaration pd: moduleDeclaration.getPackages()) {
				jvmPackages.add(new JvmPackage(pd));
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
		
		this.nodeModule = new JvmNodeModule(declaration);
	}

	public void createByteCode(List<ClassWriterListener> listeners) {
		nodeModule.createByteCode(listeners);
	}

	@Override
	public void end(ModuleDeclaration declaration) {
	}

}
