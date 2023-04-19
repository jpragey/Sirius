package org.sirius.compiler.core;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.sirius.backend.core.Backend;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.error.Reporter;
import org.sirius.compiler.options.CompileOptionsValues;
import org.sirius.compiler.options.RootOptionValues;
import org.sirius.frontend.core.FileInputTextProvider;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.InputTextProvider;

/** Compiler, compiles sources for a list of backends 
 */
public class CompilerTool {

	private Reporter reporter;
	private RootOptionValues optionValues;
	private CompileOptionsValues compileOptions;
	private Function<String, InputTextProvider> inputTextFactory;
	
	private static InputTextProvider defaultInputTextFactory(Reporter reporter, String source) {
		return new FileInputTextProvider(reporter, 
			new File("."), //rootDirectory, 
			"", //packagePhysicalName, 
			source, //resourcePhysicalName, 
			Charset.forName("UTF8"));};

	public CompilerTool(Reporter reporter, RootOptionValues optionValues, CompileOptionsValues compileOptions, Function<String, InputTextProvider> inputTextFactory) {
		super();
		this.reporter = reporter;
		this.optionValues = optionValues;
		this.compileOptions = compileOptions;
		this.inputTextFactory = inputTextFactory;
	}
	public CompilerTool(Reporter reporter, RootOptionValues optionValues, CompileOptionsValues compileOptions) {
		this(reporter, optionValues, compileOptions, (source -> defaultInputTextFactory(reporter, source)) );
	}
	
	private JvmBackend createJvmBackend() {
		BackendOptions options = new BackendOptions(reporter, compileOptions.getJvmMain());
		JvmBackend backend = new JvmBackend(reporter, 
				options);
		
		compileOptions.getModuleDir().ifPresent(moduleDir -> backend.addFileOutput(moduleDir, compileOptions.getClassDir()));
		return backend;
	}

	/** Compiles for default backend (JVM) */
	public void runCompileTool() {
		List<Backend> backends = List.of(createJvmBackend());
		runCompileTool(backends);
	}
	
	/** Compiles for a list of backends */
	public void runCompileTool(List<Backend> backends) {
		
		FrontEnd frontEnd = new FrontEnd(reporter);
		ScriptCompiler compiler = new ScriptCompiler(reporter, backends, frontEnd, optionValues, compileOptions, inputTextFactory);
		compiler.compile();
	}

}
