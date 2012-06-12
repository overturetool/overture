package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.TypeCheckerErrors;

public class ASystemClassDefinitionAssistantTC
{

	public static void implicitDefinitions(ASystemClassDefinition def,
			Environment publicClasses)
	{
		SClassDefinitionAssistantTC.implicitDefinitionsBase(def, publicClasses);
		
		for (PDefinition d : def.getDefinitions())
		{
			if (d instanceof AInstanceVariableDefinition)
			{
				AInstanceVariableDefinition iv = (AInstanceVariableDefinition)d;

				PType ivType = PDefinitionAssistantTC.getType(iv);
				if (ivType instanceof AUnresolvedType &&
					iv.getExpression() instanceof AUndefinedExp)
				{
					AUnresolvedType ut = (AUnresolvedType) ivType;

					if (ut.getName().getName().equals("BUS"))
					{
						TypeCheckerErrors.warning(5014, "Uninitialized BUS ignored",d.getLocation(),d);
					}
				}else if (ivType instanceof AUnresolvedType &&
						iv.getExpression() instanceof ANewExp)
				{
					AUnresolvedType ut = (AUnresolvedType)ivType;

					if (ut.getName().getName().equals("CPU"))
					{
						ANewExp newExp = (ANewExp) iv.getExpression();
						PExp exp = newExp.getArgs().get(1);
						double speed = 0;
						if (exp instanceof AIntLiteralExp)
						{
							AIntLiteralExp frequencyExp = (AIntLiteralExp) newExp.getArgs().get(1);
							speed = frequencyExp.getValue().value;
						}else if (exp instanceof ARealLiteralExp)
						{
							ARealLiteralExp frequencyExp = (ARealLiteralExp) newExp.getArgs().get(1);
							speed = frequencyExp.getValue().value;
						}

						if (speed == 0)
						{
							TypeCheckerErrors.report(3305, "CPU frequency to slow: " + speed + " Hz",d.getLocation(),d);
						}else if (speed > ACpuClassDefinitionAssistantTC.CPU_MAX_FREQUENCY)
						{
							TypeCheckerErrors.report(3306, "CPU frequency to fast: " + speed + " Hz",d.getLocation(),d);
						}
					}
				}
			}
			else if (d instanceof AExplicitOperationDefinition)
			{
				AExplicitOperationDefinition edef = (AExplicitOperationDefinition)d;

				if (!edef.getName().name.equals(def.getName().name) ||
					!edef.getParameterPatterns().isEmpty())
				{
					TypeCheckerErrors.report(3285, "System class can only define a default constructor",d.getLocation(),d);
				}
			}
			else if (d instanceof AImplicitOperationDefinition)
			{
				AImplicitOperationDefinition idef = (AImplicitOperationDefinition)d;

				if (!d.getName().name.equals(def.getName().name))
				{
					TypeCheckerErrors.report(3285, "System class can only define a default constructor",d.getLocation(),d);
				}

				if (idef.getBody() == null)
				{
					TypeCheckerErrors.report(3283, "System class constructor cannot be implicit",d.getLocation(),d);
				}
			}
			else
			{
				TypeCheckerErrors.report(3284, "System class can only define instance variables and a constructor",d.getLocation(),d);
			}
		}
		
	}

}
