package org.overture.typechecker.assistant;

import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.typechecker.assistant.definition.*;
import org.overture.typechecker.assistant.expression.*;
import org.overture.typechecker.assistant.module.*;
import org.overture.typechecker.assistant.pattern.*;
import org.overture.typechecker.assistant.statement.*;
import org.overture.typechecker.assistant.type.*;

public interface ITypeCheckerAssistantFactory extends IAstAssistantFactory
{
	//Definition
	AAssignmentDefinitionAssistantTC createAAssignmentDefinitionAssistantTC();
	ABusClassDefinitionAssistantTC createABusClassDefinitionAssistantTC();
	AClassInvariantDefinitionAssistantTC createAClassInvariantDefinitionAssistantTC();
	ACpuClassDefinitionAssistantTC createACpuClassDefinitionAssistantTC();
	AEqualsDefinitionAssistantTC createAEqualsDefinitionAssistantTC();
	AExplicitFunctionDefinitionAssistantTC createAExplicitFunctionDefinitionAssistantTC();
	AExplicitOperationDefinitionAssistantTC createAExplicitOperationDefinitionAssistantTC();

	AExternalDefinitionAssistantTC createAExternalDefinitionAssistantTC();
	AImplicitFunctionDefinitionAssistantTC createAImplicitFunctionDefinitionAssistantTC();
	AImplicitOperationDefinitionAssistantTC createAImplicitOperationDefinitionAssistantTC();

	AImportedDefinitionAssistantTC createAImportedDefinitionAssistantTC();
	AInheritedDefinitionAssistantTC createAInheritedDefinitionAssistantTC();
	AInstanceVariableDefinitionAssistantTC createAInstanceVariableDefinitionAssistantTC();
	ALocalDefinitionAssistantTC createALocalDefinitionAssistantTC();
	AMultiBindListDefinitionAssistantTC createAMultiBindListDefinitionAssistantTC();
	AMutexSyncDefinitionAssistantTC createAMutexSyncDefinitionAssistantTC();
	ANamedTraceDefinitionAssistantTC createANamedTraceDefinitionAssistantTC();
	APerSyncDefinitionAssistantTC createAPerSyncDefinitionAssistantTC();
	ARenamedDefinitionAssistantTC createARenamedDefinitionAssistantTC();
	AStateDefinitionAssistantTC createAStateDefinitionAssistantTC();
	ASystemClassDefinitionAssistantTC createASystemClassDefinitionAssistantTC();
	AThreadDefinitionAssistantTC createAThreadDefinitionAssistantTC();
	ATypeDefinitionAssistantTC createATypeDefinitionAssistantTC();
	AUntypedDefinitionAssistantTC createAUntypedDefinitionAssistantTC();
	AValueDefinitionAssistantTC createAValueDefinitionAssistantTC();
	PAccessSpecifierAssistantTC createPAccessSpecifierAssistantTC();
	PDefinitionAssistantTC createPDefinitionAssistantTC();
	PDefinitionListAssistantTC createPDefinitionListAssistantTC();
	PDefinitionSet createPDefinitionSet();
	PTraceDefinitionAssistantTC createPTraceDefinitionAssistantTC();
	SClassDefinitionAssistantTC createSClassDefinitionAssistantTC();
	
	//expression
	AApplyExpAssistantTC createAApplyExpAssistantTC();
	ACaseAlternativeAssistantTC createACaseAlternativeAssistantTC();
	ACasesExpAssistantTC createACasesExpAssistantTC();
	AElementsUnaryExpAssistantTC createAElementsUnaryExpAssistantTC();
	AElseIfExpAssistantTC createAElseIfExpAssistantTC();
	AExists1ExpAssistantTC createAExists1ExpAssistantTC();
	AExistsExpAssistantTC createAExistsExpAssistantTC();
	AFieldExpAssistantTC createAFieldExpAssistantTC();
	AFieldNumberExpAssistantTC createAFieldNumberExpAssistantTC();
	AForAllExpAssistantTC createAForAllExpAssistantTC();
	AFuncInstatiationExpAssistantTC createAFuncInstatiationExpAssistantTC();
	AIfExpAssistantTC createAIfExpAssistantTC();
	AIotaExpAssistantTC createAIotaExpAssistantTC();
	AIsExpAssistantTC createAIsExpAssistantTC();
	AIsOfBaseClassExpAssistantTC createAIsOfBaseClassExpAssistantTC();
	AIsOfClassExpAssistantTC createAIsOfClassExpAssistantTC();
	ALambdaExpAssistantTC createALambdaExpAssistantTC();
	ALetBeStExpAssistantTC createALetBeStExpAssistantTC();
	ALetDefExpAssistantTC createALetDefExpAssistantTC();
	AMapCompMapExpAssistantTC createAMapCompMapExpAssistantTC();
	AMapEnumMapExpAssistantTC createAMapEnumMapExpAssistantTC();
	AMapletExpAssistantTC createAMapletExpAssistantTC();
	AMkBasicExpAssistantTC createAMkBasicExpAssistantTC();
	AMkTypeExpAssistantTC createAMkTypeExpAssistantTC();
	AMuExpAssistantTC createAMuExpAssistantTC();
	ANarrowExpAssistantTC createANarrowExpAssistantTC();
	ANewExpAssistantTC createANewExpAssistantTC();
	APostOpExpAssistantTC createAPostOpExpAssistantTC();
	ARecordModifierAssistantTC createARecordModifierAssistantTC();
	ASameBaseClassExpAssistantTC createASameBaseClassExpAssistantTC();
	ASameClassExpAssistantTC createASameClassExpAssistantTC();
	ASeqCompSeqExpAssistantTC createASeqCompSeqExpAssistantTC();
	ASeqEnumSeqExpAssistantTC createASeqEnumSeqExpAssistantTC();
	ASetCompSetExpAssistantTC createASetCompSetExpAssistantTC();
	ASetEnumSetExpAssistantTC createASetEnumSetExpAssistantTC();
	ASubseqExpAssistantTC createASubseqExpAssistantTC();
	ATupleExpAssistantTC createATupleExpAssistantTC();
	AVariableExpAssistantTC createAVariableExpAssistantTC();
	PExpAssistantTC createPExpAssistantTC();
	SBinaryExpAssistantTC createSBinaryExpAssistantTC();
	SMapExpAssistantTC createSMapExpAssistantTC();
	SSeqExpAssistantTC createSSeqExpAssistantTC();
	SSetExpAssistantTC createSSetExpAssistantTC();
	SUnaryExpAssistantTC createSUnaryExpAssistantTC();
	
