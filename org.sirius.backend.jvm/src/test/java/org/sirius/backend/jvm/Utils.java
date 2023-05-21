package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;

public class Utils {


	public static record Module(String name, int access, String version) {}

	public static record Require(String module, int access, String version){}

	public static record Export(String packaze, int access, List<String> modules) {}
		

	public static class ModuleInfo {
		public String mainClass;
		public Module module = null;
		public ArrayList<Require> requires = new ArrayList<>();
		public ArrayList<Export> exports = new ArrayList<>();
	}

	public static ModuleInfo parseModuleBytecode(byte [] bytes) {
		ModuleInfo moduleInfo = new ModuleInfo();
		ClassReader cr = new ClassReader(bytes);
		ClassVisitor cv = new ClassVisitor(Opcodes.ASM9) {
			@Override
			public ModuleVisitor visitModule(String name, int access, String version) {
//				System.out.println("Visiting module " + name + ", version " + version);
				moduleInfo.module = new Module(name, access, version);
				return new ModuleVisitor(Opcodes.ASM9) {

					@Override
					public void visitMainClass(String mainClass) {
//						System.out.println("Module: Main class : " + mainClass);
						moduleInfo.mainClass = mainClass;
					}

					@Override
					public void visitRequire(String module, int access, String version) {
//						System.out.println("Module: Require : " + module + ",  access=" + access + ", version: " + version);
						moduleInfo.requires.add(new Require(module, access, version));
					}

					@Override
					public void visitExport(String packaze, int access, String... modules) {
//						System.out.println("Module: export package : " + packaze + ",  access=" + access 
//								+ ", modules: " + (modules == null ? "<null>" : Stream.of(modules).collect(Collectors.joining(","))));
						List<String> moduleArray = modules == null ? List.of() : List.of(modules); 
						moduleInfo.exports.add(new Export(packaze, access, moduleArray));
					}

					@Override
					public void visitProvide(String service, String... providers) {
//						System.out.println("Module: Provide, service :" + service  
//								+ ", providers: " + Stream.of(providers).collect(Collectors.joining(",")));					
					}
					
				};
			}

			@Override
			public void visitSource(String source, String debug) {
//				System.out.println("Visiting source " + source + ", debug " + debug);
			}
			
		};
		cr.accept(cv, 0);

		return moduleInfo;
	}

}
