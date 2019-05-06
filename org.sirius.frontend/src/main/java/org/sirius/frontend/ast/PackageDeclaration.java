package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.symbols.LocalSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

public class PackageDeclaration implements Scoped, Visitable {

	private List<AstToken> qname = new ArrayList<>();
	private String qnameString = null;
	
	//public static PackageDeclaration root = new PackageDeclaration();
	
	private Reporter reporter;
	
	private LocalSymbolTable symbolTable; 

	
	public PackageDeclaration(Reporter reporter) {
		super();
		this.reporter = reporter;
		this.symbolTable = new LocalSymbolTable(reporter); 
	}

	public void addNamePart(AstToken partName) {
		qname.add(partName);
	}

	public void addNamePart(Token partName) {
		addNamePart(new AstToken(partName));
	}


	public String getQnameString() {
		if(qnameString == null) {
			StringBuilder sb = new StringBuilder();
			for(AstToken tk: qname) {
				if(sb.length() > 0)
					sb.append('.');
				sb.append(tk.getText());
			}
			qnameString = sb.toString();
		}
		return qnameString;
	}


	public List<AstToken> getQname() {
		return qname;
	}
	
	@Override
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startPackageDeclaration(this);
		visitor.endPackageDeclaration(this);
	}

}