	//module
	AAllImportAssistantTC createAAllImportAssistantTC();
	AFromModuleImportsAssistantTC createAFromModuleImportsAssistantTC();
	AModuleExportsAssistantTC createAModuleExportsAssistantTC();
	AModuleImportsAssistantTC createAModuleImportsAssistantTC();
	AModuleModulesAssistantTC createAModuleModulesAssistantTC();
	ATypeImportAssistantTC createATypeImportAssistantTC();
	AValueValueImportAssistantTC createAValueValueImportAssistantTC();
	PExportAssistantTC createPExportAssistantTC();
	PImportAssistantTC createPImportAssistantTC();
	SValueImportAssistantTC createSValueImportAssistantTC();
	
	//pattern
	ABooleanPatternAssistantTC createABooleanPatternAssistantTC();
	ACharacterPatternAssistantTC createACharacterPatternAssistantTC();
	AConcatenationPatternAssistantTC createAConcatenationPatternAssistantTC();
	AExpressionPatternAssistantTC createAExpressionPatternAssistantTC();
	AIdentifierPatternAssistantTC createAIdentifierPatternAssistantTC();
	AIgnorePatternAssistantTC createAIgnorePatternAssistantTC();
	AIntegerPatternAssistantTC createAIntegerPatternAssistantTC();
	AMapletPatternMapletAssistantTC createAMapletPatternMapletAssistantTC();
	AMapPatternAssistantTC createAMapPatternAssistantTC();
	AMapUnionPatternAssistantTC createAMapUnionPatternAssistantTC();
	ANilPatternAssistantTC createANilPatternAssistantTC();
	APatternTypePairAssistant createAPatternTypePairAssistant();
	AQuotePatternAssistantTC createAQuotePatternAssistantTC();
	ARealPatternAssistantTC createARealPatternAssistantTC();
	ARecordPatternAssistantTC createARecordPatternAssistantTC();
	ASeqPatternAssistantTC createASeqPatternAssistantTC();
	ASetBindAssistantTC createASetBindAssistantTC();
	ASetMultipleBindAssistantTC createASetMultipleBindAssistantTC();
	ASetPatternAssistantTC createASetPatternAssistantTC();
	AStringPatternAssistantTC createAStringPatternAssistantTC();
	ATuplePatternAssistantTC createATuplePatternAssistantTC();
	ATypeBindAssistantTC createATypeBindAssistantTC();
	ATypeMultipleBindAssistantTC createATypeMultipleBindAssistantTC();
	AUnionPatternAssistantTC createAUnionPatternAssistantTC();
	PatternListTC createPatternListTC();
	PBindAssistantTC createPBindAssistantTC();
	PMultipleBindAssistantTC createPMultipleBindAssistantTC();
	PPatternAssistantTC createPPatternAssistantTC();
	PPatternBindAssistantTC createPPatternBindAssistantTC();
	PPatternListAssistantTC createPPatternListAssistantTC();
	
	//statement
	AAlwaysStmAssistantTC createAAlwaysStmAssistantTC();
	AAssignmentStmAssistantTC createAAssignmentStmAssistantTC();
	ABlockSimpleBlockStmAssistantTC createABlockSimpleBlockStmAssistantTC();
	ACallObjectStatementAssistantTC createACallObjectStatementAssistantTC();
	ACallStmAssistantTC createACallStmAssistantTC();
	ACaseAlternativeStmAssistantTC createACaseAlternativeStmAssistantTC();
	ACasesStmAssistantTC createACasesStmAssistantTC();
	AElseIfStmAssistantTC createAElseIfStmAssistantTC();
	AExitStmAssistantTC createAExitStmAssistantTC();
	AExternalClauseAssistantTC createAExternalClauseAssistantTC();
	AForAllStmAssistantTC createAForAllStmAssistantTC();
	AForIndexStmAssistantTC createAForIndexStmAssistantTC();
	AForPatternBindStmAssitantTC createAForPatternBindStmAssitantTC();
	AIfStmAssistantTC createAIfStmAssistantTC();
	ALetBeStStmAssistantTC createALetBeStStmAssistantTC();
	ANonDeterministicSimpleBlockStmAssistantTC createANonDeterministicSimpleBlockStmAssistantTC();

	AReturnStmAssistantTC createAReturnStmAssistantTC();
	ATixeStmAssistantTC createATixeStmAssistantTC();
	ATrapStmAssistantTC createATrapStmAssistantTC();
	AWhileStmAssistantTC createAWhileStmAssistantTC();
	PStateDesignatorAssistantTC createPStateDesignatorAssistantTC();
	PStmAssistantTC createPStmAssistantTC();
	SLetDefStmAssistantTC createSLetDefStmAssistantTC();
	SSimpleBlockStmAssistantTC createSSimpleBlockStmAssistantTC();
	
	//Type
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
