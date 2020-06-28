package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;

import com.google.common.collect.ImmutableList;

public class AstFunctionDeclarationBuilder implements Scoped, Visitable, AstParametric<AstFunctionDeclarationBuilder>, Named {

	private AstToken name;
	
	private ImmutableList<TypeParameter> typeParameters;
	
	private ImmutableList<AstFunctionParameter> formalArguments;
	

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
	private boolean member = false;
	
	
	private QName qName; // deduced from containerQName + name
	
private List<Partial> partials = Collections.emptyList();
	
	public AstFunctionDeclarationBuilder(
			Reporter reporter, 
			AnnotationList annotationList, 
			AstToken name, 
			AstType returnType,
			ImmutableList<TypeParameter> typeParameters,
			ImmutableList<AstFunctionParameter> formalArguments,
			QName containerQName,
			boolean concrete,
			boolean member,
			DefaultSymbolTable symbolTable,
			List<AstStatement> statements,
//			Optional<AstFunctionDeclaration> delegate,
			List<Partial> partials
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
//		this.delegate = delegate;
		this.partials = partials;
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
	public AstFunctionDeclarationBuilder(
			Reporter reporter, 
			AnnotationList annotationList, 
			AstToken name, 
			AstType returnType,
			QName containerQName,
			boolean concrete,
			boolean member,
//			Optional<AstFunctionDeclaration> delegate,
			List<Partial> partials
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
				new ArrayList<AstStatement> (),
//				delegate,
				partials
				); 
	}

	/** Get the list of partially-applied functions, sorted by their number of captures.
	 * The first one has all arguments and no capture. 
	 * The last one has no argument and all captures.   
	 * */
	public List<Partial> getPartials() {
		return partials;
	}

	public AstFunctionDeclarationBuilder withFormalParameter(TypeParameter param) {
		
		ImmutableList.Builder<TypeParameter> builder = ImmutableList.builderWithExpectedSize(typeParameters.size() + 1);
		ImmutableList<TypeParameter> newTypeParams = builder.addAll(typeParameters).add(param).build();
		
		AstFunctionDeclarationBuilder fd = new AstFunctionDeclarationBuilder(
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
				statements,
//				delegate,	// TODO: ???
				partials);
		
		return fd;
	}
	
	
	
	
	public PartialList withFunctionArguments(List<AstFunctionParameter> args) {
		
		if(!annotationList.contains("static"))
			setMember(true);

//		List<Partial> partials = new ArrayList<>(args.size() + 1);
//		for(int from = 0; from <= args.size(); from++) 
//		{
//			List<AstFunctionParameter> partialArgs = args.subList(from, args.size());
//			
//			
//			
//			Partial partial = new Partial(
//					name,
//////					args.subList(0, from) .stream().map(arg -> new Capture(arg.getType(), arg.getName())).collect(Collectors.toList()), 
//					partialArgs, 
//					this, 
//					returnType,
//					statements
//					/*,
//					symbolTable*/);
//			partials.add(partial);
//		}
//		PartialList partialList = new PartialList(partials, qName, concrete, name, statements);
		
		PartialList partialList = new PartialList(args, returnType, this, qName, concrete, name, statements); 
		
		return partialList;
/*		
		ImmutableList<AstFunctionParameter> newFormalArguments = ImmutableList.copyOf(args);

		DefaultSymbolTable newSymbolTable = new DefaultSymbolTable(symbolTable, AstFunctionDeclarationBuilder.class.getSimpleName());

		AstFunctionDeclarationBuilder fd = new AstFunctionDeclarationBuilder(reporter,
				annotationList, 
				name, 
				returnType, 
				typeParameters,
				newFormalArguments,
				containerQName, 
				concrete, member,
				newSymbolTable,
				statements,
				partials);
		return fd;
		*/
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
//		this.symbolTable.addFunction(this);
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
		
		partials.stream().forEach(partial -> partial.visit(visitor));
		
		
		visitor.endFunctionDeclaration(this);
	}

	public AstType getReturnType() {
		return returnType;
	}

	public void setReturnType(AstType returnType) {
		this.returnType = returnType;
	}

	@Override
	public DefaultSymbolTable getSymbolTable() {
		return symbolTable;
	}

	public List<TypeParameter> getTypeParameters() {
		return typeParameters;
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
	public Optional<AstFunctionDeclarationBuilder> apply(AstType parameter) {
		throw new UnsupportedOperationException();
	}

	
	public QName getContainerQName() {
		return containerQName;
	}

	@Override
	public QName getQName() {
		return qName;
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
	
	static public class FunctionImpl implements AbstractFunction {
//		QName functionQName = containerQName.child(name.getText());
		QName functionQName;
//		List<FunctionFormalArgument> implArguments = 
//				formalArguments.stream()
//				.map(arg -> arg.toAPI(functionQName))
//				.collect(Collectors.toList());
		private ImmutableList<FunctionFormalArgument> implArguments;
//		Type returnType = resolveReturnType();
		Type returnType;
		
		Optional<List<Statement>> bodyStatements 
		/*=
				concrete ?
						Optional.of(statements.stream()
					.map(st -> st.toAPI())
					.collect(Collectors.toList()))
						: Optional.empty()*/;
		boolean member;
		
		public FunctionImpl(QName functionQName, ImmutableList<AstFunctionParameter> formalArguments, Type returnType, 
				Optional<List<Statement>> bodyStatements, boolean member) {
			this.functionQName = functionQName;
//					formalArguments.stream()
//					.map(arg -> arg.toAPI(functionQName))
//					.collect(Collectors.toList());
			this.returnType = returnType;
			this.bodyStatements = bodyStatements;
			this.member = member;
			
			ArrayList<FunctionFormalArgument> implArgs = new ArrayList(formalArguments.size()); 
			for(AstFunctionParameter arg: formalArguments) {
				FunctionFormalArgument formalArg = arg.toAPI(functionQName);
				implArgs.add(formalArg);
			}
			this.implArguments = ImmutableList.copyOf(implArgs); 
		}
		
		@Override
		public String toString() {
			
			return "API function " + functionQName.dotSeparated() + "(" + implArguments.size() + " args)";
		}
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
				return functionQName.parent();
//				return Optional.of(  containerQName);
			}
			
			return Optional.empty();
		}
	}
	private FunctionImpl functionImpl = null;
	
	private AbstractFunction toAPI_todelete() {	// TODO delete (-> partials)
		if(functionImpl == null) 
		{
			Partial partial = partials.get(0);
			assert(partial != null);
			functionImpl = partial.toAPI();
			
			
//			functionImpl = new FunctionImpl(containerQName.child(name.getText()), this.formalArguments, this.resolveReturnType(),
//					concrete ? Optional.of(statements.stream()
//						.map(st -> st.toAPI())
//						.collect(Collectors.toList()))
//							: Optional.empty(),
//					member
//					);
		}
		return functionImpl;
	}
	
	
}
