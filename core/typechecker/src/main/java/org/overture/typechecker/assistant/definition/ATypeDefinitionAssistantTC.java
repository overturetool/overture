package org.overture.typechecker.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ATypeDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATypeDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static List<PDefinition> getDefinitions(ATypeDefinition d)
//	{
//		List<PDefinition> defs = new Vector<PDefinition>();
//		defs.add(d);
//
//		if (d.getInvdef() != null)
//		{
//			defs.add(d.getInvdef());
//		}
//
//		return defs;
//	}

//	public static void typeResolve(ATypeDefinition d,
//			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
//			TypeCheckInfo question) throws AnalysisException
//	{
//		try
//		{
//			d.setInfinite(false);
//			d.setInvType((SInvariantType) PTypeAssistantTC.typeResolve((SInvariantType) d.getInvType(), d, rootVisitor, question));
//
//			if (d.getInfinite())
//			{
//				TypeCheckerErrors.report(3050, "Type '" + d.getName()
//						+ "' is infinite", d.getLocation(), d);
//			}
//
//			// set type before in case the invdef uses a type defined in this one
//			d.setType(d.getInvType());
//
//			if (d.getInvdef() != null)
//			{
//				PDefinitionAssistantTC.typeResolve(d.getInvdef(), rootVisitor, question);
//				PPatternAssistantTC.typeResolve(d.getInvPattern(), rootVisitor, question);
//			}
//
//			d.setType(d.getInvType());
//		} catch (TypeCheckException e)
//		{
//			PTypeAssistantTC.unResolve(d.getInvType());
//			throw e;
//		}
//	}

//	public static void implicitDefinitions(ATypeDefinition d, Environment env)
//	{
//		if (d.getInvPattern() != null)
//		{
//			d.setInvdef(getInvDefinition(d));
//			d.getInvType().setInvDef(d.getInvdef());
//		} else
//		{
//			d.setInvdef(null);
//		}
//
//	}

	public AExplicitFunctionDefinition getInvDefinition(
			ATypeDefinition d)
	{

		ILexLocation loc = d.getInvPattern().getLocation();
		List<PPattern> params = new Vector<PPattern>();
		params.add(d.getInvPattern().clone());

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		parameters.add(params);

		PTypeList ptypes = new PTypeList();

		if (d.getInvType() instanceof ARecordInvariantType)
		{
			// Records are inv_R: R +> bool
			ptypes.add(AstFactory.newAUnresolvedType(d.getName().clone()));
		} else
		{
			// Named types are inv_T: x +> bool, for T = x
			ANamedInvariantType nt = (ANamedInvariantType) d.getInvType();
			ptypes.add(nt.getType().clone());
		}

		AFunctionType ftype = AstFactory.newAFunctionType(loc, false, ptypes, AstFactory.newABooleanBasicType(loc));

		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getInvName(loc), NameScope.GLOBAL, null, ftype, parameters, d.getInvExpression(), null, null, true, null);

		def.setAccess(d.getAccess().clone()); // Same as type's
		def.setClassDefinition(d.getClassDefinition());

		return def;
	}

}
