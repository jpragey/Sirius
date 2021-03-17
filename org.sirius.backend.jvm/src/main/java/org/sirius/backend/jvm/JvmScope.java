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

	public static class IndexedVariable {
		private Type localVarType;
		private String localVarName;
		private Optional<Expression> initExp;
		private int index;
		public IndexedVariable(LocalVarHolder v, int index) {
			this.localVarType = v.localVarType;
			this.localVarName = v.localVarName;
			this.initExp = v.initExp;
			this.index = index;
		}
		
	}
	public static class IndexedScope {
		private DescriptorFactory descriptorFactory;
		private int startIndex;
		private JvmScope jvmScope;
		private List<IndexedScope> subScopes;
		private List<IndexedVariable> indexedVariables;
		public IndexedScope(DescriptorFactory descriptorFactory, JvmScope jvmScope, int startIndex) {
			this.descriptorFactory = descriptorFactory;
			this.jvmScope = jvmScope;
			this.startIndex = startIndex;

			int currentIndex = startIndex;
			// -- indexed variables
			this.indexedVariables = new ArrayList<IndexedVariable>(jvmScope.locVarsStmts.size());
			for(LocalVarHolder v : jvmScope.locVarsStmts) {
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
//			System.out.println(" << scope: entering " + this.jvmScope.dbgName + " by index " + this.startIndex);

			for(IndexedVariable h: this.indexedVariables) {
				String name = h.localVarName;

//				System.out.println(" -- Write scope var " + name);

				String descriptor = descriptorFactory.fieldDescriptor(h.localVarType);		// TODO: field ???      var descriptor
				String signature = null; // the type signature of this local variable. May be {@literal null} if the local variable type does not use generic types.
				
				int index=h.index;
				assert(this.jvmScope.startLabel != null);
				if(this.jvmScope.endLabel == null) {
//					System.out.println();
				}
				
				assert(this.jvmScope.endLabel != null);
				
				mv.visitLocalVariable(name, descriptor, signature, this.jvmScope.startLabel, this.jvmScope.endLabel, index);
			}
			
			for(IndexedScope subScope: subScopes) {
				subScope.writeLocalVariableStatements(classWriter, mv);
			}
			
//			System.out.println(" >> leaving scope " + this.jvmScope.dbgName + " index " + this.startIndex);
		}
	}
	
	
	public class LocalVarHolder {
		private Type localVarType;
		private String localVarName;
		private Optional<Expression> initExp;
		
		private int index;

		public LocalVarHolder(Type localVarType, String localVarName, Optional<Expression> initExp) {
			this.index = varIndex++;
			this.localVarType = localVarType;
			this.localVarName = localVarName;
			this.initExp = initExp;
		}
		public LocalVarHolder(LocalVariableStatement statement) {
			this(statement.getType(), statement.getName().getText(), statement.getInitialValue());
		}
		
		public LocalVarHolder(FunctionFormalArgument statement) {
			this(statement.getType(), statement.getQName().getLast(), Optional.empty() /* TODO */);
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
		
	}
	
	private List<LocalVarHolder> locVarsStmts = new ArrayList<>();
	private HashMap<String, LocalVarHolder> varByName = new HashMap<>();
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

	public LocalVarHolder addLocalVariable(LocalVariableStatement st) {
		LocalVarHolder h = new LocalVarHolder(st);
		locVarsStmts.add(h);
		varByName.put(st.getName().getText(), h);
		return h;
	}
	public LocalVarHolder addFunctionArgument(FunctionFormalArgument st) {
		LocalVarHolder h = new LocalVarHolder(st);
		locVarsStmts.add(h);
		varByName.put(h.localVarName, h);
		return h;
	}

	
	public Optional<LocalVarHolder> getVarByName(String varName) {
		LocalVarHolder h = this.varByName.get(varName);
		if(h == null)
			return Optional.empty();
		return Optional.of(h);
	}

	@Override
	public String toString() {
		return "Scope: " + dbgName + 
				subScopes.stream().map(sc->sc.toString()).collect(Collectors.joining(", ", "[", "]")) ;
	}

	public IndexedScope indexedScope(DescriptorFactory descriptorFactory) {
		return new IndexedScope(descriptorFactory, this, 0);
	}
	public List<LocalVarHolder> getLocVarsStmts() {
		return locVarsStmts;
	}
}
