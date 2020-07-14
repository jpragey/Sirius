package org.sirius.frontend.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PhysicalPathTest {

	@Test
	public void starWith() {
		assertEquals((new PhysicalPath("a", "b", "c")).startWith(new PhysicalPath("a", "b")), true);
		assertEquals((new PhysicalPath("a", "b")).startWith(new PhysicalPath("a", "b")), true);
		assertEquals((new PhysicalPath("a", "b")).startWith(new PhysicalPath("a", "b", "c")), false);
		assertEquals((new PhysicalPath()).startWith(new PhysicalPath()), true);
	}
}
