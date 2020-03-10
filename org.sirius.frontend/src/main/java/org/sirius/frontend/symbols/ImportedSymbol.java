package org.sirius.frontend.symbols;

import org.sirius.common.core.QName;
import org.sirius.common.core.Token;
import org.sirius.frontend.ast.ImportDeclarationElement;

/**
 * 
 * @author jpragey
 *
 */
public class ImportedSymbol {

	Token simpleName;
	QName symbolQName;
	ImportDeclarationElement e;	// TODO: optional?
	
	
	public ImportedSymbol(Token simpleName, QName symbolQName, ImportDeclarationElement e) {
		super();
		this.simpleName = simpleName;
		this.symbolQName = symbolQName;
		this.e = e;
	}
	public Token getSimpleName() {
		return simpleName;
	}
	public QName getSymbolQName() {
		return symbolQName;
	}
	public ImportDeclarationElement getImportDeclarationElement() {
		return e;
	}
	
	
}
