package org.overture.ast.types.assistants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.assistants.PTypeList;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.util.Utils;


public class AOperationTypeAssistantTC {

	public static AOperationType typeResolve(AOperationType ot,
			ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (ot.getResolved()) return ot; else { ot.setResolved(true); }

		try
		{
			List<PType> fixed = new ArrayList<PType>();

			for (PType type: ot.getParameters())
			{
				fixed.add(PTypeAssistantTC.typeResolve(type, root, rootVisitor, question));
			}

			ot.setParameters(fixed);
			ot.setResult(PTypeAssistantTC.typeResolve(ot.getResult(), root, rootVisitor, question));
			return ot;
		}
		catch (TypeCheckException e)
		{
			unResolve(ot);
			throw e;
		}		
	}
	
	public static void unResolve(AOperationType ot)
	{
		if (!ot.getResolved()) return; else { ot.setResolved(false); }

		for (PType type: ot.getParameters())
		{
			PTypeAssistantTC.unResolve(type);
		}

		PTypeAssistantTC.unResolve(ot.getResult());
	}

	public static AFunctionType getPreType(AOperationType type,
			AStateDefinition state, SClassDefinition classname,
			boolean isStatic) {
		
		if (state != null)
		{
			PTypeList params = new PTypeList();
			params.addAll((LinkedList<PType>) type.getParameters());
			params.add(AstFactory.newAUnresolvedType(state.getName()));
			return AstFactory.newAFunctionType(type.getLocation(), false,params, AstFactory.newABooleanBasicType(type.getLocation()));
		}
		else if (classname != null && !isStatic)
		{
			PTypeList params = new PTypeList();
			params.addAll(type.getParameters());
			params.add(AstFactory.newAUnresolvedType(classname.getName()));
			return AstFactory.newAFunctionType(type.getLocation(), false, params, AstFactory.newABooleanBasicType(type.getLocation()));
		}
		else
		{
			return AstFactory.newAFunctionType(type.getLocation(), false, (List<PType>) type.getParameters(), AstFactory.newABooleanBasicType(type.getLocation()));
		}
	}

	@SuppressWarnings("unchecked")
	public static AFunctionType getPostType(AOperationType type,
			AStateDefinition state, SClassDefinition classname,
			boolean isStatic) {
		
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
		}
		else if (classname != null && !isStatic)
		{
			AMapMapType map = 
					AstFactory.newAMapMapType(
							type.getLocation(),
							AstFactory.newASeqSeqType(type.getLocation(),AstFactory.newACharBasicType(type.getLocation())), 
							AstFactory.newAUnknownType(type.getLocation()));
			params.add(map);
			params.add(AstFactory.newAUnresolvedType(classname.getName()));
		}

		return AstFactory.newAFunctionType(type.getLocation(), false, params, AstFactory.newABooleanBasicType(type.getLocation()));
	}

	public static String toDisplay(AOperationType exptype) {
		List<PType> parameters = exptype.getParameters();
		String params = (parameters.isEmpty() ?
				"()" : Utils.listToString(parameters, " * "));
		return "(" + params + " ==> " + exptype.getResult() + ")";
	}

	public static boolean equals(AOperationType type, PType other) {
		other = PTypeAssistantTC.deBracket(other);

		if (!(other instanceof AOperationType))
		{
			return false;
		}

		AOperationType oother = (AOperationType)other;
		return (PTypeAssistantTC.equals(type.getResult(),oother.getResult()) &&
				PTypeAssistantTC.equals(type.getParameters(), oother.getParameters()));
	}

	public static boolean narrowerThan(AOperationType type,
			AAccessSpecifierAccessSpecifier accessSpecifier) {
		
		for (PType t: type.getParameters())
		{
			if (PTypeAssistantTC.narrowerThan(t, accessSpecifier))
			{
				return true;
			}
		}

		return PTypeAssistantTC.narrowerThan(type.getResult(),accessSpecifier);
	}

	

}
