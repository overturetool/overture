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
import org.overture.ast.expressions.ANilExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.expressions.ATupleExp;
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
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.AStringPattern;
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
import org.overturetool.vdmj.lex.LexBooleanToken;
import org.overturetool.vdmj.lex.LexCharacterToken;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexQuoteToken;
import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.lex.LexStringToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class PPatternAssistantTC extends PPatternAssistant
{

	public static List<PDefinition> getDefinitions(PPattern rp, PType ptype,
			NameScope scope)
	{
		switch (rp.kindPPattern())
		{
			case IDENTIFIER:
				AIdentifierPattern idPattern = (AIdentifierPattern) rp;
				List<PDefinition> defs = new ArrayList<PDefinition>();
				defs.add(new ALocalDefinition(idPattern.getLocation(), scope, false, null, null, ptype, false, idPattern.getName().clone()));
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
				return AConcatenationPatternAssistantTC.getDefinitions((AConcatenationPattern) rp, ptype, scope);
			case RECORD:
				return ARecordPatternAssistantTC.getDefinitions((ARecordPattern) rp, ptype, scope);
			case SEQ:
				return ASeqPatternAssistantTC.getDefinitions((ASeqPattern) rp, ptype, scope);
			case SET:
				return ASetPatternAssistantTC.getDefinitions((ASetPattern) rp, ptype, scope);
			case TUPLE:
				return ATuplePatternAssistantTC.getDefinitions((ATuplePattern) rp, ptype, scope);
			case UNION:
				return AUnionPatternAssistantTC.getDefinitions((AUnionPattern) rp, ptype, scope);
			default:
				assert false : "PPatternAssistant.getDefinitions - should not hit this case";
				return null;
		}

	}

	public static void typeResolve(PPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question)
	{
		switch (pattern.kindPPattern())
		{
			case CONCATENATION:
				if (pattern instanceof AConcatenationPattern)
				{
					AConcatenationPatternAssistantTC.typeResolve((AConcatenationPattern) pattern, rootVisitor, question);
				}
				break;
			case EXPRESSION:
				if (pattern instanceof AExpressionPattern)
				{
					AExpressionPatternAssistant.typeResolve((AExpressionPattern) pattern, rootVisitor, question);
				}
				break;
			case RECORD:
				if (pattern instanceof ARecordPattern)
				{
					ARecordPatternAssistantTC.typeResolve((ARecordPattern) pattern, rootVisitor, question);
				}
				break;
			case SEQ:
				if (pattern instanceof ASeqPattern)
				{
					ASeqPatternAssistantTC.typeResolve((ASeqPattern) pattern, rootVisitor, question);
				}
				break;
			case SET:
				if (pattern instanceof ASetPattern)
				{
					ASetPatternAssistantTC.typeResolve((ASetPattern) pattern, rootVisitor, question);
				}
				break;
			case TUPLE:
				if (pattern instanceof ATuplePattern)
				{
					ATuplePatternAssistantTC.typeResolve((ATuplePattern) pattern, rootVisitor, question);
				}
				break;
			case UNION:
				if (pattern instanceof AUnionPattern)
				{
					AUnionPatternAssistantTC.typeResolve((AUnionPattern) pattern, rootVisitor, question);
				}
				break;
			default:
				pattern.setResolved(true);
		}

	}

	public static void unResolve(PPattern pattern)
	{
		switch (pattern.kindPPattern())
		{
			case CONCATENATION:
				if (pattern instanceof AConcatenationPattern)
				{
					AConcatenationPatternAssistantTC.unResolve((AConcatenationPattern) pattern);
				}
				break;
			case RECORD:
				if (pattern instanceof ARecordPattern)
				{
					ARecordPatternAssistantTC.unResolve((ARecordPattern) pattern);
				}
				break;
			case SEQ:
				if (pattern instanceof ASeqPattern)
				{
					ASeqPatternAssistantTC.unResolve((ASeqPattern) pattern);
				}
				break;
			case SET:
				if (pattern instanceof ASetPattern)
				{
					ASetPatternAssistantTC.unResolve((ASetPattern) pattern);
				}
				break;
			case TUPLE:
				if (pattern instanceof ATuplePattern)
				{
					ATuplePatternAssistantTC.unResolve((ATuplePattern) pattern);
				}
				break;
			case UNION:
				if (pattern instanceof AUnionPattern)
				{
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

	public static PType getPossibleType(PPattern pattern)
	{
		switch (pattern.kindPPattern())
		{
			case BOOLEAN:
				return new ABooleanBasicType(pattern.getLocation(), false);
			case CHARACTER:
				return new ACharBasicType(pattern.getLocation(), false);
			case EXPRESSION:
				return new AUnknownType(pattern.getLocation(), false);
			case IDENTIFIER:
				return new AUnknownType(pattern.getLocation(), false);
			case IGNORE:
				return new AUnknownType(pattern.getLocation(), false);
			case INTEGER:
				return SNumericBasicTypeAssistant.typeOf(((AIntegerPattern) pattern).getValue().value, pattern.getLocation());
			case NIL:
				return new AOptionalType(pattern.getLocation(), false,null, new AUnknownType(pattern.getLocation(), false));
			case QUOTE:
				return new AQuoteType(pattern.getLocation(), false, ((AQuotePattern) pattern).getValue().clone());
			case REAL:
				return new ARealNumericBasicType(pattern.getLocation(), false);
			case RECORD:
				return ((ARecordPattern) pattern).getType();
			case SET:
			{
				ASetType t = new ASetType(pattern.getLocation(), false,  true, false);
				t.setSetof(new AUnknownType(pattern.getLocation(), false));
				return t;
			}
			case SEQ:
			case STRING:
			case CONCATENATION:
			{
				ASeqSeqType t = new ASeqSeqType(pattern.getLocation(), false, false);
				t.setSeqof( new AUnknownType(pattern.getLocation(), false));
				return t;
			}
			case TUPLE:
				ATuplePattern tupplePattern = (ATuplePattern) pattern;
				PTypeList list = new PTypeList();

				for (PPattern p : tupplePattern.getPlist())
				{
					list.add(getPossibleType(p));
				}

				return list.getType(tupplePattern.getLocation());
			case UNION:
				AUnionPattern unionPattern = (AUnionPattern) pattern;
				PTypeSet set = new PTypeSet();

				set.add(getPossibleType(unionPattern.getLeft()));
				set.add(getPossibleType(unionPattern.getRight()));

				PType s = set.getType(unionPattern.getLocation());

				return PTypeAssistant.isUnknown(s) ? new ASetType(unionPattern.getLocation(), false, null, new AUnknownType(unionPattern.getLocation(), false), true, false)
						: s;
		}
		return null;
	}

	public static boolean matches(PPattern pattern, PType expType)
	{
		return TypeComparator.compatible(getPossibleType(pattern), expType);
	}

	public static List<PExp> getMatchingExpressionList(List<PPattern> pl)
	{
		List<PExp> list = new Vector<PExp>();

		for (PPattern p : pl)
		{
			list.add(getMatchingExpression(p));
		}

		return list;
	}

	public static <T extends PPattern> PExp getMatchingExpression(T p)
	{
		switch (p.kindNode())
		{
			case ACCESS:
			case ACCESSSPECIFIER:
			case ALTERNATIVE:
			case ALTERNATIVESTM:
			case BIND:
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
			case PATTERN:
			{
				if (p instanceof AIgnorePattern)
				{	
					return getExpression((AIgnorePattern)p);
				}
				
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
				System.out.println("PPatternAssistantTC - getMatchingExpression - NOT IMPLEMENTED "
						+ p.kindNode());
				assert (false);
				return null;
		}
	}

	// Runtime mapping from Class<T extends PPattern> to a method taking T as
	// argument
	private static Map<Class<?>, Method> map;

	private static <T extends PPattern> void addNewPatternClass(Class<T> pc)
	{
		map.put(pc, getMethod("getExpression", PPatternAssistantTC.class, pc));
	}

	private static Method getMethod(String s, Class<?> base,
			Class<?>... argTypes)
	{
		try
		{
			Method declMethod = base.getDeclaredMethod(s, argTypes);
			declMethod.setAccessible(true);
			return declMethod;
		} catch (Exception e)
		{
			throw new RuntimeException("Could not find method: " + e);
		}
	}

	// Static initializer adding handled class to the map
	static
	{
		map = new HashMap<Class<?>, Method>();
		addNewPatternClass(AIdentifierPattern.class);
		addNewPatternClass(ABooleanPattern.class);
		addNewPatternClass(ARecordPattern.class);
		addNewPatternClass(ACharacterPattern.class);
		addNewPatternClass(AExpressionPattern.class);
		addNewPatternClass(AIgnorePattern.class);
		addNewPatternClass(AIntegerPattern.class);
		addNewPatternClass(AConcatenationPattern.class);
		addNewPatternClass(ANilPattern.class);
		addNewPatternClass(AQuotePattern.class);
		addNewPatternClass(ASeqPattern.class);
		addNewPatternClass(ASetPattern.class);
		addNewPatternClass(ARealPattern.class);
		addNewPatternClass(AStringPattern.class);
		addNewPatternClass(ATuplePattern.class);
		addNewPatternClass(AUnionPattern.class);

	}

	private static <T> T typeSafeClone(T o, Class<T> clz)

	{
		Method cloneMethod = getMethod("clone", clz);
		try
		{
			return clz.cast(cloneMethod.invoke(o));
		} catch (Exception e)
		{
			throw new RuntimeException("Not cloneable ast-node: "
					+ o.getClass().getSimpleName());
		}
	}

	// A boolean pattern should yield a boolean const expression
	@SuppressWarnings("unused")
	private static PExp getExpression(ABooleanPattern bp)
	{
		LexBooleanToken tok = bp.getValue();
		ABooleanConstExp res = new ABooleanConstExp(null, bp.getLocation(), typeSafeClone(tok, LexBooleanToken.class));
		return res;
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(ACharacterPattern chr)
	{
		LexCharacterToken v = chr.getValue();
		return new ACharLiteralExp(null, chr.getLocation(), typeSafeClone(v, LexCharacterToken.class));
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(AConcatenationPattern ccp)
	{
		LexToken op = new LexKeywordToken(VDMToken.CONCATENATE, ccp.getLocation());
		PExp le = getMatchingExpression(ccp.getLeft());
		PExp re = getMatchingExpression(ccp.getRight());
		return new ASeqConcatBinaryExp(null, ccp.getLocation(), le, op, re);
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(ADefPatternBind dpb)
	{
		return null;
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(AExpressionPattern eptrn)
	{
		return eptrn.getExp();
	}

	// An Identifier should yield a variable expression.
	@SuppressWarnings("unused")
	private static PExp getExpression(AIdentifierPattern idp)
	{

		LexNameToken name = idp.getName();
		LexLocation loc = idp.getLocation().clone();
		return new AVariableExp(typeSafeClone(loc, LexLocation.class), typeSafeClone(name, LexNameToken.class), name != null ? name.getName()
				: "");
	}

	private static int var = 1;

	@SuppressWarnings("unused")
	private static PExp getExpression(AIgnorePattern iptrn)
	{
		LexNameToken any = new LexNameToken("", "any" + var++, typeSafeClone(iptrn.getLocation(), LexLocation.class));
		return new AVariableExp(typeSafeClone(iptrn.getLocation(), LexLocation.class), any, any.getName());

	}

	@SuppressWarnings("unused")
	private static PExp getExpression(AIntegerPattern intptrn)
	{
		return new AIntLiteralExp(null, intptrn.getLocation().clone(), typeSafeClone(intptrn.getValue(), LexIntegerToken.class));
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(ANilPattern np)
	{
		return new ANilExp(null, np.getLocation());
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(AQuotePattern qp)
	{
		LexQuoteToken v = qp.getValue();
		return new AQuoteLiteralExp(null, qp.getLocation(), typeSafeClone(v, LexQuoteToken.class));
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(ARealPattern rp)
	{
		LexRealToken v = rp.getValue();
		return new ARealLiteralExp(null, rp.getLocation(), typeSafeClone(v, LexRealToken.class));
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(ARecordPattern ptrn)
	{
		List<PExp> list = new LinkedList<PExp>();

		for (PPattern p : ptrn.getPlist())
		{
			list.add(getMatchingExpression(p));
		}

		// FIXME Consider Type info here that is set to null (type and record
		// type) is that correct?
		LexNameToken tpName = ptrn.getTypename();
		return new AMkTypeExp(ptrn.getLocation(), typeSafeClone(tpName, LexNameToken.class), list);
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(ASeqPattern seqp)
	{
		return new ASeqEnumSeqExp(seqp.getLocation(), getMatchingExpressionList(seqp.getPlist()));

	}

	@SuppressWarnings("unused")
	private static PExp getExpression(ASetPattern sp)
	{
		return new ASetEnumSetExp(sp.getLocation(), getMatchingExpressionList(sp.getPlist()));
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(AStringPattern sp)
	{
		LexStringToken v = sp.getValue();
		return new AStringLiteralExp(null, sp.getLocation(), typeSafeClone(v, LexStringToken.class));
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(ATuplePattern tp)
	{
		return new ATupleExp(tp.getLocation(), getMatchingExpressionList(tp.getPlist()));
	}

	@SuppressWarnings("unused")
	private static PExp getExpression(AUnionPattern up)
	{
		LexToken op = new LexKeywordToken(VDMToken.UNION, up.getLocation());
		return new ASetUnionBinaryExp(null, up.getLocation(), getMatchingExpression(up.getLeft()), op, getMatchingExpression(up.getRight()));

	}

	private static Random r = new Random();

	public static PExp getExpression(PPattern p)
	{
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
		try
		{
			return (PExp) m.invoke(null, clz.cast(p));
		} catch (Exception e)
		{
			// ooops something went wrong, check that the getExpression method
			// is static...
			byte[] rnd = new byte[3];
			r.nextBytes(rnd);
			BigInteger b = new BigInteger(1, rnd).nextProbablePrime();
			System.err.println(b.toString(16));
			e.printStackTrace();
			throw new RuntimeException("FixMe: Method invocation failed unexpectedly: (see console for stacktrace near: \""
					+ b.toString(16) + "\")" + e, e);
		}
	}
}
