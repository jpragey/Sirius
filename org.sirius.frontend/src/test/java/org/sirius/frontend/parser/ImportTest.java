package org.sirius.frontend.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTable;

public class ImportTest {

	@Test
	public void testImportStatementParsedCorrectly() {
		ScriptSession session = Compiler.compileScript("#!\n import a.b {aa,bb=bbb,CC=CCC} package p.k; class C(){C s;}");
		
		ScriptCompilationUnit scu =  session.getCompilationUnit();
		
		assertEquals(scu.getImportDeclarations().size(), 1);
		
		ImportDeclaration id = scu.getImportDeclarations().get(0);
		assertEquals(id.getElements().size(), 3);

		assertEquals(id.getElements().get(0).getImportedTypeName().getText(), "aa");
		assertEquals(id.getElements().get(1).getImportedTypeName().getText(), "bbb");
		assertEquals(id.getElements().get(1).getAlias().get().getText(), "bb");
		assertEquals(id.getElements().get(2).getImportedTypeName().getText(), "CCC");
		assertEquals(id.getElements().get(2).getAlias().get().getText(), "CC");
		
		SymbolTable st = scu.getSymbolTable();
		
		System.out.println(st.lookupBySimpleName("aa"));
		
		assertTrue(st.lookupBySimpleName("aa").isPresent());
		Symbol aa = st.lookupBySimpleName("aa").get();
		assertEquals(aa.getImportDeclaration().get().getSymbolQName().dotSeparated(), "a.b.aa");
		
		assertTrue(st.lookupBySimpleName("bb").isPresent());
		Symbol bb = st.lookupBySimpleName("bb").get();
		assertEquals(bb.getImportDeclaration().get().getSymbolQName().dotSeparated(), "a.b.bbb");
		
		assertTrue(st.lookupBySimpleName("CC").isPresent());
		Symbol cc = st.lookupBySimpleName("CC").get();
		assertEquals(cc.getImportDeclaration().get().getSymbolQName().dotSeparated(), "a.b.CCC");
		
		
		
	}
	
}
