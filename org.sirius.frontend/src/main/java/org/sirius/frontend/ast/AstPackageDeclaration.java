package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.apiimpl.PackageDeclarationImpl;
import org.sirius.frontend.symbols.SymbolTableImpl;
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
	private Map<QName, AstClassDeclaration> classDeclarationByQname = new HashMap<>();
	
	private List<AstInterfaceDeclaration> interfaceDeclarations = new ArrayList<>();
	private List<AstMemberValueDeclaration> valueDeclarations = new ArrayList<>();
	
	private SymbolTable symbolTable; 

	private PackageDeclaration packageDeclaration = null;

	public AstPackageDeclaration(Reporter reporter, QName qname, 
			List<FunctionDefinition> functionDeclarations, List<AstClassDeclaration> classDeclarations, 
			List<AstInterfaceDeclaration> interfaceDeclarations, List<AstMemberValueDeclaration> valueDeclarations) {
		super();
		this.reporter = reporter;
		this.qname = qname;
//		this.symbolTable = new LocalSymbolTable(reporter);
		this.symbolTable = new SymbolTableImpl("<TODO>" /*TODO*/);
		
		functionDeclarations.forEach (fct -> {this.functionDeclarations.add(fct);	this.visitables.add(fct);});
		classDeclarations.forEach	 (cd  -> {
			cd.setPackageQName(qname);
			this.classDeclarations.add(cd);		
			classDeclarationByQname.put(cd.getQName(), cd); 
			this.visitables.add(cd);
			});
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
			List<ClassType> apiClassDeclarations = classDeclarations.stream()
					.map(cd -> cd.getClassDeclaration( /*qname*/ ))
					.collect(Collectors.toList());
			List<ClassType> apiInterfaceDeclarations = interfaceDeclarations.stream()
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
	public Optional<AstClassDeclaration> getClassDeclaration(QName classQNname) {
		AstClassDeclaration cd = classDeclarationByQname.get(classQNname);
		return Optional.ofNullable(cd);
	}
	
	public List<AstInterfaceDeclaration> getInterfaceDeclarations() {
		return interfaceDeclarations;
	}
	
	public boolean isEmpty() {
		boolean empty = 
				functionDeclarations.isEmpty() &&
				classDeclarations.isEmpty() &&
				interfaceDeclarations.isEmpty() &&
				valueDeclarations.isEmpty();
		return empty;
	}
	
}
