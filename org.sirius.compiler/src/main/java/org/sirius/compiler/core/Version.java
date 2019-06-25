package org.sirius.compiler.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.sirius.common.error.Reporter;

public class Version {
	public String getVersion() throws IOException {
		InputStream is = Version.class.getResourceAsStream("/version.txt");
		BufferedReader breader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF8")));
		String line = breader.readLine();
		return line;
	}
	/** Print version in reporter. Dump */
	public void printVersion(Reporter reporter) {
		try {
			String version = getVersion();
			System.out.println(version);
//			reporter.info(version);
		} catch (IOException e) {
			reporter.error(e.getMessage(), e);
		}
	}
}
