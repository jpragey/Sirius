package org.sirius.backend.jvm.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.Bytecode;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.core.ScriptSession;

public class ModuleTest {

	private Reporter reporter;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		
	}

	@Test
	@DisplayName("Happy path for JVM module creation")
	public void moduleDescriptorCreationHappyPathTest() throws Exception {
		String script = "#!\n "
				+ "module a.b.c \"1.0\" {}\n"
				+ "class A(){}\n"
		+ "Integer main() {Integer a = 10; return a;}";
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, /*classDir, moduleDir, */ false /*verboseAst*/, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		
		HashMap<QName, Bytecode> bytecodeMap = new HashMap<QName, Bytecode>();
		backend.addInMemoryMapOutput(bytecodeMap);
				
		backend.process(session);
		
		bytecodeMap.forEach((qn, bc) -> { System.out.println("Class: " + qn + " -> " + bc); });
		
//		Bytecode moduleInfoBc = bytecodeMap.get(new QName("a", "b", "c", "module-info"));
		Bytecode moduleInfoBc = bytecodeMap.get(new QName("module-info"));
		assertThat(moduleInfoBc, notNullValue());
		byte [] bytes = moduleInfoBc.getBytes();
		
		ModuleInfo mi = parseModuleBytecode(bytes);

		// -- 
		assertThat(mi.module.name, is("a.b.c"));
		assertThat(mi.module.access, is(1));
		assertThat(mi.module.version, is("1.0"));

		
		assertThat(mi.mainClass, nullValue());
		assertThat(mi.exports.size(), is(1));
		assertThat(mi.exports.get(0).packaze, is("a/b/c"));
		assertThat(mi.exports.get(0).modules.length, is(0));

		assertThat(mi.requires.size(), is(3));
		assertThat(mi.requires.get(0).module, is("java.base"));
		assertThat(mi.requires.get(1).module, is("org.sirius.runtime"));
		assertThat(mi.requires.get(2).module, is("org.sirius.sdk"));

//		ClassLoader classLoader = l.getClassLoader();
//		
//		String mainClassQName = "$package$"; 
//		
//		Class<?> cls = classLoader.loadClass(mainClassQName);
//
//		Object helloObj = cls.getDeclaredConstructor().newInstance();
//		Method[] methods = helloObj.getClass().getDeclaredMethods();
//		
//		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
//		Object[] argTypes = new Object[] {};
//		
//		Object result = main.invoke(null, argTypes /*, args*/);
//		
//		assertEquals(result.getClass().getName(), "sirius.lang.Integer");
//		assertEquals( ((sirius.lang.Integer)result).getValue(), 10);
//
	}
	
	private static class ModuleInfo {
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
	
	private ModuleInfo parseModuleBytecode(byte [] bytes) {
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
	
	@Test 
	@Disabled("Temp, to remove")
	public void dumpExistingModuleInfo() throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get("/tmp/siriusDist/module-info.class"));
		parseModuleBytecode(bytes);
	}
	
}
