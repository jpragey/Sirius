package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.TopLevelValue;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.LocalSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

/** Package declaration, eg as written in package descriptor.
 * It may also be an anonymous package declaration (without package descriptor) 
 * 
 * @author jpragey
 *
 */
public class AstPackageDeclaration implements Scoped, Visitable {

	private QName qname = new QName();
	private String qnameString = null;
	
	private Reporter reporter;

	private List<Visitable> visitables = new ArrayList<>();

	private List<PartialList> functionDeclarations = new ArrayList<>();
	private List<AstClassDeclaration> classDeclarations = new ArrayList<>();
	private List<AstInterfaceDeclaration> interfaceDeclarations = new ArrayList<>();
	private List<AstMemberValueDeclaration> valueDeclarations = new ArrayList<>();
	
	private LocalSymbolTable symbolTable; 

	// Set after parsing
//	private Optional<AstModuleDeclaration> containingModule = Optional.empty();
	
	public AstPackageDeclaration(Reporter reporter, QName qname, 
			List<PartialList> functionDeclarations, List<AstClassDeclaration> classDeclarations, 
			List<AstInterfaceDeclaration> interfaceDeclarations, List<AstMemberValueDeclaration> valueDeclarations) {
		super();
		this.reporter = reporter;
		this.qname = qname;
		this.symbolTable = new LocalSymbolTable(reporter);
		
		functionDeclarations.forEach(fct -> addFunctionDeclaration(fct));
		classDeclarations.forEach(cd -> addClassDeclaration(cd));
		interfaceDeclarations.forEach(id -> addInterfaceDeclaration(id));
		valueDeclarations.forEach(mvd -> addValueDeclaration(mvd));
	}
	
	public AstPackageDeclaration(Reporter reporter, QName qname) {
		this(reporter, qname, 
				new ArrayList<PartialList>(), new ArrayList<AstClassDeclaration> (), 
				new ArrayList<AstInterfaceDeclaration> (), new ArrayList<AstMemberValueDeclaration> ()) ;
	}

	public AstPackageDeclaration(Reporter reporter) {
		this(reporter, new QName());
	}

	public String getQnameString() {
		if(qnameString == null) {
			qnameString = qname.dotSeparated();
		}
		return qnameString;
	}


	public QName getQname() {
		return qname;
	}

	public void addFunctionDeclaration(PartialList declaration) {
		assert(declaration != null);
		this.functionDeclarations.add(declaration);
		this.visitables.add(declaration);
	}

	public void addClassDeclaration(AstClassDeclaration declaration) {
		assert(declaration != null);
		this.classDeclarations.add(declaration);
		this.visitables.add(declaration);
	}
	public void addInterfaceDeclaration(AstInterfaceDeclaration declaration) {
		assert(declaration != null);
		this.interfaceDeclarations.add(declaration);
		this.visitables.add(declaration);	// TODO: ???
	}
	
	
	public void addValueDeclaration(AstMemberValueDeclaration declaration) {
		assert(declaration != null);
		this.valueDeclarations.add(declaration);
		this.visitables.add(declaration);
	}

	public List<PartialList> getFunctionDeclarations() {
		return functionDeclarations;
	}

//	public void setContainingModule(AstModuleDeclaration declaration) {
////		this.containingModule = Optional.of(declaration);
//	}

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
	
	private PackageDeclaration packageDeclaration = null;
	
	private class PackageDeclarationImpl implements PackageDeclaration {

		@Override
		public List<ClassDeclaration> getClasses() {
			return classDeclarations.stream()
					.map(cd -> cd.getClassDeclaration( /*qname*/ ))
					.collect(Collectors.toList());
		}

		@Override
		public List<InterfaceDeclaration> getInterfaces() {
			return interfaceDeclarations.stream()
					.map(cd -> cd.getInterfaceDeclaration())
					.collect(Collectors.toList());
		}

		@Override
		public List<TopLevelValue> getValues() {
			return valueDeclarations.stream()
					.map(AstMemberValueDeclaration::getTopLevelValue)
					.filter(v -> v.isPresent())
					.map(v -> v.get())
					.collect(Collectors.toList());
		}

		@Override
		public List<AbstractFunction> getFunctions() {
			List<AbstractFunction> funcs = new ArrayList<>();
			
			for(PartialList fdBuilder: functionDeclarations) {
				for(Partial partial: fdBuilder.getPartials()) {
					AbstractFunction apiFunc = partial.toAPI();
					funcs.add(apiFunc);
				}
			}
			return funcs;
		}

		@Override
		public QName getQName() {
			return qname;
		}
		@Override
		public String toString() {
			return "Pack: \"" + qname + "\"";
		}

	};
		
	
	public PackageDeclaration getPackageDeclaration() {
		if(packageDeclaration == null) {
			packageDeclaration = new PackageDeclarationImpl();
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
