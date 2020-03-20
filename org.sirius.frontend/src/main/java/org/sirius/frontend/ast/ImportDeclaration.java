package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;

/** Import, at start of compilation unit
 * 
 * @author jpragey
 *
 */
public class ImportDeclaration implements Visitable {
	
	private Reporter reporter;
	private QualifiedName pack;
	
	private List<ImportDeclarationElement> elements = new ArrayList<>();

	public ImportDeclaration(Reporter reporter, QualifiedName pack) {
		super();
		this.reporter = reporter;
		this.pack = pack;
	}
	
	public void add(ImportDeclarationElement element) {
		this.elements.add(element);
	}

	
	public QualifiedName getPack() {
		return pack;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startImportDeclaration(this);
		visitor.endImportDeclaration(this);
	}

	public List<ImportDeclarationElement> getElements() {
		return elements;
	}
	@Override
	public String toString() {
		return 
				pack.toString() + 
				"{" + 
				String.join(", ", elements.stream().map(elem -> elem.toString()).collect(Collectors.toList())) +  
				"}";
	}
}
