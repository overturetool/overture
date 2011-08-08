package org.overture.typecheck.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.definitions.assistants.SClassDefinitionAssistant;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.typecheck.Environment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;


public class TypeCheckerOthersVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType> {

	final private QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;
	
	public TypeCheckerOthersVisitor(TypeCheckVisitor typeCheckVisitor) {
		this.rootVisitor = typeCheckVisitor;
	}
	
	
	@Override
	public PType caseAIdentifierStateDesignator(
			AIdentifierStateDesignator node, TypeCheckInfo question) {
		Environment env = question.env;
		
		if (env.isVDMPP())
		{
			// We generate an explicit name because accessing a variable
			// by name in VDM++ does not "inherit" values from a superclass.

			LexNameToken name = node.getName();
			LexNameToken exname = name.getExplicit(true);
			PDefinition def = env.findName(exname, NameScope.STATE);

			if (def == null)
			{
				TypeCheckerErrors.report(3247, "Unknown variable '" + name + "' in assignment",name.getLocation(),name);				
				return new AUnknownType(name.getLocation(),false);
			}
			else if (!PDefinitionAssistant.isUpdatable(def))
			{
				TypeCheckerErrors.report(3301, "Variable '" + name + "' in scope is not updatable",name.getLocation(),name);
				return new AUnknownType(name.getLocation(),false);
			}
			else if (def.getClassDefinition() != null)
			{
    			if (!SClassDefinitionAssistant.isAccessible(env, def, true))
    			{
    				TypeCheckerErrors.report(3180, "Inaccessible member '" + name + "' of class " +
    					def.getClassDefinition().getName().name,name.getLocation(),name);
    				return new AUnknownType(name.getLocation(),false);
    			}
    			else if (!PDefinitionAssistant.isStatic(def) && env.isStatic())
    			{
    				TypeCheckerErrors.report(3181, "Cannot access " + name + " from a static context",name.getLocation(),name);
    				return new AUnknownType(name.getLocation(),false);
    			}
			}

			return PDefinitionAssistant.getType(def);
		}
		else
		{
			LexNameToken name = node.getName();
			PDefinition def = env.findName(name, NameScope.STATE);

			if (def == null)
			{
				TypeCheckerErrors.report(3247, "Unknown state variable '" + name + "' in assignment",name.getLocation(),name);
				return new AUnknownType(name.location,false);
			}
			else if (!PDefinitionAssistant.isUpdatable(def))
			{
				TypeCheckerErrors.report(3301, "Variable '" + name + "' in scope is not updatable",name.getLocation(),name);
				return new AUnknownType(name.location,false);
			}
			else if (def instanceof AExternalDefinition)
			{
				AExternalDefinition d = (AExternalDefinition)def;

				if (d.getReadOnly())
				{
					TypeCheckerErrors.report(3248, "Cannot assign to 'ext rd' state " + name,name.getLocation(),name);
				}
			}
			// else just state access in (say) an explicit operation

			return def.getType();
		}
	}

}
