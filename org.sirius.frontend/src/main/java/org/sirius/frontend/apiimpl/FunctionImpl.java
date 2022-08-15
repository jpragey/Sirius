package org.sirius.frontend.apiimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.Annotation;
import org.sirius.frontend.api.FunctionParameter;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstFunctionParameter;

/** Function implementation
 * 
 * @author jpragey
 *
 */
public record FunctionImpl(
		List<Annotation> annotations,
		QName qName,
		List<FunctionParameter> parameters,
		Type returnType,
		List<Statement> bodyStatements,
		boolean member
		) implements AbstractFunction 
{
	@Override
	public String toString() {
		return "API function " + qName.dotSeparated() + "(" + parameters.size() + " args)";
	}
	
	@Override
	public Optional<QName> getClassOrInterfaceContainerQName() {
		return  member? qName.parent() : Optional.empty();
	}
	
	public static class Builder {
		private List<org.sirius.frontend.api.Annotation> annotations = new ArrayList<org.sirius.frontend.api.Annotation>();
		private List<FunctionParameter> parameters = new ArrayList<FunctionParameter>();
		private org.sirius.frontend.api.Type returnType = org.sirius.frontend.api.Type.voidType;
		private boolean member = false;
		private QName qName = QName.empty;// ???
		private List<org.sirius.frontend.api.Statement> bodyStatements = new ArrayList<org.sirius.frontend.api.Statement>();
		
		public Builder() {
			super();
		}
		public Builder(QName qName) {
			super();
			this.qName = qName;
		}
		public Builder(org.sirius.frontend.api.Type returnType, QName qName) {
			super();
			this.qName = qName;
			this.returnType = returnType;
		}
		public Builder(org.sirius.frontend.api.Type returnType, String... qName) {
			this(returnType, new QName(qName) );
		}
		public Builder addAnnotation(org.sirius.frontend.api.Annotation annotation) {
			this.annotations.add(annotation);
			return this;
		}
		public Builder addAnnotations(org.sirius.frontend.api.Annotation annotations) {
			this.annotations.addAll(Arrays.asList(annotations));
			return this;
		}
		public Builder addParameter(FunctionParameter parameter) {
			this.parameters.add(parameter);
			return this;
		}
		public Builder addParameters(FunctionParameter... parameters) {
			this.parameters.addAll(Arrays.asList(parameters));
			return this;
		}
		public Builder withReturnType(org.sirius.frontend.api.Type returnType) {
			this.returnType = returnType;
			return this;
		}
		public Builder withMember(boolean member) {
			this.member = member;
			return this;
		}
		public Builder withQName(QName qName) {
			this.qName = qName;
			return this;
		}
		public Builder withBodyStatements(List<org.sirius.frontend.api.Statement> bodyStatements) {
			this.bodyStatements = bodyStatements;
			return this;
		}
		public Builder withBody(org.sirius.frontend.api.Statement bodyStatements) {
			this.bodyStatements = List.of(bodyStatements);
			return this;
		}

		public FunctionImpl create() {
			return new FunctionImpl(
					annotations,
					qName,
					parameters,
					returnType,
					bodyStatements,
					member);
			
		}
	}
	public static Builder builder(org.sirius.frontend.api.Type returnType, String... qName) {
		return new Builder(returnType, new QName(qName) );
	}

}