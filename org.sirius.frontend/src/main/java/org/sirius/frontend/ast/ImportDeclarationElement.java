package org.sirius.frontend.ast;

import java.util.Optional;

import org.antlr.v4.runtime.Token;

/** Type (or other elements, TBD) inside an 'import' statement
 * 
 * @author jpragey
 *
 */
public class ImportDeclarationElement {

	private AstToken importedTypeName;
	
	private Optional<AstToken> alias;

	public ImportDeclarationElement(Token importedTypeName, Optional<Token> alias) {
		super();
		this.importedTypeName = new AstToken(importedTypeName);
		this.alias = alias.isPresent() ?  Optional.of(new AstToken(alias.get())) : Optional.empty();
	}

	public AstToken getImportedTypeName() {
		return importedTypeName;
	}

	public Optional<AstToken> getAlias() {
		return alias;
	}
	
	
	@Override
	public String toString() {
		if(alias.isPresent())
			return alias.get().getText() + "->" + importedTypeName.getText();
		else
			return importedTypeName.getText();
	}
	
}
