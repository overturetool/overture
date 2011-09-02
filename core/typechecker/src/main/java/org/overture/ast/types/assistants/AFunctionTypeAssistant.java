package org.overture.ast.types.assistants;

import java.util.ArrayList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.util.Utils;


public class AFunctionTypeAssistant {

	public static AFunctionType typeResolve(AFunctionType ft,
			ATypeDefinition root, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) {

		if (ft.getResolved())
			return ft;
		else {
			ft.setResolved(true);
		}

		try {
			List<PType> fixed = new ArrayList<PType>();

			for (PType type : ft.getParameters()) {
				fixed.add(PTypeAssistant.typeResolve(type, root,rootVisitor,question).clone());
			}

			ft.setParameters(fixed);
			ft.setResult(PTypeAssistant.typeResolve(ft.getResult(), root, rootVisitor, question).clone());
			return ft;
		} catch (TypeCheckException e) {
			unResolve(ft);
			throw e;
		}
	}
	
	public static void unResolve(AFunctionType ft)
	{
		if (!ft.getResolved()) return; else { ft.setResolved(false); }

		for (PType type: ft.getParameters())
		{
			PTypeAssistant.unResolve(type);
		}

		PTypeAssistant.unResolve(ft.getResult());
	}

	public static AFunctionType getCurriedPreType(AFunctionType t,
			Boolean isCurried) {
		
		if (isCurried && t.getResult() instanceof AFunctionType)
		{
			AFunctionType ft = (AFunctionType)t.getResult();
			AFunctionType type = new AFunctionType(t.getLocation(),
				false, false, (List<PType>) t.getParameters().clone(), getCurriedPreType(ft,isCurried));
			type.setDefinitions((List<PDefinition>)t.getDefinitions().clone());
			return type;
		}
		else
		{
			return getPreType(t);
		}
	}

	public static AFunctionType getPreType(AFunctionType t) {
			AFunctionType type =
				new AFunctionType(t.getLocation().clone(), false,false, (List<PType>) t.getParameters().clone(), new ABooleanBasicType(t.getLocation().clone(),false));
			type.setDefinitions((List<PDefinition>)t.getDefinitions().clone());
			return type;
	}

	public static AFunctionType getCurriedPostType(AFunctionType type,
			Boolean isCurried) {
		
		if (isCurried && type.getResult() instanceof AFunctionType)
		{
			AFunctionType ft = (AFunctionType)type.getResult().clone();
			AFunctionType t = new AFunctionType(type.getLocation(),false,
				false, (List<PType>)type.getParameters().clone(), getCurriedPostType(ft,isCurried));
			t.setDefinitions(type.getDefinitions());
			return t;
		}
		else
		{
			return getPostType(type);
		}
	}

	public static AFunctionType getPostType(AFunctionType t) {
		List<PType> params = new PTypeList();
		params.addAll((List<PType>)t.getParameters().clone());
		params.add((PType)t.getResult().clone());
		AFunctionType type =
			new AFunctionType(t.getLocation(),false, false, params, new ABooleanBasicType(t.getLocation(),false));
		type.setDefinitions(t.getDefinitions());
		return type;
	}

	public static String toDisplay(AFunctionType exptype) {
		List<PType> parameters = exptype.getParameters();
		String params = (parameters.isEmpty() ?
				"()" : Utils.listToString(parameters, " * "));
		return "(" + params + (exptype.getPartial() ? " -> " : " +> ") + exptype.getResult() + ")";
	}

	public static boolean equals(AFunctionType type, PType other) {
		other = PTypeAssistant.deBracket(other);

		if (!(other instanceof AFunctionType))
		{
			return false;
		}

		AFunctionType fo = (AFunctionType)other;
		return (type.getPartial() == fo.getPartial() &&
				PTypeAssistant.equals(type.getResult(),fo.getResult()) &&
				PTypeAssistant.equals(type.getParameters(),fo.getParameters()));
	}

	public static boolean narrowerThan(AFunctionType type,
			AAccessSpecifierAccessSpecifier accessSpecifier) {
		
		for (PType t: type.getParameters())
		{
			if (PTypeAssistant.narrowerThan(t, accessSpecifier))
			{
				return true;
			}
		}

		return  PTypeAssistant.narrowerThan(type.getResult(),accessSpecifier);
	}

}
