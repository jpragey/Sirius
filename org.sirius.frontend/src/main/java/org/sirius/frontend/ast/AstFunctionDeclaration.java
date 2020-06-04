package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.checkerframework.checker.units.qual.m;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;

import com.google.common.collect.ImmutableList;

public class AstFunctionDeclaration implements Scoped, Visitable, AstParametric<AstFunctionDeclaration>, Named {

	private AstToken name;
	
	private ImmutableList<TypeParameter> typeParameters;
	
	private ImmutableList<AstFunctionFormalArgument> formalArguments;
	

	private List<AstStatement> statements/* = new ArrayList<>()*/; 
	
	private AstType returnType = new AstVoidType();

	private Reporter reporter;
	
	private AnnotationList annotationList;

	private DefaultSymbolTable symbolTable; 

	// True it it has a body
	private boolean concrete;
	// 
	private QName containerQName;
	
	// -- nonnull for instance method
//	private Optional<AstClassOrInterface> memberContainer = Optional.empty();
	private boolean member = false;
	
	
	private QName qName; // deduced from containerQName + name
	
	public AstFunctionDeclaration(
			Reporter reporter, 
			AnnotationList annotationList, 
			AstToken name, 
			AstType returnType,
			ImmutableList<TypeParameter> typeParameters,
			ImmutableList<AstFunctionFormalArgument> formalArguments,
			QName containerQName,
			boolean concrete,
			boolean member,
			DefaultSymbolTable symbolTable,
			List<AstStatement> statements
			) {
		super();
		this.reporter = reporter;
		this.annotationList = annotationList;
		this.name = name;
		this.returnType = returnType;
		this.typeParameters = typeParameters;
		this.formalArguments = formalArguments;
		
		this.containerQName = containerQName;
		this.qName = containerQName.child(name.getText());
		this.concrete = concrete;
		this.member = member;
		this.symbolTable = symbolTable;
		this.statements = statements;
	}

