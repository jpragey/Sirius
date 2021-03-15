package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.sirius.backend.core.Backend;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.Session;

public class JvmBackend implements Backend {

	private Reporter reporter;

	private List<ClassWriterListener> listeners = new ArrayList<>();

	// '--verbose' cli option has the 'ast' flag
	private boolean verboseAst = false;
	
	private BackendOptions backendOptions;
	
	public JvmBackend(Reporter reporter, boolean verboseAst, BackendOptions backendOptions) {
		super();
		this.reporter = reporter;
		this.verboseAst = verboseAst;
		this.backendOptions = backendOptions;
	}

	public JarCreatorListener addFileOutput(String moduleDir, Optional<String> classDir) {
		JarCreatorListener listener = JarCreatorListener.createAsFile(reporter, moduleDir, classDir);
		listeners.add(listener);
		return listener;
	}

	public JarCreatorListener addInMemoryMapOutput(HashMap<QName /*class QName */, Bytecode> bytecodeMap) {
		JarCreatorListener listener = JarCreatorListener.createInMemoryMap(reporter, bytecodeMap);
		listeners.add(listener);
		return listener;
	}
	
	public InMemoryClassWriterListener addInMemoryOutput() {
		InMemoryClassWriterListener l = new InMemoryClassWriterListener();
		listeners.add(l);
		return l;
	}
	
	
	@Override
	public String getBackendId() {
		return "jvm";
	}

	private void printIfVerbose(String s) {
		if(verboseAst)
			System.out.println(s);
	}
	
	@Override
	public void process(Session session) {
//		processSDK();
		printIfVerbose("JVM: starting session, nb of modules: " + session.getModuleDeclarations().size());
		session.getModuleDeclarations().stream().forEach(this::processModule);
		
		backendOptions.checkAllJvmMainBytecodeWritten();
	}
	
	private void processModule(ModuleDeclaration declaration) {
		printIfVerbose("Jvm: processing module " + declaration);

		listeners.forEach(l ->  l.start(declaration) );


		CodeTreeBuilder codeTreeBuilder = new CodeTreeBuilder(reporter, backendOptions);
		declaration.visitMe(codeTreeBuilder);
		codeTreeBuilder.createByteCode(listeners);
		
		listeners.forEach(ClassWriterListener::end);

	}
	
}
