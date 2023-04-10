package org.sirius.backend.jvm.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.Bytecode;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.backend.jvm.Utils;
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
		
		Utils.ModuleInfo mi = Utils.parseModuleBytecode(bytes);

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

	}
	
}
