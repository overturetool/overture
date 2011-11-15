package org.overture.ast.patterns.assistants;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.ast.types.assistants.PTypeList;
import org.overture.ast.types.assistants.PTypeSet;
import org.overture.ast.types.assistants.SNumericBasicTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeComparator;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class PPatternAssistantTC extends PPatternAssistant {

	public static List<PDefinition> getDefinitions(PPattern rp, PType ptype,
			NameScope scope) {
		switch (rp.kindPPattern()) {
		case IDENTIFIER:
			AIdentifierPattern idPattern = (AIdentifierPattern) rp;
			List<PDefinition> defs = new ArrayList<PDefinition>();
			defs.add(new ALocalDefinition(idPattern.getLocation(), scope,
					false, null, null, ptype, false, idPattern.getName()));
			return defs;
		case BOOLEAN:
		case CHARACTER:
		case EXPRESSION:
		case IGNORE:
		case INTEGER:
		case NIL:
		case QUOTE:
		case REAL:
		case STRING:
			return new Vector<PDefinition>();
		case CONCATENATION:
			return AConcatenationPatternAssistantTC.getDefinitions(
					(AConcatenationPattern) rp, ptype, scope);
		case RECORD:
			return ARecordPatternAssistantTC.getDefinitions(
					(ARecordPattern) rp, ptype, scope);
		case SEQ:
			return ASeqPatternAssistantTC.getDefinitions((ASeqPattern) rp,
					ptype, scope);
		case SET:
			return ASetPatternAssistantTC.getDefinitions((ASetPattern) rp,
					ptype, scope);
		case TUPLE:
			return ATuplePatternAssistantTC.getDefinitions((ATuplePattern) rp,
					ptype, scope);
		case UNION:
			return AUnionPatternAssistantTC.getDefinitions((AUnionPattern) rp,
					ptype, scope);
		default:
			assert false : "PPatternAssistant.getDefinitions - should not hit this case";
			return null;
		}

	}

	public static void typeResolve(PPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		switch (pattern.kindPPattern()) {
		case CONCATENATION:
			if (pattern instanceof AConcatenationPattern) {
				AConcatenationPatternAssistantTC.typeResolve(
						(AConcatenationPattern) pattern, rootVisitor, question);
			}
			break;
		case EXPRESSION:
			if (pattern instanceof AExpressionPattern) {
				AExpressionPatternAssistant.typeResolve(
						(AExpressionPattern) pattern, rootVisitor, question);
			}
			break;
		case RECORD:
			if (pattern instanceof ARecordPattern) {
				ARecordPatternAssistantTC.typeResolve((ARecordPattern) pattern,
						rootVisitor, question);
			}
			break;
		case SEQ:
			if (pattern instanceof ASeqPattern) {
				ASeqPatternAssistantTC.typeResolve((ASeqPattern) pattern,
						rootVisitor, question);
			}
			break;
		case SET:
			if (pattern instanceof ASetPattern) {
				ASetPatternAssistantTC.typeResolve((ASetPattern) pattern,
						rootVisitor, question);
			}
			break;
		case TUPLE:
			if (pattern instanceof ATuplePattern) {
				ATuplePatternAssistantTC.typeResolve((ATuplePattern) pattern,
						rootVisitor, question);
			}
			break;
		case UNION:
			if (pattern instanceof AUnionPattern) {
				AUnionPatternAssistantTC.typeResolve((AUnionPattern) pattern,
						rootVisitor, question);
			}
			break;
		default:
			pattern.setResolved(true);
		}

	}

	public static void unResolve(PPattern pattern) {
		switch (pattern.kindPPattern()) {
		case CONCATENATION:
			if (pattern instanceof AConcatenationPattern) {
				AConcatenationPatternAssistantTC
						.unResolve((AConcatenationPattern) pattern);
			}
			break;
		case RECORD:
			if (pattern instanceof ARecordPattern) {
				ARecordPatternAssistantTC.unResolve((ARecordPattern) pattern);
			}
			break;
		case SEQ:
			if (pattern instanceof ASeqPattern) {
				ASeqPatternAssistantTC.unResolve((ASeqPattern) pattern);
			}
			break;
		case SET:
			if (pattern instanceof ASetPattern) {
				ASetPatternAssistantTC.unResolve((ASetPattern) pattern);
			}
			break;
		case TUPLE:
			if (pattern instanceof ATuplePattern) {
				ATuplePatternAssistantTC.unResolve((ATuplePattern) pattern);
			}
			break;
		case UNION:
			if (pattern instanceof AUnionPattern) {
				AUnionPatternAssistantTC.unResolve((AUnionPattern) pattern);
			}
			break;
		default:
			pattern.setResolved(false);
		}

	}

	// public static LexNameList getVariableNames(PPattern pattern) {
	// switch (pattern.kindPPattern()) {
	// case RECORD:
	// return ARecordPatternAssistant.getVariableNames((ARecordPattern)pattern);
	// case SEQ:
	// return ASeqPatternAssistant.getVariableNames((ASeqPattern)pattern);
	// case SET:
	// return ASetPatternAssistant.getVariableNames((ASetPattern)pattern);
	// case TUPLE:
	// return ATuplePatternAssistant.getVariableNames((ATuplePattern)pattern);
	// case UNION:
	// return AUnionPatternAssistant.getVariableNames((AUnionPattern)pattern);
	// default:
	// return getVariableNamesBaseCase(pattern);
	// }
	// }
	//
	// public static LexNameList getVariableNamesBaseCase(PPattern pattern)
	// {
	// return new LexNameList();
	// }

	public static PType getPossibleType(PPattern pattern) {
		switch (pattern.kindPPattern()) {
		case BOOLEAN:
			return new ABooleanBasicType(pattern.getLocation(), false);
		case CHARACTER:
			return new ACharBasicType(pattern.getLocation(), false);
		case CONCATENATION:
			return new ASeqSeqType(pattern.getLocation(), false,
					new AUnknownType(pattern.getLocation(), false), false);
		case EXPRESSION:
			return new AUnknownType(pattern.getLocation(), false);
		case IDENTIFIER:
			return new AUnknownType(pattern.getLocation(), false);
		case IGNORE:
			return new AUnknownType(pattern.getLocation(), false);
		case INTEGER:
			return SNumericBasicTypeAssistant.typeOf(
					((AIntegerPattern) pattern).getValue().value,
					pattern.getLocation());
		case NIL:
			return new AOptionalType(pattern.getLocation(), false,
					new AUnknownType(pattern.getLocation(), false));
		case QUOTE:
			return new AQuoteType(pattern.getLocation(), false,
					((AQuotePattern) pattern).getValue());
		case REAL:
			return new ARealNumericBasicType(pattern.getLocation(), false);
		case RECORD:
			return ((ARecordPattern) pattern).getType();
		case SEQ:
			return new ASeqSeqType(pattern.getLocation(), false,
					new AUnknownType(pattern.getLocation(), false), false);
		case SET:
			return new ASetType(pattern.getLocation(), false, new AUnknownType(
					pattern.getLocation(), false), true, false);
		case STRING:
			return new ASeqSeqType(pattern.getLocation(), false,
					new ACharBasicType(pattern.getLocation(), false), false);
		case TUPLE:
			ATuplePattern tupplePattern = (ATuplePattern) pattern;
			PTypeList list = new PTypeList();

			for (PPattern p : tupplePattern.getPlist()) {
				list.add(getPossibleType(p));
			}

			return list.getType(tupplePattern.getLocation());
		case UNION:
			AUnionPattern unionPattern = (AUnionPattern) pattern;
			PTypeSet set = new PTypeSet();

			set.add(getPossibleType(unionPattern.getLeft()));
			set.add(getPossibleType(unionPattern.getRight()));

			PType s = set.getType(unionPattern.getLocation());

			return PTypeAssistant.isUnknown(s) ? new ASetType(
					unionPattern.getLocation(), false, null, new AUnknownType(
							unionPattern.getLocation(), false), true, false)
					: s;
		}
		return null;
	}

	public static boolean matches(PPattern pattern, PType expType) {
		return TypeComparator.compatible(getPossibleType(pattern), expType);
	}

	public static Object getMatchingExpressionList(List<PPattern> pl) {
		List<PExp> list = new Vector<PExp>();

		for (PPattern p : pl) {
			list.add(getMatchingExpression(p));
		}

		return list;
	}

	public static <T extends PPattern> PExp getMatchingExpression(T p) {
		switch (p.kindNode()) {
		case ACCESS:
		case ACCESSSPECIFIER:
		case ALTERNATIVE:
		case ALTERNATIVESTM:
		case BIND:
		case BOOLEANCONST:
		case CASE:
		case CLAUSE:
		case DEFINITION:
		case EXP:
		case EXPORT:
		case EXPORTS:
		case ExternalDefined:
		case FIELD:
		case IMPORT:
		case IMPORTS:
		case MODIFIER:
		case MODULES:
		case MULTIPLEBIND:
		case OBJECTDESIGNATOR:
		case PAIR:
		case PATTERN: {
			return getExpression(p);
		}
		case PATTERNBIND:
		case STATEDESIGNATOR:
		case STM:
		case STMTALTERNATIVE:
		case TOKEN:
		case TRACECOREDEFINITION:
		case TRACEDEFINITION:
		case TYPE:

		default:
			// TODO: NOT IMPLEMENTED
			System.out
					.println("PPatternAssistantTC - getMatchingExpression - NOT IMPLEMENTED "
							+ p.kindNode());
			assert (false);
			return null;
		}
	}

	// Runtime mapping from Class<T extends PPattern> to a method taking T as
	// argument
	private static Map<Class<?>, Method> map;

	private static <T extends PPattern> void addNewPatternClass(Class<T> pc) {
		map.put(pc, getMethod("getExpression", pc));
	}

	private static Method getMethod(String s, Class<?> a) {
		try {
			Method declMethod = PPatternAssistantTC.class.getDeclaredMethod(
					"getExpression", a);
			declMethod.setAccessible(true);
			return declMethod;
		} catch (Exception e) {
			throw new RuntimeException("Could not find method: " + e);
		}
	}

	// Static initializer adding handled class to the map
	static {
		map = new HashMap<Class<?>, Method>();
		addNewPatternClass(AIdentifierPattern.class);
		addNewPatternClass(ABooleanPattern.class);
		addNewPatternClass(ARecordPattern.class);
		addNewPatternClass(ACharacterPattern.class);
		addNewPatternClass(AExpressionPattern.class);
		addNewPatternClass(AIgnorePattern.class);
		addNewPatternClass(AIntegerPattern.class);
	}

	// A boolean pattern should yield a boolean const expression
	@SuppressWarnings("unused")
	private static PExp getExpression(ABooleanPattern bp) {
		ABooleanConstExp res = new ABooleanConstExp(null, bp.getLocation(),
				bp.getValue());
		return res;
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(ACharacterPattern chr) {
		return new ACharLiteralExp(null, chr.getLocation(), chr.getValue());
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(AConcatenationPattern ccp) {
		LexToken op = new LexKeywordToken(VDMToken.TOKEN.CONCATENATE,
				ccp.getLocation());
		PExp le = getMatchingExpression(ccp.getLeft());
		PExp re = getMatchingExpression(ccp.getRight());
		return new ASeqConcatBinaryExp(null, ccp.getLocation(), le, op, re);
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(ADefPatternBind dpb) {
		return null;
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(AExpressionPattern eptrn) {
		return eptrn.getExp();
	}

	private static int var = 1;

	@SuppressWarnings("unused")
	private static PExp getExpression(AIgnorePattern iptrn) {

		LexNameToken any = new LexNameToken("", "any" + var++,
				iptrn.getLocation());
		return new AVariableExp(null, iptrn.getLocation(), any, "any");

	}

	@SuppressWarnings("unused")
	private static PExp getExpression(AIntegerPattern intptrn) {
		return new AIntLiteralExp(null, intptrn.getLocation(),
				intptrn.getValue());
	}

	// An Identifier should yield a variable expression.
	@SuppressWarnings("unused")
	private static PExp getExpression(AIdentifierPattern idp) {

		return new AVariableExp(null, idp.getLocation(), idp.getName(), "");
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(ARecordPattern ptrn) {
		List<PExp> list = new LinkedList<PExp>();

		for (PPattern p : ptrn.getPlist()) {
			list.add(getExpression(p));
		}

		// FIXME Type info here is set null (type and record type) correct?
		return new AMkTypeExp(null, ptrn.getLocation(), ptrn.getTypename(),
				list, null);
	}

	private static Random r = new Random();

	public static PExp getExpression(PPattern p) {
		// get class of p and lookup method to invoke
		Class<?> clz = p.getClass();
		Method m = map.get(clz);

		// If m==null the getExpression(T p) method, for T being the type in the
		// error message that we cannot handle, is not implemented yet. Do the
		// following:
		// 1) Implement the missing getExpresion taking the pattern type as
		// argument
		// 2) Add this method to the map in the static initializer above, e.g.
		// addNewPatternClass(T.class);
		// 3) Run the test again
		if (m == null)
			throw new RuntimeException("FixMe: "
					+ PPatternAssistantTC.class.getSimpleName()
					+ " cannot handle: " + p.getClass().getSimpleName()
					+ " yet.");

		// invoke the found method
		try {
			throw new Exception("TEsting");
			// return (PExp) m.invoke(null, clz.cast(p));
		} catch (Exception e) {
			// ooops something went wrong, check that the getExpression method
			// is static...
			byte[] rnd = new byte[3];
			r.nextBytes(rnd);
			BigInteger b = new BigInteger(rnd).nextProbablePrime();
			System.err.println(b.toString(16));
			e.printStackTrace();
			throw new RuntimeException(
					"FixMe: Method invocation failed unexpectedly: (see console for stacktrace near: \""
							+ b.toString(16) + "\")" + e, e);
		}
	}
}
