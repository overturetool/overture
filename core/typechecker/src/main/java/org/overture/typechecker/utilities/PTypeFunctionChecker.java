package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to check if a node is a function.
 * 
 * @author kel
 */
public class PTypeFunctionChecker extends AnswerAdaptor<Boolean>
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public PTypeFunctionChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Boolean caseABracketType(ABracketType node) throws AnalysisException
	{
		return node.getType().apply(THIS);
	}
	
	@Override
	public Boolean caseAFunctionType(AFunctionType node)
			throws AnalysisException
	{
		return true;
	}	

	@Override
	public Boolean defaultSInvariantType(SInvariantType node)
			throws AnalysisException
	{
		if (node instanceof ANamedInvariantType)
			{
				if (node.getOpaque()) return false;
				return ((ANamedInvariantType) node).getType().apply(THIS); //PTypeAssistantTC.isFunction(type.getType());
			}
		//FIXME:Added code from gkanos in order to return a value; I returned the default one.
		else
		{
			return false;
		}
	}
	
	@Override
	public Boolean caseAOptionalType(AOptionalType node)
			throws AnalysisException
	{
		
		
		return node.getType().apply(THIS);
	}
	
	@Override
	public Boolean caseAUnionType(AUnionType node) throws AnalysisException
	{
		
		return af.createAUnionTypeAssistant().getFunction(node) != null;
	}
	
	@Override
	public Boolean caseAUnknownType(AUnknownType node) throws AnalysisException
	{
		
		return true;
	}
	@Override
	public Boolean defaultPType(PType node) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node)
	{
		assert false : "should not happen";
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node)
	{
		assert false : "should not happen";
		return null;
	}
}
