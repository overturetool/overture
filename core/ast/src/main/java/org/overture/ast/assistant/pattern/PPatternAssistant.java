package org.overture.ast.assistant.pattern;

import java.util.HashSet;
import java.util.Set;

import static org.overture.ast.assistant.InvocationAssistant.invokePreciseMethod;

import org.overture.ast.assistant.InvocationAssistantException;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;

public class PPatternAssistant {

	public static LexNameList getAllVariableNames(AConcatenationPattern pattern) throws InvocationAssistantException {
		LexNameList list = new LexNameList();
	
		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getLeft()));
		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getRight()));
	
		return list;
	}
	
	public static LexNameList getAllVariableNames(AIdentifierPattern pattern) {
		LexNameList list = new LexNameList();
		list.add(pattern.getName()); 
		return list;
	}

	public static LexNameList getAllVariableNames(AIgnorePattern pattern) {
		return new LexNameList();
	}

	public static LexNameList getAllVariableNames(AMapPattern pattern) {
		return new LexNameList();
	}
	
	public static LexNameList getAllVariableNames(ARecordPattern pattern) throws InvocationAssistantException {
		LexNameList list = new LexNameList();
	
		for (PPattern p: pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getAllVariableNames(p));
		}
	
		return list;
		
	}
	
	public static LexNameList getAllVariableNames(ASeqPattern pattern) throws InvocationAssistantException {
		LexNameList list = new LexNameList();
	
		for (PPattern p: pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getAllVariableNames(p));
		}
	
		return list;
	}

	public static LexNameList getAllVariableNames(ASetPattern pattern) throws InvocationAssistantException {
		LexNameList list = new LexNameList();
	
		for (PPattern p: pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getAllVariableNames(p));
		}
	
		return list;
	}

	public static LexNameList getAllVariableNames(ATuplePattern pattern) throws InvocationAssistantException {
		LexNameList list = new LexNameList();
	
		for (PPattern p: pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getAllVariableNames(p));
		}
	
		return list;
	}

	public static LexNameList getAllVariableNames(AUnionPattern pattern) throws InvocationAssistantException {
		LexNameList list = new LexNameList();
	
		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getLeft()));
		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getRight()));
	
		return list;
	}

	/**
	 * This method should only be called by subclasses of PPattern. For other classes
	 * call {@link PPatternAssistant#getVariableNames(PPattern)}.
	 * @param pattern
	 * @return
	 * @throws InvocationAssistantException 
	 */
	public static LexNameList getAllVariableNames(PPattern pattern) throws InvocationAssistantException {
		return (LexNameList)invokePreciseMethod(new PPatternAssistant(), "getAllVariableNames", pattern);
	}

	public static LexNameList getVariableNames(PPattern pattern) throws InvocationAssistantException {		
		return getVariableNamesBaseCase(pattern);
	}

	private static LexNameList getVariableNamesBaseCase(PPattern pattern) throws InvocationAssistantException {
		Set<ILexNameToken> set = new HashSet<ILexNameToken>();
		set.addAll(getAllVariableNames(pattern));
		LexNameList list = new LexNameList();
		list.addAll(set);
		return list;
	}

	

}
