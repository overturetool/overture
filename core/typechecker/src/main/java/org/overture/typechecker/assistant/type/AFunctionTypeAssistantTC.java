package org.overture.typechecker.assistant.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AFunctionTypeAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AFunctionTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}


	@SuppressWarnings("unchecked")
	public static AFunctionType getCurriedPreType(AFunctionType t,
			Boolean isCurried)
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
	public static AFunctionType getPreType(AFunctionType t)
	{
		AFunctionType type = AstFactory.newAFunctionType(t.getLocation(), false, t.getParameters(), AstFactory.newABooleanBasicType(t.getLocation()));
		type.setDefinitions((List<PDefinition>) t.getDefinitions().clone());
		return type;
	}

	public static AFunctionType getCurriedPostType(AFunctionType type,
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

	public static AFunctionType getPostType(AFunctionType t)
	{
		List<PType> params = new PTypeList();
		params.addAll((List<PType>) t.getParameters());
		params.add((PType) t.getResult());
		AFunctionType type = AstFactory.newAFunctionType(t.getLocation(), false, params, AstFactory.newABooleanBasicType(t.getLocation()));
		type.setDefinitions(t.getDefinitions());
		return type;
	}

	public static PType polymorph(AFunctionType type, ILexNameToken pname,
			PType actualType)
	{

		List<PType> polyparams = new Vector<PType>();

		for (PType ptype : type.getParameters())
		{
			polyparams.add(PTypeAssistantTC.polymorph(ptype, pname, actualType));
		}

		PType polyresult = PTypeAssistantTC.polymorph(type.getResult(), pname, actualType);
		AFunctionType ftype = AstFactory.newAFunctionType(type.getLocation(), false, polyparams, polyresult);
		ftype.setDefinitions(type.getDefinitions());
		return ftype;

	}

	public static int hashCode(AFunctionType type)
	{
		return PTypeAssistantTC.hashCode(type.getParameters())
				+ PTypeAssistantTC.hashCode(type.getResult());
	}

}
