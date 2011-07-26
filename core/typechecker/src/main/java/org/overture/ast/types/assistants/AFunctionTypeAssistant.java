package org.overture.ast.types.assistants;

import java.util.ArrayList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;



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
				fixed.add(PTypeAssistant.typeResolve(type, root,rootVisitor,question));
			}

			ft.setParameters(fixed);
			ft.setResult(PTypeAssistant.typeResolve(ft.getResult(), root, rootVisitor, question));
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
				false, false, t.getParameters(), getCurriedPreType(ft,isCurried));
			type.setDefinitions(t.getDefinitions());
			return type;
		}
		else
		{
			return getPreType(t);
		}
	}

	public static AFunctionType getPreType(AFunctionType t) {
			AFunctionType type =
				new AFunctionType(t.getLocation(), false,false, t.getParameters(), new ABooleanBasicType(t.getLocation(),false));
			type.setDefinitions(t.getDefinitions());
			return type;
	}

	public static AFunctionType getCurriedPostType(AFunctionType type,
			Boolean isCurried) {
		
		if (isCurried && type.getResult() instanceof AFunctionType)
		{
			AFunctionType ft = (AFunctionType)type.getResult();
			AFunctionType t = new AFunctionType(type.getLocation(),false,
				false, type.getParameters(), getCurriedPostType(ft,isCurried));
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
		params.addAll(t.getParameters());
		params.add(t.getResult());
		AFunctionType type =
			new AFunctionType(t.getLocation(),false, false, params, new ABooleanBasicType(t.getLocation(),false));
		type.setDefinitions(t.getDefinitions());
		return type;
	}

}
