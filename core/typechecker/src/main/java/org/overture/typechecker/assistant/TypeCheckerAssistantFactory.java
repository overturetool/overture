package org.overture.typechecker.assistant;

import java.util.List;

import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.AstAssistantFactory;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.definition.ABusClassDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AClassInvariantDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ACpuClassDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AEqualsDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AExplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AExternalDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImportedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AInheritedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AInstanceVariableDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ALocalDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AMultiBindListDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AMutexSyncDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ANamedTraceDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.APerSyncDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ARenamedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AStateDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ASystemClassDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AThreadDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ATypeDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AUntypedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AValueDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionSet;
import org.overture.typechecker.assistant.definition.PTraceDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;
import org.overture.typechecker.assistant.expression.AApplyExpAssistantTC;
import org.overture.typechecker.assistant.expression.ACaseAlternativeAssistantTC;
import org.overture.typechecker.assistant.expression.PExpAssistantTC;
import org.overture.typechecker.assistant.expression.SBinaryExpAssistantTC;
import org.overture.typechecker.assistant.module.AAllImportAssistantTC;
import org.overture.typechecker.assistant.module.AFromModuleImportsAssistantTC;
import org.overture.typechecker.assistant.module.AModuleExportsAssistantTC;
import org.overture.typechecker.assistant.module.AModuleImportsAssistantTC;
import org.overture.typechecker.assistant.module.AModuleModulesAssistantTC;
import org.overture.typechecker.assistant.module.ATypeImportAssistantTC;
import org.overture.typechecker.assistant.module.AValueValueImportAssistantTC;
import org.overture.typechecker.assistant.module.PExportAssistantTC;
import org.overture.typechecker.assistant.module.PImportAssistantTC;
import org.overture.typechecker.assistant.module.SValueImportAssistantTC;
import org.overture.typechecker.assistant.pattern.ABooleanPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ACharacterPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AConcatenationPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AExpressionPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AIdentifierPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AIgnorePatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AIntegerPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AMapPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AMapUnionPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AMapletPatternMapletAssistantTC;
import org.overture.typechecker.assistant.pattern.ANilPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.APatternTypePairAssistant;
import org.overture.typechecker.assistant.pattern.AQuotePatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ARealPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ARecordPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ASeqPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ASetBindAssistantTC;
import org.overture.typechecker.assistant.pattern.ASetMultipleBindAssistantTC;
import org.overture.typechecker.assistant.pattern.ASetPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AStringPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ATuplePatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ATypeBindAssistantTC;
import org.overture.typechecker.assistant.pattern.ATypeMultipleBindAssistantTC;
import org.overture.typechecker.assistant.pattern.AUnionPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.PBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;
import org.overture.typechecker.assistant.pattern.PatternListTC;
import org.overture.typechecker.assistant.statement.ABlockSimpleBlockStmAssistantTC;
import org.overture.typechecker.assistant.statement.ACallObjectStatementAssistantTC;
import org.overture.typechecker.assistant.statement.ACallStmAssistantTC;
import org.overture.typechecker.assistant.statement.AExternalClauseAssistantTC;
import org.overture.typechecker.assistant.statement.ANonDeterministicSimpleBlockStmAssistantTC;
import org.overture.typechecker.assistant.statement.PStateDesignatorAssistantTC;
import org.overture.typechecker.assistant.statement.PStmAssistantTC;
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
import org.overture.typechecker.utilities.DefinitionCollector;
import org.overture.typechecker.utilities.DefinitionEqualityChecker;
import org.overture.typechecker.utilities.DefinitionTypeFinder;
import org.overture.typechecker.utilities.ExitTypeCollector;
import org.overture.typechecker.utilities.SelfDefinitionFinder;
import org.overture.typechecker.utilities.TypeFinder;
import org.overture.typechecker.utilities.TypeUtils;
import org.overture.typechecker.utilities.VariableNameCollector;
import org.overture.typechecker.utilities.TypeFinder.Newquestion;

