package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Type;

public class JvmPackage {
	private Reporter reporter;
	private PackageDeclaration packageDeclaration;
	private ArrayList<JvmClass> jvmClasses = new ArrayList<JvmClass>();
	private JvmClass packageClass;
	private BackendOptions backendOptions;

//	private static JvmClass debugJvmMainClass(Reporter reporter, BackendOptions backendOptions) {
//		QName classQName = new QName("org", "jpr", "debug", "MyClass");
//		AbstractFunction mainFunction = new AbstractF
//		ClassDeclaration cd = new ClassDeclaration() {
//
//			@Override
//			public List<MemberValue> getMemberValues() {
//				return List.of();
//			}
//
//			@Override
//			public List<AbstractFunction> getFunctions() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			@Override
//			public QName getQName() {
//				return classQName;
//			}
//
//			@Override
//			public boolean isAncestorOrSame(Type type) {
//				return false;
//			}
//			
//		};
//		return new JvmClass(reporter, cd, backendOptions);
//	}
	
	public JvmPackage(Reporter reporter, PackageDeclaration packageDeclaration, BackendOptions backendOptions) {
		super();
		this.reporter = reporter;
		this.packageDeclaration = packageDeclaration;
		this.packageClass = new JvmClass(reporter, packageDeclaration, backendOptions);
		this.backendOptions = backendOptions;

		jvmClasses.add(this.packageClass);
		for(ClassDeclaration cd: packageDeclaration.getClasses()) {
			jvmClasses.add(new JvmClass(reporter, cd, backendOptions));

//			if(Util.debugMainClass)
//				jvmClasses.add(debugJvmMainClass(reporter, backendOptions));
		}
		for(InterfaceDeclaration id: packageDeclaration.getInterfaces()) {
			jvmClasses.add(new JvmClass(reporter, id, backendOptions));
		}

		Collection<AbstractFunction> packageFuncs = packageDeclaration.getFunctions();
		packageFuncs = retainOnlyAllArgsFunctions(packageFuncs);	// TODO: remove

		for(AbstractFunction func: packageFuncs) {
			packageClass.addTopLevelFunction(func);
		}
	}

	@Override
	public String toString() {
		return "Package '" + packageDeclaration.getQName().dotSeparated() + "'";
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
		for(JvmClass c: jvmClasses) {
			c.toBytecode(listeners);
		}
	}		
}