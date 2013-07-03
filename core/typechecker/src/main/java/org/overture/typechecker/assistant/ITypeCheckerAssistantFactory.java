package org.overture.typechecker.assistant;

import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.typechecker.assistant.type.*;

public interface ITypeCheckerAssistantFactory extends IAstAssistantFactory
{
	AApplyObjectDesignatorAssistantTC createAApplyObjectDesignatorAssistantTC();
	ABracketTypeAssistantTC createABracketTypeAssistantTC();
	AClassTypeAssistantTC createAClassTypeAssistantTC(); 
	AFieldFieldAssistantTC createAFieldFieldAssistantTC();
	AFunctionTypeAssistantTC createAFunctionTypeAssistantTC(); 
	AInMapMapTypeAssistantTC createAInMapMapTypeAssistantTC(); 
	AMapMapTypeAssistantTC createAMapMapTypeAssistantTC();
	ANamedInvariantTypeAssistantTC createANamedInvariantTypeAssistantTC();
	AOperationTypeAssistantTC createAOperationTypeAssistantTC();
	AOptionalTypeAssistantTC createAOptionalTypeAssistantTC();
	AParameterTypeAssistantTC createAParameterTypeAssistantTC();
	APatternListTypePairAssistantTC createAPatternListTypePairAssistantTC();
	AProductTypeAssistantTC createAProductTypeAssistantTC();
	AQuoteTypeAssistantTC createAQuoteTypeAssistantTC();
	ARecordInvariantTypeAssistantTC createARecordInvariantTypeAssistantTC();
	ASeq1SeqTypeAssistantTC createASeq1SeqTypeAssistantTC();
	ASeqSeqTypeAssistantTC createASeqSeqTypeAssistantTC();
	ASetTypeAssistantTC createASetTypeAssistantTC();
	AUndefinedTypeAssistantTC createAUndefinedTypeAssistantTC();
	AUnionTypeAssistantTC createAUnionTypeAssistantTC();
	AUnknownTypeAssistantTC createAUnknownTypeAssistantTC();
	AUnresolvedTypeAssistantTC createAUnresolvedTypeAssistantTC();
	AVoidReturnTypeAssistantTC createAVoidReturnTypeAssistantTC();
	AVoidTypeAssistantTC createAVoidTypeAssistantTC();
	PTypeAssistantTC createPTypeAssistantTC();
	SMapTypeAssistantTC createSMapTypeAssistantTC();
	SNumericBasicTypeAssistantTC createSNumericBasicTypeAssistantTC();
	SSeqTypeAssistantTC createSSeqTypeAssistantTC(); 
}
