package org.overture.codegen.assistant;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.patterns.ATuplePatternIR;
import org.overture.codegen.ir.types.ATupleTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;

public class PatternAssistantIR  extends AssistantBase
{
	public PatternAssistantIR(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public AIdentifierPatternIR consIdPattern(String name)
	{
		AIdentifierPatternIR idPattern = new AIdentifierPatternIR();
		idPattern.setName(name);

		return idPattern;
	}
	
	public ATupleTypeIR getTupleType(AUnionTypeIR unionType, ATuplePatternIR tuplePattern)
	{
		List<ATupleTypeIR> tupleTypes = new LinkedList<ATupleTypeIR>();

		for (STypeIR nextType : unionType.getTypes())
		{
			if (nextType instanceof ATupleTypeIR)
			{
				ATupleTypeIR nextTupleType = (ATupleTypeIR) nextType;

				if (nextTupleType.getTypes().size() == tuplePattern.getPatterns().size())
				{
					tupleTypes.add(nextTupleType);
				}
			}
		}

		ATupleTypeIR resTupleType = new ATupleTypeIR();

		if (tupleTypes.size() == 1)
		{
			resTupleType = tupleTypes.get(0);
		} else
		{
			for (int i = 0; i < tuplePattern.getPatterns().size(); i++)
			{
				AUnionTypeIR fieldType = new AUnionTypeIR();

				for (ATupleTypeIR t : tupleTypes)
				{
					fieldType.getTypes().add(t.getTypes().get(i).clone());
				}

				resTupleType.getTypes().add(fieldType);
			}
		}
		return resTupleType;
	}
}
