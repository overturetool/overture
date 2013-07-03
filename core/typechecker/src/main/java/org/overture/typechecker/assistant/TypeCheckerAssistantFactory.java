package org.overture.typechecker.assistant;

import org.overture.ast.assistant.AstAssistantFactory;
import org.overture.typechecker.assistant.type.AApplyObjectDesignatorAssistantTC;
import org.overture.typechecker.assistant.type.ABracketTypeAssistantTC;
import org.overture.typechecker.assistant.type.AClassTypeAssistantTC;
import org.overture.typechecker.assistant.type.AFieldFieldAssistantTC;
import org.overture.typechecker.assistant.type.AFunctionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AInMapMapTypeAssistantTC;
import org.overture.typechecker.assistant.type.AMapMapTypeAssistantTC;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOperationTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOptionalTypeAssistantTC;
import org.overture.typechecker.assistant.type.AParameterTypeAssistantTC;
import org.overture.typechecker.assistant.type.APatternListTypePairAssistantTC;
import org.overture.typechecker.assistant.type.AProductTypeAssistantTC;
import org.overture.typechecker.assistant.type.AQuoteTypeAssistantTC;
import org.overture.typechecker.assistant.type.ARecordInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.ASeq1SeqTypeAssistantTC;
import org.overture.typechecker.assistant.type.ASeqSeqTypeAssistantTC;
import org.overture.typechecker.assistant.type.ASetTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUndefinedTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnknownTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnresolvedTypeAssistantTC;
import org.overture.typechecker.assistant.type.AVoidReturnTypeAssistantTC;
import org.overture.typechecker.assistant.type.AVoidTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;
import org.overture.typechecker.assistant.type.SMapTypeAssistantTC;
import org.overture.typechecker.assistant.type.SNumericBasicTypeAssistantTC;
import org.overture.typechecker.assistant.type.SSeqTypeAssistantTC;

public class TypeCheckerAssistantFactory extends AstAssistantFactory implements
		ITypeCheckerAssistantFactory
{

	@Override
	public AApplyObjectDesignatorAssistantTC createAApplyObjectDesignatorAssistantTC()
	{
		return new AApplyObjectDesignatorAssistantTC();
	}

	@Override
	public ABracketTypeAssistantTC createABracketTypeAssistantTC()
	{
		return new ABracketTypeAssistantTC();
	}

	@Override
	public AClassTypeAssistantTC createAClassTypeAssistantTC()
	{
		return new AClassTypeAssistantTC();
	}

	@Override
	public AFieldFieldAssistantTC createAFieldFieldAssistantTC()
	{
		return new AFieldFieldAssistantTC();
	}

	@Override
	public AFunctionTypeAssistantTC createAFunctionTypeAssistantTC()
	{
		return new AFunctionTypeAssistantTC();
	}

	@Override
	public AInMapMapTypeAssistantTC createAInMapMapTypeAssistantTC()
	{
		return new AInMapMapTypeAssistantTC();
	}

	@Override
	public AMapMapTypeAssistantTC createAMapMapTypeAssistantTC()
	{
		return new AMapMapTypeAssistantTC();
	}

	@Override
	public ANamedInvariantTypeAssistantTC createANamedInvariantTypeAssistantTC()
	{
		return new ANamedInvariantTypeAssistantTC();
	}

	@Override
	public AOperationTypeAssistantTC createAOperationTypeAssistantTC()
	{
		return new AOperationTypeAssistantTC();
	}

	@Override
	public AOptionalTypeAssistantTC createAOptionalTypeAssistantTC()
	{
		return new AOptionalTypeAssistantTC();
	}

	@Override
	public AParameterTypeAssistantTC createAParameterTypeAssistantTC()
	{
		return new AParameterTypeAssistantTC();
	}

	@Override
	public APatternListTypePairAssistantTC createAPatternListTypePairAssistantTC()
	{
		return new APatternListTypePairAssistantTC();
	}

	@Override
	public AProductTypeAssistantTC createAProductTypeAssistantTC()
	{
		return new AProductTypeAssistantTC();
	}

	@Override
	public AQuoteTypeAssistantTC createAQuoteTypeAssistantTC()
	{
		return new AQuoteTypeAssistantTC();
	}

	@Override
	public ARecordInvariantTypeAssistantTC createARecordInvariantTypeAssistantTC()
	{
		return new ARecordInvariantTypeAssistantTC();
	}

	@Override
	public ASeq1SeqTypeAssistantTC createASeq1SeqTypeAssistantTC()
	{
		return new ASeq1SeqTypeAssistantTC();
	}

	@Override
	public ASeqSeqTypeAssistantTC createASeqSeqTypeAssistantTC()
	{
		return new ASeqSeqTypeAssistantTC();
	}

	@Override
	public ASetTypeAssistantTC createASetTypeAssistantTC()
	{
		return new ASetTypeAssistantTC();
	}

	@Override
	public AUndefinedTypeAssistantTC createAUndefinedTypeAssistantTC()
	{
		return new AUndefinedTypeAssistantTC();
	}

	@Override
	public AUnionTypeAssistantTC createAUnionTypeAssistantTC()
	{
		return new AUnionTypeAssistantTC();
	}

	@Override
	public AUnknownTypeAssistantTC createAUnknownTypeAssistantTC()
	{
		return new AUnknownTypeAssistantTC();
	}

	@Override
	public AUnresolvedTypeAssistantTC createAUnresolvedTypeAssistantTC()
	{
		return new AUnresolvedTypeAssistantTC();
	}

	@Override
	public AVoidReturnTypeAssistantTC createAVoidReturnTypeAssistantTC()
	{
		return new AVoidReturnTypeAssistantTC();
	}

	@Override
	public AVoidTypeAssistantTC createAVoidTypeAssistantTC()
	{
		return new AVoidTypeAssistantTC();
	}

	@Override
	public PTypeAssistantTC createPTypeAssistantTC()
	{
		return new PTypeAssistantTC();
	}

	@Override
	public SMapTypeAssistantTC createSMapTypeAssistantTC()
	{
		return new SMapTypeAssistantTC();
	}

	@Override
	public SNumericBasicTypeAssistantTC createSNumericBasicTypeAssistantTC()
	{
		return new SNumericBasicTypeAssistantTC();
	}

	@Override
	public SSeqTypeAssistantTC createSSeqTypeAssistantTC()
	{
		return new SSeqTypeAssistantTC();
	}
}
