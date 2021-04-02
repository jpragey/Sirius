package org.sirius.backend.jvm;

import java.util.ArrayList;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;

public class Utils {

	public static class ModuleInfo {
		public String mainClass;
//		public ModuleVisitor visitModule(String name, int access, String version) {
//		public ModuleVisitor visitModule(String name, int access, String version) {
		public static class Module {
			public String name;
			public int access;
			public String version;
			public Module(String name, int access, String version) {
				super();
				this.name = name;
				this.access = access;
				this.version = version;
			}
		}
		public Module module = null;
		public static class Require {
			public String module;
			public int access;
			public String version;
			public Require(String module, int access, String version) {
				super();
				this.module = module;
				this.access = access;
				this.version = version;
			}
		
		}
		public static class Export {
			public String packaze;
			public int access;
			public String[] modules = {};
			public Export(String packaze, int access, String[] modules) {
				super();
				this.packaze = packaze;
				this.access = access;
				this.modules = modules == null ? new String[0] : modules;
			}
			
//				public void visitExport(String packaze, int access, String... modules) {

		}
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
				moduleInfo.module = new ModuleInfo.Module(name, access, version);
				return new ModuleVisitor(Opcodes.ASM9) {

					@Override
					public void visitMainClass(String mainClass) {
//						System.out.println("Module: Main class : " + mainClass);
						moduleInfo.mainClass = mainClass;
					}

					@Override
					public void visitRequire(String module, int access, String version) {
//						System.out.println("Module: Require : " + module + ",  access=" + access + ", version: " + version);
						moduleInfo.requires.add(new ModuleInfo.Require(module, access, version));
					}

					@Override
					public void visitExport(String packaze, int access, String... modules) {
//						System.out.println("Module: export package : " + packaze + ",  access=" + access 
//								+ ", modules: " + (modules == null ? "<null>" : Stream.of(modules).collect(Collectors.joining(","))));
						moduleInfo.exports.add(new ModuleInfo.Export(packaze, access, modules));
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
