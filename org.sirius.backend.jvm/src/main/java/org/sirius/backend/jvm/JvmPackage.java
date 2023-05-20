package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.PackageDeclaration;

public class JvmPackage {
	private PackageDeclaration packageDeclaration;
	private ArrayList<JvmClass> jvmClasses = new ArrayList<JvmClass>();
	private JvmClass packageClass;
	private BackendOptions backendOptions;
	
	public JvmPackage(Reporter reporter, PackageDeclaration packageDeclaration, BackendOptions backendOptions) {
		super();
		this.packageDeclaration = packageDeclaration;
		DescriptorFactory descriptorFactory = new DescriptorFactory(reporter);
		
		Collection<AbstractFunction> funcs = retainOnlyAllArgsFunctions(packageDeclaration.getFunctions());// TODO: remove ???
				
		this.packageClass = JvmClass.createPackageClass(reporter, packageDeclaration, backendOptions, descriptorFactory,
				funcs
				);
		this.backendOptions = backendOptions;

		this.jvmClasses.add(this.packageClass);
		for(ClassType cd: packageDeclaration.getClasses()) {
			jvmClasses.add(new JvmClass(reporter, cd, backendOptions, descriptorFactory));
		}
	}

	@Override
	public String toString() {
		return "Package '" + packageDeclaration.qName().get()/* TODO ??? */.dotSeparated() + "'";
	}
	
	// TODO: remove
	private Collection<AbstractFunction> retainOnlyAllArgsFunctions(Collection<AbstractFunction> allFunctions) {
		TreeMap<String, AbstractFunction> map = new TreeMap<>();
		for(AbstractFunction func: allFunctions) {
			String name = func.qName()
					.map(qn -> qn.getLast())
					.orElse("");
			map.put(name, func);
		}
		return map.values();
	}


	public void createByteCode(List<ClassWriterListener> listeners) {
		for(JvmClass c: jvmClasses) {
			c.visitBytecode(listeners);
		}
	}

	public ArrayList<JvmClass> getJvmClasses() {
		return jvmClasses;
	}

	public JvmClass getPackageClass() {
		return packageClass;
	}
	
	
}