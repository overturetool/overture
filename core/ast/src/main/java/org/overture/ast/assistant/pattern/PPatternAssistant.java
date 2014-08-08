package org.overture.ast.assistant.pattern;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.PPattern;

public class PPatternAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public PPatternAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

	public LexNameList getAllVariableNames(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getAllVariableNameLocator());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	// FIXME Delete commented code
	// public static LexNameList getAllVariableNames(AConcatenationPattern pattern)
	// throws InvocationAssistantException
	// {
	// LexNameList list = new LexNameList();
	//
	// list.addAll(PPatternAssistant.getAllVariableNames(pattern.getLeft()));
	// list.addAll(PPatternAssistant.getAllVariableNames(pattern.getRight()));
	//
	// return list;
	// }
	//
	// public static LexNameList getAllVariableNames(AIdentifierPattern pattern)
	// {
	// LexNameList list = new LexNameList();
	// list.add(pattern.getName());
	// return list;
	// }
	//
	// public static LexNameList getAllVariableNames(ARecordPattern pattern)
	// throws InvocationAssistantException
	// {
	// LexNameList list = new LexNameList();
	//
	// for (PPattern p : pattern.getPlist())
	// {
	// list.addAll(PPatternAssistant.getAllVariableNames(p));
	// }
	//
	// return list;
	//
	// }
	//
	// public static LexNameList getAllVariableNames(ASeqPattern pattern)
	// throws InvocationAssistantException
	// {
	// LexNameList list = new LexNameList();
	//
	// for (PPattern p : pattern.getPlist())
	// {
	// list.addAll(PPatternAssistant.getAllVariableNames(p));
	// }
	//
	// return list;
	// }
	//
	// public static LexNameList getAllVariableNames(ASetPattern pattern)
	// throws InvocationAssistantException
	// {
	// LexNameList list = new LexNameList();
	//
	// for (PPattern p : pattern.getPlist())
	// {
	// list.addAll(PPatternAssistant.getAllVariableNames(p));
	// }
	//
	// return list;
	// }
	//
	// public static LexNameList getAllVariableNames(ATuplePattern pattern)
	// throws InvocationAssistantException
	// {
	// LexNameList list = new LexNameList();
	//
	// for (PPattern p : pattern.getPlist())
	// {
	// list.addAll(PPatternAssistant.getAllVariableNames(p));
	// }
	//
	// return list;
	// }
	//
	// public static LexNameList getAllVariableNames(AUnionPattern pattern)
	// throws InvocationAssistantException
	// {
	// LexNameList list = new LexNameList();
	//
	// list.addAll(PPatternAssistant.getAllVariableNames(pattern.getLeft()));
	// list.addAll(PPatternAssistant.getAllVariableNames(pattern.getRight()));
	//
	// return list;
	// }
	//
	// /**
	// * This method should only be called by subclasses of PPattern. For other classes call
	// * {@link PPatternAssistant#getVariableNames(PPattern)}.
	// *
	// * @param pattern
	// * @return
	// * @throws InvocationAssistantException
	// */
	// public static LexNameList getAllVariableNames(PPattern pattern)
	// throws InvocationAssistantException
	// {
	// try
	// {
	// return (LexNameList) invokePreciseMethod(af.createPPatternAssistant(), "getAllVariableNames", pattern);
	// } catch (InvocationAssistantNotFoundException ianfe)
	// {
	// /*
	// * Default case is to return a new LexNameList, which corresponds to a InvocationAssistantException with no
	// * embedded cause. However, if there is an embedded cause in the exception, then it's something more complex
	// * than not being able to find a specific method, so we just re-throw that.
	// */
	// return new LexNameList();
	// }
	// }

	public LexNameList getVariableNames(PPattern pattern)
	{
		return af.createPPatternAssistant().getVariableNamesBaseCase(pattern);
	}

	public LexNameList getVariableNamesBaseCase(PPattern pattern)
	{
		Set<ILexNameToken> set = new HashSet<ILexNameToken>();
		set.addAll(af.createPPatternAssistant().getAllVariableNames(pattern));
		LexNameList list = new LexNameList();
		list.addAll(set);
		return list;
	}

}
