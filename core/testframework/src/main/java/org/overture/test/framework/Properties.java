package org.overture.test.framework;

public class Properties {
	/**
	 * Flag used globally to re-created all test result files used for result comparison
	 */
	public static boolean recordTestResults = false;

	/**
	 * Flag used when writing result files to determine if UNIX-style
	 * line separators should be used.  Default to true so that even
	 * Windows-based machines will generate LF rather than CRLF line
	 * endings.
	 */
	public static boolean forceUnixLineEndings = true;
}
