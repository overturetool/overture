package org.overture.interpreter.assistant;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.definition.ACpuClassDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AExplicitFunctionDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ANamedTraceDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AStateDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.PDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.PDefinitionListAssistantInterpreter;
import org.overture.interpreter.assistant.definition.SClassDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AFieldExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.APostOpExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.assistant.module.AModuleModulesAssistantInterpreter;
import org.overture.interpreter.assistant.module.ModuleListAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AMapPatternMapletAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PMultipleBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.assistant.statement.PStmAssistantInterpreter;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.SInvariantTypeAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.traces.TraceNode;
import org.overture.interpreter.utilities.pattern.AllNamedValuesLocator;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public interface IInterpreterAssistantFactory extends
		ITypeCheckerAssistantFactory
{
	// definition

	// AApplyExpressionTraceCoreDefinitionAssistantInterpreter createAApplyExpressionTraceCoreDefinitionAssistant();

	// AAssignmentDefinitionAssistantInterpreter createAAssignmentDefinitionAssistant();

	// ABracketedExpressionTraceCoreDefinitionAssitantInterpreter
	// createABracketedExpressionTraceCoreDefinitionAssitant();

	//ABusClassDefinitionAssistantInterpreter createABusClassDefinitionAssitant();

//	AClassClassDefinitionAssistantInterpreter createAClassClassDefinitionAssistant();

	// AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter
	// createAConcurrentExpressionTraceCoreDefinitionAssistant();

	ACpuClassDefinitionAssistantInterpreter createACpuClassDefinitionAssistant();

	// AEqualsDefinitionAssistantInterpreter createAEqualsDefinitionAssistant();

	// AErrorCaseAssistantInterpreter createAErrorCaseAssistant();

	AExplicitFunctionDefinitionAssistantInterpreter createAExplicitFunctionDefinitionAssistant();

	// AExplicitOperationDefinitionAssistantInterpreter createAExplicitOperationDefinitionAssistant();

	//AImplicitFunctionDefinitionAssistantInterpreter createAImplicitFunctionDefinitionAssistant();

	// AImplicitOperationDefinitionAssistantInterpreter createAImplicitOperationDefinitionAssistant();

	// AImportedDefinitionAssistantInterpreter createAImportedDefinitionAssistant();

	// AInheritedDefinitionAssistantInterpreter createAInheritedDefinitionAssistant();

	// AInstanceVariableDefinitionAssistantInterpreter createAInstanceVariableDefinitionAssistant();

	// ALetBeStBindingTraceDefinitionAssistantInterpreter createALetBeStBindingTraceDefinitionAssistant();

	// ALetDefBindingTraceDefinitionAssistantInterpreter createALetDefBindingTraceDefinitionAssistant();

	// ALocalDefinitionAssistantInterpreter createALocalDefinitionAssistant();

//	AMutexSyncDefinitionAssistantInterpreter createAMutexSyncDefinitionAssistant();

	ANamedTraceDefinitionAssistantInterpreter createANamedTraceDefinitionAssistant();

	// APerSyncDefinitionAssistantInterpreter createAPerSyncDefinitionAssistant();

	// ARenamedDefinitionAssistantInterpreter createARenamedDefinitionAssistant();

	// ARepeatTraceDefinitionAssistantInterpreter createARepeatTraceDefinitionAssistant();

	AStateDefinitionAssistantInterpreter createAStateDefinitionAssistant();

//	ASystemClassDefinitionAssistantInterpreter createASystemClassDefinitionAssistant();

	// AThreadDefinitionAssistantInterpreter createAThreadDefinitionAssistant();

	// ATraceDefinitionTermAssistantInterpreter createATraceDefinitionTermAssistant();

	// ATypeDefinitionAssistantInterpreter createATypeDefinitionAssistant();

	// /AUntypedDefinitionAssistantInterpreter createAUntypedDefinitionAssistant();

	// AValueDefinitionAssistantInterpreter createAValueDefinitionAssistant();

	PDefinitionAssistantInterpreter createPDefinitionAssistant();

	PDefinitionListAssistantInterpreter createPDefinitionListAssistant();

	//PTraceCoreDefinitionAssistantInterpreter createPTraceCoreDefinitionAssistant();

	SClassDefinitionAssistantInterpreter createSClassDefinitionAssistant();

	// expression

	// AApplyExpAssistantInterpreter createAApplyExpAssistant();

	// ACaseAlternativeAssistantInterpreter createACaseAlternativeAssistant();

	// ACasesExpAssistantInterpreter createACasesExpAssistant();

	// ADefExpAssistantInterpreter createADefExpAssistant();

	// AElseIfExpAssistantInterpreter createAElseIfExpAssistant();

	// AExists1ExpAssistantInterpreter createAExists1ExpAssistant();

	// AExistsExpAssistantInterpreter createAExistsExpAssistant();

	AFieldExpAssistantInterpreter createAFieldExpAssistant();

	// AFieldNumberExpAssistantInterpreter createAFieldNumberExpAssistant();

	// AForAllExpAssistantInterpreter createAForAllExpAssistant();

	// AFuncInstatiationExpAssistantInterpreter createAFuncInstatiationExpAssistant();

	// AIfExpAssistantInterpreter createAIfExpAssistant();

	// AIotaExpAssistantInterpreter createAIotaExpAssistant();

	// AIsExpAssistantInterpreter createAIsExpAssistant();

//	AIsOfBaseClassExpAssistantInterpreter createAIsOfBaseClassExpAssistant();

//	AIsOfClassExpAssistantInterpreter createAIsOfClassExpAssistant();

	// ALambdaExpAssistantInterpreter createALambdaExpAssistant();

	// ALetBeStExpAssistantInterpreter createALetBeStExpAssistant();

	// ALetDefExpAssistantInterpreter createALetDefExpAssistant();

	// AMapCompMapExpAssistantInterpreter createAMapCompMapExpAssistant();
	//
	// AMapEnumMapExpAssistantInterpreter createAMapEnumMapExpAssistant();
	//
	// AMapletExpAssistantInterpreter createAMapletExpAssistant();
	//
	// AMkBasicExpAssistantInterpreter createAMkBasicExpAssistant();
	//
	// AMkTypeExpAssistantInterpreter createAMkTypeExpAssistant();
	//
	// AMuExpAssistantInterpreter createAMuExpAssistant();

	// ANarrowExpAssistantInterpreter createANarrowExpAssistant();
	//
	// ANewExpAssistantInterpreter createANewExpAssistant();

	APostOpExpAssistantInterpreter createAPostOpExpAssistant();

	// ARecordModifierAssistantInterpreter createARecordModifierAssistant();
	//
	// ASameBaseClassExpAssistantInterpreter createASameBaseClassExpAssistant();
	//
	// ASameClassExpAssistantInterpreter createASameClassExpAssistant();

	// ASeqCompSeqExpAssistantInterpreter createASeqCompSeqExpAssistant();

	// ASeqEnumSeqExpAssistantInterpreter createASeqEnumSeqExpAssistant();
	//
	// ASetCompSetExpAssistantInterpreter createASetCompSetExpAssistant();
	//
	// ASetEnumSetExpAssistantInterpreter createASetEnumSetExpAssistant();
	//
	// ASetRangeSetExpAssistantInterpreter createASetRangeSetExpAssistant();
	//
	// ASubseqExpAssistantInterpreter createASubseqExpAssistant();
	//
	// ATupleExpAssistantInterpreter createATupleExpAssistant();
	//
	// AVariableExpAssistantInterpreter createAVariableExpAssistant();

	PExpAssistantInterpreter createPExpAssistant();

	// SBinaryExpAssistantInterpreter createSBinaryExpAssistant();
	//
	// SMapExpAssistantInterpreter createSMapExpAssistant();
	//
	// SSeqExpAssistantInterpreter createSSeqExpAssistant();
	//
	// SSetExpAssistantInterpreter createSSetExpAssistant();
	//
	// SUnaryExpAssistantInterpreter createSUnaryExpAssistant();

	// module

	AModuleModulesAssistantInterpreter createAModuleModulesAssistant();

	ModuleListAssistantInterpreter createModuleListAssistant();

	// pattern

	// ABooleanPatternAssistantInterpreter createABooleanPatternAssistant();
	//
	// ACharacterPatternAssistantInterpreter createACharacterPatternAssistant();
	//
	// AConcatenationPatternAssistantInterpreter createAConcatenationPatternAssistant();
	//
	// AExpressionPatternAssistantInterpreter createAExpressionPatternAssistant();
	//
	// AIdentifierPatternAssistantInterpreter createAIdentifierPatternAssistant();
	//
	// AIgnorePatternAssistantInterpreter createAIgnorePatternAssistant();
	//
	// AIntegerPatternAssistantInterpreter createAIntegerPatternAssistant();
	//
	// AMapPatternAssistantInterpreter createAMapPatternAssistant();
	//
	AMapPatternMapletAssistantInterpreter createAMapPatternMapletAssistant();

	//
	// AMapUnionPatternAssistantInterpreter createAMapUnionPatternAssistant();

	// ANilPatternAssistantInterpreter createANilPatternAssistant();

	// ARealPatternAssistantInterpreter createARealPatternAssistant();

	// ARecordPatternAssistantInterpreter createARecordPatternAssistant();

	// ASeqPatternAssistantInterpreter createASeqPatternAssistant();

	// ASetBindAssistantInterpreter createASetBindAssistant();

	// ASetMultipleBindAssistantInterpreter createASetMultipleBindAssistant();

	// ASetPatternAssistantInterpreter createASetPatternAssistant();

	// AStringPatternAssistantInterpreter createAStringPatternAssistant();

	// ATuplePatternAssistantInterpreter createATuplePatternAssistant();

	// ATypeBindAssistantInterpreter createATypeBindAssistant();

	// ATypeMultipleBindAssistantInterpreter createATypeMultipleBindAssistant();

	// AUnionPatternAssistantInterpreter createAUnionPatternAssistant();

	PBindAssistantInterpreter createPBindAssistant();

	PMultipleBindAssistantInterpreter createPMultipleBindAssistant();

	PPatternAssistantInterpreter createPPatternAssistant();

	//PPatternListAssistantInterpreter createPPatternListAssistant();

	// statement

	// AAlwaysStmAssistantInterpreter createAAlwaysStmAssistant();
	//
	// AAssignmentStmAssistantInterpreter createAAssignmentStmAssistant();
	//
	// AAtomicStmAssistantInterpreter createAAtomicStmAssistant();
	//
	// ACallObjectStatementAssistantInterpreter createACallObjectStatementAssistant();
	//
	// ACallStmAssistantInterpreter createACallStmAssistant();

//	ACaseAlternativeStmAssistantInterpreter createACaseAlternativeStmAssistant();

	// ACasesStmAssistantInterpreter createACasesStmAssistant();
	//
	// ACyclesStmAssistantInterpreter createACyclesStmAssistant();
	//
	// ADurationStmAssistantInterpreter createADurationStmAssistant();
	//
	// AElseIfStmAssistantInterpreter createAElseIfStmAssistant();
	//
	// AExitStmAssistantInterpreter createAExitStmAssistant();
	//
	// AForAllStmAssistantInterpreter createAForAllStmAssistant();
	//
	// AForIndexStmAssistantInterpreter createAForIndexStmAssistant();
	//
	// AForPatternBindStmAssitantInterpreter createAForPatternBindStmAssitant();
	//
	// AIfStmAssistantInterpreter createAIfStmAssistant();
	//
	// ALetBeStStmAssistantInterpreter createALetBeStStmAssistant();
	//
	// AReturnStmAssistantInterpreter createAReturnStmAssistant();

//	AStartStmAssistantInterpreter createAStartStmAssistant();

	// ATixeStmAssistantInterpreter createATixeStmAssistant();

	//ATixeStmtAlternativeAssistantInterpreter createATixeStmtAlternativeAssistant();

	// ATrapStmAssistantInterpreter createATrapStmAssistant();

	// AWhileStmAssistantInterpreter createAWhileStmAssistant();

	PStmAssistantInterpreter createPStmAssistant();

	// SLetDefStmAssistantInterpreter createSLetDefStmAssistant();

	// type

	// ABooleanBasicTypeAssistantInterpreter createABooleanBasicTypeAssistant();
	//
	// AInMapMapTypeAssistantInterpreter createAInMapMapTypeAssistant();
	//
	// ANamedInvariantTypeAssistantInterpreter createANamedInvariantTypeAssistant();
	//
	// AOptionalTypeAssistantInterpreter createAOptionalTypeAssistant();
	//
	// AParameterTypeAssistantInterpreter createAParameterTypeAssistant();
	//
	// AProductTypeAssistantInterpreter createAProductTypeAssistant();
	//
	// AQuoteTypeAssistantInterpreter createAQuoteTypeAssistant();
	//
	// ARecordInvariantTypeAssistantInterpreter createARecordInvariantTypeAssistant();
	//
	// ASetTypeAssistantInterpreter createASetTypeAssistant();
	//
	// AUnionTypeAssistantInterpreter createAUnionTypeAssistant();

	PTypeAssistantInterpreter createPTypeAssistant();

//	PTypeListAssistant createPTypeListAssistant();

	// SBasicTypeAssistantInterpreter createSBasicTypeAssistant();

	SInvariantTypeAssistantInterpreter createSInvariantTypeAssistant();

	// SMapTypeAssistantInterpreter createSMapTypeAssistant();

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

	IQuestionAnswer<Integer, PStm> getDefinitionStatementFinder();

	IQuestionAnswer<Context, TraceNode> getTraceExpander();

	IQuestionAnswer<Integer, PExp> getStatementExpressionFinder();

	IQuestionAnswer<Integer, PStm> getStatementFinder();

	IQuestionAnswer<ObjectContext, ValueList> getExpressionValueCollector();

	IQuestionAnswer<Integer, PExp> getExpExpressionFinder();

	IAnswer<List<PExp>> getSubExpressionsLocator();

	IQuestionAnswer<Context, ValueList> getSingleBindValuesCollector();

	IQuestionAnswer<ObjectContext, ValueList> getBindValueCollector();

	IQuestionAnswer<Context, ValueList> getAllValuesCollector();

}
