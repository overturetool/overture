package org.overture.interpreter.assistant;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.definition.AApplyExpressionTraceCoreDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AAssignmentDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ABracketedExpressionTraceCoreDefinitionAssitantInterpreter;
import org.overture.interpreter.assistant.definition.ABusClassDefinitionAssitantInterpreter;
import org.overture.interpreter.assistant.definition.AClassClassDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ACpuClassDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AEqualsDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AErrorCaseAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AExplicitFunctionDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AExplicitOperationDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AImplicitFunctionDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AImplicitOperationDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AImportedDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AInheritedDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AInstanceVariableDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ALetBeStBindingTraceDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ALetDefBindingTraceDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ALocalDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AMutexSyncDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ANamedTraceDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.APerSyncDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ARenamedDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ARepeatTraceDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AStateDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ASystemClassDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AThreadDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ATraceDefinitionTermAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ATypeDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AUntypedDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AValueDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.PDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.PDefinitionListAssistantInterpreter;
import org.overture.interpreter.assistant.definition.PTraceCoreDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.PTraceDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.SClassDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AApplyExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ACaseAlternativeAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ACasesExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ADefExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AElseIfExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AExists1ExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AExistsExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AFieldExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AFieldNumberExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AForAllExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AFuncInstatiationExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIfExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIotaExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIsExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIsOfBaseClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIsOfClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ALambdaExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ALetBeStExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ALetDefExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMapCompMapExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMapEnumMapExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMapletExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMkBasicExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMkTypeExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AMuExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ANarrowExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ANewExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.APostOpExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ARecordModifierAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASameBaseClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASameClassExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASeqCompSeqExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASeqEnumSeqExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASetCompSetExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASetEnumSetExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASetRangeSetExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ASubseqExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ATupleExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AVariableExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SBinaryExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SMapExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SSeqExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SSetExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SUnaryExpAssistantInterpreter;
import org.overture.interpreter.assistant.module.AModuleModulesAssistantInterpreter;
import org.overture.interpreter.assistant.module.ModuleListAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ABooleanPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ACharacterPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AConcatenationPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AExpressionPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AIdentifierPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AIgnorePatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AIntegerPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AMapPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AMapPatternMapletAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AMapUnionPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ANilPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AQuotePatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ARealPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ARecordPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASeqPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASetBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASetMultipleBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASetPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AStringPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ATuplePatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ATypeBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ATypeMultipleBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AUnionPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PMultipleBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PPatternListAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AAlwaysStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AAssignmentStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AAtomicStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ACallObjectStatementAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ACallStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ACaseAlternativeStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ACasesStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ACyclesStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ADurationStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AElseIfStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AExitStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AForAllStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AForIndexStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AForPatternBindStmAssitantInterpreter;
import org.overture.interpreter.assistant.statement.AIfStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ALetBeStStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AReturnStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AStartStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ATixeStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ATixeStmtAlternativeAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ATrapStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AWhileStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.PStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.SLetDefStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.SSimpleBlockStmAssistantInterpreter;
import org.overture.interpreter.assistant.type.ABooleanBasicTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.AInMapMapTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.ANamedInvariantTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.AOptionalTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.AParameterTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.AProductTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.AQuoteTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.ARecordInvariantTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.ASetTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.AUnionTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.PTypeListAssistant;
import org.overture.interpreter.assistant.type.SBasicTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.SInvariantTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.SMapTypeAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.utilities.pattern.AllNamedValuesLocator;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ValueList;
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

	/* visitors */
	IAnswer<LexNameList> getOldNameCollector();
	
	QuestionAnswerAdaptor<Context, ValueList> getBindValuesCollector();
	
	QuestionAnswerAdaptor<ObjectContext, ValueList> getValueCollector();
	
	IAnswer<List<AIdentifierPattern>> getIdentifierPatternFinder();
	
	IAnswer<Integer> getLengthFinder();
	
	IAnswer<Boolean> getConstrainedPatternChecker();
	
	QuestionAnswerAdaptor<AllNamedValuesLocator.Newquestion, List<NameValuePairList>> getAllNamedValuesLocator();
	
	IQuestionAnswer<Context, NameValuePairList> getNamedValueLister();
	
	IQuestionAnswer<Integer, PExp> getExpressionFinder();
	
	IQuestionAnswer<ObjectContext, ValueList> getValuesDefinitionLocator();
	
	IAnswer<Boolean> getTypeDefinitionChecker();
	
	IAnswer<Boolean> getDefinitionRunTimeChecker();
	
	IAnswer<Boolean> getDefintionValueChecker();
	
	IAnswer<Boolean> getInstanceVariableChecker();
	
	IQuestionAnswer<Integer, PStm> getStatementFinder();
	
}
