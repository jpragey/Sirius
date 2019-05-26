package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class PhysicalPathTest {

	@Test
	public void starWith() {
		assertEquals((new PhysicalPath("a", "b", "c")).startWith(new PhysicalPath("a", "b")), true);
		assertEquals((new PhysicalPath("a", "b")).startWith(new PhysicalPath("a", "b")), true);
		assertEquals((new PhysicalPath("a", "b")).startWith(new PhysicalPath("a", "b", "c")), false);
		assertEquals((new PhysicalPath()).startWith(new PhysicalPath()), true);
	}
}
