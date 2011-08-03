package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternTCAssistant;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.ast.types.assistants.PTypeList;
import org.overture.typecheck.Environment;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class ATypeDefinitionAssistant {

	public static PDefinition findType(ATypeDefinition d, LexNameToken sought,
			String fromModule) {
		
		PType type = d.getType();
		
		if (type instanceof ANamedInvariantType)
		{
			ANamedInvariantType nt = (ANamedInvariantType)type;

			if (nt.getType() instanceof ARecordInvariantType)
			{
				ARecordInvariantType rt = (ARecordInvariantType)nt.getType();

				if (rt.getName().equals(sought))
				{
					return d;	// T1 = compose T2 x:int end;
				}
			}
		}

		return PDefinitionAssistant.findNameBaseCase(d,sought, NameScope.TYPENAME);
	}

	public static PDefinition findName(ATypeDefinition d, LexNameToken sought,
			NameScope scope) {

		PDefinition invdef = d.getInvdef();
		
		if (invdef != null &&  PDefinitionAssistant.findName(invdef, sought, scope)  != null)
		{
			return invdef;
		}

		return null;
	}

	public static List<PDefinition> getDefinitions(ATypeDefinition d) {
		List<PDefinition> defs = new Vector<PDefinition>();
		defs.add(d);

		if (d.getInvdef() != null)
		{
			defs.add(d.getInvdef());
		}

		return defs;
	}

	public static LexNameList getVariableNames(ATypeDefinition d) {
		// This is only used in VDM++ type inheritance
		return new LexNameList(d.getName());
	}

	public static void typeResolve(ATypeDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		try
		{
			d.setInfinite(false);
			d.setType((SInvariantType) PTypeAssistant.typeResolve((SInvariantType)d.getType(), d, rootVisitor, question));

			if (d.getInfinite())
			{
				TypeCheckerErrors.report(3050, "Type '" + d.getName() + "' is infinite",d.getLocation(),d);
			}

			if (d.getInvdef() != null)
			{
				PDefinitionAssistant.typeResolve(d.getInvdef(), rootVisitor, question);
				PPatternTCAssistant.typeResolve(d.getInvPattern(), rootVisitor, question);
			}
			
			d.setType(d.getType());
		}
		catch (TypeCheckException e)
		{
			PTypeAssistant.unResolve(d.getType());
			throw e;
		}
	}

	public static void implicitDefinitions(ATypeDefinition d, Environment env) {
		if (d.getInvPattern() != null)
		{
    		d.setInvdef(getInvDefinition(d));
    		d.getInvType().setInvDef(d.getInvdef());
		}
		else
		{
			d.setInvdef(null);
		}
		
	}

	private static AExplicitFunctionDefinition getInvDefinition(
			ATypeDefinition d) {
		
		LexLocation loc = d.getInvPattern().getLocation();
		List<PPattern> params = new Vector<PPattern>();
		params.add(d.getInvPattern().clone());

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		parameters.add(params);

		PTypeList ptypes = new PTypeList();

		if (d.getType() instanceof ARecordInvariantType)
		{
			// Records are inv_R: R +> bool
			ptypes.add(new AUnresolvedType(d.getLocation(),false, d.getName().clone()));
		}
		else
		{
			// Named types are inv_T: x +> bool, for T = x
			ANamedInvariantType nt = (ANamedInvariantType) d.getInvType();
			ptypes.add(nt.getType().clone());
		}

		AFunctionType ftype =
			new AFunctionType(loc, false, false, ptypes, new ABooleanBasicType(loc,false));

		AExplicitFunctionDefinition def = new AExplicitFunctionDefinition(
				loc,
				d.getName().getInvName(loc),
				NameScope.GLOBAL, 
				false,
				PAccessSpecifierAssistant.getDefault(),
				null,
				parameters,
				ftype, 
				d.getInvExpression().clone(),
				null, null, null);
		def.setTypeInvariant(true);

		def.setAccess(d.getAccess().clone());	// Same as type's
		def.setClassDefinition(d.getClassDefinition());
		
		List<PDefinition> defList = new Vector<PDefinition>();
		defList.add(def);
		ftype.setDefinitions(defList);
		return def;
	}

}
