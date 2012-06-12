package org.overture.typechecker.assistant.type;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.PrivateClassEnvironment;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;

public class AClassTypeAssistantTC {

	public static LexNameToken getMemberName(AClassType cls,
			LexIdentifierToken id) {
		// Note: not explicit
		return new LexNameToken(cls.getName().name, id.name, id.location, false, false);
	}

	public static PDefinition findName(AClassType cls, LexNameToken tag, NameScope scope) {
		return  SClassDefinitionAssistantTC.findName(cls.getClassdef(),tag, scope);
	}

	public static boolean hasSupertype(AClassType sclass, PType other) {
		return SClassDefinitionAssistantTC.hasSupertype(sclass.getClassdef(),other);
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
				question = new TypeCheckInfo(self,question.scope,question.qualifiers);				
				PTypeAssistantTC.typeResolve(PDefinitionAssistantTC.getType(d), root, rootVisitor, question);
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
    			PTypeAssistantTC.unResolve(PDefinitionAssistantTC.getType(d));
    		}
		}
		
	}

	public static String toDisplay(AClassType exptype) {
		return exptype.getClassdef().getName().name;
	}

	public static boolean equals(AClassType type, Object other) {
		other = PTypeAssistantTC.deBracket(other);

		if (other instanceof AClassType)
		{
			AClassType oc = (AClassType)other;
			return type.getName().equals(oc.getName());		// NB. name only
		}

		return false;
	}

	public static boolean isClass(AClassType type) {
		return true;
	}

	public static SClassDefinition getClass(SClassDefinition type)
	{
		return type;
	}
	
}
