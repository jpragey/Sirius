package org.sirius.backend.jvm;

import java.util.HashMap;
import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;

public class InMemoryClassWriterListener implements ClassWriterListener {

	// dot-separated class name -> class bytecode
	private HashMap<String, Bytecode> byteCodesMap = new HashMap<>();


	public static class InMemoryClassLoader extends ClassLoader{
		
		private HashMap<String /** binary class name */, Bytecode> byteCodesMap;
		/** Class loader that loads bytecode from {@link Bytecode} objects before delegating to parent classloader.
		 * Note that {@link #byteCodesMap} is index by "binary class name" in JVM sense, eg "java.security.KeyStore$Builder$FileBuilder$1"
		 * 
		 * @param parent parent classloader, will be searched for if byteCodesMap has no matching entry
		 * @param byteCodesMap Map binary class name -> Bytecode
		 */
		public InMemoryClassLoader(ClassLoader parent, HashMap<String, Bytecode> byteCodesMap) {
	        super(parent);
			this.byteCodesMap = byteCodesMap;
	    }
		/** Creates a InMemoryClassLoader, reports an error if byteCodes is invalid (eg duplicate class names)
		 * 
		 * @param reporter
		 * @param parent
		 * @param byteCodes
		 * @return
		 */
		public static InMemoryClassLoader create(Reporter reporter, ClassLoader parent, List<Bytecode> byteCodes) {
			HashMap<String, Bytecode> byteCodesMap = new HashMap<String, Bytecode>();
			byteCodes.forEach(bc-> {
				String binaryClassName = bc.getBinaryClassName();
				if(byteCodesMap.containsKey(binaryClassName)) {
					reporter.fatal("Duplicate class name found while creating classloader: " + binaryClassName); // of fatal ???
				} else {
					byteCodesMap.put(binaryClassName, bc);
				}
			});
			
			InMemoryClassLoader cl = new InMemoryClassLoader(parent, byteCodesMap);
			return cl;
		}

		@Override
	    public Class<?> loadClass(String name) throws ClassNotFoundException {
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

	private InMemoryClassLoader classLoader = new InMemoryClassLoader(getClass().getClassLoader(), byteCodesMap);

	public InMemoryClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public void start(ModuleDeclaration moduleDeclaration) {
		// Nothing to do
	}

	@Override
	public void addByteCode(Bytecode bytecode) {
		byteCodesMap.put(bytecode.getClassQName().dotSeparated(), bytecode);
	}

	@Override
	public void end() {
		// Nothing to do
	}
	
	/** Return map <dot-separated class name> -> <class bytecode>
	 * @return
	 */
	public HashMap<String, Bytecode> getByteCodesMap() {
		return byteCodesMap;
	}

}
