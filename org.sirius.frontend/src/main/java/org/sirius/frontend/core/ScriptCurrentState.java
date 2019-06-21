package org.sirius.frontend.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;

public class ScriptCurrentState {

//	private Reporter reporter;
//	private LinkedList<AstModuleDeclaration> moduleList = new LinkedList<>();
//
//	
//	public ScriptCurrentState(Reporter reporter) {
//		super();
//		this.reporter = reporter;
//	}
//
//	/** Get current module, create an empty one if none
//	 * 
//	 * @return
//	 */
//	public AstModuleDeclaration getCurrentModule() {
//		AstModuleDeclaration last = moduleList.peekLast();
//		if(last == null) {
//			last = new AstModuleDeclaration(reporter);
//			moduleList.add(last);
//		}
//		return last;
//	}
//	
//	public void addModule(AstModuleDeclaration declaration) {
//		moduleList.add(declaration);
//	}
//	
//	public AstPackageDeclaration getCurrentPackage() {
//		AstModuleDeclaration currentModule = getCurrentModule();
//		AstPackageDeclaration current = currentModule.getCurrentPackage();
//		return current;
//	}
//
//	public LinkedList<AstModuleDeclaration> getModuleList() {
//		return moduleList;
//	}
	
}
