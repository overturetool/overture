package org.overture.typechecker.assistant.type;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AOperationTypeAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public AOperationTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public AFunctionType getPreType(AOperationType type,
			AStateDefinition state, SClassDefinition classname, boolean isStatic)
	{

		if (state != null)
		{
			PTypeList params = new PTypeList();
			params.addAll((LinkedList<PType>) type.getParameters());
			params.add(AstFactory.newAUnresolvedType(state.getName()));
			return AstFactory.newAFunctionType(type.getLocation(), false, params, AstFactory.newABooleanBasicType(type.getLocation()));
		} else if (classname != null && !isStatic)
		{
			PTypeList params = new PTypeList();
			params.addAll(type.getParameters());
			params.add(AstFactory.newAUnresolvedType(classname.getName()));
			return AstFactory.newAFunctionType(type.getLocation(), false, params, AstFactory.newABooleanBasicType(type.getLocation()));
		} else
		{
			return AstFactory.newAFunctionType(type.getLocation(), false, (List<PType>) type.getParameters(), AstFactory.newABooleanBasicType(type.getLocation()));
		}
	}

	@SuppressWarnings("unchecked")
	public AFunctionType getPostType(AOperationType type,
			AStateDefinition state, SClassDefinition classname, boolean isStatic)
	{

		PTypeList params = new PTypeList();
		params.addAll((LinkedList<PType>) type.getParameters().clone());

		if (!(type.getResult() instanceof AVoidType))
		{
			params.add(type.getResult().clone());
		}

		if (state != null)
		{
			params.add(AstFactory.newAUnresolvedType(state.getName()));
			params.add(AstFactory.newAUnresolvedType(state.getName()));
		} else if (classname != null)
		{
			AMapMapType map = AstFactory.newAMapMapType(type.getLocation(), AstFactory.newASeqSeqType(type.getLocation(), AstFactory.newACharBasicType(type.getLocation())), AstFactory.newAUnknownType(type.getLocation()));
			params.add(map);

			if (!isStatic)
			{
				params.add(AstFactory.newAUnresolvedType(classname.getName()));
			}
		}

		return AstFactory.newAFunctionType(type.getLocation(), false, params, AstFactory.newABooleanBasicType(type.getLocation()));
	}

}
