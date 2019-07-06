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
import org.sirius.frontend.symbols.LocalSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

public class AstFunctionDeclaration implements Scoped, Visitable {

	private AstToken name;
	
	private List<TypeFormalParameterDeclaration> typeParameters = new ArrayList<>();
	
	private List<AstFunctionFormalArgument> formalArguments = new ArrayList<>();
	

	private List<AstStatement> statements = new ArrayList<>(); 
	
	private AstType returnType = new AstVoidType();

	private Reporter reporter;
	
	private AnnotationList annotationList;

	private LocalSymbolTable symbolTable; 

	// 
	private Optional<QName> containerQName = Optional.empty();
	private QName qName;
	
	public AstFunctionDeclaration(Reporter reporter, AnnotationList annotationList, AstToken name, AstType returnType) {
		super();
		this.reporter = reporter;
		this.annotationList = annotationList;
		this.name = name;
		this.returnType = returnType;
		this.symbolTable = new LocalSymbolTable(reporter); 
		
		this.symbolTable.addFunction(name, this);
	}

	public AstToken getName() {
		return name;
	}
	
	public void addStatement(AstStatement statement) {
		this.statements.add(statement);
	}

	public List<AstStatement> getStatements() {
		return statements;
	}
	
	public void visit(AstVisitor visitor) {
		visitor.startFunctionDeclaration(this);
		statements.stream().forEach(st -> st.visit(visitor));
		visitor.endFunctionDeclaration(this);
	}

	public AstType getReturnType() {
		return returnType;
	}

	public void setReturnType(AstType returnType) {
		this.returnType = returnType;
	}

	public void setSymbolTableParent(SymbolTable newParent) {
		this.symbolTable.setParentSymbolTable(newParent);
	}

	@Override
	public LocalSymbolTable getSymbolTable() {
		return symbolTable;
	}

	public List<TypeFormalParameterDeclaration> getTypeParameters() {
		return typeParameters;
	}

	public void addTypeParameterDeclaration(TypeFormalParameterDeclaration typeParameter) {
		this.typeParameters.add(typeParameter);
	}

	public void addFormalArgument(AstFunctionFormalArgument argument) {
		this.formalArguments.add(argument);
		this.symbolTable.addFunctionArgument(argument.getName(), argument);
	}

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
	
	public Optional<AstFunctionDeclaration> apply(AstType parameter) {
		TypeFormalParameterDeclaration formalParam = typeParameters.get(0);
		if(formalParam == null) {
			reporter.error("Can't apply type " + parameter.messageStr() + " to function " + messageStr() + ", it has no formal parameter." );
			return Optional.empty();
		}
		
		AstFunctionDeclaration cd = new AstFunctionDeclaration(reporter, annotationList, name, returnType);
		
		cd.typeParameters.addAll(typeParameters.subList(1, typeParameters.size()));
	
//		for(FunctionDeclaration fd: functionDeclarations) {
//			cd.addFunctionDeclaration(fd.apply(parameter));
//		}
		
		return Optional.of(cd);
	}

	
	public Optional<QName> getContainerQName() {
		return containerQName;
	}

	public QName getQName() {
		return qName;
	}

	public void setContainerQName(QName containerQName) {
		this.containerQName = Optional.of(containerQName);
		this.qName = containerQName.child(this.name.getText()); // TODO: ugly
	}

	@Override
	public String toString() {
		return name.getText() + "(" + formalArguments.size() + " args)";
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
				return AstFunctionDeclaration.this.getReturnType().getApiType();
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
				return AstFunctionDeclaration.this.getReturnType().getApiType();
			}
		});
	}
	
	
}
