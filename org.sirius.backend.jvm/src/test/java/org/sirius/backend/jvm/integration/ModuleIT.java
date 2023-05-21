package org.sirius.backend.jvm.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

public class ModuleIT {

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
		
		JvmBackend backend = new JvmBackend.Builder(reporter).build();
		
		HashMap<QName, Bytecode> bytecodeMap = backend.addInMemoryMapOutput();
		backend.process(session);
		
//		bytecodeMap.forEach((qn, bc) -> { System.out.println("Class: " + qn + " -> " + bc); });
		
		Bytecode moduleInfoBytecode = bytecodeMap.get(new QName("module-info"));
		assertThat(moduleInfoBytecode, notNullValue());
		byte [] bytes = moduleInfoBytecode.getBytes();
		
		Utils.ModuleInfo mi = Utils.parseModuleBytecode(bytes);

		// -- 
		Utils.Module moduleUT = mi.module; 
		assertThat(moduleUT.name(), is("a.b.c"));
		assertThat(moduleUT.access(), is(1));
		assertThat(moduleUT.version(), is("1.0"));
		
		assertThat(mi.mainClass, nullValue());
		
		List<Utils.Export> exportsUT = mi.exports;
		assertThat(exportsUT.size(), is(1));
		assertThat(exportsUT.get(0).packaze(), is("a/b/c"));
		assertThat(exportsUT.get(0).modules().size(), is(0));

		List<String> requiredModules = mi.requires.stream()
				.map(Utils.Require::module)
				.toList();
		assertThat(requiredModules, 
				contains("java.base", "org.sirius.runtime", "org.sirius.sdk"));
		
	}
	
}
