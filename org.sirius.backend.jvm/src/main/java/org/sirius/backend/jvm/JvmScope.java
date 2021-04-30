package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.Type;

/** Jvm variable scope (functions, block)
 * 
 * 
 * @author jpragey
 *
 */
public class JvmScope {

	private Label startLabel;
	private Label endLabel;
	private DescriptorFactory descriptorFactory;
	
	private int varIndex = 0;
	private List<JvmScope> subScopes;
	private Optional<JvmScope> parentScope;

	private static class IndexedVariable {
		private Type localVarType;
		private String localVarName;
		private Optional<Expression> initExp;
		private int index;
		public IndexedVariable(JvmLocalVariable v, int index) {
			this.localVarType = v.localVarType;
			this.localVarName = v.localVarName;
			this.initExp = v.initExp;
			this.index = index;
		}
		
	}
	private static class IndexedScope {
		private DescriptorFactory descriptorFactory;
		private JvmScope jvmScope;
		private List<IndexedScope> subScopes;
		private List<IndexedVariable> indexedVariables;
		private IndexedScope(DescriptorFactory descriptorFactory, JvmScope jvmScope, int startIndex) {
			this.descriptorFactory = descriptorFactory;
			this.jvmScope = jvmScope;

			int currentIndex = startIndex;
			// -- indexed variables
			this.indexedVariables = new ArrayList<IndexedVariable>(jvmScope.locVarsStmts.size());
			for(JvmLocalVariable v : jvmScope.locVarsStmts) {
				IndexedVariable iv = new IndexedVariable(v, currentIndex++);
				this.indexedVariables.add(iv);
			}
			// -- subscopes
			this.subScopes = new ArrayList<>(jvmScope.subScopes.size());
			for(JvmScope subJvmScope: jvmScope.subScopes) {
				IndexedScope is = new IndexedScope(descriptorFactory, subJvmScope, currentIndex /* NB : all subscopes have the same index */ );
				this.subScopes.add(is);
			}

		}
		
		public void writeLocalVariableStatements(ClassWriter classWriter, MethodVisitor mv) {

			for(IndexedVariable h: this.indexedVariables) {
				String name = h.localVarName;

				String descriptor = descriptorFactory.fieldDescriptor(h.localVarType);		// TODO: field ???      var descriptor
				String signature = null; // the type signature of this local variable. May be {@literal null} if the local variable type does not use generic types.
				
				int index=h.index;
				Label startLabel = this.jvmScope.startLabel;  
				Label endLabel = this.jvmScope.endLabel;  
				assert(startLabel != null);
				assert(endLabel != null);
				
				mv.visitLocalVariable(name, descriptor, signature, startLabel, endLabel, index);
			}
			
			for(IndexedScope subScope: subScopes) {
				subScope.writeLocalVariableStatements(classWriter, mv);
			}
		}
	}
	
	
	public static class JvmLocalVariable {
		private Type localVarType;
		private String localVarName;
		private Optional<Expression> initExp;
		
		private int index;

		private JvmLocalVariable(Type localVarType, String localVarName, Optional<Expression> initExp, int index) {
			this.index = index;
			this.localVarType = localVarType;
			this.localVarName = localVarName;
			this.initExp = initExp;
		}
		public JvmLocalVariable(LocalVariableStatement statement, int index) {
			this(statement.getType(), statement.getName().getText(), statement.getInitialValue(), index);
		}
		
		public JvmLocalVariable(FunctionFormalArgument statement, int index) {
			this(statement.getType(), statement.getQName().getLast(), Optional.empty() /* TODO */, index);
		}
		
		public int getIndex() {
			return index;
		}
		public Optional<Expression> getInitExp() {
			return initExp;
		}
		public Type getLocalVarType() {
			return localVarType;
		}
		public String getLocalVarName() {
			return localVarName;
		}
		@Override
		public String toString() {
			return "[" + index + "]" + this.localVarName;
		}
	}
	
	private List<JvmLocalVariable> locVarsStmts = new ArrayList<>();
	private HashMap<String, JvmLocalVariable> varByName = new HashMap<>();
	private String dbgName;
	
	public JvmScope(DescriptorFactory descriptorFactory, Optional<JvmScope> parentScope, String dbgName) {
		super();
		startLabel = new Label();
		this.descriptorFactory = descriptorFactory;
		this.parentScope = parentScope;
		this.subScopes = new ArrayList<JvmScope>();
		this.dbgName = dbgName;
	}
	
	
	public Optional<JvmScope> getParentScope() {
		return parentScope;
	}

	public List<JvmScope> getSubScopes() {
		return subScopes;
	}
	public void addSubScopes(JvmScope child) {
		subScopes.add(child);
	}

	public void markEnd() {
		this.endLabel = new Label();
	}

	public JvmLocalVariable addLocalVariable(LocalVariableStatement st) {
		JvmLocalVariable h = new JvmLocalVariable(st, this.varIndex++);
		locVarsStmts.add(h);
		varByName.put(st.getName().getText(), h);
		return h;
	}
	public JvmLocalVariable addFunctionArgument(FunctionFormalArgument st) {
		JvmLocalVariable h = new JvmLocalVariable(st, this.varIndex++);
		locVarsStmts.add(h);
		varByName.put(h.localVarName, h);
		return h;
	}

	
	public Optional<JvmLocalVariable> getVarByName(String varName) {
		JvmLocalVariable locVar = this.varByName.get(varName);
		return Optional.ofNullable(locVar);
	}

	@Override
	public String toString() {
		return "Scope: " + dbgName + 
				subScopes.stream().map(sc->sc.toString()).collect(Collectors.joining(", ", "[", "]")) ;
	}

	public void writeLocalVariableStatements(ClassWriter classWriter, MethodVisitor mv) {
		IndexedScope iscope = new IndexedScope(descriptorFactory, this, 0);
		iscope.writeLocalVariableStatements(classWriter, mv);
	}
	
	public List<JvmLocalVariable> getLocVarsStmts() {
		return locVarsStmts;
	}
}
