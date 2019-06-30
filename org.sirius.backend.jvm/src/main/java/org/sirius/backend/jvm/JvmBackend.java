package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.objectweb.asm.ClassWriter;
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
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.Type;

public class JvmBackend implements Backend {

	private final static int VERSION = 49;

//	private ClassWriter classWriter;

	private Reporter reporter;
	private Optional<String> classDir;
	
	// '--verbose' cli option has the 'ast' flag
	private boolean verboseAst = false;
	
	public JvmBackend(Reporter reporter, Optional<String> classDir, boolean verboseAst) {
		super();
		this.reporter = reporter;
		this.classDir = classDir;
		this.verboseAst = verboseAst;
		
		System.out.println("JVM backend: verboseAst=" + verboseAst);
		
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
		printIfVerbose("JVM: starting session, nb of modules: " + session.getModuleDeclarations().size());
		session.getModuleDeclarations().stream().forEach(this::processModule);
	}
	
	private void processModule(ModuleDeclaration declaration) {
		printIfVerbose("Jvm: processing module " + declaration);
//				System.out.println("Jvm: processing module " + declaration);
		////		reporter.info("Jvm: processing module " + declaration.getQName());
		
		declaration.getPackages().stream().forEach(this::processPackage);
	}
	private void processPackage(PackageDeclaration pkgDeclaration) {
		printIfVerbose("Jvm: processing package ..." /*+ pkgDeclaration*/);
//		declaration.getFunctions().forEach(this::processTopLevelFunction);
		processTopLevelFunctions(pkgDeclaration.getFunctions(), new QName("TODO_pkgqname"));
		pkgDeclaration.getClasses().forEach(this::processClass);
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
						
					};
					memberFunctions.add(mf);
				}
				return memberFunctions;
			}
		};
		processClass(classDeclaration);
//		declaration.getFunctions();
	}
	private void processClass(ClassDeclaration declaration) {
		printIfVerbose("Jvm: processing class " + declaration.getQName().toString());

		JvmClassWriter writer = new JvmClassWriter(reporter, classDir, verboseAst);
		Bytecode bytecode = writer.createByteCode(declaration);
		classDir.ifPresent(cdir -> bytecode.createClassFiles(reporter, cdir, declaration.getQName()));
		
////		System.out.println("bytecode: " + bytecode.size() + " bytes: " + bytecode);
	}
}
