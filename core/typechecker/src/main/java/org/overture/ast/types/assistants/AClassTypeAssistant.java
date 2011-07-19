package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.SClassDefinitionAssistant;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.typecheck.Environment;
import org.overture.typecheck.PrivateClassEnvironment;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;


public class AClassTypeAssistant {

	public static LexNameToken getMemberName(AClassType cls,
			LexIdentifierToken id) {
		// Note: not explicit
		return new LexNameToken(cls.getName().name, id.name, id.location, false, false);
	}

	public static PDefinition findName(AClassType cls, LexNameToken tag) {
		return  SClassDefinitionAssistant.findName(cls.getClassdef(),tag, NameScope.NAMESANDSTATE);
	}

	public static boolean hasSupertype(AClassType sclass, PType other) {
		return SClassDefinitionAssistant.hasSupertype(sclass.getClassdef(),other);
	}

	public static PType typeResolve(AClassType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved()) return type; else type.setResolved(true);

		try
		{
			// We have to add a private class environment here because the
			// one passed in may be from a class that contains a reference
			// to this class. We need the private environment to see all
			// the definitions that are available to us while resolving...

			Environment self = new PrivateClassEnvironment(type.getClassdef(), question.env);

			for (PDefinition d: type.getClassdef().getDefinitions())
			{
				// There is a problem resolving ParameterTypes via a FunctionType
				// when this is not being done via ExplicitFunctionDefinition
				// which extends the environment with the type names that
				// are in scope. So we skip these here.

				if (d instanceof AExplicitFunctionDefinition)
				{
					AExplicitFunctionDefinition fd = (AExplicitFunctionDefinition)d;

					if (fd.getTypeParams() != null)
					{
						continue;	// Skip polymorphic functions
					}
				}
				question.env = self;
				PTypeAssistant.typeResolve(d.getType(), root, rootVisitor, question);
			}

			return type;
		}
		catch (TypeCheckException e)
		{
			unResolve(type);
			throw e;
		}
		
	}

	public static void unResolve(AClassType type) {
		if (type.getResolved())
		{
    		type.setResolved(false);

    		for (PDefinition d: type.getClassdef().getDefinitions())
    		{
    			PTypeAssistant.unResolve(d.getType());
    		}
		}
		
	}

}
