package org.overture.codegen.assistant;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ATuplePatternCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;

public class PatternAssistantCG  extends AssistantBase
{
	public PatternAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public AIdentifierPatternCG consIdPattern(String name)
	{
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(name);

		return idPattern;
	}
	
	public ATupleTypeCG getTupleType(AUnionTypeCG unionType, ATuplePatternCG tuplePattern)
	{
		List<ATupleTypeCG> tupleTypes = new LinkedList<ATupleTypeCG>();

		for (STypeCG nextType : unionType.getTypes())
		{
			if (nextType instanceof ATupleTypeCG)
			{
				ATupleTypeCG nextTupleType = ((ATupleTypeCG) nextType);

				if (nextTupleType.getTypes().size() == tuplePattern.getPatterns().size())
				{
					tupleTypes.add(nextTupleType);
				}
			}
		}

		ATupleTypeCG resTupleType = new ATupleTypeCG();

		if (tupleTypes.size() == 1)
		{
			resTupleType = tupleTypes.get(0);
		} else
		{
			for (int i = 0; i < tuplePattern.getPatterns().size(); i++)
			{
				AUnionTypeCG fieldType = new AUnionTypeCG();

				for (ATupleTypeCG t : tupleTypes)
				{
					fieldType.getTypes().add(t.getTypes().get(i).clone());
				}

				resTupleType.getTypes().add(fieldType);
			}
		}
		return resTupleType;
	}
}
