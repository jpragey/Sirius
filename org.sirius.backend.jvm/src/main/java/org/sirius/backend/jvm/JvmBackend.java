package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.sirius.backend.core.Backend;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.Type;

public class JvmBackend implements Backend {

	private Reporter reporter;
//	private Optional<String> classDir;
	// -- as given by '--module' option
//	private Optional<String> moduleDir;

	private List<ClassWriterListener> listeners = new ArrayList<>();

	// '--verbose' cli option has the 'ast' flag
	private boolean verboseAst = false;
	
	public JvmBackend(Reporter reporter, /*Optional<String> classDir, */ /*Optional<String> moduleDir, */boolean verboseAst
			
//			List<ClassWriterListener> listeners
			) {
		super();
		this.reporter = reporter;
//		this.classDir = classDir;
//		this.moduleDir = moduleDir;
		this.verboseAst = verboseAst;
//		this.listeners = listeners;
	
//		System.out.println("JVM backend: verboseAst=" + verboseAst);
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
