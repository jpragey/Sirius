package org.sirius.backend.jvm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
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
	
	private int varIndex = 1;
	private List<JvmScope> subScopes; 

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

	public JvmScope(DescriptorFactory descriptorFactory) {
		super();
		startLabel = new Label();
		this.descriptorFactory = descriptorFactory;
		this.subScopes = new ArrayList<JvmScope>(); 
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
//	private final static int locvarIndex = 1;

	
	public Optional<LocalVarHolder> getVarByName(String varName) {
		LocalVarHolder h = this.varByName.get(varName);
		if(h == null)
			return Optional.empty();
		return Optional.of(h);
	}

	public void writeLocalVariableStatements(ClassWriter classWriter, MethodVisitor mv, int startIndex) {

		for(LocalVarHolder h: this.locVarsStmts) {
//			LocalVariableStatement statement = h.statement;
//			String name = statement.getName().getText();
			String name = h.localVarName;

			//		statement.getType();
//			String descriptor = descriptorFactory.fieldDescriptor(statement.getType());		// TODO: field ???      var descriptor
			String descriptor = descriptorFactory.fieldDescriptor(h.localVarType);		// TODO: field ???      var descriptor
			String signature = null; // the type signature of this local variable. May be {@literal null} if the local variable type does not use generic types.
			//		Label start;
			//		Label end;
			int index=startIndex++;
			mv.visitLocalVariable(name, descriptor, signature, startLabel, endLabel, index);
		}
		
		for(JvmScope subScope: subScopes) {
			subScope.writeLocalVariableStatements(classWriter, mv, startIndex);
		}
		
	}
//	/** Write init code (for start of functions)
//	 * 
//	 * @param classWriter
//	 * @param mv
//	 */
//	public void writeLocalVariableInit(ClassWriter classWriter, MethodVisitor mv //LocalVariableStatement statement, 
//			/*JvmScope scope /*Label start, Label end*/) {
//
//		for(LocalVarHolder h: this.locVarsStmts) {
//			LocalVariableStatement statement = h.statement;
//			Optional<Expression> optInitExp = statement.getInitialValue();
//			if(optInitExp.isPresent()) {
//				Expression expr = optInitExp.get();
//				writeExpressionBytecode(mv, expr);
//				
//				mv.visitVarInsn(Opcodes.ASTORE, locvarIndex);
//
//			}
//		}
//	}

	public List<LocalVarHolder> getLocVarsStmts() {
		return locVarsStmts;
	}
	
	
}
