package org.overture.codegen.assistant;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqTypeBase;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AIntBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.types.SBasicTypeCGBase;
import org.overture.codegen.cgast.types.SBasicTypeWrappersTypeCGBase;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.ooast.OoAstInfo;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class TypeAssistantCG
{
	public static PDefinition getTypeDef(ILexNameToken nameToken)
	{
		PDefinition def = (PDefinition) nameToken.getAncestor(PDefinition.class);

		if (def == null)
			return null;

		SClassDefinition enclosingClass = nameToken.getAncestor(SClassDefinition.class);

		if (enclosingClass == null)
			return null;

		TypeCheckerAssistantFactory factory = new TypeCheckerAssistantFactory();
		PDefinitionAssistantTC defAssistant = factory.createPDefinitionAssistant();
		
		enclosingClass.getName().getModule();
		PDefinition typeDef = defAssistant.findType(def, nameToken, enclosingClass.getName().getModule());
	
		return typeDef;
	}
	
	public static PTypeCG constructSeqType(SSeqTypeBase node, OoAstInfo question)
			throws AnalysisException
	{
		PTypeCG seqOf = node.getSeqof().apply(question.getTypeVisitor(), question);

		// This is a special case since sequence of characters are strings
		if (seqOf instanceof ACharBasicTypeCG)
			return new AStringTypeCG();

		ASeqSeqTypeCG seqType = new ASeqSeqTypeCG();
		seqType.setSeqOf(seqOf);

		return seqType;
	}
	
	public static boolean isBasicType(PTypeCG type)
	{
		return type instanceof SBasicTypeCGBase;
	}

	public static SBasicTypeWrappersTypeCGBase getWrapperType(
			SBasicTypeCGBase basicType)
	{

		if (basicType instanceof AIntNumericBasicTypeCG)
			return new AIntBasicTypeWrappersTypeCG();
		else if (basicType instanceof ARealNumericBasicTypeCG)
			return new ARealBasicTypeWrappersTypeCG();
		else if (basicType instanceof ACharBasicTypeCG)
			return new ACharBasicTypeWrappersTypeCG();
		else if (basicType instanceof ABoolBasicTypeCG)
			return new ABoolBasicTypeWrappersTypeCG();
		else
		{
			Logger.getLog().printErrorln("Unexpected basic type encountered in getWrapperType method: "
					+ basicType);
			return null;
		}

	}
	
	//TODO: Copied from UML2VDM. Factor out in assistant
	public static boolean isUnionOfQuotes(AUnionType type)
	{
		TypeCheckerAssistantFactory factory = new TypeCheckerAssistantFactory();
		PTypeAssistantTC typeAssistant = factory.createPTypeAssistant();
		
		try
		{
			for (PType t : type.getTypes())
			{
				if (!typeAssistant.isType(t, AQuoteType.class))
				{
					return false;
				}
			}
		} catch (Error t)//Hack for stackoverflowError
		{
			return false;
		}

		return true;
	}
	
}
