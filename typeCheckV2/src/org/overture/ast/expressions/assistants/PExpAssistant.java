package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.PExp;
import org.overture.runtime.TypeChecker;

public class PExpAssistant {

	public static void report(int number, String msg, PExp exp) {
		TypeChecker.report(number, msg, exp.getLocation());
	}

	public static void concern(boolean serious, int number, String msg,
			AApplyExp node) {

		if (serious) {
			TypeChecker.report(number, msg, node.getLocation());
		} else {
			TypeChecker.warning(number, msg, node.getLocation());
		}
	}

	public static void detail(String tag, Object obj) {
		TypeChecker.detail(tag, obj);
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

}
