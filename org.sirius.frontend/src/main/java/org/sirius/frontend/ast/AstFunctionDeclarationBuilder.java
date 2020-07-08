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
	

	private Optional<List<AstStatement>> statements; 
	
	private AstType returnType = new AstVoidType();

	private Reporter reporter;
	
	private AnnotationList annotationList;

	private DefaultSymbolTable symbolTable; 

	// True it it has a body
	private boolean concrete;
	// 
	private QName containerQName = null;
	
	// -- nonnull for instance method
	private boolean member = false;
	
	
	private QName qName = null; // deduced from containerQName + name
	
private List<Partial> partials = Collections.emptyList();
	
	public AstFunctionDeclarationBuilder(
			Reporter reporter, 
			AnnotationList annotationList, 
			AstToken name, 
			AstType returnType,
			ImmutableList<TypeParameter> typeParameters,
			ImmutableList<AstFunctionParameter> formalArguments,
			boolean concrete,
			boolean member,
			DefaultSymbolTable symbolTable,
			Optional<List<AstStatement>> statements,
			List<Partial> partials
			) {
		super();
		this.reporter = reporter;
		this.annotationList = annotationList;
		this.name = name;
		this.returnType = returnType;
		this.typeParameters = typeParameters;
		this.formalArguments = formalArguments;
		
		this.concrete = concrete;
		this.member = member;
		this.symbolTable = symbolTable;
		this.statements = statements;
		this.partials = partials;
	}

	public AstFunctionDeclarationBuilder(
			Reporter reporter, 
			AnnotationList annotationList, 
			AstToken name, 
			AstType returnType,
			ImmutableList<TypeParameter> typeParameters,
			ImmutableList<AstFunctionParameter> formalArguments,
			boolean concrete,
			boolean member,
			DefaultSymbolTable symbolTable,
			List<Partial> partials
			) {
		this(
				reporter, 
				annotationList, 
				name, 
				returnType,
				typeParameters,
				formalArguments,
				concrete,
				member,
				symbolTable,
				Optional.empty() /*statements*/,
				partials
				);
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
			boolean concrete,
			boolean member,
			List<Partial> partials
			) {
		this(reporter, annotationList, name, returnType,
				ImmutableList.of(),	//<TypeParameter> typeParameters,
				ImmutableList.of(),	//<AstFunctionFormalArgument> formalArguments,
				concrete,
				member,
				null, //DefaultSymbolTable symbolTable,
				Optional.of(new ArrayList<AstStatement> ()),	// TODO: ???
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
				concrete,
				member,
				symbolTable,
				statements,
				partials);
		
		return fd;
	}
	
	
	
	
	public PartialList withFunctionArguments(List<AstFunctionParameter> args) {
		
		if(!annotationList.contains("static"))
			setMember(true);

		PartialList partialList = new PartialList(args, returnType, member /* this*/, /*qName,*/ /*concrete,*/ name, statements); 
		
		return partialList;
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
		if(statements.isEmpty())
			statements = Optional.of(new ArrayList<>());
		this.statements.get().add(statement);
	}

	public Optional<List<AstStatement>> getStatements() {
		return statements;
	}

	public void assignSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public boolean isConcrete() {
		return concrete;
	}

	public void setConcrete(boolean concrete) {
		this.concrete = concrete;
		if(concrete) {
			this.statements = Optional.of(new ArrayList<>());
		}
	}

	public void visit(AstVisitor visitor) {
		visitor.startFunctionDeclaration(this);
		formalArguments.stream().forEach(formalArg -> formalArg.visit(visitor));
		if(statements.isPresent())
			statements.get().stream().forEach(st -> st.visit(visitor));
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
		assert(qName != null);
		return qName;
	}

	@Override
	public String toString() {
		String qnString = (qName == null) ? "<null>" : qName.toString();
		return qnString + "(" + formalArguments.size() + " args)";
	}
	
	/** Convert 'simple' return type using symbol table
	 * 
	 */
	private Type resolveReturnType() {
		Type resolved = returnType.getApiType();
		return resolved;
	}
	
}
