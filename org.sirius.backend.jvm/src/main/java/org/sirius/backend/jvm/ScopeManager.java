package org.sirius.backend.jvm;

import java.util.Optional;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class ScopeManager {
	private JvmScope currentScope ;
	private DescriptorFactory descriptorFactory;
	private JvmScope rootScope;
	
	public ScopeManager(DescriptorFactory descriptorFactory, JvmScope rootScope) {
		this.descriptorFactory = descriptorFactory;
		this.rootScope = rootScope;
		this.currentScope = this.rootScope;
	}
	public ScopeManager(DescriptorFactory descriptorFactory) {
		this(descriptorFactory, new JvmScope(descriptorFactory, Optional.empty(), "<root>"));
//		this.descriptorFactory = descriptorFactory;
//		this.rootScope = new JvmScope(descriptorFactory, Optional.empty(), "<root>");
//		this.currentScope = this.rootScope;
	}
	
	@Override
	public String toString() {
		return currentScope.toString();
	}
	public JvmScope enterNewScope(String dbgName) {
		JvmScope current = this.currentScope;
		assert(current != null);
		JvmScope scope = new JvmScope(descriptorFactory, Optional.of(current),  dbgName);
		current.addSubScopes(scope);
		this.currentScope = scope;
		return scope;
	}
	public void leaveScope() {
		this.currentScope = this.currentScope.getParentScope().get();
	}

	public void writeLocalVariables(ClassWriter classWriter, MethodVisitor mv) {
//		rootScope.indexedScope(descriptorFactory).writeLocalVariableStatements(classWriter, mv);
		rootScope.writeLocalVariableStatements(classWriter, mv);
	}
	
}