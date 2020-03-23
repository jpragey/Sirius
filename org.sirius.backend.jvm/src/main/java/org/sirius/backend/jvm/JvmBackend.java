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
//				System.out.println("Jvm: processing module " + declaration);
		////		reporter.info("Jvm: processing module " + declaration.getQName());

		
		// -- Listener to create jar
//		moduleDir.ifPresent(modulePath ->  listeners.add(new JarCreatorListener(reporter, modulePath, declaration.getQName())));

		
		listeners.forEach(l ->  l.start(declaration) );

		declaration.getPackages().stream().forEach(this::processPackage);
		
		listeners.forEach(ClassWriterListener::end);

	}
	private void processPackage(PackageDeclaration pkgDeclaration) {
		printIfVerbose("Jvm: processing package '" + pkgDeclaration.getQName() + "'");
		processTopLevelFunctions(pkgDeclaration.getFunctions(), pkgDeclaration.getQName());
		pkgDeclaration.getClasses().forEach(classDecl -> processClass(classDecl) );
	}
	
	
	private void processTopLevelFunctions(List<TopLevelFunction> declarations, QName pkgQName) {
		QName classQName = pkgQName.child("$package$");
		printIfVerbose("Jvm: processing top-level function in package " + pkgQName.toString());
		
		ClassDeclaration classDeclaration = new ClassDeclaration() {
			
			@Override
			public List<MemberValue> getValues() {
				return Collections.emptyList();
			}
			
			@Override
			public QName getQName() {
				return classQName;
			}
			
			@Override
			public List<MemberFunction> getFunctions() {
				List<MemberFunction> memberFunctions = new ArrayList<>();
				for(TopLevelFunction tlf:declarations ) {
					MemberFunction mf = new MemberFunction() {

						@Override
						public QName getQName() {
							return classQName.child(tlf.getQName());
						}

						@Override
						public List<FunctionFormalArgument> getArguments() {
							return tlf.getArguments();
						}

						@Override
						public Type getReturnType() {
							return tlf.getReturnType();
						}

						@Override
						public List<Statement> getBodyStatements() {
							return tlf.getBodyStatements();
						}
						
					};
					memberFunctions.add(mf);
				}
				return memberFunctions;
			}
			@Override
			public boolean isAncestorOrSame(Type type) {
				throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
			}

		};
		processClass(classDeclaration);
	}
	private void processClass(ClassDeclaration declaration) {
		printIfVerbose("Jvm: processing class " + declaration.getQName().toString());

		JvmClassWriter writer = new JvmClassWriter(reporter, /*classDir, */listeners, verboseAst);
		Bytecode bytecode = writer.createByteCode(declaration);
//		classDir.ifPresent(cdir -> bytecode.createClassFiles(reporter, cdir, declaration.getQName()));

//		System.out.println("bytecode: " + bytecode.size() + " bytes: " + bytecode);
	}
}
