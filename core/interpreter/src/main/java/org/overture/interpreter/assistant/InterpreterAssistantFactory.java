package org.overture.interpreter.assistant;

import java.io.Serializable;
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
import org.overture.interpreter.traces.TraceExpander;
import org.overture.interpreter.traces.TraceNode;
import org.overture.interpreter.utilities.OldNameCollector;
import org.overture.interpreter.utilities.definition.DefinitionRunTimeChecker;
import org.overture.interpreter.utilities.definition.DefinitionStatementFinder;
import org.overture.interpreter.utilities.definition.DefinitionValueChecker;
import org.overture.interpreter.utilities.definition.ExpressionFinder;
import org.overture.interpreter.utilities.definition.InstanceVariableChecker;
import org.overture.interpreter.utilities.definition.NamedValueLister;
import org.overture.interpreter.utilities.definition.TypeDefinitionChecker;
import org.overture.interpreter.utilities.definition.ValuesDefinitionLocator;
import org.overture.interpreter.utilities.expression.ExpExpressionFinder;
import org.overture.interpreter.utilities.expression.ExpressionValueCollector;
import org.overture.interpreter.utilities.expression.SubExpressionsLocator;
import org.overture.interpreter.utilities.pattern.AllNamedValuesLocator;
import org.overture.interpreter.utilities.pattern.BindValueCollector;
import org.overture.interpreter.utilities.pattern.ConstrainedPatternChecker;
import org.overture.interpreter.utilities.pattern.IdentifierPatternFinder;
import org.overture.interpreter.utilities.pattern.LengthFinder;
import org.overture.interpreter.utilities.pattern.MultipleBindValuesCollector;
import org.overture.interpreter.utilities.pattern.SingleBindValuesCollector;
import org.overture.interpreter.utilities.pattern.ValueCollector;
import org.overture.interpreter.utilities.statement.StatementExpressionFinder;
import org.overture.interpreter.utilities.statement.StatementFinder;
import org.overture.interpreter.utilities.type.AllValuesCollector;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class InterpreterAssistantFactory extends TypeCheckerAssistantFactory
		implements IInterpreterAssistantFactory, Serializable
{

	// definition

	// public AApplyExpressionTraceCoreDefinitionAssistantInterpreter
	// createAApplyExpressionTraceCoreDefinitionAssistant()
	// {
	// return new AApplyExpressionTraceCoreDefinitionAssistantInterpreter(this);
	// }

	// public AAssignmentDefinitionAssistantInterpreter createAAssignmentDefinitionAssistant()
	// {
	// return new AAssignmentDefinitionAssistantInterpreter(this);
	// }

	// public ABracketedExpressionTraceCoreDefinitionAssitantInterpreter
	// createABracketedExpressionTraceCoreDefinitionAssitant()
	// {
	// return new ABracketedExpressionTraceCoreDefinitionAssitantInterpreter(this);
	// }

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	public ABusClassDefinitionAssistantInterpreter createABusClassDefinitionAssitant()
//	{
//		return new ABusClassDefinitionAssistantInterpreter(this);
//	}

//	public AClassClassDefinitionAssistantInterpreter createAClassClassDefinitionAssistant()
//	{
//		return new AClassClassDefinitionAssistantInterpreter(this);
//	}

	// public AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter
	// createAConcurrentExpressionTraceCoreDefinitionAssistant()
	// {
	// return new AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter(this);
	// }

	public ACpuClassDefinitionAssistantInterpreter createACpuClassDefinitionAssistant()
	{
		return new ACpuClassDefinitionAssistantInterpreter(this);
	}

	// public AEqualsDefinitionAssistantInterpreter createAEqualsDefinitionAssistant()
	// {
	// return new AEqualsDefinitionAssistantInterpreter(this);
	// }

	// public AErrorCaseAssistantInterpreter createAErrorCaseAssistant()
	// {
	// return new AErrorCaseAssistantInterpreter(this);
	// }

	public AExplicitFunctionDefinitionAssistantInterpreter createAExplicitFunctionDefinitionAssistant()
	{
		return new AExplicitFunctionDefinitionAssistantInterpreter(this);
	}

	// public AExplicitOperationDefinitionAssistantInterpreter createAExplicitOperationDefinitionAssistant()
	// {
	// return new AExplicitOperationDefinitionAssistantInterpreter(this);
	// }

//	public AImplicitFunctionDefinitionAssistantInterpreter createAImplicitFunctionDefinitionAssistant()
//	{
//		return new AImplicitFunctionDefinitionAssistantInterpreter(this);
//	}

	//
	// public AImplicitOperationDefinitionAssistantInterpreter createAImplicitOperationDefinitionAssistant()
	// {
	// return new AImplicitOperationDefinitionAssistantInterpreter(this);
	// }

	// public AImportedDefinitionAssistantInterpreter createAImportedDefinitionAssistant()
	// {
	// return new AImportedDefinitionAssistantInterpreter(this);
	// }

	// public AInheritedDefinitionAssistantInterpreter createAInheritedDefinitionAssistant()
	// {
	// return new AInheritedDefinitionAssistantInterpreter(this);
	// }

	// public AInstanceVariableDefinitionAssistantInterpreter createAInstanceVariableDefinitionAssistant()
	// {
	// return new AInstanceVariableDefinitionAssistantInterpreter(this);
	// }

	// public ALetBeStBindingTraceDefinitionAssistantInterpreter createALetBeStBindingTraceDefinitionAssistant()
	// {
	// return new ALetBeStBindingTraceDefinitionAssistantInterpreter(this);
	// }

	// public ALetDefBindingTraceDefinitionAssistantInterpreter createALetDefBindingTraceDefinitionAssistant()
	// {
	// return new ALetDefBindingTraceDefinitionAssistantInterpreter(this);
	// }

	// public ALocalDefinitionAssistantInterpreter createALocalDefinitionAssistant()
	// {
	// return new ALocalDefinitionAssistantInterpreter(this);
	// }

//	public AMutexSyncDefinitionAssistantInterpreter createAMutexSyncDefinitionAssistant()
//	{
//		return new AMutexSyncDefinitionAssistantInterpreter(this);
//	}

	public ANamedTraceDefinitionAssistantInterpreter createANamedTraceDefinitionAssistant()
	{
		return new ANamedTraceDefinitionAssistantInterpreter(this);
	}

	// public APerSyncDefinitionAssistantInterpreter createAPerSyncDefinitionAssistant()
	// {
	// return new APerSyncDefinitionAssistantInterpreter(this);
	// }

	// public ARenamedDefinitionAssistantInterpreter createARenamedDefinitionAssistant()
	// {
	// return new ARenamedDefinitionAssistantInterpreter(this);
	// }

	// public ARepeatTraceDefinitionAssistantInterpreter createARepeatTraceDefinitionAssistant()
	// {
	// return new ARepeatTraceDefinitionAssistantInterpreter(this);
	// }

	public AStateDefinitionAssistantInterpreter createAStateDefinitionAssistant()
	{
		return new AStateDefinitionAssistantInterpreter(this);
	}

//	public ASystemClassDefinitionAssistantInterpreter createASystemClassDefinitionAssistant()
//	{
//		return new ASystemClassDefinitionAssistantInterpreter(this);
//	}

	// public AThreadDefinitionAssistantInterpreter createAThreadDefinitionAssistant()
	// {
	// return new AThreadDefinitionAssistantInterpreter(this);
	// }

	// public ATraceDefinitionTermAssistantInterpreter createATraceDefinitionTermAssistant()
	// {
	// return new ATraceDefinitionTermAssistantInterpreter(this);
	// }

	// public ATypeDefinitionAssistantInterpreter createATypeDefinitionAssistant()
	// {
	// return new ATypeDefinitionAssistantInterpreter(this);
	// }

	// public AUntypedDefinitionAssistantInterpreter createAUntypedDefinitionAssistant()
	// {
	// return new AUntypedDefinitionAssistantInterpreter(this);
	// }

	// public AValueDefinitionAssistantInterpreter createAValueDefinitionAssistant()
	// {
	// return new AValueDefinitionAssistantInterpreter(this);
	// }

	public PDefinitionAssistantInterpreter createPDefinitionAssistant()
	{
		return new PDefinitionAssistantInterpreter(this);
	}

	public PDefinitionListAssistantInterpreter createPDefinitionListAssistant()
	{
		return new PDefinitionListAssistantInterpreter(this);
	}

//	public PTraceCoreDefinitionAssistantInterpreter createPTraceCoreDefinitionAssistant()
//	{
//		return new PTraceCoreDefinitionAssistantInterpreter(this);
//	}

	public SClassDefinitionAssistantInterpreter createSClassDefinitionAssistant()
	{
		return new SClassDefinitionAssistantInterpreter(this);
	}

	// expression

	// public AApplyExpAssistantInterpreter createAApplyExpAssistant()
	// {
	// return new AApplyExpAssistantInterpreter(this);
	// }

	// public ACaseAlternativeAssistantInterpreter createACaseAlternativeAssistant()
	// {
	// return new ACaseAlternativeAssistantInterpreter(this);
	// }

	// public ACasesExpAssistantInterpreter createACasesExpAssistant()
	// {
	// return new ACasesExpAssistantInterpreter(this);
	// }

	// public ADefExpAssistantInterpreter createADefExpAssistant()
	// {
	// return new ADefExpAssistantInterpreter(this);
	// }

	// public AElseIfExpAssistantInterpreter createAElseIfExpAssistant()
	// {
	// return new AElseIfExpAssistantInterpreter(this);
	// }

	// public AExists1ExpAssistantInterpreter createAExists1ExpAssistant()
	// {
	// return new AExists1ExpAssistantInterpreter(this);
	// }

	// public AExistsExpAssistantInterpreter createAExistsExpAssistant()
	// {
	// return new AExistsExpAssistantInterpreter(this);
	// }

	public AFieldExpAssistantInterpreter createAFieldExpAssistant()
	{
		return new AFieldExpAssistantInterpreter(this);
	}

	// public AFieldNumberExpAssistantInterpreter createAFieldNumberExpAssistant()
	// {
	// return new AFieldNumberExpAssistantInterpreter(this);
	// }

	// public AForAllExpAssistantInterpreter createAForAllExpAssistant()
	// {
	// return new AForAllExpAssistantInterpreter(this);
	// }

	// public AFuncInstatiationExpAssistantInterpreter createAFuncInstatiationExpAssistant()
	// {
	// return new AFuncInstatiationExpAssistantInterpreter(this);
	// }

	// public AIfExpAssistantInterpreter createAIfExpAssistant()
	// {
	// return new AIfExpAssistantInterpreter(this);
	// }

	// public AIotaExpAssistantInterpreter createAIotaExpAssistant()
	// {
	// return new AIotaExpAssistantInterpreter(this);
	// }

	// public AIsExpAssistantInterpreter createAIsExpAssistant()
	// {
	// return new AIsExpAssistantInterpreter(this);
	// }

//	public AIsOfBaseClassExpAssistantInterpreter createAIsOfBaseClassExpAssistant()
//	{
//		return new AIsOfBaseClassExpAssistantInterpreter(this);
//	}

//	public AIsOfClassExpAssistantInterpreter createAIsOfClassExpAssistant()
//	{
//		return new AIsOfClassExpAssistantInterpreter(this);
//	}

	// public ALambdaExpAssistantInterpreter createALambdaExpAssistant()
	// {
	// return new ALambdaExpAssistantInterpreter(this);
	// }

	// public ALetBeStExpAssistantInterpreter createALetBeStExpAssistant()
	// {
	// return new ALetBeStExpAssistantInterpreter(this);
	// }

	// public ALetDefExpAssistantInterpreter createALetDefExpAssistant()
	// {
	// return new ALetDefExpAssistantInterpreter(this);
	// }

	// public AMapCompMapExpAssistantInterpreter createAMapCompMapExpAssistant()
	// {
	// return new AMapCompMapExpAssistantInterpreter(this);
	// }
	//
	// public AMapEnumMapExpAssistantInterpreter createAMapEnumMapExpAssistant()
	// {
	// return new AMapEnumMapExpAssistantInterpreter(this);
	// }
	//
	// public AMapletExpAssistantInterpreter createAMapletExpAssistant()
	// {
	// return new AMapletExpAssistantInterpreter(this);
	// }
	//
	// public AMkBasicExpAssistantInterpreter createAMkBasicExpAssistant()
	// {
	// return new AMkBasicExpAssistantInterpreter(this);
	// }
	//
	// public AMkTypeExpAssistantInterpreter createAMkTypeExpAssistant()
	// {
	// return new AMkTypeExpAssistantInterpreter(this);
	// }
	//
	// public AMuExpAssistantInterpreter createAMuExpAssistant()
	// {
	// return new AMuExpAssistantInterpreter(this);
	// }

	// public ANarrowExpAssistantInterpreter createANarrowExpAssistant()
	// {
	// return new ANarrowExpAssistantInterpreter(this);
	// }
	//
	// public ANewExpAssistantInterpreter createANewExpAssistant()
	// {
	// return new ANewExpAssistantInterpreter(this);
	// }

	public APostOpExpAssistantInterpreter createAPostOpExpAssistant()
	{
		return new APostOpExpAssistantInterpreter(this);
	}

	// public ARecordModifierAssistantInterpreter createARecordModifierAssistant()
	// {
	// return new ARecordModifierAssistantInterpreter(this);
	// }
	//
	// public ASameBaseClassExpAssistantInterpreter createASameBaseClassExpAssistant()
	// {
	// return new ASameBaseClassExpAssistantInterpreter(this);
	// }
	//
	// public ASameClassExpAssistantInterpreter createASameClassExpAssistant()
	// {
	// return new ASameClassExpAssistantInterpreter(this);
	// }

	// public ASeqCompSeqExpAssistantInterpreter createASeqCompSeqExpAssistant()
	// {
	// return new ASeqCompSeqExpAssistantInterpreter(this);
	// }

	// public ASeqEnumSeqExpAssistantInterpreter createASeqEnumSeqExpAssistant()
	// {
	// return new ASeqEnumSeqExpAssistantInterpreter(this);
	// }
	//
	// public ASetCompSetExpAssistantInterpreter createASetCompSetExpAssistant()
	// {
	// return new ASetCompSetExpAssistantInterpreter(this);
	// }
	//
	// public ASetEnumSetExpAssistantInterpreter createASetEnumSetExpAssistant()
	// {
	// return new ASetEnumSetExpAssistantInterpreter(this);
	// }
	//
	// public ASetRangeSetExpAssistantInterpreter createASetRangeSetExpAssistant()
	// {
	// return new ASetRangeSetExpAssistantInterpreter(this);
	// }
	//
	// public ASubseqExpAssistantInterpreter createASubseqExpAssistant()
	// {
	// return new ASubseqExpAssistantInterpreter(this);
	// }
	//
	// public ATupleExpAssistantInterpreter createATupleExpAssistant()
	// {
	// return new ATupleExpAssistantInterpreter(this);
	// }
	//
	// public AVariableExpAssistantInterpreter createAVariableExpAssistant()
	// {
	// return new AVariableExpAssistantInterpreter(this);
	// }

	public PExpAssistantInterpreter createPExpAssistant()
	{
		return new PExpAssistantInterpreter(this);
	}

	// public SBinaryExpAssistantInterpreter createSBinaryExpAssistant()
	// {
	// return new SBinaryExpAssistantInterpreter(this);
	// }
	//
	// public SMapExpAssistantInterpreter createSMapExpAssistant()
	// {
	// return new SMapExpAssistantInterpreter(this);
	// }
	//
	// public SSeqExpAssistantInterpreter createSSeqExpAssistant()
	// {
	// return new SSeqExpAssistantInterpreter(this);
	// }
	//
	// public SSetExpAssistantInterpreter createSSetExpAssistant()
	// {
	// return new SSetExpAssistantInterpreter(this);
	// }
	//
	// public SUnaryExpAssistantInterpreter createSUnaryExpAssistant()
	// {
	// return new SUnaryExpAssistantInterpreter(this);
	// }

	// module

	public AModuleModulesAssistantInterpreter createAModuleModulesAssistant()
	{
		return new AModuleModulesAssistantInterpreter(this);
	}

	public ModuleListAssistantInterpreter createModuleListAssistant()
	{
		return new ModuleListAssistantInterpreter(this);
	}

	// pattern

	// public ABooleanPatternAssistantInterpreter createABooleanPatternAssistant()
	// {
	// return new ABooleanPatternAssistantInterpreter(this);
	// }
	//
	// public ACharacterPatternAssistantInterpreter createACharacterPatternAssistant()
	// {
	// return new ACharacterPatternAssistantInterpreter(this);
	// }
	//
	// public AConcatenationPatternAssistantInterpreter createAConcatenationPatternAssistant()
	// {
	// return new AConcatenationPatternAssistantInterpreter(this);
	// }
	//
	// public AExpressionPatternAssistantInterpreter createAExpressionPatternAssistant()
	// {
	// return new AExpressionPatternAssistantInterpreter(this);
	// }
	//
	// public AIdentifierPatternAssistantInterpreter createAIdentifierPatternAssistant()
	// {
	// return new AIdentifierPatternAssistantInterpreter(this);
	// }
	//
	// public AIgnorePatternAssistantInterpreter createAIgnorePatternAssistant()
	// {
	// return new AIgnorePatternAssistantInterpreter(this);
	// }
	//
	// public AIntegerPatternAssistantInterpreter createAIntegerPatternAssistant()
	// {
	// return new AIntegerPatternAssistantInterpreter(this);
	// }
	//
	// public AMapPatternAssistantInterpreter createAMapPatternAssistant()
	// {
	// return new AMapPatternAssistantInterpreter(this);
	// }

	public AMapPatternMapletAssistantInterpreter createAMapPatternMapletAssistant()
	{
		return new AMapPatternMapletAssistantInterpreter(this);
	}

	// public AMapUnionPatternAssistantInterpreter createAMapUnionPatternAssistant()
	// {
	// return new AMapUnionPatternAssistantInterpreter(this);
	// }

	// public ANilPatternAssistantInterpreter createANilPatternAssistant()
	// {
	// return new ANilPatternAssistantInterpreter(this);
	// }

	// public ARealPatternAssistantInterpreter createARealPatternAssistant()
	// {
	// return new ARealPatternAssistantInterpreter(this);
	// }

	// public ARecordPatternAssistantInterpreter createARecordPatternAssistant()
	// {
	// return new ARecordPatternAssistantInterpreter(this);
	// }

	// public ASeqPatternAssistantInterpreter createASeqPatternAssistant()
	// {
	// return new ASeqPatternAssistantInterpreter(this);
	// }

	// public ASetBindAssistantInterpreter createASetBindAssistant()
	// {
	// return new ASetBindAssistantInterpreter(this);
	// }

	// public ASetMultipleBindAssistantInterpreter createASetMultipleBindAssistant()
	// {
	// return new ASetMultipleBindAssistantInterpreter(this);
	// }

	// public ASetPatternAssistantInterpreter createASetPatternAssistant()
	// {
	// return new ASetPatternAssistantInterpreter(this);
	// }

	// public AStringPatternAssistantInterpreter createAStringPatternAssistant()
	// {
	// return new AStringPatternAssistantInterpreter(this);
	// }

	// public ATuplePatternAssistantInterpreter createATuplePatternAssistant()
	// {
	// return new ATuplePatternAssistantInterpreter(this);
	// }

	// public ATypeBindAssistantInterpreter createATypeBindAssistant()
	// {
	// return new ATypeBindAssistantInterpreter(this);
	// }

	// public ATypeMultipleBindAssistantInterpreter createATypeMultipleBindAssistant()
	// {
	// return new ATypeMultipleBindAssistantInterpreter(this);
	// }

	// public AUnionPatternAssistantInterpreter createAUnionPatternAssistant()
	// {
	// return new AUnionPatternAssistantInterpreter(this);
	// }

	public PBindAssistantInterpreter createPBindAssistant()
	{
		return new PBindAssistantInterpreter(this);
	}

	public PMultipleBindAssistantInterpreter createPMultipleBindAssistant()
	{
		return new PMultipleBindAssistantInterpreter(this);
	}

	public PPatternAssistantInterpreter createPPatternAssistant()
	{
		return new PPatternAssistantInterpreter(this);
	}

//	public PPatternListAssistantInterpreter createPPatternListAssistant()
//	{
//		return new PPatternListAssistantInterpreter(this);
//	}

	// statement

	// public AAlwaysStmAssistantInterpreter createAAlwaysStmAssistant()
	// {
	// return new AAlwaysStmAssistantInterpreter(this);
	// }
	//
	// public AAssignmentStmAssistantInterpreter createAAssignmentStmAssistant()
	// {
	// return new AAssignmentStmAssistantInterpreter(this);
	// }
	//
	// public AAtomicStmAssistantInterpreter createAAtomicStmAssistant()
	// {
	// return new AAtomicStmAssistantInterpreter(this);
	// }
	//
	// public ACallObjectStatementAssistantInterpreter createACallObjectStatementAssistant()
	// {
	// return new ACallObjectStatementAssistantInterpreter(this);
	// }
	//
	// public ACallStmAssistantInterpreter createACallStmAssistant()
	// {
	// return new ACallStmAssistantInterpreter(this);
	// }

	//	public ACaseAlternativeStmAssistantInterpreter createACaseAlternativeStmAssistant()
	//	{
	//		return new ACaseAlternativeStmAssistantInterpreter(this);
	//	}

	// public ACasesStmAssistantInterpreter createACasesStmAssistant()
	// {
	// return new ACasesStmAssistantInterpreter(this);
	// }
	//
	// public ACyclesStmAssistantInterpreter createACyclesStmAssistant()
	// {
	// return new ACyclesStmAssistantInterpreter(this);
	// }
	//
	// public ADurationStmAssistantInterpreter createADurationStmAssistant()
	// {
	// return new ADurationStmAssistantInterpreter(this);
	// }
	//
	// public AElseIfStmAssistantInterpreter createAElseIfStmAssistant()
	// {
	// return new AElseIfStmAssistantInterpreter(this);
	// }
	//
	// public AExitStmAssistantInterpreter createAExitStmAssistant()
	// {
	// return new AExitStmAssistantInterpreter(this);
	// }
	//
	// public AForAllStmAssistantInterpreter createAForAllStmAssistant()
	// {
	// return new AForAllStmAssistantInterpreter(this);
	// }
	//
	// public AForIndexStmAssistantInterpreter createAForIndexStmAssistant()
	// {
	// return new AForIndexStmAssistantInterpreter(this);
	// }
	//
	// public AForPatternBindStmAssitantInterpreter createAForPatternBindStmAssitant()
	// {
	// return new AForPatternBindStmAssitantInterpreter(this);
	// }
	//
	// public AIfStmAssistantInterpreter createAIfStmAssistant()
	// {
	// return new AIfStmAssistantInterpreter(this);
	// }
	//
	// public ALetBeStStmAssistantInterpreter createALetBeStStmAssistant()
	// {
	// return new ALetBeStStmAssistantInterpreter(this);
	// }
	//
	// public AReturnStmAssistantInterpreter createAReturnStmAssistant()
	// {
	// return new AReturnStmAssistantInterpreter(this);
	// }

//	public AStartStmAssistantInterpreter createAStartStmAssistant()
//	{
//		return new AStartStmAssistantInterpreter(this);
//	}

	// public ATixeStmAssistantInterpreter createATixeStmAssistant()
	// {
	// return new ATixeStmAssistantInterpreter(this);
	// }
	//
//	public ATixeStmtAlternativeAssistantInterpreter createATixeStmtAlternativeAssistant()
//	{
//		return new ATixeStmtAlternativeAssistantInterpreter(this);
//	}

	//
	// public ATrapStmAssistantInterpreter createATrapStmAssistant()
	// {
	// return new ATrapStmAssistantInterpreter(this);
	// }
	//
	// public AWhileStmAssistantInterpreter createAWhileStmAssistant()
	// {
	// return new AWhileStmAssistantInterpreter(this);
	// }

	public PStmAssistantInterpreter createPStmAssistant()
	{
		return new PStmAssistantInterpreter(this);
	}

	// public SLetDefStmAssistantInterpreter createSLetDefStmAssistant()
	// {
	// return new SLetDefStmAssistantInterpreter(this);
	// }

	// type

	// public ABooleanBasicTypeAssistantInterpreter createABooleanBasicTypeAssistant()
	// {
	// return new ABooleanBasicTypeAssistantInterpreter(this);
	// }
	//
	// public AInMapMapTypeAssistantInterpreter createAInMapMapTypeAssistant()
	// {
	// return new AInMapMapTypeAssistantInterpreter(this);
	// }
	//
	// public ANamedInvariantTypeAssistantInterpreter createANamedInvariantTypeAssistant()
	// {
	// return new ANamedInvariantTypeAssistantInterpreter(this);
	// }
	//
	// public AOptionalTypeAssistantInterpreter createAOptionalTypeAssistant()
	// {
	// return new AOptionalTypeAssistantInterpreter(this);
	// }
	//
	// public AParameterTypeAssistantInterpreter createAParameterTypeAssistant()
	// {
	// return new AParameterTypeAssistantInterpreter(this);
	// }
	//
	// public AProductTypeAssistantInterpreter createAProductTypeAssistant()
	// {
	// return new AProductTypeAssistantInterpreter(this);
	// }
	//
	// public AQuoteTypeAssistantInterpreter createAQuoteTypeAssistant()
	// {
	// return new AQuoteTypeAssistantInterpreter(this);
	// }
	//
	// public ARecordInvariantTypeAssistantInterpreter createARecordInvariantTypeAssistant()
	// {
	// return new ARecordInvariantTypeAssistantInterpreter(this);
	// }
	//
	// public ASetTypeAssistantInterpreter createASetTypeAssistant()
	// {
	// return new ASetTypeAssistantInterpreter(this);
	// }
	//
	// public AUnionTypeAssistantInterpreter createAUnionTypeAssistant()
	// {
	// return new AUnionTypeAssistantInterpreter(this);
	// }

	public PTypeAssistantInterpreter createPTypeAssistant()
	{
		return new PTypeAssistantInterpreter(this);
	}

//	public PTypeListAssistant createPTypeListAssistant()
//	{
//		return new PTypeListAssistant(this);
//	}

	// public SBasicTypeAssistantInterpreter createSBasicTypeAssistant()
	// {
	// return new SBasicTypeAssistantInterpreter(this);
	// }

	public SInvariantTypeAssistantInterpreter createSInvariantTypeAssistant()
	{
		return new SInvariantTypeAssistantInterpreter(this);
	}

	// public SMapTypeAssistantInterpreter createSMapTypeAssistant()
	// {
	// return new SMapTypeAssistantInterpreter(this);
	// }

	@Override
	public IAnswer<LexNameList> getOldNameCollector()
	{
		return new OldNameCollector(this);
	}

	@Override
	public QuestionAnswerAdaptor<Context, ValueList> getBindValuesCollector()
	{
		return new MultipleBindValuesCollector(this);
	}

	@Override
	public QuestionAnswerAdaptor<ObjectContext, ValueList> getValueCollector()
	{
		return new ValueCollector(this);
	}

	@Override
	public IAnswer<List<AIdentifierPattern>> getIdentifierPatternFinder()
	{
		return new IdentifierPatternFinder(this);
	}

	@Override
	public IAnswer<Integer> getLengthFinder()
	{
		return new LengthFinder(this);
	}

	@Override
	public IAnswer<Boolean> getConstrainedPatternChecker()
	{
		return new ConstrainedPatternChecker(this);
	}

	@Override
	public QuestionAnswerAdaptor<AllNamedValuesLocator.Newquestion, List<NameValuePairList>> getAllNamedValuesLocator()
	{
		return new AllNamedValuesLocator(this);
	}

	@Override
	public QuestionAnswerAdaptor<Context, NameValuePairList> getNamedValueLister()
	{
		return new NamedValueLister(this);
	}

	@Override
	public QuestionAnswerAdaptor<Integer, PExp> getExpressionFinder()
	{
		return new ExpressionFinder(this);
	}

	@Override
	public IQuestionAnswer<ObjectContext, ValueList> getValuesDefinitionLocator()
	{
		return new ValuesDefinitionLocator(this);
	}

	@Override
	public IAnswer<Boolean> getTypeDefinitionChecker()
	{
		return new TypeDefinitionChecker(this);
	}

	@Override
	public IAnswer<Boolean> getDefinitionRunTimeChecker()
	{
		return new DefinitionRunTimeChecker(this);
	}

	@Override
	public IAnswer<Boolean> getDefintionValueChecker()
	{
		return new DefinitionValueChecker(this);
	}

	@Override
	public IAnswer<Boolean> getInstanceVariableChecker()
	{
		return new InstanceVariableChecker(this);
	}

	@Override
	public IQuestionAnswer<Integer, PStm> getDefinitionStatementFinder()
	{
		return new DefinitionStatementFinder(this);
	}

	@Override
	public IQuestionAnswer<Context, TraceNode> getTraceExpander()
	{
		return new TraceExpander(this);
	}

	@Override
	public IQuestionAnswer<Integer, PExp> getStatementExpressionFinder()
	{
		return new StatementExpressionFinder(this);
	}

	@Override
	public IQuestionAnswer<Integer, PStm> getStatementFinder()
	{
		return new StatementFinder(this);
	}

	@Override
	public IQuestionAnswer<ObjectContext, ValueList> getExpressionValueCollector()
	{
		return new ExpressionValueCollector(this);
	}

	@Override
	public IQuestionAnswer<Integer, PExp> getExpExpressionFinder()
	{
		return new ExpExpressionFinder(this);
	}

	@Override
	public IAnswer<List<PExp>> getSubExpressionsLocator()
	{
		return new SubExpressionsLocator(this);
	}

	@Override
	public IQuestionAnswer<Context, ValueList> getSingleBindValuesCollector()
	{
		return new SingleBindValuesCollector(this);
	}

	@Override
	public IQuestionAnswer<ObjectContext, ValueList> getBindValueCollector()
	{
		return new BindValueCollector(this);
	}

	@Override
	public IQuestionAnswer<Context, ValueList> getAllValuesCollector()
	{
		return new AllValuesCollector(this);
	}

}
