package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.backend.core.Backend;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.Session;

public class JvmBackend implements Backend {

	private Reporter reporter;

	private List<ClassWriterListener> listeners = new ArrayList<>();

	// '--verbose' cli option has the 'ast' flag
	private boolean verboseAst = false;
	
	public JvmBackend(Reporter reporter, boolean verboseAst) {
		super();
		this.reporter = reporter;
		this.verboseAst = verboseAst;
	}

	public void addFileOutput(String moduleDir, Optional<String> classDir) {
		listeners.add(new JarCreatorListener(reporter, moduleDir/*, declaration.getQName()*/, classDir));
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
		processSDK();
		printIfVerbose("JVM: starting session, nb of modules: " + session.getModuleDeclarations().size());
		session.getModuleDeclarations().stream().forEach(this::processModule);
	}
	
	private void processSDK() {
		
	}
	
	private void processModule(ModuleDeclaration declaration) {
		printIfVerbose("Jvm: processing module " + declaration);

		listeners.forEach(l ->  l.start(declaration) );

		CodeTreeBuilder codeTreeBuilder = new CodeTreeBuilder();
		declaration.visitMe(codeTreeBuilder);
		codeTreeBuilder.createByteCode(listeners);
		
		listeners.forEach(ClassWriterListener::end);

	}
	
}