public class TypeCheckerAssistantFactory extends AstAssistantFactory implements
		ITypeCheckerAssistantFactory
{
	static
	{
		// FIXME: remove this when conversion to factory obtained assistants are completed.
		// init(new AstAssistantFactory());
		init(new TypeCheckerAssistantFactory());
	}

	// Type

	@Override
	public AApplyObjectDesignatorAssistantTC createAApplyObjectDesignatorAssistant()
	{
		return new AApplyObjectDesignatorAssistantTC(this);
	}

	@Override
	public ABracketTypeAssistantTC createABracketTypeAssistant()
	{
		return new ABracketTypeAssistantTC(this);
	}

	@Override
	public AClassTypeAssistantTC createAClassTypeAssistant()
	{
		return new AClassTypeAssistantTC(this);
	}

	@Override
	public AFieldFieldAssistantTC createAFieldFieldAssistant()
	{
		return new AFieldFieldAssistantTC(this);
	}

	@Override
	public AFunctionTypeAssistantTC createAFunctionTypeAssistant()
	{
		return new AFunctionTypeAssistantTC(this);
	}

	@Override
	public AInMapMapTypeAssistantTC createAInMapMapTypeAssistant()
	{
		return new AInMapMapTypeAssistantTC(this);
	}

	@Override
	public AMapMapTypeAssistantTC createAMapMapTypeAssistant()
	{
		return new AMapMapTypeAssistantTC(this);
	}

	@Override
	public ANamedInvariantTypeAssistantTC createANamedInvariantTypeAssistant()
	{
		return new ANamedInvariantTypeAssistantTC(this);
	}

	@Override
	public AOperationTypeAssistantTC createAOperationTypeAssistant()
	{
		return new AOperationTypeAssistantTC(this);
	}

	@Override
	public AOptionalTypeAssistantTC createAOptionalTypeAssistant()
	{
		return new AOptionalTypeAssistantTC(this);
	}

	@Override
	public AParameterTypeAssistantTC createAParameterTypeAssistant()
	{
		return new AParameterTypeAssistantTC(this);
	}

	@Override
	public APatternListTypePairAssistantTC createAPatternListTypePairAssistant()
	{
		return new APatternListTypePairAssistantTC(this);
	}

	@Override
	public AProductTypeAssistantTC createAProductTypeAssistant()
	{
		return new AProductTypeAssistantTC(this);
	}

	@Override
	public AQuoteTypeAssistantTC createAQuoteTypeAssistant()
	{
		return new AQuoteTypeAssistantTC(this);
	}

	@Override
	public ARecordInvariantTypeAssistantTC createARecordInvariantTypeAssistant()
	{
		return new ARecordInvariantTypeAssistantTC(this);
	}

	@Override
	public ASeq1SeqTypeAssistantTC createASeq1SeqTypeAssistant()
	{
		return new ASeq1SeqTypeAssistantTC(this);
	}

	@Override
	public ASeqSeqTypeAssistantTC createASeqSeqTypeAssistant()
	{
		return new ASeqSeqTypeAssistantTC(this);
	}

	@Override
	public ASetTypeAssistantTC createASetTypeAssistant()
	{
		return new ASetTypeAssistantTC(this);
	}

	@Override
	public AUndefinedTypeAssistantTC createAUndefinedTypeAssistant()
	{
		return new AUndefinedTypeAssistantTC(this);
	}

	@Override
	public AUnionTypeAssistantTC createAUnionTypeAssistant()
	{
		return new AUnionTypeAssistantTC(this);
	}

	@Override
	public AUnknownTypeAssistantTC createAUnknownTypeAssistant()
	{
		return new AUnknownTypeAssistantTC(this);
	}

	@Override
	public AUnresolvedTypeAssistantTC createAUnresolvedTypeAssistant()
	{
		return new AUnresolvedTypeAssistantTC(this);
	}

	@Override
	public AVoidReturnTypeAssistantTC createAVoidReturnTypeAssistant()
	{
		return new AVoidReturnTypeAssistantTC(this);
	}

	@Override
	public AVoidTypeAssistantTC createAVoidTypeAssistant()
	{
		return new AVoidTypeAssistantTC(this);
	}

	@Override
	public PTypeAssistantTC createPTypeAssistant()
	{
		return new PTypeAssistantTC(this);
	}

	@Override
	public SMapTypeAssistantTC createSMapTypeAssistant()
	{
		return new SMapTypeAssistantTC(this);
	}

	@Override
	public SNumericBasicTypeAssistantTC createSNumericBasicTypeAssistant()
	{
		return new SNumericBasicTypeAssistantTC(this);
	}

	@Override
	public SSeqTypeAssistantTC createSSeqTypeAssistant()
	{
		return new SSeqTypeAssistantTC(this);
	}

	// definition

	// @Override
	// public AAssignmentDefinitionAssistantTC createAAssignmentDefinitionAssistant()
	// {
	// return new AAssignmentDefinitionAssistantTC(this);
	// }

	@Override
	public ABusClassDefinitionAssistantTC createABusClassDefinitionAssistant()
	{
		return new ABusClassDefinitionAssistantTC(this);
	}

	@Override
	public AClassInvariantDefinitionAssistantTC createAClassInvariantDefinitionAssistant()
	{
		return new AClassInvariantDefinitionAssistantTC(this);
	}

	@Override
	public ACpuClassDefinitionAssistantTC createACpuClassDefinitionAssistant()
	{
		return new ACpuClassDefinitionAssistantTC(this);
	}

	@Override
	public AEqualsDefinitionAssistantTC createAEqualsDefinitionAssistant()
	{
		return new AEqualsDefinitionAssistantTC(this);
	}

	@Override
	public AExplicitFunctionDefinitionAssistantTC createAExplicitFunctionDefinitionAssistant()
	{
		return new AExplicitFunctionDefinitionAssistantTC(this);
	}

	@Override
	public AExplicitOperationDefinitionAssistantTC createAExplicitOperationDefinitionAssistant()
	{
		return new AExplicitOperationDefinitionAssistantTC(this);
	}

	@Override
	public AExternalDefinitionAssistantTC createAExternalDefinitionAssistant()
	{
		return new AExternalDefinitionAssistantTC(this);
	}

	@Override
	public AImplicitFunctionDefinitionAssistantTC createAImplicitFunctionDefinitionAssistant()
	{
		return new AImplicitFunctionDefinitionAssistantTC(this);
	}

	@Override
	public AImplicitOperationDefinitionAssistantTC createAImplicitOperationDefinitionAssistant()
	{
		return new AImplicitOperationDefinitionAssistantTC(this);
	}

	@Override
	public AImportedDefinitionAssistantTC createAImportedDefinitionAssistant()
	{
		return new AImportedDefinitionAssistantTC(this);
	}

	@Override
	public AInheritedDefinitionAssistantTC createAInheritedDefinitionAssistant()
	{
		return new AInheritedDefinitionAssistantTC(this);
	}

	@Override
	public AInstanceVariableDefinitionAssistantTC createAInstanceVariableDefinitionAssistant()
	{
		return new AInstanceVariableDefinitionAssistantTC(this);
	}

	@Override
	public ALocalDefinitionAssistantTC createALocalDefinitionAssistant()
	{
		return new ALocalDefinitionAssistantTC(this);
	}

	@Override
	public AMultiBindListDefinitionAssistantTC createAMultiBindListDefinitionAssistant()
	{
		return new AMultiBindListDefinitionAssistantTC(this);
	}

	@Override
	public AMutexSyncDefinitionAssistantTC createAMutexSyncDefinitionAssistant()
	{
		return new AMutexSyncDefinitionAssistantTC(this);
	}

	@Override
	public ANamedTraceDefinitionAssistantTC createANamedTraceDefinitionAssistant()
	{
		return new ANamedTraceDefinitionAssistantTC(this);
	}

	@Override
	public APerSyncDefinitionAssistantTC createAPerSyncDefinitionAssistant()
	{
		return new APerSyncDefinitionAssistantTC(this);
	}

	@Override
	public ARenamedDefinitionAssistantTC createARenamedDefinitionAssistant()
	{
		return new ARenamedDefinitionAssistantTC(this);
	}

	@Override
	public AStateDefinitionAssistantTC createAStateDefinitionAssistant()
	{
		return new AStateDefinitionAssistantTC(this);
	}

	@Override
	public ASystemClassDefinitionAssistantTC createASystemClassDefinitionAssistant()
	{
		return new ASystemClassDefinitionAssistantTC(this);
	}

	@Override
	public AThreadDefinitionAssistantTC createAThreadDefinitionAssistant()
	{
		return new AThreadDefinitionAssistantTC(this);
	}

	@Override
	public ATypeDefinitionAssistantTC createATypeDefinitionAssistant()
	{
		return new ATypeDefinitionAssistantTC(this);
	}

	@Override
	public AUntypedDefinitionAssistantTC createAUntypedDefinitionAssistant()
	{
		return new AUntypedDefinitionAssistantTC(this);
	}

	@Override
	public AValueDefinitionAssistantTC createAValueDefinitionAssistant()
	{
		return new AValueDefinitionAssistantTC(this);
	}

	@Override
	public PAccessSpecifierAssistantTC createPAccessSpecifierAssistant()
	{
		return new PAccessSpecifierAssistantTC(this);
	}

	@Override
	public PDefinitionAssistantTC createPDefinitionAssistant()
	{
		return new PDefinitionAssistantTC(this);
	}

	@Override
	public PDefinitionListAssistantTC createPDefinitionListAssistant()
	{
		return new PDefinitionListAssistantTC(this);
	}

	@Override
	public PDefinitionSet createPDefinitionSet()
	{
		return new PDefinitionSet(this);
	}

	@Override
	public PTraceDefinitionAssistantTC createPTraceDefinitionAssistant()
	{
		return new PTraceDefinitionAssistantTC(this);
	}

	@Override
	public SClassDefinitionAssistantTC createSClassDefinitionAssistant()
	{
		return new SClassDefinitionAssistantTC(this);
	}

	// expression

	@Override
	public AApplyExpAssistantTC createAApplyExpAssistant()
	{
		return new AApplyExpAssistantTC(this);
	}

	@Override
	public ACaseAlternativeAssistantTC createACaseAlternativeAssistant()
	{
		return new ACaseAlternativeAssistantTC(this);
	}

	@Override
	public PExpAssistantTC createPExpAssistant()
	{
		return new PExpAssistantTC(this);
	}

	@Override
	public SBinaryExpAssistantTC createSBinaryExpAssistant()
	{
		return new SBinaryExpAssistantTC(this);
	}

	// module

	@Override
	public AAllImportAssistantTC createAAllImportAssistant()
	{
		return new AAllImportAssistantTC(this);
	}

	@Override
	public AFromModuleImportsAssistantTC createAFromModuleImportsAssistant()
	{
		return new AFromModuleImportsAssistantTC(this);
	}

	@Override
	public AModuleExportsAssistantTC createAModuleExportsAssistant()
	{
		return new AModuleExportsAssistantTC(this);
	}

	@Override
	public AModuleImportsAssistantTC createAModuleImportsAssistant()
	{
		return new AModuleImportsAssistantTC(this);
	}

	@Override
	public AModuleModulesAssistantTC createAModuleModulesAssistant()
	{
		return new AModuleModulesAssistantTC(this);
	}

	@Override
	public ATypeImportAssistantTC createATypeImportAssistant()
	{
		return new ATypeImportAssistantTC(this);
	}

	@Override
	public AValueValueImportAssistantTC createAValueValueImportAssistant()
	{
		return new AValueValueImportAssistantTC(this);
	}

	@Override
	public PExportAssistantTC createPExportAssistant()
	{
		return new PExportAssistantTC(this);
	}

	@Override
	public PImportAssistantTC createPImportAssistant()
	{
		return new PImportAssistantTC(this);
	}

	@Override
	public SValueImportAssistantTC createSValueImportAssistant()
	{
		return new SValueImportAssistantTC(this);
	}

	// pattern

	@Override
	public ABooleanPatternAssistantTC createABooleanPatternAssistant()
	{
		return new ABooleanPatternAssistantTC(this);
	}

	@Override
	public ACharacterPatternAssistantTC createACharacterPatternAssistant()
	{
		return new ACharacterPatternAssistantTC(this);
	}

	@Override
	public AConcatenationPatternAssistantTC createAConcatenationPatternAssistant()
	{
		return new AConcatenationPatternAssistantTC(this);
	}

	@Override
	public AExpressionPatternAssistantTC createAExpressionPatternAssistant()
	{
		return new AExpressionPatternAssistantTC(this);
	}

	@Override
	public AIdentifierPatternAssistantTC createAIdentifierPatternAssistant()
	{
		return new AIdentifierPatternAssistantTC(this);
	}

	@Override
	public AIgnorePatternAssistantTC createAIgnorePatternAssistant()
	{
		return new AIgnorePatternAssistantTC(this);
	}

	@Override
	public AIntegerPatternAssistantTC createAIntegerPatternAssistant()
	{
		return new AIntegerPatternAssistantTC(this);
	}

	@Override
	public AMapletPatternMapletAssistantTC createAMapletPatternMapletAssistant()
	{
		return new AMapletPatternMapletAssistantTC(this);
	}

	@Override
	public AMapPatternAssistantTC createAMapPatternAssistant()
	{
		return new AMapPatternAssistantTC(this);
	}

	@Override
	public AMapUnionPatternAssistantTC createAMapUnionPatternAssistant()
	{
		return new AMapUnionPatternAssistantTC(this);
	}

	@Override
	public ANilPatternAssistantTC createANilPatternAssistant()
	{
		return new ANilPatternAssistantTC(this);
	}

	@Override
	public APatternTypePairAssistant createAPatternTypePairAssistant()
	{
		return new APatternTypePairAssistant(this);
	}

	@Override
	public AQuotePatternAssistantTC createAQuotePatternAssistant()
	{
		return new AQuotePatternAssistantTC(this);
	}

	@Override
	public ARealPatternAssistantTC createARealPatternAssistant()
	{
		return new ARealPatternAssistantTC(this);
	}

	@Override
	public ARecordPatternAssistantTC createARecordPatternAssistant()
	{
		return new ARecordPatternAssistantTC(this);
	}

	@Override
	public ASeqPatternAssistantTC createASeqPatternAssistant()
	{
		return new ASeqPatternAssistantTC(this);
	}

	@Override
	public ASetBindAssistantTC createASetBindAssistant()
	{
		return new ASetBindAssistantTC(this);
	}

	@Override
	public ASetMultipleBindAssistantTC createASetMultipleBindAssistant()
	{
		return new ASetMultipleBindAssistantTC(this);
	}

	@Override
	public ASetPatternAssistantTC createASetPatternAssistant()
	{
		return new ASetPatternAssistantTC(this);
	}

	@Override
	public AStringPatternAssistantTC createAStringPatternAssistant()
	{
		return new AStringPatternAssistantTC(this);
	}

	@Override
	public ATuplePatternAssistantTC createATuplePatternAssistant()
	{
		return new ATuplePatternAssistantTC(this);
	}

	@Override
	public ATypeBindAssistantTC createATypeBindAssistant()
	{
		return new ATypeBindAssistantTC(this);
	}

	@Override
	public ATypeMultipleBindAssistantTC createATypeMultipleBindAssistant()
	{
		return new ATypeMultipleBindAssistantTC(this);
	}

	@Override
	public AUnionPatternAssistantTC createAUnionPatternAssistant()
	{
		return new AUnionPatternAssistantTC(this);
	}

	@Override
	public PatternListTC createPatternList()
	{
		return new PatternListTC(this);
	}

	@Override
	public PBindAssistantTC createPBindAssistant()
	{
		return new PBindAssistantTC(this);
	}

	@Override
	public PMultipleBindAssistantTC createPMultipleBindAssistant()
	{
		return new PMultipleBindAssistantTC(this);
	}

	@Override
	public PPatternAssistantTC createPPatternAssistant()
	{
		return new PPatternAssistantTC(this);
	}

	@Override
	public PPatternBindAssistantTC createPPatternBindAssistant()
	{
		return new PPatternBindAssistantTC(this);
	}

	@Override
	public PPatternListAssistantTC createPPatternListAssistant()
	{
		return new PPatternListAssistantTC(this);
	}

	// statement

	// @Override
	// public AAlwaysStmAssistantTC createAAlwaysStmAssistant()
	// {
	// return new AAlwaysStmAssistantTC(this);
	// }
	//
	// @Override
	// public AAssignmentStmAssistantTC createAAssignmentStmAssistant()
	// {
	// return new AAssignmentStmAssistantTC(this);
	// }

	@Override
	public ABlockSimpleBlockStmAssistantTC createABlockSimpleBlockStmAssistant()
	{
		return new ABlockSimpleBlockStmAssistantTC(this);
	}

	@Override
	public ACallObjectStatementAssistantTC createACallObjectStatementAssistant()
	{
		return new ACallObjectStatementAssistantTC(this);
	}

	@Override
	public ACallStmAssistantTC createACallStmAssistant()
	{
		return new ACallStmAssistantTC(this);
	}

	// @Override
	// public ACaseAlternativeStmAssistantTC createACaseAlternativeStmAssistant()
	// {
	// return new ACaseAlternativeStmAssistantTC(this);
	// }
	//
	// @Override
	// public ACasesStmAssistantTC createACasesStmAssistant()
	// {
	// return new ACasesStmAssistantTC(this);
	// }
	//
	// @Override
	// public AElseIfStmAssistantTC createAElseIfStmAssistant()
	// {
	// return new AElseIfStmAssistantTC(this);
	// }
	//
	// @Override
	// public AExitStmAssistantTC createAExitStmAssistant()
	// {
	// return new AExitStmAssistantTC(this);
	// }

	@Override
	public AExternalClauseAssistantTC createAExternalClauseAssistant()
	{
		return new AExternalClauseAssistantTC(this);
	}

	// @Override
	// public AForAllStmAssistantTC createAForAllStmAssistant()
	// {
	// return new AForAllStmAssistantTC(this);
	// }
	//
	// @Override
	// public AForIndexStmAssistantTC createAForIndexStmAssistant()
	// {
	// return new AForIndexStmAssistantTC(this);
	// }

	// @Override
	// public AForPatternBindStmAssitantTC createAForPatternBindStmAssitant()
	// {
	// return new AForPatternBindStmAssitantTC(this);
	// }
	//
	// @Override
	// public AIfStmAssistantTC createAIfStmAssistant()
	// {
	// return new AIfStmAssistantTC(this);
	// }
	//
	// @Override
	// public ALetBeStStmAssistantTC createALetBeStStmAssistant()
	// {
	// return new ALetBeStStmAssistantTC(this);
	// }

	@Override
	public ANonDeterministicSimpleBlockStmAssistantTC createANonDeterministicSimpleBlockStmAssistant()
	{
		return new ANonDeterministicSimpleBlockStmAssistantTC(this);
	}

	// @Override
	// public AReturnStmAssistantTC createAReturnStmAssistant()
	// {
	// return new AReturnStmAssistantTC(this);
	// }
	//
	// @Override
	// public ATixeStmAssistantTC createATixeStmAssistant()
	// {
	// return new ATixeStmAssistantTC(this);
	// }

	// @Override
	// public ATrapStmAssistantTC createATrapStmAssistant()
	// {
	// return new ATrapStmAssistantTC(this);
	// }
	//
	// @Override
	// public AWhileStmAssistantTC createAWhileStmAssistant()
	// {
	// return new AWhileStmAssistantTC(this);
	// }

	@Override
	public PStateDesignatorAssistantTC createPStateDesignatorAssistant()
	{
		return new PStateDesignatorAssistantTC(this);
	}

	@Override
	public PStmAssistantTC createPStmAssistant()
	{
		return new PStmAssistantTC(this);
	}

	// @Override
	// public SLetDefStmAssistantTC createSLetDefStmAssistant()
	// {
	// return new SLetDefStmAssistantTC(this);
	// }
	//
	// @Override
	// public SSimpleBlockStmAssistantTC createSSimpleBlockStmAssistant()
	// {
	// return new SSimpleBlockStmAssistantTC(this);
	// }

	/* New visitor utilities */

	@Override
	public IAnswer<List<PDefinition>> getDefinitionCollector()
	{
		return new DefinitionCollector(this);
	}

	@Override
	public IAnswer<PType> getDefinitionTypeFinder()
	{
		return new DefinitionTypeFinder(this);
	}

	@Override
	public IQuestionAnswer<Object, Boolean> getDefinitionEqualityChecker()
	{
		return new DefinitionEqualityChecker(this);
	}

	@Override
	public AnswerAdaptor<Boolean> getMapBasisChecker()
	{
		return new TypeUtils.MapBasisChecker(this);
	}

	@Override
	public IAnswer<LexNameList> getVariableNameCollector()
	{
		return new VariableNameCollector(this);
	}

	@Override
	public IAnswer<PDefinition> getSelfDefinitionFinder()
	{
		return new SelfDefinitionFinder(this);
	}

	@Override
	public IAnswer<PTypeSet> getExitTypeCollector()
	{
		return new ExitTypeCollector(this);
	}
	
	@Override
	public IQuestionAnswer<Newquestion, PDefinition> getTypeFinder()
	{
		return new TypeFinder(this);
	}
	

}