	/** Create a no-formal no-arg no-symboltable function
	 * 
	 * @param reporter
	 * @param annotationList
	 * @param name
	 * @param returnType
	 * @param containerQName
	 * @param qName
	 * @param concrete
	 * @param member
	 * @param symbolTable
	 * @param statements
	 */
	public AstFunctionDeclaration(
			Reporter reporter, 
			AnnotationList annotationList, 
			AstToken name, 
			AstType returnType,
			QName containerQName,
//			QName qName,
			boolean concrete,
			boolean member
//			,
//			ImmutableList<AstStatement> statements
			) {
		this(reporter, annotationList, name, returnType,
				ImmutableList.of(),	//<TypeParameter> typeParameters,
				ImmutableList.of(),	//<AstFunctionFormalArgument> formalArguments,
				containerQName,
				concrete,
				member,
				null, //DefaultSymbolTable symbolTable,
				new ArrayList<AstStatement> ()
				); 
	}

//	public static class Builder {
//		Reporter reporter;
//		
//		AnnotationList annotationList;
//		AstToken name;
//		AstType returnType; 
//		
//		List<TypeParameter> typeParameters = new ArrayList<>();
//		List<AstFunctionFormalArgument> formalArguments = new ArrayList<>();
//		QName containerQName;
////		QName qName;
//		boolean concrete;
//		boolean member;
//		private List<AstStatement> statements = new ArrayList<>(); 
//		
//		
//		public Builder(Reporter reporter, AnnotationList annotationList, AstToken name, AstType returnType, QName containerQName) {
//			super();
//			this.reporter = reporter;
//			this.annotationList = annotationList;
//			this.name = name;
//			this.returnType = returnType;
//			this.containerQName = containerQName;
//		}
//		public Builder withFormalParameter(TypeParameter param) {
//			typeParameters.add(param);
//			return this;
//		}
//		public Builder withFunctionArgument(AstFunctionFormalArgument arg) {
//			formalArguments.add(arg);
//			return this;
//		}
//		
//		public Builder(Reporter reporter, AnnotationList annotationList, AstToken name, AstType returnType) {
//			super();
//			this.reporter = reporter;
//			this.annotationList = annotationList;
//			this.name = name;
//			this.returnType = returnType;
//		}
//		public void addStatement(AstStatement statement) {
//			this.statements.add(statement);
//		}
//
//		public void setConcrete(boolean concrete) {
//			this.concrete = concrete;
//		}
//
//		public void setMember(boolean member) {
//			this.member = member;
//		}
//		
//		public void setContainerQName(QName containerQName) {
////			this.containerQName = Optional.of(containerQName);
//			this.containerQName = containerQName;
//		}
//		public AstFunctionDeclaration build(DefaultSymbolTable parentSymbolTable) {
////			if(!fd.getAnnotationList().contains("static"))
//			member = annotationList.contains("static");
//
//			QName qName = containerQName.child(name.getText());
//
//			DefaultSymbolTable symbolTable = new DefaultSymbolTable(parentSymbolTable);
//			
//			
//			AstFunctionDeclaration fd = new AstFunctionDeclaration(
//					reporter, 
//					annotationList, 
//					name, 
//					returnType,
//					ImmutableList.copyOf(typeParameters),
////					ImmutableList.copyOf(formalArguments),
//					ImmutableList.of(),
//					containerQName,
//					qName,
//					concrete,
//					member,
//					symbolTable,
//					ImmutableList.copyOf(statements)
//					);
//			
//			for(AstFunctionFormalArgument arg: formalArguments) {
//				fd = fd.withFunctionArgument(arg);
//			}
//			
//			// Function itself is in symbol table (???)
////			symbolTable.addFunction(fd);
//			fd.getSymbolTable().addFunction(fd);	// TODO: ugly
//			
//			// Add function args in symbol table
////			for(AstFunctionFormalArgument arg: formalArguments) {
////				symbolTable.addFunctionArgument(arg);
////			}
//			return fd;
//		}
//	}
	
	

//	public AstFunctionDeclaration(Reporter reporter, AnnotationList annotationList, AstToken name, AstType returnType, 
//			boolean concrete, boolean member, DefaultSymbolTable symbolTable) 
//	{
//		this(reporter, annotationList, name, returnType, ImmutableList.of(), ImmutableList.of(), 
//				Optional.empty(), null /*qName*/, concrete, member, symbolTable);	// TODO: oops qName
//	}
	
	
	public AstFunctionDeclaration withFormalParameter(TypeParameter param) {
		
		ImmutableList.Builder<TypeParameter> builder = ImmutableList.builderWithExpectedSize(typeParameters.size() + 1);
		ImmutableList<TypeParameter> newTypeParams = builder.addAll(typeParameters).add(param).build();
		
//		AstFunctionDeclaration fd = new AstFunctionDeclaration(reporter,
//				annotationList, 
//				name, 
//				returnType, 
//				newTypeParams,
//				formalArguments,
//				containerQName, qName, concrete, member, 
//				symbolTable);	// ???
		
		
		AstFunctionDeclaration fd = new AstFunctionDeclaration(
				reporter, 
				annotationList, 
				name, 
				returnType,
				newTypeParams,
				formalArguments,
				containerQName,
				concrete,
				member,
				symbolTable,
				statements);

		
		return fd;
	}
	
	public AstFunctionDeclaration withFunctionArgument(AstFunctionFormalArgument arg) {
		
		ImmutableList.Builder<AstFunctionFormalArgument> builder = ImmutableList.builderWithExpectedSize(formalArguments.size() + 1);
		ImmutableList<AstFunctionFormalArgument> newFormalArguments = builder.addAll(formalArguments).add(arg).build();
		
		// Symbol table including new param
		DefaultSymbolTable newSymbolTable = new DefaultSymbolTable(symbolTable);
		newSymbolTable.addFunctionArgument(arg);
		
		
		AstFunctionDeclaration fd = new AstFunctionDeclaration(reporter,
				annotationList, 
				name, 
				returnType, 
				typeParameters,
				newFormalArguments,
				containerQName, 
//				qName, 
				concrete, member,
				newSymbolTable,
				statements);
		return fd;
	}
	
	
	
	
	@Override
	public AstToken getName() {
		return name;
	}
	
	
	public boolean isMember() {
		return member;
	}

	public void setMember(boolean member) {
		this.member = member;
	}

