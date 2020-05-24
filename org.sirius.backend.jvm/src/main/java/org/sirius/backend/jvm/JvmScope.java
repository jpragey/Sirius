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
import org.sirius.frontend.api.LocalVariableStatement;

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
	public class LocalVarHolder {
		private LocalVariableStatement statement;
		private int index;
		public LocalVarHolder(LocalVariableStatement statement) {
			super();
			this.statement = statement;
			this.index = varIndex++;
		}
		public LocalVariableStatement getStatement() {
			return statement;
		}
		public int getIndex() {
			return index;
		}
	}
	
	private List<LocalVarHolder> locVarsStmts = new ArrayList<>();
	private HashMap<String, LocalVarHolder> varByName = new HashMap<>();

	public JvmScope(DescriptorFactory descriptorFactory) {
		super();
		startLabel = new Label();
		this.descriptorFactory = descriptorFactory;
	}
	
	public void markEnd() {
		this.endLabel = new Label();
	}

//	public Label getStartLabel() {
//		return startLabel;
//	}
//
//	public Label getEndLabel() {
//		assert(endLabel != null);
//		
//		return endLabel;
//	}
	public void addLocalVariable(LocalVariableStatement st) {
		LocalVarHolder h = new LocalVarHolder(st);
		locVarsStmts.add(h);
		varByName.put(st.getName().getText(), h);
	}
	private final static int locvarIndex = 1;

	
	public Optional<LocalVarHolder> getVarByName(String varName) {
		LocalVarHolder h = this.varByName.get(varName);
		if(h == null)
			return Optional.empty();
		return Optional.of(h);
	}

	public void writeLocalVariableStatements(ClassWriter classWriter, MethodVisitor mv //LocalVariableStatement statement, 
			/*JvmScope scope /*Label start, Label end*/) {

		for(LocalVarHolder h: this.locVarsStmts) {
			LocalVariableStatement statement = h.statement;
			String name = statement.getName().getText();

			//		statement.getType();
			String descriptor = descriptorFactory.fieldDescriptor(statement.getType());		// TODO: field ???      var descriptor
			String signature = null; // the type signature of this local variable. May be {@literal null} if the local variable type does not use generic types.
			//		Label start;
			//		Label end;
			int index=locvarIndex;
			mv.visitLocalVariable(name, descriptor, signature, startLabel, endLabel, index);
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
