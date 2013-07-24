package org.overture.interpreter.assistant;

import org.overture.interpreter.assistant.definition.*;
import org.overture.interpreter.assistant.expression.*;
import org.overture.interpreter.assistant.module.*;
import org.overture.interpreter.assistant.pattern.*;
import org.overture.interpreter.assistant.statement.*;
import org.overture.interpreter.assistant.type.*;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public interface IInterpreterAssistantFactory extends
		ITypeCheckerAssistantFactory
{
	// definition

	AApplyExpressionTraceCoreDefinitionAssistantInterpreter createAApplyExpressionTraceCoreDefinitionAssistant();

	AAssignmentDefinitionAssistantInterpreter createAAssignmentDefinitionAssistant();

	ABracketedExpressionTraceCoreDefinitionAssitantInterpreter createABracketedExpressionTraceCoreDefinitionAssitant();

	ABusClassDefinitionAssitantInterpreter createABusClassDefinitionAssitant();

	AClassClassDefinitionAssistantInterpreter createAClassClassDefinitionAssistant();

	AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter createAConcurrentExpressionTraceCoreDefinitionAssistant();

	ACpuClassDefinitionAssistantInterpreter createACpuClassDefinitionAssistant();

	AEqualsDefinitionAssistantInterpreter createAEqualsDefinitionAssistant();

	AErrorCaseAssistantInterpreter createAErrorCaseAssistant();

	AExplicitFunctionDefinitionAssistantInterpreter createAExplicitFunctionDefinitionAssistant();

	AExplicitOperationDefinitionAssistantInterpreter createAExplicitOperationDefinitionAssistant();

	AImplicitFunctionDefinitionAssistantInterpreter createAImplicitFunctionDefinitionAssistant();

	AImplicitOperationDefinitionAssistantInterpreter createAImplicitOperationDefinitionAssistant();

	AImportedDefinitionAssistantInterpreter createAImportedDefinitionAssistant();

	AInheritedDefinitionAssistantInterpreter createAInheritedDefinitionAssistant();

	AInstanceVariableDefinitionAssistantInterpreter createAInstanceVariableDefinitionAssistant();

	ALetBeStBindingTraceDefinitionAssistantInterpreter createALetBeStBindingTraceDefinitionAssistant();

	ALetDefBindingTraceDefinitionAssistantInterpreter createALetDefBindingTraceDefinitionAssistant();

	ALocalDefinitionAssistantInterpreter createALocalDefinitionAssistant();

	AMutexSyncDefinitionAssistantInterpreter createAMutexSyncDefinitionAssistant();

	ANamedTraceDefinitionAssistantInterpreter createANamedTraceDefinitionAssistant();

	APerSyncDefinitionAssistantInterpreter createAPerSyncDefinitionAssistant();

	ARenamedDefinitionAssistantInterpreter createARenamedDefinitionAssistant();

	ARepeatTraceDefinitionAssistantInterpreter createARepeatTraceDefinitionAssistant();

	AStateDefinitionAssistantInterpreter createAStateDefinitionAssistant();

	ASystemClassDefinitionAssistantInterpreter createASystemClassDefinitionAssistant();

	AThreadDefinitionAssistantInterpreter createAThreadDefinitionAssistant();

	ATraceDefinitionTermAssistantInterpreter createATraceDefinitionTermAssistant();

	ATypeDefinitionAssistantInterpreter createATypeDefinitionAssistant();

	AUntypedDefinitionAssistantInterpreter createAUntypedDefinitionAssistant();

	AValueDefinitionAssistantInterpreter createAValueDefinitionAssistant();

	PDefinitionAssistantInterpreter createPDefinitionAssistant();

	PDefinitionListAssistantInterpreter createPDefinitionListAssistant();

	PTraceCoreDefinitionAssistantInterpreter createPTraceCoreDefinitionAssistant();

	PTraceDefinitionAssistantInterpreter createPTraceDefinitionAssistant();

	SClassDefinitionAssistantInterpreter createSClassDefinitionAssistant();

	// expression

	AApplyExpAssistantInterpreter createAApplyExpAssistant();

	ACaseAlternativeAssistantInterpreter createACaseAlternativeAssistant();

	ACasesExpAssistantInterpreter createACasesExpAssistant();

	ADefExpAssistantInterpreter createADefExpAssistant();

	AElseIfExpAssistantInterpreter createAElseIfExpAssistant();

	AExists1ExpAssistantInterpreter createAExists1ExpAssistant();

	AExistsExpAssistantInterpreter createAExistsExpAssistant();

	AFieldExpAssistantInterpreter createAFieldExpAssistant();

	AFieldNumberExpAssistantInterpreter createAFieldNumberExpAssistant();

	AForAllExpAssistantInterpreter createAForAllExpAssistant();

	AFuncInstatiationExpAssistantInterpreter createAFuncInstatiationExpAssistant();

	AIfExpAssistantInterpreter createAIfExpAssistant();

	AIotaExpAssistantInterpreter createAIotaExpAssistant();

	AIsExpAssistantInterpreter createAIsExpAssistant();

	AIsOfBaseClassExpAssistantInterpreter createAIsOfBaseClassExpAssistant();

	AIsOfClassExpAssistantInterpreter createAIsOfClassExpAssistant();

	ALambdaExpAssistantInterpreter createALambdaExpAssistant();

	ALetBeStExpAssistantInterpreter createALetBeStExpAssistant();

	ALetDefExpAssistantInterpreter createALetDefExpAssistant();

	AMapCompMapExpAssistantInterpreter createAMapCompMapExpAssistant();

	AMapEnumMapExpAssistantInterpreter createAMapEnumMapExpAssistant();

	AMapletExpAssistantInterpreter createAMapletExpAssistant();

	AMkBasicExpAssistantInterpreter createAMkBasicExpAssistant();

	AMkTypeExpAssistantInterpreter createAMkTypeExpAssistant();

	AMuExpAssistantInterpreter createAMuExpAssistant();

	ANarrowExpAssistantInterpreter createANarrowExpAssistant();

	ANewExpAssistantInterpreter createANewExpAssistant();

	APostOpExpAssistantInterpreter createAPostOpExpAssistant();

	ARecordModifierAssistantInterpreter createARecordModifierAssistant();

	ASameBaseClassExpAssistantInterpreter createASameBaseClassExpAssistant();

	ASameClassExpAssistantInterpreter createASameClassExpAssistant();

	ASeqCompSeqExpAssistantInterpreter createASeqCompSeqExpAssistant();

	ASeqEnumSeqExpAssistantInterpreter createASeqEnumSeqExpAssistant();

	ASetCompSetExpAssistantInterpreter createASetCompSetExpAssistant();

	ASetEnumSetExpAssistantInterpreter createASetEnumSetExpAssistant();

	ASetRangeSetExpAssistantInterpreter createASetRangeSetExpAssistant();

	ASubseqExpAssistantInterpreter createASubseqExpAssistant();

	ATupleExpAssistantInterpreter createATupleExpAssistant();

	AVariableExpAssistantInterpreter createAVariableExpAssistant();

	PExpAssistantInterpreter createPExpAssistant();

	SBinaryExpAssistantInterpreter createSBinaryExpAssistant();

	SMapExpAssistantInterpreter createSMapExpAssistant();

	SSeqExpAssistantInterpreter createSSeqExpAssistant();

	SSetExpAssistantInterpreter createSSetExpAssistant();

	SUnaryExpAssistantInterpreter createSUnaryExpAssistant();

	// module

	AModuleModulesAssistantInterpreter createAModuleModulesAssistant();

	ModuleListAssistantInterpreter createModuleListAssistant();

	// pattern

	ABooleanPatternAssistantInterpreter createABooleanPatternAssistant();

	ACharacterPatternAssistantInterpreter createACharacterPatternAssistant();

	AConcatenationPatternAssistantInterpreter createAConcatenationPatternAssistant();

	AExpressionPatternAssistantInterpreter createAExpressionPatternAssistant();

	AIdentifierPatternAssistantInterpreter createAIdentifierPatternAssistant();

	AIgnorePatternAssistantInterpreter createAIgnorePatternAssistant();

	AIntegerPatternAssistantInterpreter createAIntegerPatternAssistant();

	AMapPatternAssistantInterpreter createAMapPatternAssistant();

	AMapPatternMapletAssistantInterpreter createAMapPatternMapletAssistant();

	AMapUnionPatternAssistantInterpreter createAMapUnionPatternAssistant();

	ANilPatternAssistantInterpreter createANilPatternAssistant();

	AQuotePatternAssistantInterpreter createAQuotePatternAssistant();

	ARealPatternAssistantInterpreter createARealPatternAssistant();

	ARecordPatternAssistantInterpreter createARecordPatternAssistant();

	ASeqPatternAssistantInterpreter createASeqPatternAssistant();

	ASetBindAssistantInterpreter createASetBindAssistant();

	ASetMultipleBindAssistantInterpreter createASetMultipleBindAssistant();

	ASetPatternAssistantInterpreter createASetPatternAssistant();

	AStringPatternAssistantInterpreter createAStringPatternAssistant();

	ATuplePatternAssistantInterpreter createATuplePatternAssistant();

	ATypeBindAssistantInterpreter createATypeBindAssistant();

	ATypeMultipleBindAssistantInterpreter createATypeMultipleBindAssistant();

	AUnionPatternAssistantInterpreter createAUnionPatternAssistant();

	PBindAssistantInterpreter createPBindAssistant();

	PMultipleBindAssistantInterpreter createPMultipleBindAssistant();

	PPatternAssistantInterpreter createPPatternAssistant();

	PPatternListAssistantInterpreter createPPatternListAssistant();

	// statement

	AAlwaysStmAssistantInterpreter createAAlwaysStmAssistant();

	AAssignmentStmAssistantInterpreter createAAssignmentStmAssistant();

	AAtomicStmAssistantInterpreter createAAtomicStmAssistant();

	ACallObjectStatementAssistantInterpreter createACallObjectStatementAssistant();

	ACallStmAssistantInterpreter createACallStmAssistant();

	ACaseAlternativeStmAssistantInterpreter createACaseAlternativeStmAssistant();

	ACasesStmAssistantInterpreter createACasesStmAssistant();

	ACyclesStmAssistantInterpreter createACyclesStmAssistant();

	ADurationStmAssistantInterpreter createADurationStmAssistant();

	AElseIfStmAssistantInterpreter createAElseIfStmAssistant();

	AExitStmAssistantInterpreter createAExitStmAssistant();

	AForAllStmAssistantInterpreter createAForAllStmAssistant();

	AForIndexStmAssistantInterpreter createAForIndexStmAssistant();

	AForPatternBindStmAssitantInterpreter createAForPatternBindStmAssitant();

	AIfStmAssistantInterpreter createAIfStmAssistant();

	ALetBeStStmAssistantInterpreter createALetBeStStmAssistant();

	AReturnStmAssistantInterpreter createAReturnStmAssistant();

	AStartStmAssistantInterpreter createAStartStmAssistant();

	ATixeStmAssistantInterpreter createATixeStmAssistant();

	ATixeStmtAlternativeAssistantInterpreter createATixeStmtAlternativeAssistant();

	ATrapStmAssistantInterpreter createATrapStmAssistant();

	AWhileStmAssistantInterpreter createAWhileStmAssistant();

	PStmAssistantInterpreter createPStmAssistant();

	SLetDefStmAssistantInterpreter createSLetDefStmAssistant();

	SSimpleBlockStmAssistantInterpreter createSSimpleBlockStmAssistant();

	// type

	ABooleanBasicTypeAssistantInterpreter createABooleanBasicTypeAssistant();

	AInMapMapTypeAssistantInterpreter createAInMapMapTypeAssistant();

	ANamedInvariantTypeAssistantInterpreter createANamedInvariantTypeAssistant();

	AOptionalTypeAssistantInterpreter createAOptionalTypeAssistant();

	AParameterTypeAssistantInterpreter createAParameterTypeAssistant();

	AProductTypeAssistantInterpreter createAProductTypeAssistant();

	AQuoteTypeAssistantInterpreter createAQuoteTypeAssistant();

	ARecordInvariantTypeAssistantInterpreter createARecordInvariantTypeAssistant();

	ASetTypeAssistantInterpreter createASetTypeAssistant();

	AUnionTypeAssistantInterpreter createAUnionTypeAssistant();

	PTypeAssistantInterpreter createPTypeAssistant();

	PTypeListAssistant createPTypeListAssistant();

	SBasicTypeAssistantInterpreter createSBasicTypeAssistant();

	SInvariantTypeAssistantInterpreter createSInvariantTypeAssistant();

	SMapTypeAssistantInterpreter createSMapTypeAssistant();
}
