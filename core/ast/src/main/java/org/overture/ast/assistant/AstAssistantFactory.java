package org.overture.ast.assistant;

import java.lang.reflect.Method;

import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.assistant.definition.PAccessSpecifierAssistant;
import org.overture.ast.assistant.definition.PDefinitionAssistant;
import org.overture.ast.assistant.pattern.PPatternAssistant;
import org.overture.ast.assistant.type.AParameterTypeAssistant;
import org.overture.ast.assistant.type.AUnionTypeAssistant;
import org.overture.ast.assistant.type.AUnknownTypeAssistant;
import org.overture.ast.assistant.type.PTypeAssistant;
import org.overture.ast.assistant.type.SNumericBasicTypeAssistant;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.util.pattern.AllVariableNameLocator;
import org.overture.ast.util.type.HashChecker;
import org.overture.ast.util.type.NumericBasisChecker;
import org.overture.ast.util.type.NumericFinder;


//TODO Add assistant Javadoc
/** 
 * This is the main AST assistant factory. everyone ultimately inherits from here.
 * @author ldc
 *
 */

public class AstAssistantFactory implements IAstAssistantFactory
{

	static
	{
		// FIXME: remove this when conversion to factory obtained assistants are completed.
		init(new AstAssistantFactory());
	}

	/**
	 * Remove this when conversion is completed it just configures the static factory fields in the assistants
	 */
	public static void init(Object o)
	{
		for (Method m : o.getClass().getMethods())
		{
			if (m.getParameterTypes().length == 0 && m.getName().startsWith("create"))
			{
				try
				{
					m.invoke(o, new Object[] {});
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public PAccessSpecifierAssistant createPAccessSpecifierAssistant()
	{
		return new PAccessSpecifierAssistant(this);
	}

	@Override
	public PDefinitionAssistant createPDefinitionAssistant()
	{
		return new PDefinitionAssistant(this);
	}

	@Override
	public PPatternAssistant createPPatternAssistant()
	{
		return new PPatternAssistant(this);
	}

//	@Override
//	public ABracketTypeAssistant createABracketTypeAssistant()
//	{
//		return new ABracketTypeAssistant(this);
//	}

//	@Override
//	public ANamedInvariantTypeAssistant createANamedInvariantTypeAssistant()
//	{
//		return new ANamedInvariantTypeAssistant(this);
//	}

//	@Override
//	public AOptionalTypeAssistant createAOptionalTypeAssistant()
//	{
//		return new AOptionalTypeAssistant(this);
//	}

	@Override
	public AParameterTypeAssistant createAParameterTypeAssistant()
	{
		return new AParameterTypeAssistant(this);
	}

	@Override
	public AUnionTypeAssistant createAUnionTypeAssistant()
	{
		return new AUnionTypeAssistant(this);
	}

	@Override
	public AUnknownTypeAssistant createAUnknownTypeAssistant()
	{
		return new AUnknownTypeAssistant(this);
	}

	@Override
	public PTypeAssistant createPTypeAssistant()
	{
		return new PTypeAssistant(this);
	}

	@Override
	public SNumericBasicTypeAssistant createSNumericBasicTypeAssistant()
	{
		return new SNumericBasicTypeAssistant(this);
	}

	//visitors
	
	@Override
	public IAnswer<LexNameList> getAllVariableNameLocator()
	{
		return new AllVariableNameLocator(this);
	}
	
	@Override
	public IAnswer<Boolean> getNumericFinder()
	{
		return new NumericFinder(this);
	}
	
	@Override
	public IAnswer<SNumericBasicType> getNumericBasisChecker()
	{
		return new NumericBasisChecker(this);
	}
	
	@Override
	public IAnswer<Integer> getHashChecker()
	{
		return new HashChecker(this);
	}
	

}
