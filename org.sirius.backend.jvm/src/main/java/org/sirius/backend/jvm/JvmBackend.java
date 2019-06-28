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

public class JvmBackend implements Backend {

	private final static int VERSION = 49;

//	private ClassWriter classWriter;

	private Reporter reporter;
	private Optional<String> classDir;
	
	public JvmBackend(Reporter reporter, Optional<String> classDir) {
		super();
		this.reporter = reporter;
		this.classDir = classDir;
	}

	@Override
	public String getBackendId() {
		return "jvm";
	}

	@Override
	public void process(Session session) {
		session.getModuleDeclarations().stream().forEach(this::processModule);
	}
	
	private void processModule(ModuleDeclaration declaration) {
		System.out.println("Jvm: processing module " + declaration);
		reporter.info("Jvm: processing module " + declaration.getQName());
		
		declaration.getPackages().stream().forEach(this::processPackage);
	}
	private void processPackage(PackageDeclaration pkgDeclaration) {
//		declaration.getFunctions().forEach(this::processTopLevelFunction);
		processTopLevelFunctions(pkgDeclaration.getFunctions(), new QName("TODO_pkgqname"));
		pkgDeclaration.getClasses().forEach(this::processClass);
	}
	
	
	private void processTopLevelFunctions(List<TopLevelFunction> declarations, QName pkgQName) {
		QName classQName = pkgQName.child("$package$");
		
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
		JvmClassWriter writer = new JvmClassWriter(reporter, classDir);
		Bytecode bytecode = writer.createByteCode(declaration);
		classDir.ifPresent(cdir -> bytecode.createClassFiles(reporter, cdir, declaration.getQName()));
		
		System.out.println("bytecode: " + bytecode.size() + " bytes: " + bytecode);
	}
}
