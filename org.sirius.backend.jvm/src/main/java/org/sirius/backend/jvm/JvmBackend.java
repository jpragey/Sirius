package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sirius.backend.core.Backend;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.FunctionParameter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.api.Visitor;

public class JvmBackend implements Backend {
	private static Logger logger = LogManager.getLogger(JvmBackend.class);
	private Reporter reporter;

	private List<ClassWriterListener> listeners = new ArrayList<>();

	private BackendOptions backendOptions;
	
	public JvmBackend(Reporter reporter, BackendOptions backendOptions, List<ClassWriterListener> listeners) {
		super();
		this.reporter = reporter;
		this.backendOptions = backendOptions;
		this.listeners = listeners;
	}
	/** Create a JvmBackend without any output - add one later if needed
	 * 
	 * @param reporter
	 * @param backendOptions
	 */
	public JvmBackend(Reporter reporter, BackendOptions backendOptions) {
		this(reporter, backendOptions, new ArrayList<>());
	}

	public JvmBackend(Reporter reporter) {
		this(reporter, new BackendOptions(reporter, Optional.empty() /* optJvmMainOption */));
	}

	public static class Builder {
		private Reporter reporter;
		private Optional<BackendOptions> backendOptions;
		private List<ClassWriterListener> listeners;
		
		public Builder(Reporter reporter) {
			super();
			this.reporter = reporter;
			this.listeners = new ArrayList<>();
			this.backendOptions = Optional.empty();
		}
		public Builder backendOptions(BackendOptions backendOptions) {
			this.backendOptions = Optional.of(backendOptions); 
			return this;
		}
		public Builder addClassWriterListener(ClassWriterListener listener) {
			this.listeners.add(listener); 
			return this;
		}
		public JvmBackend build() {
			return new JvmBackend(reporter, 
					backendOptions.orElse(new BackendOptions(reporter, Optional.empty() /* jvmMain */)), 
					listeners);
		}
		
	}
	
	public JarCreatorListener addFileOutput(String moduleDir, Optional<String> classDir) {
		JarCreatorListener listener = JarCreatorListener.createAsFile(reporter, moduleDir, classDir);
		listeners.add(listener);
		return listener;
	}

	public JarCreatorListener addInMemoryMapOutput(Map<QName /*class QName */, Bytecode> bytecodeMap) {
		JarCreatorListener listener = JarCreatorListener.createInMemoryMap(reporter, bytecodeMap);
		listeners.add(listener);
		return listener;
	}
	public HashMap<QName /*class QName */, Bytecode> addInMemoryMapOutput() {
		HashMap<QName, Bytecode> bytecodeMap = new HashMap<>();
		JarCreatorListener listener = JarCreatorListener.createInMemoryMap(reporter, bytecodeMap);
		listeners.add(listener);
		return bytecodeMap;
	}
	
	public InMemoryClassWriterListener addInMemoryOutput() {
		InMemoryClassWriterListener l = new InMemoryClassWriterListener();
		listeners.add(l);
		return l;
	}
	
	/**
	 * @return {@link JvmConstants#BACKEND_ID} 
	 */
	@Override
	public String getBackendId() {
		return JvmConstants.BACKEND_ID;
	}

	@Override
	public void process(Session session) {
		List<JvmModule> ignored = jvmProcess(session);
		
	}
	public List<JvmModule> jvmProcess(Session session) {
		logger.debug("JVM: starting session, {} modules", () -> session.getModuleDeclarations().size());
		List<JvmModule> modules = session.getModuleDeclarations().stream()
				.map( moduleDeclaration -> processModule(moduleDeclaration))
				.collect(Collectors.toList());
		
		backendOptions.checkAllJvmMainBytecodeWritten();
		
		return modules;
	}
	
	private JvmModule processModule(ModuleDeclaration moduleDeclaration) {
		logger.debug("Jvm (debug) : processing module {}", () -> moduleDeclaration.toString());
		
		listeners.forEach(l ->  l.start(moduleDeclaration) );
		
		JvmModuleBuilderVisitor jvmModuleBuilder = new JvmModuleBuilderVisitor(reporter, backendOptions);
		moduleDeclaration.visitMe(jvmModuleBuilder);
		JvmModule jvmModule = jvmModuleBuilder.createByteCode(listeners);
		
		listeners.forEach(ClassWriterListener::end);
		return jvmModule;
	}
	
}
