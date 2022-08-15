package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.Annotation;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ImportDeclarationElement;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.AnnotationContext;
import org.sirius.frontend.parser.SiriusParser.AnnotationListContext;
import org.sirius.frontend.parser.SiriusParser.ImportDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.ImportDeclarationElementContext;
import org.sirius.frontend.parser.SiriusParser.PackageDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.QnameContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
//public class AnnotationListParser() {

//	public static class AnnotationVisitor extends SiriusBaseVisitor<Annotation> {
//		@Override
//		public Annotation visitAnnotation(AnnotationContext ctx) {
//			AstToken name = new AstToken(ctx.LOWER_ID().getSymbol());
//			return new Annotation(name);
//		}
//	}
//
//	public static class AnnotationListVisitor extends SiriusBaseVisitor<AnnotationList> {
//		@Override
//		public AnnotationList visitAnnotationList(AnnotationListContext ctx) {
//			
//			AnnotationVisitor visitor = new AnnotationVisitor();
//			List<Annotation> annotations = ctx.annotation().stream()
//					.map(annoCtxt -> annoCtxt.accept(visitor))
//					.collect(Collectors.toList());
//			
//			return new AnnotationList(annotations);
//		}
//	}
	
//}
