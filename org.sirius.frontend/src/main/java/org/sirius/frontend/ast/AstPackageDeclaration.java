package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.apiimpl.PackageDeclarationImpl;
import org.sirius.frontend.symbols.LocalSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

/** Package declaration, eg as written in package descriptor.
 * It may also be an anonymous package declaration (without package descriptor) 
 * 
 * @author jpragey
 *
 */
public class AstPackageDeclaration implements Scoped, Visitable, Verifiable {

	private QName qname = new QName();
	
	private Reporter reporter;

	private List<Visitable> visitables = new ArrayList<>();

	private List<FunctionDefinition> functionDeclarations = new ArrayList<>();
	private List<AstClassDeclaration> classDeclarations = new ArrayList<>();
	private List<AstInterfaceDeclaration> interfaceDeclarations = new ArrayList<>();
	private List<AstMemberValueDeclaration> valueDeclarations = new ArrayList<>();
	
	private LocalSymbolTable symbolTable; 

	private PackageDeclaration packageDeclaration = null;

	public AstPackageDeclaration(Reporter reporter, QName qname, 
			List<FunctionDefinition> functionDeclarations, List<AstClassDeclaration> classDeclarations, 
			List<AstInterfaceDeclaration> interfaceDeclarations, List<AstMemberValueDeclaration> valueDeclarations) {
		super();
		this.reporter = reporter;
		this.qname = qname;
		this.symbolTable = new LocalSymbolTable(reporter);
		
		functionDeclarations.forEach (fct -> {this.functionDeclarations.add(fct);	this.visitables.add(fct);});
		classDeclarations.forEach	 (cd  -> {this.classDeclarations.add(cd);		this.visitables.add(cd);});
		interfaceDeclarations.forEach(id  -> {this.interfaceDeclarations.add(id);	this.visitables.add(id);});
		valueDeclarations.forEach    (vd  -> {this.valueDeclarations.add(vd);		this.visitables.add(vd);});
	}

	@Override
	public void verify(int featureFlags) {// TODO

		verifyList(functionDeclarations, featureFlags);
		verifyList(classDeclarations, featureFlags);
		verifyList(interfaceDeclarations, featureFlags);
		verifyList(valueDeclarations, featureFlags);
		
		//private LocalSymbolTable symbolTable; 

		verifyCachedObjectNotNull(packageDeclaration, "AstPackageDeclaration.packageDeclaration (API) ", featureFlags);
	}

	public String getQnameString() {
		return qname.dotSeparated();
	}

	public QName getQname() {
		return qname;
	}


	public List<FunctionDefinition> getFunctionDeclarations() {
		return functionDeclarations;
	}

	@Override
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startPackageDeclaration(this);
		for(Visitable v: this.visitables) {
			v.visit(visitor);
		}
		visitor.endPackageDeclaration(this);
	}
	
	public boolean isUnnamed() {
		return this.qname.isEmpty();
	}

	@Override
	public String toString() {
		return "\"" + getQnameString() + "\"";
	}
	
	
	public PackageDeclaration getPackageDeclaration() {
		if(packageDeclaration == null) {
			List<ClassDeclaration> apiClassDeclarations = classDeclarations.stream()
					.map(cd -> cd.getClassDeclaration( /*qname*/ ))
					.collect(Collectors.toList());
			List<InterfaceDeclaration> apiInterfaceDeclarations = interfaceDeclarations.stream()
					.map(cd -> cd.getInterfaceDeclaration())
					.collect(Collectors.toList());

			List<AbstractFunction> apiFunctions = new ArrayList<>();
			for(FunctionDefinition fdBuilder: functionDeclarations) {
				for(Partial partial: fdBuilder.getPartials()) {
					AbstractFunction apiFunc = partial.toAPI();
					apiFunctions.add(apiFunc);
				}
			}
			
			packageDeclaration = new PackageDeclarationImpl(qname, apiClassDeclarations, apiInterfaceDeclarations, apiFunctions);

		}
		return packageDeclaration;
	}

	public List<AstClassDeclaration> getClassDeclarations() {
		return classDeclarations;
	}
	
	public List<AstInterfaceDeclaration> getInterfaceDeclarations() {
		return interfaceDeclarations;
	}
}
