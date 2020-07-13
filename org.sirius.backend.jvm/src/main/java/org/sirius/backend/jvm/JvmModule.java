package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;

public class JvmModule {
	private Reporter reporter;
	private ModuleDeclaration moduleDeclaration;
	private ArrayList<JvmPackage> jvmPackages = new ArrayList<JvmPackage>();

	public JvmModule(Reporter reporter, ModuleDeclaration moduleDeclaration) {
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