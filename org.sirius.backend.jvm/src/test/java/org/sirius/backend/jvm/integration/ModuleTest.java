package org.sirius.backend.jvm.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
//		InMemoryClassWriterListener l = backend.addInMemoryOutput();
		
//		backend.addFileOutput("/tmp/siriusTmp/module", Optional.of("/tmp/siriusTmp/classes"));
		
		HashMap<QName, Bytecode> bytecodeMap = new HashMap<QName, Bytecode>();
		backend.addInMemoryMapOutput(bytecodeMap);
				
		backend.process(session);
		
		bytecodeMap.forEach((qn, bc) -> { System.out.println("Class: " + qn + " -> " + bc); });
		
		Bytecode moduleInfoBc = bytecodeMap.get(new QName("a", "b", "c", "module-info"));
		assertThat(moduleInfoBc, notNullValue());
		byte [] bytes = moduleInfoBc.getBytes();
		
		ClassReader cr = new ClassReader(bytes);
		ClassVisitor cv = new ClassVisitor(Opcodes.ASM9) {
			@Override
			public ModuleVisitor visitModule(String name, int access, String version) {
				System.out.println("Visiting module " + name + ", version " + version);
				return new ModuleVisitor(Opcodes.ASM9) {

					@Override
					public void visitMainClass(String mainClass) {
						System.out.println("Module: Main class : " + mainClass);
					}

					@Override
					public void visitRequire(String module, int access, String version) {
						System.out.println("Module: Require : " + module + ",  access=" + access + ", version: " + version);
					}

					@Override
					public void visitExport(String packaze, int access, String... modules) {
						System.out.println("Module: export package : " + packaze + ",  access=" + access 
								+ ", modules: " + Stream.of(modules).collect(Collectors.joining(",")));
					}

					@Override
					public void visitProvide(String service, String... providers) {
						System.out.println("Module: Provide, service :" + service  
								+ ", providers: " + Stream.of(providers).collect(Collectors.joining(",")));					}
					
				};
			}

			@Override
			public void visitSource(String source, String debug) {
				System.out.println("Visiting source " + source + ", debug " + debug);
			}
			
		};
		cr.accept(cv, 0);
		
		
		
		
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
	
	public void dumpModuleInfo(byte [] bytes) {
		ClassReader cr = new ClassReader(bytes);
		ClassVisitor cv = new ClassVisitor(Opcodes.ASM9) {
			@Override
			public ModuleVisitor visitModule(String name, int access, String version) {
				System.out.println("Visiting module " + name + ", version " + version);
				return new ModuleVisitor(Opcodes.ASM9) {

					@Override
					public void visitMainClass(String mainClass) {
						System.out.println("Module: Main class : " + mainClass);
					}

					@Override
					public void visitRequire(String module, int access, String version) {
						System.out.println("Module: Require : " + module + ",  access=" + access + ", version: " + version);
					}

					@Override
					public void visitExport(String packaze, int access, String... modules) {
						System.out.println("Module: export package : " + packaze + ",  access=" + access 
								+ ", modules: " + (modules == null ? "<null>" : Stream.of(modules).collect(Collectors.joining(","))));
					}

					@Override
					public void visitProvide(String service, String... providers) {
						System.out.println("Module: Provide, service :" + service  
								+ ", providers: " + Stream.of(providers).collect(Collectors.joining(",")));					}
					
				};
			}

			@Override
			public void visitSource(String source, String debug) {
				System.out.println("Visiting source " + source + ", debug " + debug);
			}
			
		};
		cr.accept(cv, 0);

	}
	
	@Test 
	@Disabled("Temp, to remove")
	public void dumpExistingModuleInfo() throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get("/tmp/siriusDist/module-info.class"));
		dumpModuleInfo(bytes);
	}
	
}
