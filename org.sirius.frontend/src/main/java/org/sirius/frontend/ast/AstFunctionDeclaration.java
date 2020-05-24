package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;

import com.google.common.collect.ImmutableList;

public class AstFunctionDeclaration implements Scoped, Visitable, AstParametric<AstFunctionDeclaration>, Named {

	private AstToken name;
	
	private ImmutableList<TypeParameter> typeParameters;
	
	private ImmutableList<AstFunctionFormalArgument> formalArguments;
	

	private List<AstStatement> statements = new ArrayList<>(); 
	
	private AstType returnType = new AstVoidType();

	private Reporter reporter;
	
	private AnnotationList annotationList;

	private DefaultSymbolTable symbolTable; 

	private boolean concrete;
	// 
	private Optional<QName> containerQName = Optional.empty();
	private QName qName;
	
	public AstFunctionDeclaration(Reporter reporter, AnnotationList annotationList, AstToken name, AstType returnType,
			ImmutableList<TypeParameter> typeParameters,
			ImmutableList<AstFunctionFormalArgument> formalArguments,
			Optional<QName> containerQName,
			QName qName,
			boolean concrete
			) {
		super();
		this.reporter = reporter;
		this.annotationList = annotationList;
		this.name = name;
		this.returnType = returnType;
		this.typeParameters = typeParameters;
		this.formalArguments = formalArguments;
		
		this.containerQName = containerQName;
		this.qName = qName;
		this.concrete = concrete;
	}

	public AstFunctionDeclaration(Reporter reporter, AnnotationList annotationList, AstToken name, AstType returnType, boolean concrete) {
		this(reporter, annotationList, name, returnType, ImmutableList.of(), ImmutableList.of(), Optional.empty(), null /*qName*/, concrete);	// TODO: oops qName
	}
	
	
	public AstFunctionDeclaration withFormalParameter(TypeParameter param) {
		
		ImmutableList.Builder<TypeParameter> builder = ImmutableList.builderWithExpectedSize(typeParameters.size() + 1);
		ImmutableList<TypeParameter> newTypeParams = builder.addAll(typeParameters).add(param).build();
		
		AstFunctionDeclaration fd = new AstFunctionDeclaration(reporter,
				annotationList, 
				name, 
				returnType, 
				newTypeParams,
				formalArguments,
				containerQName, qName, concrete);
		return fd;
	}
	
	public AstFunctionDeclaration withFunctionArgument(AstFunctionFormalArgument arg) {
		
		ImmutableList.Builder<AstFunctionFormalArgument> builder = ImmutableList.builderWithExpectedSize(formalArguments.size() + 1);
		ImmutableList<AstFunctionFormalArgument> newFormalArguments = builder.addAll(formalArguments).add(arg).build();
		
		AstFunctionDeclaration fd = new AstFunctionDeclaration(reporter,
				annotationList, 
				name, 
				returnType, 
				typeParameters,
				newFormalArguments,
				containerQName, qName, concrete);
		return fd;
	}
	
	
	
	
	@Override
	public AstToken getName() {
		return name;
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
		TypeParameter formalParam = typeParameters.get(0);
		if(formalParam == null) {
			reporter.error("Can't apply type " + parameter.messageStr() + " to function " + messageStr() + ", it has no formal parameter." );
			return Optional.empty();
		}
		
		AstFunctionDeclaration cd = new AstFunctionDeclaration(reporter, annotationList, name, returnType, 
				typeParameters.subList(1, typeParameters.size()), 
				formalArguments,
				containerQName, qName, concrete);
		
//		cd.typeParameters.addAll(typeParameters.subList(1, typeParameters.size()));
	
//		for(FunctionDeclaration fd: functionDeclarations) {
//			cd.addFunctionDeclaration(fd.apply(parameter));
//		}
		
		return Optional.of(cd);
	}

	
	public Optional<QName> getContainerQName() {
		return containerQName;
	}

	@Override
	public QName getQName() {
		return qName;
	}

	public void setContainerQName(QName containerQName) {
		this.containerQName = Optional.of(containerQName);
		this.qName = containerQName.child(this.name.getText()); // TODO: ugly
	}

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
	
	public Optional<TopLevelFunction> getTopLevelFunction() 
	{// TODO: filter top-level
		return Optional.of(new TopLevelFunction() {
			QName functionQName = containerQName.get().child(name.getText());

			@Override
			public QName getQName() {
				return functionQName;
			}

			@Override
			public List<FunctionFormalArgument> getArguments() {
				return formalArguments.stream()
						.map(arg -> arg.toAPI(functionQName))
						.collect(Collectors.toList());
			}

			@Override
			public List<Statement> getBodyStatements() {
				return statements.stream()
					.map(st -> st.toAPI())
					.collect(Collectors.toList());
			}

			@Override
			public Type getReturnType() {
				return resolveReturnType();
			}
		});
	}
	
	public Optional<MemberFunction> getMemberFunction() {// TODO: filter top-level
		return Optional.of(new MemberFunction() {
			QName functionQName = containerQName.get().child(name.getText());
			
			@Override
			public QName getQName() {
				return functionQName;
			}

			@Override
			public List<FunctionFormalArgument> getArguments() {
				return formalArguments.stream()
						.map(arg -> arg.toAPI(functionQName))
						.collect(Collectors.toList());
			}
			@Override
			public List<Statement> getBodyStatements() {
				return statements.stream()
					.map(st -> st.toAPI())
					.collect(Collectors.toList());
			}
			@Override
			public Type getReturnType() {
				return resolveReturnType();
			}
		});
	}
	
	
}
