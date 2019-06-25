package org.sirius.compiler.core;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.testng.annotations.Test;

public class VersionTest {

	@Test
	public void testVersionIsReadInResource() throws IOException {
		String version = new Version().getVersion().trim();
		assertEquals(version, "0.0.1-SNAPSHOT");	// not ${sirius.version}
	}
}
