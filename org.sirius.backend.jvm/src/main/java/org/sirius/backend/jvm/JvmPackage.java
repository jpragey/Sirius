package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.PackageDeclaration;

public class JvmPackage {
		private Reporter reporter;
		private PackageDeclaration packageDeclaration;
		private ArrayList<JvmClass> jvmClasses = new ArrayList<JvmClass>();
		private JvmClass packageClass;

//		public JvmPackage(Reporter reporter) {
//			this.reporter = reporter;
//		}

		public JvmPackage(Reporter reporter, PackageDeclaration packageDeclaration) {
			super();
			this.reporter = reporter;
			this.packageDeclaration = packageDeclaration;
			this.packageClass = new JvmClass(reporter, packageDeclaration);
			
			jvmClasses.add(this.packageClass);
			for(ClassDeclaration cd: packageDeclaration.getClasses()) {
				jvmClasses.add(new JvmClass(reporter, cd));
			}
			for(InterfaceDeclaration id: packageDeclaration.getInterfaces()) {
				jvmClasses.add(new JvmClass(reporter, id));
			}
			
			Collection<AbstractFunction> packageFuncs = packageDeclaration.getFunctions();
			packageFuncs = retainOnlyAllArgsFunctions(packageFuncs);	// TODO: remove
			
			for(AbstractFunction func: packageFuncs) {
				packageClass.addTopLevelFunction(func);
			}
		}
		
		// TODO: remove
		private Collection<AbstractFunction> retainOnlyAllArgsFunctions(Collection<AbstractFunction> allFunctions) {
			TreeMap<String, AbstractFunction> map = new TreeMap<>();
			for(AbstractFunction func: allFunctions) {
				String name = func.getQName().getLast();
				map.put(name, func);
			}
			return map.values();
		}

		
		public void createByteCode(List<ClassWriterListener> listeners) {
//			packageClass.toBytecode(listeners);
			for(JvmClass c: jvmClasses) {
				c.toBytecode(listeners);
			}
		}		
	}