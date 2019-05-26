package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.core.PhysicalPath;
import org.sirius.frontend.symbols.LocalSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

/** Package declaration, eg as written in package descriptor.
 * It may also be an anonymous package declaration (without package descriptor) 
 * 
 * @author jpragey
 *
 */
public class PackageDeclaration implements Scoped, Visitable {

	private QName qname = new QName();
	private String qnameString = null;
	
	private Reporter reporter;

	private List<Visitable> visitables = new ArrayList<>();

	private List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
	private List<ClassDeclaration> classDeclarations = new ArrayList<>();

	private LocalSymbolTable symbolTable; 

	
	public PackageDeclaration(Reporter reporter,List<AstToken> qname) {
		super();
		this.reporter = reporter;
		this.qname.addAll(qname);
		this.symbolTable = new LocalSymbolTable(reporter); 
	}

	public PackageDeclaration(Reporter reporter) {
		this(reporter, Collections.emptyList());
	}

	public void addNamePart(AstToken partName) {
		qnameString = null;
		qname.add(partName);
	}

	public void addNamePart(Token partName) {
		addNamePart(new AstToken(partName));
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
	public List<String> getPathElements() { // TODO: cache (?)
		return qname.getStringElements();
	}

	public void addFunctionDeclaration(FunctionDeclaration declaration) {
		this.functionDeclarations.add(declaration);
		this.visitables.add(declaration);
	}

	public void addClassDeclaration(ClassDeclaration declaration) {
		this.classDeclarations.add(declaration);
		this.visitables.add(declaration);
	}

	public List<FunctionDeclaration> getFunctionDeclarations() {
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
}