	public void addStatement(AstStatement statement) {
		this.statements.add(statement);
	}

	public List<AstStatement> getStatements() {
		return statements;
	}

	public void assignSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
		this.symbolTable.addFunction(this);
		
////		formalArguments.stream().forEach(argument -> symbolTable.addFunctionArgument(argument.getName(), argument));
		

	}

	public boolean isConcrete() {
		return concrete;
	}

	public void setConcrete(boolean concrete) {
		this.concrete = concrete;
	}

	public void visit(AstVisitor visitor) {
		visitor.startFunctionDeclaration(this);
		formalArguments.stream().forEach(formalArg -> formalArg.visit(visitor));
		statements.stream().forEach(st -> st.visit(visitor));
		returnType.visit(visitor);
		visitor.endFunctionDeclaration(this);
	}

	public AstType getReturnType() {
		return returnType;
	}

	public void setReturnType(AstType returnType) {
		this.returnType = returnType;
	}

//	public void setSymbolTableParent(SymbolTable newParent) {
//		this.symbolTable.setParentSymbolTable(newParent);
//	}

	@Override
	public DefaultSymbolTable getSymbolTable() {
		return symbolTable;
	}

	public List<TypeParameter> getTypeParameters() {
		return typeParameters;
	}

//	public void addTypeParameterDeclaration(TypeFormalParameterDeclaration typeParameter) {
//		this.typeParameters.add(typeParameter);
//	}

//	public void addFormalArgument(AstFunctionFormalArgument argument) {
//		this.formalArguments.add(argument);
////		this.symbolTable.addFunctionArgument(argument.getName(), argument);
//	}

	public List<AstFunctionFormalArgument> getFormalArguments() {
		return formalArguments;
	}
	

	public AnnotationList getAnnotationList() {
		return annotationList;
	}

	public String messageStr() {
		List<String> typeParams = typeParameters.stream().map(p -> p.messageStr()).collect(Collectors.toList());

		return returnType.messageStr() + 
				" " + 
				name.getText() +
				(typeParams.isEmpty() ? "" : ("<" + String.join(", ", typeParams) + ">")) +
				"(" + ")"; // TODO: parameters
	}

	@Override
	public Optional<AstFunctionDeclaration> apply(AstType parameter) {
		throw new UnsupportedOperationException();
	}

	
	public QName getContainerQName() {
		return containerQName;
	}

	@Override
	public QName getQName() {
		return qName;
	}

//	public void setContainerQName(QName containerQName) {
//		this.containerQName = containerQName;
//		this.qName = containerQName.child(this.name.getText()); // TODO: ugly
//	}

	@Override
	public String toString() {
		return qName.toString() + "(" + formalArguments.size() + " args)";
	}
	
	/** Convert 'simple' return type using symbol table
	 * 
	 */
	private Type resolveReturnType() {
		Type resolved = returnType.getApiType();
		return resolved;
	}
	
	private class FunctionImpl implements AbstractFunction {
		QName functionQName = containerQName.child(name.getText());
		List<FunctionFormalArgument> implArguments = 
				formalArguments.stream()
				.map(arg -> arg.toAPI(functionQName))
				.collect(Collectors.toList());
		Type returnType = resolveReturnType();
		
		Optional<List<Statement>> bodyStatements =
				concrete ?
						Optional.of(statements.stream()
					.map(st -> st.toAPI())
					.collect(Collectors.toList()))
						: Optional.empty();
		
		
		@Override
		public QName getQName() {
			return functionQName;
		}

		@Override
		public List<FunctionFormalArgument> getArguments() {
			return implArguments;
		}

		@Override
		public Type getReturnType() {
			return returnType;
		}

		@Override
		public Optional<List<Statement>> getBodyStatements() {
			return bodyStatements;
		}

		@Override
		public Optional<QName> getClassOrInterfaceContainerQName() {
			if(member /*&& containerQName.isPresent()*/) {
				return Optional.of(containerQName);
			}
			
			return Optional.empty();
		}
	}
	private FunctionImpl functionImpl = null;
	
	public AbstractFunction toAPI() {
		if(functionImpl == null) {
			functionImpl = new FunctionImpl();
		}
		return functionImpl;
	}
	
	
}
