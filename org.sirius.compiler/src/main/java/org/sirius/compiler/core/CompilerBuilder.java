package org.sirius.compiler.core;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.sirius.backend.core.Backend;
import org.sirius.backend.jvm.JvmBackend;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.compiler.cli.framework.BoundOption;
import org.sirius.compiler.cli.framework.OptionParser;
import org.sirius.compiler.options.AbstractOptionValues;
import org.sirius.compiler.options.CompilerOptionValues;
import org.sirius.compiler.options.Help;
import org.sirius.compiler.options.OptionsRepository;
import org.sirius.frontend.core.FileInputTextProvider;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.InputTextProvider;

/**
 * 
 * @author jpragey
 *
 */
public class CompilerBuilder {
	private Reporter reporter = new ShellReporter();
//	private List<Backend> backends = new ArrayList<>();
	//Stream<InputTextProvider>
//	private List<Stream<InputTextProvider>> inputTextProviderFactories = new ArrayList<>();  
	private List<InputTextProvider> inputTextProviders = new ArrayList<>();  
	
	private List<Supplier<Backend> > backendFactories = new ArrayList<>(); 

	
//	private Optional<AbstractOptionValues> compilerOptionValues = Optional.empty();
	private Optional<String[]> cliArgs = Optional.empty();
	
	
	public CompilerBuilder(Reporter reporter) {
		super();
		this.reporter = reporter;
	}
	public CompilerBuilder() {
		this(new ShellReporter());
	}
	
	
	public CompilerBuilder setReporter(Reporter reporter) {
		this.reporter = reporter;
		return this;
	}
	
	public CompilerBuilder addJvmBackend() {
		this.backendFactories.add(() -> new JvmBackend());
		return this;
	}

	public void addInputTextProvider(InputTextProvider provider) {
		this.inputTextProviders.add(provider);
	}

//	private class FileInputTextProvider implements InputTextProvider {
//		private File file;
//
//		public FileInputTextProvider(File file) {
//			super();
//			this.file = file;
//		}
//
//		@Override
//		public String getText() {
//			StringBuilder sb = new StringBuilder();
//			try {
//				for(String line: Files.readAllLines(file.toPath())) {
//					sb.append(line);
//				}
//			} catch (IOException e) {
//				reporter.error("Can't read content of file " + getInputLocation(), e);
//			}
//			return sb.toString();
//		}
//
//		@Override
//		public String getInputLocation() {
//			return file.getAbsolutePath(); // TODO: not optimal
//		}
//		
//		@Override
//		public Boolean isModuleDescriptor() {
//			return file.getName() == "module.sirius";	// TODO : not clean
//		}
//
//		@Override
//		public Boolean isPackageDescriptor() {
//			return file.getName() == "package.sirius";	// TODO : not clean
//		}
//
//	}
	private Charset charset =Charset.forName("UTF8");
	
	public void addInputDirectory(Path rootDirectory, Path directory) {
//		Paths.
		File[] content = rootDirectory.resolve(directory).toFile().listFiles(); // Null if it's not a directory
		if(content != null) {
			for(File file: content) {
				
				if(file.isDirectory())
					addInputDirectory(rootDirectory, file.toPath().relativize(rootDirectory));
				
				if(file.getName().endsWith(".sirius")) {
					Reporter r = reporter;
					InputTextProvider p = new FileInputTextProvider(r, rootDirectory.toFile(), directory.toString() /*TOD:???*/, file.getName(), charset);
				}
//					addInputTextProvider(new FileInputTextProvider(file));
			}
			
		} else {
			reporter.error(directory + " is not a directory.");	// TODO:dir name
		}
	}

//	public void setCompilerOptions(CompilerOptionValues values) {
//		this.compilerOptionValues = Optional.of(values);
//	}
	
	public void setCliArs(String[] cliArgs) {
		this.cliArgs = Optional.ofNullable(cliArgs);
	}
	
	private AbstractOptionValues getOptionValues() {
//		if(compilerOptionValues.isPresent())
//			return compilerOptionValues.get();
		
		CompilerOptionValues values = new CompilerOptionValues(reporter);
		if(this.cliArgs.isPresent()) {
			List<BoundOption<Help>> parsers = OptionsRepository.bindStandardCompilerOptions(values);
			OptionParser<CompilerOptionValues, Help> parser = new OptionParser<CompilerOptionValues, Help>(parsers);
			Optional<String> error = parser.parse(this.cliArgs.get());
			error.ifPresent(msg -> reporter.error(msg));
		}
		return values;
		
	}
	
	private List<Backend> createBackends() {
		
		List<Backend> backends = backendFactories.stream()
				.map(f -> f.get())
				.collect(Collectors.toList());
		return backends;
	}
	
	public Compiler buildStandard() {
		List<Backend> backends = createBackends();
		AbstractOptionValues options = getOptionValues();
		FrontEnd frontEnd = new FrontEnd(reporter);
				
		return new StandardCompiler(reporter, backends, frontEnd, this.inputTextProviders, options);
	}
	public Compiler buildScript() {
		List<Backend> backends = createBackends();
		FrontEnd frontEnd = new FrontEnd(reporter);
		
		return new ScriptCompiler(reporter, backends, frontEnd);
	}
}
