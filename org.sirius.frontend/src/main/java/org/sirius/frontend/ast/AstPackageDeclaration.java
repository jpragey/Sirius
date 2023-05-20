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
import org.sirius.frontend.symbols.Scope;

/** Package declaration, eg as written in package descriptor.
 * It may also be an anonymous package declaration (without package descriptor) 
 * 
 * @author jpragey
 *
 */
public class AstPackageDeclaration implements Scoped, Visitable, Verifiable {

	private Optional<QName> qname = Optional.empty();
	
	private Reporter reporter;

	private List<Visitable> visitables = new ArrayList<>();

	private List<FunctionDefinition> functionDeclarations = new ArrayList<>();
	private List<AstClassDeclaration> classDeclarations = new ArrayList<>();
	private Map<QName, AstClassDeclaration> classDeclarationByQname = new HashMap<>();
	
	private List<AstMemberValueDeclaration> valueDeclarations = new ArrayList<>();
	
	private Scope scope = null;

	private PackageDeclaration packageDeclaration = null;

	public AstPackageDeclaration(Reporter reporter, 
			Optional<QName> qname, 
			List<FunctionDefinition> functionDeclarations, 
			List<AstClassDeclaration> classDeclarations, 
			List<AstMemberValueDeclaration> valueDeclarations) {
		super();
		this.reporter = reporter;
		this.qname = qname;
		
		functionDeclarations.forEach (fct -> {
			this.functionDeclarations.add(fct);	
			this.visitables.add(fct);
			});
		classDeclarations.forEach	 (cd  -> {
			cd.setPackageQName(qname);
			this.classDeclarations.add(cd);		
			classDeclarationByQname.put(cd.getQName(), cd); 
			this.visitables.add(cd);
			});
		valueDeclarations.forEach    (vd  -> {this.valueDeclarations.add(vd);		this.visitables.add(vd);});
	}

	@Override
	public void verify(int featureFlags) {// TODO

		verifyList(functionDeclarations, featureFlags);
		verifyList(classDeclarations, featureFlags);
		verifyList(valueDeclarations, featureFlags);

		verifyCachedObjectNotNull(packageDeclaration, "AstPackageDeclaration.packageDeclaration (API) ", featureFlags);
	}

	/** Get a String representing the package qname, the elements being separated by dots ('.'), or an empty string if ther's no qname.
	 * 
	 * @return
	 */
	public String getQnameString() {
		return qname.map(QName::dotSeparated).orElse("");
	}

	public Optional<QName> getQname() {
		return qname;
	}


	public List<FunctionDefinition> getFunctionDeclarations() {
		return functionDeclarations;
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

			List<AbstractFunction> apiFunctions = new ArrayList<>();
			for(FunctionDefinition fdBuilder: functionDeclarations) {
				for(Partial partial: fdBuilder.getPartials()) {
					AbstractFunction apiFunc = partial.toAPI();
					apiFunctions.add(apiFunc);
				}
			}
			
			packageDeclaration = new PackageDeclarationImpl(qname, apiClassDeclarations, apiFunctions);

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
	
	
	public boolean isEmpty() {
		boolean empty = 
				functionDeclarations.isEmpty() &&
				classDeclarations.isEmpty() &&
				valueDeclarations.isEmpty();
		return empty;
	}

	@Override
	public Scope getScope() {
		assert(this.scope != null);
		return this.scope;
	}

	@Override
	public void setScope2(Scope scope) {
		assert(this.scope == null);
		assert(scope != null);
		this.scope = scope;
	}

}
