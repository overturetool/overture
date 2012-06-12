package org.overture.typechecker;

import org.overture.ast.lex.LexLocation;

public class TypeCheckerErrors {

	public static void report(int number, String msg, LexLocation location, Object node) {
		TypeChecker.report(number, msg, location);
	}

	public static  void concern(boolean serious, int number, String msg,
			LexLocation location, Object node) {

		if (serious) {
			TypeChecker.report(number, msg, location);
		} else {
			TypeChecker.warning(number, msg, location);
		}
	}

	
	
	public static void detail(String tag, Object obj) {
		TypeChecker.detail(tag, obj);
	}

	public static void detail(boolean serious, String tag, Object obj)
	{
		if (serious)
		{
			TypeChecker.detail(tag, obj);
		}
	}
	
	public static void detail2(boolean serious, String tag1, Object obj1,
			String tag2, Object obj2) {
		if (serious) {
			TypeChecker.detail2(tag1, obj1, tag2, obj2);
		}
	}

	public static void detail2(String tag1, Object obj1, String tag2, Object obj2) {
		TypeChecker.detail2(tag1, obj1, tag2, obj2);
	}

	public static void warning(int number, String msg, LexLocation loc, Object node)
	{
		TypeChecker.warning(number, msg, loc);
	}
	
	
}
