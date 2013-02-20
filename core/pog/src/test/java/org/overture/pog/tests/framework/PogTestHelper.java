package org.overture.pog.tests.framework;

import java.util.*;

public class PogTestHelper {

	public static final double CHECK_TOLERANCE = 0.025;
	
	public static boolean containsPermutation(String str, List<String> list) {
		for (String currentStr : list) {

			if (isPerm(str, currentStr))
				return true;
		}

		return false;
	}

	public static StringComparison findClosestMatch(String actual,
			List<String> expected) {

		if (expected == null || expected.size() <= 0)
			return null;

		if (actual == null || actual.length() <= 0)
			return null;

		StringComparison closestCompSoFar = new StringComparison(actual,
				expected.get(0), editDistance(actual, expected.get(0)));

		for (int i = 1; i < expected.size(); i++) {
			StringComparison currentComp = new StringComparison(actual,
					expected.get(i), editDistance(actual, expected.get(i)));

			if (currentComp.getDistLengthRatio() < closestCompSoFar.getDistLengthRatio()) {
				closestCompSoFar = currentComp;
			}
		}

		return closestCompSoFar;
	}
	
	public static boolean ToleranceCheckPassed(StringComparison comp)
	{
		return comp.getDistLengthRatio() > 0 && comp.getDistLengthRatio() < CHECK_TOLERANCE;
	}
	
	private static boolean isPerm(String s1, String s2) {

		if (s1.length() != s2.length())
			return false;

		char s1c[] = s1.toCharArray();
		char s2c[] = s2.toCharArray();

		Arrays.sort(s1c);
		Arrays.sort(s2c);

		for (int i = 0; i < s1c.length; i++)
			if (s1c[i] != s2c[i])
				return false;
		return true;

	}

	private static int editDistance(String str1, String str2) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 1; j <= str2.length(); j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length(); i++)
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = Min(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]
								+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
										: 1));

		return distance[str1.length()][str2.length()];
	}

	private static int Min(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}
}

class StringComparison implements Comparable<StringComparison> {
	
	private String actual;
	private String resultStr;
	private double distLengthRatio;

	public StringComparison(String actual, String resultStr, int editDist) {
		super();
		this.actual = actual;
		this.resultStr = resultStr;
		this.distLengthRatio = 1.0 * editDist
				/ ((actual.length() + resultStr.length()) / 2);
	}

	public int compareTo(StringComparison o) {

		return Double.compare(o.distLengthRatio, this.distLengthRatio);
	}

	public String getActual() {
		return actual;
	}

	public String getResultStr() {
		return resultStr;
	}

	public double getDistLengthRatio() {
		return distLengthRatio;
	}
}
