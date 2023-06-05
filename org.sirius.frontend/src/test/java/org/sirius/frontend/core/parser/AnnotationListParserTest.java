package org.sirius.frontend.core.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.Annotation;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.parser.SParser;

public class AnnotationListParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter();
//		Parsers.AnnotationListVisitor typeVisitor = new Parsers.AnnotationListVisitor();
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private AnnotationList parseAnnotationList(String inputText) {
		
		SParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.annotationList();
				
		Parsers.AnnotationListVisitor typeVisitor = new Parsers.AnnotationListVisitor();
		AnnotationList myAnnoList = typeVisitor.visit(tree);
		return myAnnoList;
	}
	
	@Test
	@DisplayName("Simplest annotations")
	public void simplestAnnotations() {
		AnnotationList annotationList = parseAnnotationList("aa bb cc");
		
		List<String> strs = annotationList.getAnnotations().stream()
				.map(Annotation::getName)
				.map(AstToken::getText)
				.toList();

		assertThat(strs, 
				contains("aa", "bb", "cc"));
	}

	@Test
	@DisplayName("Annotations with parameters")
	public void annotationsWithParameters() {
		AnnotationList annotationList = parseAnnotationList("aa() bb() cc()");
		
		List<String> annos = annotationList.getAnnotations().stream()
				.map(Annotation::getName)
				.map(AstToken::getText)
				.toList();
		assertThat(annos, contains("aa", "bb", "cc"));
	}
}
