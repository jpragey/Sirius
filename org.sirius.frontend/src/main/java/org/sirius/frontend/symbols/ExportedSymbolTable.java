package org.sirius.frontend.symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.symbols.ExportedSymbol.ExportedClass;
import org.sirius.frontend.symbols.ExportedSymbol.ExportedFunction;

public class ExportedSymbolTable {

	private Map<QName, ExportedSymbol> map = new HashMap<>();
	private Map<QName, ExportedSymbol.ExportedClass> classMap = new HashMap<>();
	private Map<QName, ExportedSymbol.ExportedFunction> functionMap = new HashMap<>();
		
	public void addClass(AstClassDeclaration classDeclaration) {
		QName qname = classDeclaration.getQName();
		ExportedSymbol.ExportedClass exportedClass = new ExportedSymbol.ExportedClass(qname);
		map.put(qname, exportedClass);
		classMap.put(qname, exportedClass);
	}

	public void addFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		QName qname = functionDeclaration.getqName();
		ExportedSymbol.ExportedFunction exportedClass = new ExportedSymbol.ExportedFunction(qname);
		map.put(qname, exportedClass);
		functionMap.put(qname, exportedClass);
	}
	// TODO: remove, replace by #{addFunctionDeclaration}
	public void addFunctionDefinition(FunctionDefinition functionDeclaration) {
		QName qname = functionDeclaration.getqName();
		ExportedSymbol.ExportedFunction exportedClass = new ExportedSymbol.ExportedFunction(qname);
		map.put(qname, exportedClass);
		functionMap.put(qname, exportedClass);
	}
	
	public Optional<ExportedSymbol> get(QName key) {
		ExportedSymbol s = map.get(key);
		return Optional.ofNullable(s);
	}
	public Optional<ExportedSymbol.ExportedClass> getExportedClass(QName key) {
		ExportedClass s = classMap.get(key);
		return Optional.ofNullable(s);
	}
	public Optional<ExportedSymbol.ExportedFunction> getExportedFunction(QName key) {
		ExportedFunction f = functionMap.get(key);
		return Optional.ofNullable(f);
	}
	
}
