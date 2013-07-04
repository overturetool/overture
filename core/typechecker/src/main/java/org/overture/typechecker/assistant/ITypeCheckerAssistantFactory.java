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
	AAssignmentDefinitionAssistantTC createAAssignmentDefinitionAssistant();
	ABusClassDefinitionAssistantTC createABusClassDefinitionAssistant();
	AClassInvariantDefinitionAssistantTC createAClassInvariantDefinitionAssistant();
	ACpuClassDefinitionAssistantTC createACpuClassDefinitionAssistant();
	AEqualsDefinitionAssistantTC createAEqualsDefinitionAssistant();
	AExplicitFunctionDefinitionAssistantTC createAExplicitFunctionDefinitionAssistant();
	AExplicitOperationDefinitionAssistantTC createAExplicitOperationDefinitionAssistant();

	AExternalDefinitionAssistantTC createAExternalDefinitionAssistant();
	AImplicitFunctionDefinitionAssistantTC createAImplicitFunctionDefinitionAssistant();
	AImplicitOperationDefinitionAssistantTC createAImplicitOperationDefinitionAssistant();

	AImportedDefinitionAssistantTC createAImportedDefinitionAssistant();
	AInheritedDefinitionAssistantTC createAInheritedDefinitionAssistant();
	AInstanceVariableDefinitionAssistantTC createAInstanceVariableDefinitionAssistant();
	ALocalDefinitionAssistantTC createALocalDefinitionAssistant();
	AMultiBindListDefinitionAssistantTC createAMultiBindListDefinitionAssistant();
	AMutexSyncDefinitionAssistantTC createAMutexSyncDefinitionAssistant();
	ANamedTraceDefinitionAssistantTC createANamedTraceDefinitionAssistant();
	APerSyncDefinitionAssistantTC createAPerSyncDefinitionAssistant();
	ARenamedDefinitionAssistantTC createARenamedDefinitionAssistant();
	AStateDefinitionAssistantTC createAStateDefinitionAssistant();
	ASystemClassDefinitionAssistantTC createASystemClassDefinitionAssistant();
	AThreadDefinitionAssistantTC createAThreadDefinitionAssistant();
	ATypeDefinitionAssistantTC createATypeDefinitionAssistant();
	AUntypedDefinitionAssistantTC createAUntypedDefinitionAssistant();
	AValueDefinitionAssistantTC createAValueDefinitionAssistant();
	PAccessSpecifierAssistantTC createPAccessSpecifierAssistant();
	PDefinitionAssistantTC createPDefinitionAssistant();
	PDefinitionListAssistantTC createPDefinitionListAssistant();
	PDefinitionSet createPDefinitionSet();
	PTraceDefinitionAssistantTC createPTraceDefinitionAssistant();
	SClassDefinitionAssistantTC createSClassDefinitionAssistant();
	
	//expression
	AApplyExpAssistantTC createAApplyExpAssistant();
	ACaseAlternativeAssistantTC createACaseAlternativeAssistant();
	ACasesExpAssistantTC createACasesExpAssistant();
	AElementsUnaryExpAssistantTC createAElementsUnaryExpAssistant();
	AElseIfExpAssistantTC createAElseIfExpAssistant();
	AExists1ExpAssistantTC createAExists1ExpAssistant();
	AExistsExpAssistantTC createAExistsExpAssistant();
	AFieldExpAssistantTC createAFieldExpAssistant();
	AFieldNumberExpAssistantTC createAFieldNumberExpAssistant();
	AForAllExpAssistantTC createAForAllExpAssistant();
	AFuncInstatiationExpAssistantTC createAFuncInstatiationExpAssistant();
	AIfExpAssistantTC createAIfExpAssistant();
	AIotaExpAssistantTC createAIotaExpAssistant();
	AIsExpAssistantTC createAIsExpAssistant();
	AIsOfBaseClassExpAssistantTC createAIsOfBaseClassExpAssistant();
	AIsOfClassExpAssistantTC createAIsOfClassExpAssistant();
	ALambdaExpAssistantTC createALambdaExpAssistant();
	ALetBeStExpAssistantTC createALetBeStExpAssistant();
	ALetDefExpAssistantTC createALetDefExpAssistant();
	AMapCompMapExpAssistantTC createAMapCompMapExpAssistant();
	AMapEnumMapExpAssistantTC createAMapEnumMapExpAssistant();
	AMapletExpAssistantTC createAMapletExpAssistant();
	AMkBasicExpAssistantTC createAMkBasicExpAssistant();
	AMkTypeExpAssistantTC createAMkTypeExpAssistant();
	AMuExpAssistantTC createAMuExpAssistant();
	ANarrowExpAssistantTC createANarrowExpAssistant();
	ANewExpAssistantTC createANewExpAssistant();
	APostOpExpAssistantTC createAPostOpExpAssistant();
	ARecordModifierAssistantTC createARecordModifierAssistant();
	ASameBaseClassExpAssistantTC createASameBaseClassExpAssistant();
	ASameClassExpAssistantTC createASameClassExpAssistant();
	ASeqCompSeqExpAssistantTC createASeqCompSeqExpAssistant();
	ASeqEnumSeqExpAssistantTC createASeqEnumSeqExpAssistant();
	ASetCompSetExpAssistantTC createASetCompSetExpAssistant();
	ASetEnumSetExpAssistantTC createASetEnumSetExpAssistant();
	ASubseqExpAssistantTC createASubseqExpAssistant();
	ATupleExpAssistantTC createATupleExpAssistant();
	AVariableExpAssistantTC createAVariableExpAssistant();
	PExpAssistantTC createPExpAssistant();
	SBinaryExpAssistantTC createSBinaryExpAssistant();
	SMapExpAssistantTC createSMapExpAssistant();
	SSeqExpAssistantTC createSSeqExpAssistant();
	SSetExpAssistantTC createSSetExpAssistant();
	SUnaryExpAssistantTC createSUnaryExpAssistant();
	
	//module
	AAllImportAssistantTC createAAllImportAssistant();
	AFromModuleImportsAssistantTC createAFromModuleImportsAssistant();
	AModuleExportsAssistantTC createAModuleExportsAssistant();
	AModuleImportsAssistantTC createAModuleImportsAssistant();
	AModuleModulesAssistantTC createAModuleModulesAssistant();
	ATypeImportAssistantTC createATypeImportAssistant();
	AValueValueImportAssistantTC createAValueValueImportAssistant();
	PExportAssistantTC createPExportAssistant();
	PImportAssistantTC createPImportAssistant();
	SValueImportAssistantTC createSValueImportAssistant();
	
	//pattern
	ABooleanPatternAssistantTC createABooleanPatternAssistant();
	ACharacterPatternAssistantTC createACharacterPatternAssistant();
	AConcatenationPatternAssistantTC createAConcatenationPatternAssistant();
	AExpressionPatternAssistantTC createAExpressionPatternAssistant();
	AIdentifierPatternAssistantTC createAIdentifierPatternAssistant();
	AIgnorePatternAssistantTC createAIgnorePatternAssistant();
	AIntegerPatternAssistantTC createAIntegerPatternAssistant();
	AMapletPatternMapletAssistantTC createAMapletPatternMapletAssistant();
	AMapPatternAssistantTC createAMapPatternAssistant();
	AMapUnionPatternAssistantTC createAMapUnionPatternAssistant();
	ANilPatternAssistantTC createANilPatternAssistant();
	APatternTypePairAssistant createAPatternTypePairAssistant();
	AQuotePatternAssistantTC createAQuotePatternAssistant();
	ARealPatternAssistantTC createARealPatternAssistant();
	ARecordPatternAssistantTC createARecordPatternAssistant();
	ASeqPatternAssistantTC createASeqPatternAssistant();
	ASetBindAssistantTC createASetBindAssistant();
	ASetMultipleBindAssistantTC createASetMultipleBindAssistant();
	ASetPatternAssistantTC createASetPatternAssistant();
	AStringPatternAssistantTC createAStringPatternAssistant();
	ATuplePatternAssistantTC createATuplePatternAssistant();
	ATypeBindAssistantTC createATypeBindAssistant();
	ATypeMultipleBindAssistantTC createATypeMultipleBindAssistant();
	AUnionPatternAssistantTC createAUnionPatternAssistant();
	PatternListTC createPatternList();
	PBindAssistantTC createPBindAssistant();
	PMultipleBindAssistantTC createPMultipleBindAssistant();
	PPatternAssistantTC createPPatternAssistant();
	PPatternBindAssistantTC createPPatternBindAssistant();
	PPatternListAssistantTC createPPatternListAssistant();
	
	//statement
	AAlwaysStmAssistantTC createAAlwaysStmAssistant();
	AAssignmentStmAssistantTC createAAssignmentStmAssistant();
	ABlockSimpleBlockStmAssistantTC createABlockSimpleBlockStmAssistant();
	ACallObjectStatementAssistantTC createACallObjectStatementAssistant();
	ACallStmAssistantTC createACallStmAssistant();
	ACaseAlternativeStmAssistantTC createACaseAlternativeStmAssistant();
	ACasesStmAssistantTC createACasesStmAssistant();
	AElseIfStmAssistantTC createAElseIfStmAssistant();
	AExitStmAssistantTC createAExitStmAssistant();
	AExternalClauseAssistantTC createAExternalClauseAssistant();
	AForAllStmAssistantTC createAForAllStmAssistant();
	AForIndexStmAssistantTC createAForIndexStmAssistant();
	AForPatternBindStmAssitantTC createAForPatternBindStmAssitant();
	AIfStmAssistantTC createAIfStmAssistant();
	ALetBeStStmAssistantTC createALetBeStStmAssistant();
	ANonDeterministicSimpleBlockStmAssistantTC createANonDeterministicSimpleBlockStmAssistant();

	AReturnStmAssistantTC createAReturnStmAssistant();
	ATixeStmAssistantTC createATixeStmAssistant();
	ATrapStmAssistantTC createATrapStmAssistant();
	AWhileStmAssistantTC createAWhileStmAssistant();
	PStateDesignatorAssistantTC createPStateDesignatorAssistant();
	PStmAssistantTC createPStmAssistant();
	SLetDefStmAssistantTC createSLetDefStmAssistant();
	SSimpleBlockStmAssistantTC createSSimpleBlockStmAssistant();
	
	//Type
	AApplyObjectDesignatorAssistantTC createAApplyObjectDesignatorAssistant();
	ABracketTypeAssistantTC createABracketTypeAssistant();
	AClassTypeAssistantTC createAClassTypeAssistant(); 
	AFieldFieldAssistantTC createAFieldFieldAssistant();
	AFunctionTypeAssistantTC createAFunctionTypeAssistant(); 
	AInMapMapTypeAssistantTC createAInMapMapTypeAssistant(); 
	AMapMapTypeAssistantTC createAMapMapTypeAssistant();
	ANamedInvariantTypeAssistantTC createANamedInvariantTypeAssistant();
	AOperationTypeAssistantTC createAOperationTypeAssistant();
	AOptionalTypeAssistantTC createAOptionalTypeAssistant();
	AParameterTypeAssistantTC createAParameterTypeAssistant();
	APatternListTypePairAssistantTC createAPatternListTypePairAssistant();
	AProductTypeAssistantTC createAProductTypeAssistant();
	AQuoteTypeAssistantTC createAQuoteTypeAssistant();
	ARecordInvariantTypeAssistantTC createARecordInvariantTypeAssistant();
	ASeq1SeqTypeAssistantTC createASeq1SeqTypeAssistant();
	ASeqSeqTypeAssistantTC createASeqSeqTypeAssistant();
	ASetTypeAssistantTC createASetTypeAssistant();
	AUndefinedTypeAssistantTC createAUndefinedTypeAssistant();
	AUnionTypeAssistantTC createAUnionTypeAssistant();
	AUnknownTypeAssistantTC createAUnknownTypeAssistant();
	AUnresolvedTypeAssistantTC createAUnresolvedTypeAssistant();
	AVoidReturnTypeAssistantTC createAVoidReturnTypeAssistant();
	AVoidTypeAssistantTC createAVoidTypeAssistant();
	PTypeAssistantTC createPTypeAssistant();
	SMapTypeAssistantTC createSMapTypeAssistant();
	SNumericBasicTypeAssistantTC createSNumericBasicTypeAssistant();
	SSeqTypeAssistantTC createSSeqTypeAssistant(); 
}
