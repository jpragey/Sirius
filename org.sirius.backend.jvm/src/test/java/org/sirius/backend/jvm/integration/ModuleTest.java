package org.sirius.backend.jvm.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.core.PhysicalPath;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.backend.jvm.aux.apiimpl.SessionImpl;

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
				+ "module a.b.c \"1.0\" {}\n";
//				+ "class A(){}\n"
//		+ "Integer main() {Integer a = 10; return a;}";
		
		ScriptSession session = CompileTools.compileScript(script, reporter);
		JvmBackend backend = new JvmBackend(reporter, new BackendOptions(reporter, Optional.empty() /*jvmMain*/));
		
		HashMap<QName, Bytecode> bytecodeMap = new HashMap<QName, Bytecode>();
		backend.addInMemoryMapOutput(bytecodeMap);
		
		
//		ModuleDeclaration moduleDeclaration = new ModuleDeclaration() {
//			
//			@Override
//			public String version() {
//				return "1.0";
//			}
//			
//			@Override
//			public Optional<QName> qName() {
//				return Optional.of(QName.of("a", "b", "c"));
//			}
//			
//			@Override
//			public PhysicalPath physicalPath() {
//				return new PhysicalPath();
//			}
//			
//			@Override
//			public List<PackageDeclaration> packageDeclarations() {
//				return List.of();
//			}
//		};
//		Session session2 = new SessionImpl(reporter, List.of());

		// -- Prepare session
		Session session3 = mock(Session.class);
		ModuleDeclaration mockedMd = mock(ModuleDeclaration.class);
		when(mockedMd.version()).thenReturn("1.0");	// !!! ModuleDeclaration is Much more complex - dont mock
		
		when(session3.getModuleDeclarations())
			.thenReturn(List.<ModuleDeclaration>of(mockedMd));
		
//		backend.process(session3);
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

		
		assertThat(mi.requires.stream().map(require -> require.module).toList(), 
				is(List.of("java.base", "org.sirius.runtime", "org.sirius.sdk")));
		
	}
	
}
