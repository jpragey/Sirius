package org.sirius.frontend.core;

import org.junit.jupiter.api.BeforeEach;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.ShellReporter;

public class ResourceInputTextProviderTest {

	AccumulatingReporter reporter;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	
//	@Test
//	public void loadExistingResourceTest() {
//		ResourceInputTextProvider provider = new ResourceInputTextProvider(reporter, "/language/sirius/lang/Anything.sirius");
//		String text = provider.getText();
//		assertTrue(text.contains("Anything"), text);
//		assert( !reporter.hasErrors());
//	}
//	
//	@Test
//	public void loadInexistingResourceTest() {
//		ResourceInputTextProvider provider = new ResourceInputTextProvider(reporter, "/this/does/not/exists.sirius");
//		String text = provider.getText();
//		assertEquals(text, "");
//
//		assert( reporter.hasErrors());
//	}
	
}
