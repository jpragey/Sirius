package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstFunctionDeclarationBuilder.FunctionImpl;
import org.sirius.frontend.symbols.DefaultSymbolTable;

import com.google.common.collect.ImmutableList;

//	public static class Capture {
//		private AstType type;
//		private AstToken name;
//		public Capture(AstType type, AstToken name) {
//			super();
//			this.type = type;
//			this.name = name;
//		}
//		@Override
//		public String toString() {
//			return type.toString() + " " + name.getText();
//		}
//		public AstType getType() {
//			return type;
//		}
//		public AstToken getName() {
//			return name;
//		}
//	}
	public class Partial implements Visitable{
		private AstToken name;
////		private ImmutableList<Capture> captures;
		private ImmutableList<AstFunctionParameter> args;
//		private AstFunctionDeclarationBuilder function;

		private boolean concrete;
		private boolean member = false;
		private QName qName;
		
		
		private AstType returnType = new AstVoidType();
		private List<AstStatement> statements/* = new ArrayList<>()*/; 
		private DefaultSymbolTable symbolTable = null;
		
		public Partial(
				AstToken name,
////				List<Capture> captures, 
				List<AstFunctionParameter> args, 
	
				//AstFunctionDeclarationBuilder function,
				boolean concrete,
				boolean member,
				QName qName,
				
				AstType returnType,
				List<AstStatement> statements/*,
				DefaultSymbolTable parentSymbolTable*/) {
			super();
			this.name = name;
////			this.captures = ImmutableList.copyOf(captures);
			this.args = ImmutableList.copyOf(args);
//			this.function = function;
			
			
			this.concrete = concrete;
			this.member = member;
			this.qName = qName;

			
			
			
			this.returnType = returnType;
			this.statements = statements;
//			this.symbolTable = new DefaultSymbolTable(parentSymbolTable);
			
//			for(AstFunctionParameter arg: args) {
//				this.symbolTable.addFunctionArgument(arg);
//			}
		}
		
		public void assignSymbolTable(DefaultSymbolTable symbolTable) {
			this.symbolTable = symbolTable;
			for(AstFunctionParameter arg: args) {
				this.symbolTable.addFunctionArgument(arg);
			}
		}
		
		
		private Type resolveReturnType() {
			Type resolved = returnType.getApiType();
			return resolved;
		}

		public AstToken getName() {
			return name;
		}

//		public ImmutableList<Capture> getCaptures() {
//			return captures;
//		}

		public ImmutableList<AstFunctionParameter> getArgs() {
			return args;
		}

//		public AstFunctionDeclarationBuilder getFunction() {
//			return function;
//		}

		public DefaultSymbolTable getSymbolTable() {
			assert(symbolTable != null);
			return symbolTable;
		}
		@Override
		public String toString() {
			String text =
					name.getText() + 
////					"_" + captures.size() + 
					"_" + args.size() + "_" +
////					captures.stream().map(capt -> capt.toString()).collect(Collectors.joining(", ", "[", "]")) +
					args.stream().map(capt -> capt.toString()).collect(Collectors.joining(", ", "(", ")"))
					;
			return text;
		}

		@Override
		public void visit(AstVisitor visitor) {
			visitor.startPartial(this);
			args.stream().forEach(formalArg -> formalArg.visit(visitor));
			statements.stream().forEach(st -> st.visit(visitor));
			returnType.visit(visitor);
			visitor.endPartial(this);
		}
		
		private FunctionImpl functionImpl = null;
		
		public FunctionImpl toAPI() {
			if(functionImpl == null) {
				List<Statement> apiStatements = new ArrayList<>(statements.size());
				for(AstStatement stmt: statements) {
					Statement st = stmt.toAPI();
					apiStatements.add(st);
				}
				
				functionImpl = new FunctionImpl(qName, args, 
						resolveReturnType(),
						concrete ? Optional.of(apiStatements) : Optional.empty(),
						member);
				assert(functionImpl.getArguments().size() == args.size());
			}
			
			assert(functionImpl.getArguments().size() == args.size());
			
			return functionImpl;
		}
		public AstType getReturnType() {
			return returnType;
		}
	}