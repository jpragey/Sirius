package org.sirius.backend.jvm;

import java.util.HashMap;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ModuleDeclaration;

public class InMemoryClassWriterListener implements ClassWriterListener {

	// dot-separated class name -> class bytecode
	private HashMap<String, Bytecode> byteCodesMap = new HashMap<>();


	public class InMemoryClassLoader extends ClassLoader{
		
	    public InMemoryClassLoader(ClassLoader parent) {
	        super(parent);
	    }

	    @Override
	    public Class loadClass(String name) throws ClassNotFoundException {
//	    	String className = "pkg." + "Hello"; 
//	    	if(! (className) .equals(name))
//	    		return super.loadClass(name);

	    	Bytecode bytecode = byteCodesMap.get(name);
	    	if(bytecode == null) {
	    		return super.loadClass(name);
	    	} else {
		    	return defineClass(name, bytecode.getBytes(), 0, bytecode.getBytes().length);
	    	}
	    }
	}

	private InMemoryClassLoader classLoader = new InMemoryClassLoader(getClass().getClassLoader());

	public InMemoryClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public void start(ModuleDeclaration moduleDeclaration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addByteCode(Bytecode bytecode, QName classQName) {
		byteCodesMap.put(classQName.dotSeparated(), bytecode);
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub

	}
	public HashMap<String, Bytecode> getByteCodesMap() {
		return byteCodesMap;
	}

}
