package org.sirius.frontend.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.parser.SiriusParser;

public class AnnotationListParserTest {

	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private AnnotationList parseAnnotationList(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.annotationList();
				
		AnnotationListParser.AnnotationListVisitor typeVisitor = new AnnotationListParser.AnnotationListVisitor();
		AnnotationList myAnnoList = typeVisitor.visit(tree);
		return myAnnoList;
	}
	
	@Test
	@DisplayName("Simplest annotations")
	public void simplestAnnotations() {
		AnnotationList partialList = parseAnnotationList("aa bb cc");
		assertThat(partialList.getAnnotations().size(), equalTo(3));
		assertEquals(partialList.getAnnotations().get(0).getName().getText(), "aa");
		assertThat(partialList.getAnnotations().stream().map(anno -> anno.getName().getText()).toArray(), 
				equalTo(new String[]{"aa", "bb", "cc"}));
	}

	@Test
	@DisplayName("Annotations with parameters")
	public void annotationsWithParameters() {
		AnnotationList partialList = parseAnnotationList("aa() bb() cc()");
		assertThat(partialList.getAnnotations().size(), equalTo(3));
		assertEquals(partialList.getAnnotations().get(0).getName().getText(), "aa");
		assertThat(partialList.getAnnotations().stream().map(anno -> anno.getName().getText()).toArray(), 
				equalTo(new String[]{"aa", "bb", "cc"}));
	}
}
