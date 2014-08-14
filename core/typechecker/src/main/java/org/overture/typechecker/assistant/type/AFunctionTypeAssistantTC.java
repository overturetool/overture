package org.overture.typechecker.assistant.type;

import java.util.List;

import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AFunctionTypeAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public AFunctionTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@SuppressWarnings("unchecked")
	public AFunctionType getCurriedPreType(AFunctionType t, Boolean isCurried)
	{

		if (isCurried && t.getResult() instanceof AFunctionType)
		{
			AFunctionType ft = (AFunctionType) t.getResult();
			AFunctionType type = AstFactory.newAFunctionType(t.getLocation(), false, t.getParameters(), getCurriedPreType(ft, isCurried));
			type.setDefinitions((List<PDefinition>) t.getDefinitions().clone());
			return type;
		} else
		{
			return getPreType(t);
		}
	}

	@SuppressWarnings("unchecked")
	public AFunctionType getPreType(AFunctionType t)
	{
		AFunctionType type = AstFactory.newAFunctionType(t.getLocation(), false, t.getParameters(), AstFactory.newABooleanBasicType(t.getLocation()));
		type.setDefinitions((List<PDefinition>) t.getDefinitions().clone());
		return type;
	}

	public AFunctionType getCurriedPostType(AFunctionType type,
			Boolean isCurried)
	{

		if (isCurried && type.getResult() instanceof AFunctionType)
		{
			AFunctionType ft = (AFunctionType) type.getResult();
			AFunctionType t = AstFactory.newAFunctionType(type.getLocation(), false, type.getParameters(), getCurriedPostType(ft, isCurried));
			t.setDefinitions(type.getDefinitions());
			return t;
		} else
		{
			return getPostType(type);
		}
	}

	public AFunctionType getPostType(AFunctionType t)
	{
		List<PType> params = new PTypeList();
		params.addAll((List<PType>) t.getParameters());
		params.add((PType) t.getResult());
		AFunctionType type = AstFactory.newAFunctionType(t.getLocation(), false, params, AstFactory.newABooleanBasicType(t.getLocation()));
		type.setDefinitions(t.getDefinitions());
		return type;
	}

}
