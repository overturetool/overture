package org.overture.typechecker.assistant.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to determine if a type has void type in it!
 * 
 * @author kel
 */

public class VoidExistanceChecker extends AnswerAdaptor<Boolean>
{
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public VoidExistanceChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		for (PType t : type.getTypes())
		{
			if (PTypeAssistantTC.isVoid(t))
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public Boolean caseAVoidType(AVoidType type) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseAVoidReturnType(AVoidReturnType type)
			throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean defaultPType(PType type) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}



}
