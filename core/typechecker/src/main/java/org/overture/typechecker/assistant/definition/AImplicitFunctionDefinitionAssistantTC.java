package org.overture.typechecker.assistant.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.AFunctionTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class AImplicitFunctionDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AImplicitFunctionDefinitionAssistantTC(
			ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static AFunctionType getType(AImplicitFunctionDefinition impdef,
			List<PType> actualTypes)
	{
		Iterator<PType> ti = actualTypes.iterator();
		AFunctionType ftype = (AFunctionType) impdef.getType();

		for (ILexNameToken pname : impdef.getTypeParams())
		{
			PType ptype = ti.next();
			// AFunctionTypeAssistent.
			ftype = (AFunctionType) PTypeAssistantTC.polymorph(ftype, pname, ptype);
		}

		return ftype;
	}

	public static List<PDefinition> getTypeParamDefinitions(
			AImplicitFunctionDefinition node)
	{

		List<PDefinition> defs = new ArrayList<PDefinition>();

		for (ILexNameToken pname : node.getTypeParams())
		{
			PDefinition p = AstFactory.newALocalDefinition(pname.getLocation(), pname.clone(), NameScope.NAMES, AstFactory.newAParameterType(pname.clone()));
			// new ALocalDefinition(
			// pname.location, NameScope.NAMES,false,null, null, new
			// AParameterType(null,false,null,pname.clone()),false,pname.clone());

			af.createPDefinitionAssistant().markUsed(p);
			defs.add(p);
		}

		return defs;
	}

	public static AExplicitFunctionDefinition getPostDefinition(
			AImplicitFunctionDefinition d)
	{

		List<List<PPattern>> parameters = getParamPatternList(d);
		parameters.get(0).add(d.getResult().getPattern().clone());

		@SuppressWarnings("unchecked")
		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPostName(d.getPostcondition().getLocation()), NameScope.GLOBAL, (List<ILexNameToken>) d.getTypeParams().clone(), AFunctionTypeAssistantTC.getPostType((AFunctionType) d.getType()), parameters, d.getPostcondition(), null, null, false, null);

		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	public static AExplicitFunctionDefinition getPreDefinition(
			AImplicitFunctionDefinition d)
	{

		@SuppressWarnings("unchecked")
		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPreName(d.getPrecondition().getLocation()), NameScope.GLOBAL, (List<ILexNameToken>) d.getTypeParams().clone(), AFunctionTypeAssistantTC.getPreType((AFunctionType) d.getType()), getParamPatternList(d), d.getPrecondition(), null, null, false, null);

		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	@SuppressWarnings("unchecked")
	public static List<List<PPattern>> getParamPatternList(
			AImplicitFunctionDefinition d)
	{

		List<List<PPattern>> parameters = new ArrayList<List<PPattern>>();
		List<PPattern> plist = new ArrayList<PPattern>();

		for (APatternListTypePair pl : d.getParamPatterns())
		{
			plist.addAll((Collection<PPattern>) pl.getPatterns().clone());
		}

		parameters.add(plist);
		return parameters;
	}
}
