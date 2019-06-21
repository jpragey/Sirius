package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.TopLevelValue;
import org.sirius.frontend.core.PhysicalPath;
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
	private List<AstValueDeclaration> valueDeclarations = new ArrayList<>();
	
	private LocalSymbolTable symbolTable; 

	
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
//	public List<String> getPathElements() { // TODO: cache (?)
//		return qname.getElements();
//	}

	public void addFunctionDeclaration(AstFunctionDeclaration declaration) {
		this.functionDeclarations.add(declaration);
		this.visitables.add(declaration);
	}

	public void addClassDeclaration(AstClassDeclaration declaration) {
		this.classDeclarations.add(declaration);
		this.visitables.add(declaration);
	}

	public void addValueDeclaration(AstValueDeclaration declaration) {
		this.valueDeclarations.add(declaration);
		this.visitables.add(declaration);
	}

	public List<AstFunctionDeclaration> getFunctionDeclarations() {
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
		return getQnameString();
	}
	
	public PackageDeclaration getPackageDeclaration() {
		return new PackageDeclaration() {

			@Override
			public List<ClassDeclaration> getClasses() {
				return classDeclarations.stream()
						.filter(cd -> !cd.isInterfaceType())
						.map(cd -> cd.getClassDeclaration())
						.collect(Collectors.toList());
			}

			@Override
			public List<InterfaceDeclaration> getInterfaces() {
				return classDeclarations.stream()
						.filter(cd -> !cd.isInterfaceType())
						.map(cd -> cd.getInterfaceDeclaration())
						.collect(Collectors.toList());
			}

			@Override
			public List<TopLevelValue> getValues() {
				return valueDeclarations.stream()
						.map(AstValueDeclaration::getTopLevelValue)
						.filter(v -> v.isPresent())
						.map(v -> v.get())
						.collect(Collectors.toList());
			}

			@Override
			public List<TopLevelFunction> getFunctions() {
				return functionDeclarations.stream()
						.map(AstFunctionDeclaration::getTopLevelFunction)
						.filter(fd -> fd.isPresent())
						.map(fd -> fd.get())
						.collect(Collectors.toList());
			}
			
		};
	}

}
