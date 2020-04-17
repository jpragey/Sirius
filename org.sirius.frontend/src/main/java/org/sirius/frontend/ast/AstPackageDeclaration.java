package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.TopLevelValue;
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

	private List<AstFunctionDeclaration> functionDeclarations = new ArrayList<>();
	private List<AstClassDeclaration> classDeclarations = new ArrayList<>();
	private List<AstMemberValueDeclaration> valueDeclarations = new ArrayList<>();
	
	private LocalSymbolTable symbolTable; 

	// Set after parsing
	private Optional<AstModuleDeclaration> containingModule = Optional.empty();
	
	public AstPackageDeclaration(Reporter reporter, QName qname) {
		super();
		this.reporter = reporter;
		this.qname = qname;
		this.symbolTable = new LocalSymbolTable(reporter); 
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

	public void addFunctionDeclaration(AstFunctionDeclaration declaration) {
		this.functionDeclarations.add(declaration);
		this.visitables.add(declaration);
	}

	public void addClassDeclaration(AstClassDeclaration declaration) {
		this.classDeclarations.add(declaration);
		this.visitables.add(declaration);
	}

	public void addValueDeclaration(AstMemberValueDeclaration declaration) {
		this.valueDeclarations.add(declaration);
		this.visitables.add(declaration);
	}

	public List<AstFunctionDeclaration> getFunctionDeclarations() {
		return functionDeclarations;
	}

	public void setContainingModule(AstModuleDeclaration declaration) {
		this.containingModule = Optional.of(declaration);
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
	
	/** Set package refs for children (classes, values, functions */
	public void updateContentContainerRefs() {
		for(AstFunctionDeclaration fd: functionDeclarations) {
			fd.setContainerQName(this.qname);
		}
//		private List<AstClassDeclaration> classDeclarations = new ArrayList<>();
//		private List<AstValueDeclaration> valueDeclarations = new ArrayList<>();
	}
	
	private PackageDeclaration packageDeclaration = null;
	
	private class PackageDeclarationImpl implements PackageDeclaration {

		@Override
		public List<ClassDeclaration> getClasses() {
			return classDeclarations.stream()
					.filter(cd -> !cd.isInterfaceType())
					.map(cd -> cd.getClassDeclaration( /*qname*/ ))
					.collect(Collectors.toList());
		}

		@Override
		public List<InterfaceDeclaration> getInterfaces() {
			return classDeclarations.stream()
					.filter(cd -> cd.isInterfaceType())
					.map(cd -> cd.getInterfaceDeclaration(/*qname*/))
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
		public List<TopLevelFunction> getFunctions() {
			return functionDeclarations.stream()
					.map(fd -> fd.getTopLevelFunction())
					.filter(fd -> fd.isPresent())
					.map(fd -> fd.get())
					.collect(Collectors.toList());
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
	

}
