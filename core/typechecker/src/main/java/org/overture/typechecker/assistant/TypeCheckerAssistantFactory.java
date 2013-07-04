package org.overture.typechecker.assistant;

import org.overture.ast.assistant.AstAssistantFactory;
import org.overture.typechecker.assistant.definition.AAssignmentDefinitionAssistantTC;
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
import org.overture.typechecker.assistant.expression.ACasesExpAssistantTC;
import org.overture.typechecker.assistant.expression.AElementsUnaryExpAssistantTC;
import org.overture.typechecker.assistant.expression.AElseIfExpAssistantTC;
import org.overture.typechecker.assistant.expression.AExists1ExpAssistantTC;
import org.overture.typechecker.assistant.expression.AExistsExpAssistantTC;
import org.overture.typechecker.assistant.expression.AFieldExpAssistantTC;
import org.overture.typechecker.assistant.expression.AFieldNumberExpAssistantTC;
import org.overture.typechecker.assistant.expression.AForAllExpAssistantTC;
import org.overture.typechecker.assistant.expression.AFuncInstatiationExpAssistantTC;
import org.overture.typechecker.assistant.expression.AIfExpAssistantTC;
import org.overture.typechecker.assistant.expression.AIotaExpAssistantTC;
import org.overture.typechecker.assistant.expression.AIsExpAssistantTC;
import org.overture.typechecker.assistant.expression.AIsOfBaseClassExpAssistantTC;
import org.overture.typechecker.assistant.expression.AIsOfClassExpAssistantTC;
import org.overture.typechecker.assistant.expression.ALambdaExpAssistantTC;
import org.overture.typechecker.assistant.expression.ALetBeStExpAssistantTC;
import org.overture.typechecker.assistant.expression.ALetDefExpAssistantTC;
import org.overture.typechecker.assistant.expression.AMapCompMapExpAssistantTC;
import org.overture.typechecker.assistant.expression.AMapEnumMapExpAssistantTC;
import org.overture.typechecker.assistant.expression.AMapletExpAssistantTC;
import org.overture.typechecker.assistant.expression.AMkBasicExpAssistantTC;
import org.overture.typechecker.assistant.expression.AMkTypeExpAssistantTC;
import org.overture.typechecker.assistant.expression.AMuExpAssistantTC;
import org.overture.typechecker.assistant.expression.ANarrowExpAssistantTC;
import org.overture.typechecker.assistant.expression.ANewExpAssistantTC;
import org.overture.typechecker.assistant.expression.APostOpExpAssistantTC;
import org.overture.typechecker.assistant.expression.ARecordModifierAssistantTC;
import org.overture.typechecker.assistant.expression.ASameBaseClassExpAssistantTC;
import org.overture.typechecker.assistant.expression.ASameClassExpAssistantTC;
import org.overture.typechecker.assistant.expression.ASeqCompSeqExpAssistantTC;
import org.overture.typechecker.assistant.expression.ASeqEnumSeqExpAssistantTC;
import org.overture.typechecker.assistant.expression.ASetCompSetExpAssistantTC;
import org.overture.typechecker.assistant.expression.ASetEnumSetExpAssistantTC;
import org.overture.typechecker.assistant.expression.ASubseqExpAssistantTC;
import org.overture.typechecker.assistant.expression.ATupleExpAssistantTC;
import org.overture.typechecker.assistant.expression.AVariableExpAssistantTC;
import org.overture.typechecker.assistant.expression.PExpAssistantTC;
import org.overture.typechecker.assistant.expression.SBinaryExpAssistantTC;
import org.overture.typechecker.assistant.expression.SMapExpAssistantTC;
import org.overture.typechecker.assistant.expression.SSeqExpAssistantTC;
import org.overture.typechecker.assistant.expression.SSetExpAssistantTC;
import org.overture.typechecker.assistant.expression.SUnaryExpAssistantTC;
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
import org.overture.typechecker.assistant.statement.AAlwaysStmAssistantTC;
import org.overture.typechecker.assistant.statement.AAssignmentStmAssistantTC;
import org.overture.typechecker.assistant.statement.ABlockSimpleBlockStmAssistantTC;
import org.overture.typechecker.assistant.statement.ACallObjectStatementAssistantTC;
import org.overture.typechecker.assistant.statement.ACallStmAssistantTC;
import org.overture.typechecker.assistant.statement.ACaseAlternativeStmAssistantTC;
import org.overture.typechecker.assistant.statement.ACasesStmAssistantTC;
import org.overture.typechecker.assistant.statement.AElseIfStmAssistantTC;
import org.overture.typechecker.assistant.statement.AExitStmAssistantTC;
import org.overture.typechecker.assistant.statement.AExternalClauseAssistantTC;
import org.overture.typechecker.assistant.statement.AForAllStmAssistantTC;
import org.overture.typechecker.assistant.statement.AForIndexStmAssistantTC;
import org.overture.typechecker.assistant.statement.AForPatternBindStmAssitantTC;
import org.overture.typechecker.assistant.statement.AIfStmAssistantTC;
import org.overture.typechecker.assistant.statement.ALetBeStStmAssistantTC;
import org.overture.typechecker.assistant.statement.ANonDeterministicSimpleBlockStmAssistantTC;
import org.overture.typechecker.assistant.statement.AReturnStmAssistantTC;
import org.overture.typechecker.assistant.statement.ATixeStmAssistantTC;
import org.overture.typechecker.assistant.statement.ATrapStmAssistantTC;
import org.overture.typechecker.assistant.statement.AWhileStmAssistantTC;
import org.overture.typechecker.assistant.statement.PStateDesignatorAssistantTC;
import org.overture.typechecker.assistant.statement.PStmAssistantTC;
import org.overture.typechecker.assistant.statement.SLetDefStmAssistantTC;
import org.overture.typechecker.assistant.statement.SSimpleBlockStmAssistantTC;
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
	static
	{
		// FIXME: remove this when conversion to factory obtained assistants are completed.
		//init(new AstAssistantFactory());
		init(new TypeCheckerAssistantFactory());
	}

	
	//Type

	@Override
	public AApplyObjectDesignatorAssistantTC createAApplyObjectDesignatorAssistantTC()
	{
		return new AApplyObjectDesignatorAssistantTC(this);
	}

	@Override
	public ABracketTypeAssistantTC createABracketTypeAssistantTC()
	{
		return new ABracketTypeAssistantTC(this);
	}

	@Override
	public AClassTypeAssistantTC createAClassTypeAssistantTC()
	{
		return new AClassTypeAssistantTC(this);
	}

	@Override
	public AFieldFieldAssistantTC createAFieldFieldAssistantTC()
	{
		return new AFieldFieldAssistantTC(this);
	}

	@Override
	public AFunctionTypeAssistantTC createAFunctionTypeAssistantTC()
	{
		return new AFunctionTypeAssistantTC(this);
	}

	@Override
	public AInMapMapTypeAssistantTC createAInMapMapTypeAssistantTC()
	{
		return new AInMapMapTypeAssistantTC(this);
	}

	@Override
	public AMapMapTypeAssistantTC createAMapMapTypeAssistantTC()
	{
		return new AMapMapTypeAssistantTC(this);
	}

	@Override
	public ANamedInvariantTypeAssistantTC createANamedInvariantTypeAssistantTC()
	{
		return new ANamedInvariantTypeAssistantTC(this);
	}

	@Override
	public AOperationTypeAssistantTC createAOperationTypeAssistantTC()
	{
		return new AOperationTypeAssistantTC(this);
	}

	@Override
	public AOptionalTypeAssistantTC createAOptionalTypeAssistantTC()
	{
		return new AOptionalTypeAssistantTC(this);
	}

	@Override
	public AParameterTypeAssistantTC createAParameterTypeAssistantTC()
	{
		return new AParameterTypeAssistantTC(this);
	}

	@Override
	public APatternListTypePairAssistantTC createAPatternListTypePairAssistantTC()
	{
		return new APatternListTypePairAssistantTC(this);
	}

	@Override
	public AProductTypeAssistantTC createAProductTypeAssistantTC()
	{
		return new AProductTypeAssistantTC(this);
	}

	@Override
	public AQuoteTypeAssistantTC createAQuoteTypeAssistantTC()
	{
		return new AQuoteTypeAssistantTC(this);
	}

	@Override
	public ARecordInvariantTypeAssistantTC createARecordInvariantTypeAssistantTC()
	{
		return new ARecordInvariantTypeAssistantTC(this);
	}

	@Override
	public ASeq1SeqTypeAssistantTC createASeq1SeqTypeAssistantTC()
	{
		return new ASeq1SeqTypeAssistantTC(this);
	}

	@Override
	public ASeqSeqTypeAssistantTC createASeqSeqTypeAssistantTC()
	{
		return new ASeqSeqTypeAssistantTC(this);
	}

	@Override
	public ASetTypeAssistantTC createASetTypeAssistantTC()
	{
		return new ASetTypeAssistantTC(this);
	}

	@Override
	public AUndefinedTypeAssistantTC createAUndefinedTypeAssistantTC()
	{
		return new AUndefinedTypeAssistantTC(this);
	}

	@Override
	public AUnionTypeAssistantTC createAUnionTypeAssistantTC()
	{
		return new AUnionTypeAssistantTC(this);
	}

	@Override
	public AUnknownTypeAssistantTC createAUnknownTypeAssistantTC()
	{
		return new AUnknownTypeAssistantTC(this);
	}

	@Override
	public AUnresolvedTypeAssistantTC createAUnresolvedTypeAssistantTC()
	{
		return new AUnresolvedTypeAssistantTC(this);
	}

	@Override
	public AVoidReturnTypeAssistantTC createAVoidReturnTypeAssistantTC()
	{
		return new AVoidReturnTypeAssistantTC(this);
	}

	@Override
	public AVoidTypeAssistantTC createAVoidTypeAssistantTC()
	{
		return new AVoidTypeAssistantTC(this);
	}

	@Override
	public PTypeAssistantTC createPTypeAssistantTC()
	{
		return new PTypeAssistantTC(this);
	}

	@Override
	public SMapTypeAssistantTC createSMapTypeAssistantTC()
	{
		return new SMapTypeAssistantTC(this);
	}

	@Override
	public SNumericBasicTypeAssistantTC createSNumericBasicTypeAssistantTC()
	{
		return new SNumericBasicTypeAssistantTC(this);
	}

	@Override
	public SSeqTypeAssistantTC createSSeqTypeAssistantTC()
	{
		return new SSeqTypeAssistantTC(this);
	}
	
	//definition

	@Override
	public AAssignmentDefinitionAssistantTC createAAssignmentDefinitionAssistantTC()
	{
		return new AAssignmentDefinitionAssistantTC(this);
	}

	@Override
	public ABusClassDefinitionAssistantTC createABusClassDefinitionAssistantTC()
	{
		return new ABusClassDefinitionAssistantTC(this);
	}

	@Override
	public AClassInvariantDefinitionAssistantTC createAClassInvariantDefinitionAssistantTC()
	{
		return new AClassInvariantDefinitionAssistantTC(this);
	}

	@Override
	public ACpuClassDefinitionAssistantTC createACpuClassDefinitionAssistantTC()
	{
		return new ACpuClassDefinitionAssistantTC(this);
	}

	@Override
	public AEqualsDefinitionAssistantTC createAEqualsDefinitionAssistantTC()
	{
		return new AEqualsDefinitionAssistantTC(this);
	}

	@Override
	public AExplicitFunctionDefinitionAssistantTC createAExplicitFunctionDefinitionAssistantTC()
	{
		return new AExplicitFunctionDefinitionAssistantTC(this);
	}

	@Override
	public AExplicitOperationDefinitionAssistantTC createAExplicitOperationDefinitionAssistantTC()
	{
		return new AExplicitOperationDefinitionAssistantTC(this);
	}

	@Override
	public AExternalDefinitionAssistantTC createAExternalDefinitionAssistantTC()
	{
		return new AExternalDefinitionAssistantTC(this);
	}

	@Override
	public AImplicitFunctionDefinitionAssistantTC createAImplicitFunctionDefinitionAssistantTC()
	{
		return new AImplicitFunctionDefinitionAssistantTC(this);
	}

	@Override
	public AImplicitOperationDefinitionAssistantTC createAImplicitOperationDefinitionAssistantTC()
	{
		return new AImplicitOperationDefinitionAssistantTC(this);
	}

	@Override
	public AImportedDefinitionAssistantTC createAImportedDefinitionAssistantTC()
	{
		return new AImportedDefinitionAssistantTC(this);
	}

	@Override
	public AInheritedDefinitionAssistantTC createAInheritedDefinitionAssistantTC()
	{
		return new AInheritedDefinitionAssistantTC(this);
	}

	@Override
	public AInstanceVariableDefinitionAssistantTC createAInstanceVariableDefinitionAssistantTC()
	{
		return new AInstanceVariableDefinitionAssistantTC(this);
	}

	@Override
	public ALocalDefinitionAssistantTC createALocalDefinitionAssistantTC()
	{
		return new ALocalDefinitionAssistantTC(this);
	}

	@Override
	public AMultiBindListDefinitionAssistantTC createAMultiBindListDefinitionAssistantTC()
	{
		return new AMultiBindListDefinitionAssistantTC(this);
	}

	@Override
	public AMutexSyncDefinitionAssistantTC createAMutexSyncDefinitionAssistantTC()
	{
		return new AMutexSyncDefinitionAssistantTC(this);
	}

	@Override
	public ANamedTraceDefinitionAssistantTC createANamedTraceDefinitionAssistantTC()
	{
		return new ANamedTraceDefinitionAssistantTC(this);
	}

	@Override
	public APerSyncDefinitionAssistantTC createAPerSyncDefinitionAssistantTC()
	{
		return new APerSyncDefinitionAssistantTC(this);
	}

	@Override
	public ARenamedDefinitionAssistantTC createARenamedDefinitionAssistantTC()
	{
		return new ARenamedDefinitionAssistantTC(this);
	}

	@Override
	public AStateDefinitionAssistantTC createAStateDefinitionAssistantTC()
	{
		return new AStateDefinitionAssistantTC(this);
	}

	@Override
	public ASystemClassDefinitionAssistantTC createASystemClassDefinitionAssistantTC()
	{
		return new ASystemClassDefinitionAssistantTC(this);
	}

	@Override
	public AThreadDefinitionAssistantTC createAThreadDefinitionAssistantTC()
	{
		return new AThreadDefinitionAssistantTC(this);
	}

	@Override
	public ATypeDefinitionAssistantTC createATypeDefinitionAssistantTC()
	{
		return new ATypeDefinitionAssistantTC(this);
	}

	@Override
	public AUntypedDefinitionAssistantTC createAUntypedDefinitionAssistantTC()
	{
		return new AUntypedDefinitionAssistantTC(this);
	}

	@Override
	public AValueDefinitionAssistantTC createAValueDefinitionAssistantTC()
	{
		return new AValueDefinitionAssistantTC(this);
	}

	@Override
	public PAccessSpecifierAssistantTC createPAccessSpecifierAssistantTC()
	{
		return new PAccessSpecifierAssistantTC(this);
	}

	@Override
	public PDefinitionAssistantTC createPDefinitionAssistantTC()
	{
		return new PDefinitionAssistantTC(this);
	}

	@Override
	public PDefinitionListAssistantTC createPDefinitionListAssistantTC()
	{
		return new PDefinitionListAssistantTC(this);
	}

	@Override
	public PDefinitionSet createPDefinitionSet()
	{
		return new PDefinitionSet(this);
	}

	@Override
	public PTraceDefinitionAssistantTC createPTraceDefinitionAssistantTC()
	{
		return new PTraceDefinitionAssistantTC(this);
	}

	@Override
	public SClassDefinitionAssistantTC createSClassDefinitionAssistantTC()
	{
		return new SClassDefinitionAssistantTC(this);
	}
	
	// expression

	@Override
	public AApplyExpAssistantTC createAApplyExpAssistantTC()
	{
		return new AApplyExpAssistantTC(this);
	}

	@Override
	public ACaseAlternativeAssistantTC createACaseAlternativeAssistantTC()
	{
		return new ACaseAlternativeAssistantTC(this);
	}

	@Override
	public ACasesExpAssistantTC createACasesExpAssistantTC()
	{
		return new ACasesExpAssistantTC(this);
	}

	@Override
	public AElementsUnaryExpAssistantTC createAElementsUnaryExpAssistantTC()
	{
		return new AElementsUnaryExpAssistantTC(this);
	}

	@Override
	public AElseIfExpAssistantTC createAElseIfExpAssistantTC()
	{
		return new AElseIfExpAssistantTC(this);
	}

	@Override
	public AExists1ExpAssistantTC createAExists1ExpAssistantTC()
	{
		return new AExists1ExpAssistantTC(this);
	}

	@Override
	public AExistsExpAssistantTC createAExistsExpAssistantTC()
	{
		return new AExistsExpAssistantTC(this);
	}

	@Override
	public AFieldExpAssistantTC createAFieldExpAssistantTC()
	{
		return new AFieldExpAssistantTC(this);
	}

	@Override
	public AFieldNumberExpAssistantTC createAFieldNumberExpAssistantTC()
	{
		return new AFieldNumberExpAssistantTC(this);
	}

	@Override
	public AForAllExpAssistantTC createAForAllExpAssistantTC()
	{
		return new AForAllExpAssistantTC(this);
	}

	@Override
	public AFuncInstatiationExpAssistantTC createAFuncInstatiationExpAssistantTC()
	{
		return new AFuncInstatiationExpAssistantTC(this);
	}

	@Override
	public AIfExpAssistantTC createAIfExpAssistantTC()
	{
		return new AIfExpAssistantTC(this);
	}

	@Override
	public AIotaExpAssistantTC createAIotaExpAssistantTC()
	{
		return new AIotaExpAssistantTC(this);
	}

	@Override
	public AIsExpAssistantTC createAIsExpAssistantTC()
	{
		return new AIsExpAssistantTC(this);
	}

	@Override
	public AIsOfBaseClassExpAssistantTC createAIsOfBaseClassExpAssistantTC()
	{
		return new AIsOfBaseClassExpAssistantTC(this);
	}

	@Override
	public AIsOfClassExpAssistantTC createAIsOfClassExpAssistantTC()
	{
		return new AIsOfClassExpAssistantTC(this);
	}

	@Override
	public ALambdaExpAssistantTC createALambdaExpAssistantTC()
	{
		return new ALambdaExpAssistantTC(this);
	}

	@Override
	public ALetBeStExpAssistantTC createALetBeStExpAssistantTC()
	{
		return new ALetBeStExpAssistantTC(this);
	}

	@Override
	public ALetDefExpAssistantTC createALetDefExpAssistantTC()
	{
		return new ALetDefExpAssistantTC(this);
	}

	@Override
	public AMapCompMapExpAssistantTC createAMapCompMapExpAssistantTC()
	{
		return new AMapCompMapExpAssistantTC(this);
	}

	@Override
	public AMapEnumMapExpAssistantTC createAMapEnumMapExpAssistantTC()
	{
		return new AMapEnumMapExpAssistantTC(this);
	}

	@Override
	public AMapletExpAssistantTC createAMapletExpAssistantTC()
	{
		return new AMapletExpAssistantTC(this);
	}

	@Override
	public AMkBasicExpAssistantTC createAMkBasicExpAssistantTC()
	{
		return new AMkBasicExpAssistantTC(this);
	}

	@Override
	public AMkTypeExpAssistantTC createAMkTypeExpAssistantTC()
	{
		return new AMkTypeExpAssistantTC(this);
	}

	@Override
	public AMuExpAssistantTC createAMuExpAssistantTC()
	{
		return new AMuExpAssistantTC(this);
	}

	@Override
	public ANarrowExpAssistantTC createANarrowExpAssistantTC()
	{
		return new ANarrowExpAssistantTC(this);
	}

	@Override
	public ANewExpAssistantTC createANewExpAssistantTC()
	{
		return new ANewExpAssistantTC(this);
	}

	@Override
	public APostOpExpAssistantTC createAPostOpExpAssistantTC()
	{
		return new APostOpExpAssistantTC(this);
	}

	@Override
	public ARecordModifierAssistantTC createARecordModifierAssistantTC()
	{
		return new ARecordModifierAssistantTC(this);
	}

	@Override
	public ASameBaseClassExpAssistantTC createASameBaseClassExpAssistantTC()
	{
		return new ASameBaseClassExpAssistantTC(this);
	}

	@Override
	public ASameClassExpAssistantTC createASameClassExpAssistantTC()
	{
		return new ASameClassExpAssistantTC(this);
	}

	@Override
	public ASeqCompSeqExpAssistantTC createASeqCompSeqExpAssistantTC()
	{
		return new ASeqCompSeqExpAssistantTC(this);
	}

	@Override
	public ASeqEnumSeqExpAssistantTC createASeqEnumSeqExpAssistantTC()
	{
		return new ASeqEnumSeqExpAssistantTC(this);
	}

	@Override
	public ASetCompSetExpAssistantTC createASetCompSetExpAssistantTC()
	{
		return new ASetCompSetExpAssistantTC(this);
	}

	@Override
	public ASetEnumSetExpAssistantTC createASetEnumSetExpAssistantTC()
	{
		return new ASetEnumSetExpAssistantTC(this);
	}

	@Override
	public ASubseqExpAssistantTC createASubseqExpAssistantTC()
	{
		return new ASubseqExpAssistantTC(this);
	}

	@Override
	public ATupleExpAssistantTC createATupleExpAssistantTC()
	{
		return new ATupleExpAssistantTC(this);
	}

	@Override
	public AVariableExpAssistantTC createAVariableExpAssistantTC()
	{
		return new AVariableExpAssistantTC(this);
	}

	@Override
	public PExpAssistantTC createPExpAssistantTC()
	{
		return new PExpAssistantTC(this);
	}

	@Override
	public SBinaryExpAssistantTC createSBinaryExpAssistantTC()
	{
		return new SBinaryExpAssistantTC(this);
	}

	@Override
	public SMapExpAssistantTC createSMapExpAssistantTC()
	{
		return new SMapExpAssistantTC(this);
	}

	@Override
	public SSeqExpAssistantTC createSSeqExpAssistantTC()
	{
		return new SSeqExpAssistantTC(this);
	}

	@Override
	public SSetExpAssistantTC createSSetExpAssistantTC()
	{
		return new SSetExpAssistantTC(this);
	}

	@Override
	public SUnaryExpAssistantTC createSUnaryExpAssistantTC()
	{
		return new SUnaryExpAssistantTC(this);
	}
	
	//module

	@Override
	public AAllImportAssistantTC createAAllImportAssistantTC()
	{
		return new AAllImportAssistantTC(this);
	}

	@Override
	public AFromModuleImportsAssistantTC createAFromModuleImportsAssistantTC()
	{
		return new AFromModuleImportsAssistantTC(this);
	}

	@Override
	public AModuleExportsAssistantTC createAModuleExportsAssistantTC()
	{
		return new AModuleExportsAssistantTC(this);
	}

	@Override
	public AModuleImportsAssistantTC createAModuleImportsAssistantTC()
	{
		return new AModuleImportsAssistantTC(this);
	}

	@Override
	public AModuleModulesAssistantTC createAModuleModulesAssistantTC()
	{
		return new AModuleModulesAssistantTC(this);
	}

	@Override
	public ATypeImportAssistantTC createATypeImportAssistantTC()
	{
		return new ATypeImportAssistantTC(this);
	}

	@Override
	public AValueValueImportAssistantTC createAValueValueImportAssistantTC()
	{
		return new AValueValueImportAssistantTC(this);
	}

	@Override
	public PExportAssistantTC createPExportAssistantTC()
	{
		return new PExportAssistantTC(this);
	}

	@Override
	public PImportAssistantTC createPImportAssistantTC()
	{
		return new PImportAssistantTC(this);
	}

	@Override
	public SValueImportAssistantTC createSValueImportAssistantTC()
	{
		return new SValueImportAssistantTC(this);
	}
	
	//pattern

	@Override
	public ABooleanPatternAssistantTC createABooleanPatternAssistantTC()
	{
		return new ABooleanPatternAssistantTC(this);
	}

	@Override
	public ACharacterPatternAssistantTC createACharacterPatternAssistantTC()
	{
		return new ACharacterPatternAssistantTC(this);
	}

	@Override
	public AConcatenationPatternAssistantTC createAConcatenationPatternAssistantTC()
	{
		return new AConcatenationPatternAssistantTC(this);
	}

	@Override
	public AExpressionPatternAssistantTC createAExpressionPatternAssistantTC()
	{
		return new AExpressionPatternAssistantTC(this);
	}

	@Override
	public AIdentifierPatternAssistantTC createAIdentifierPatternAssistantTC()
	{
		return new AIdentifierPatternAssistantTC(this);
	}

	@Override
	public AIgnorePatternAssistantTC createAIgnorePatternAssistantTC()
	{
		return new AIgnorePatternAssistantTC(this);
	}

	@Override
	public AIntegerPatternAssistantTC createAIntegerPatternAssistantTC()
	{
		return new AIntegerPatternAssistantTC(this);
	}

	@Override
	public AMapletPatternMapletAssistantTC createAMapletPatternMapletAssistantTC()
	{
		return new AMapletPatternMapletAssistantTC(this);
	}

	@Override
	public AMapPatternAssistantTC createAMapPatternAssistantTC()
	{
		return new AMapPatternAssistantTC(this);
	}

	@Override
	public AMapUnionPatternAssistantTC createAMapUnionPatternAssistantTC()
	{
		return new AMapUnionPatternAssistantTC(this);
	}

	@Override
	public ANilPatternAssistantTC createANilPatternAssistantTC()
	{
		return new ANilPatternAssistantTC(this);
	}

	@Override
	public APatternTypePairAssistant createAPatternTypePairAssistant()
	{
		return new APatternTypePairAssistant(this);
	}

	@Override
	public AQuotePatternAssistantTC createAQuotePatternAssistantTC()
	{
		return new AQuotePatternAssistantTC(this);
	}

	@Override
	public ARealPatternAssistantTC createARealPatternAssistantTC()
	{
		return new ARealPatternAssistantTC(this);
	}

	@Override
	public ARecordPatternAssistantTC createARecordPatternAssistantTC()
	{
		return new ARecordPatternAssistantTC(this);
	}

	@Override
	public ASeqPatternAssistantTC createASeqPatternAssistantTC()
	{
		return new ASeqPatternAssistantTC(this);
	}

	@Override
	public ASetBindAssistantTC createASetBindAssistantTC()
	{
		return new ASetBindAssistantTC(this);
	}

	@Override
	public ASetMultipleBindAssistantTC createASetMultipleBindAssistantTC()
	{
		return new ASetMultipleBindAssistantTC(this);
	}

	@Override
	public ASetPatternAssistantTC createASetPatternAssistantTC()
	{
		return new ASetPatternAssistantTC(this);
	}

	@Override
	public AStringPatternAssistantTC createAStringPatternAssistantTC()
	{
		return new AStringPatternAssistantTC(this);
	}

	@Override
	public ATuplePatternAssistantTC createATuplePatternAssistantTC()
	{
		return new ATuplePatternAssistantTC(this);
	}

	@Override
	public ATypeBindAssistantTC createATypeBindAssistantTC()
	{
		return new ATypeBindAssistantTC(this);
	}

	@Override
	public ATypeMultipleBindAssistantTC createATypeMultipleBindAssistantTC()
	{
		return new ATypeMultipleBindAssistantTC(this);
	}

	@Override
	public AUnionPatternAssistantTC createAUnionPatternAssistantTC()
	{
		return new AUnionPatternAssistantTC(this);
	}

	@Override
	public PatternListTC createPatternListTC()
	{
		return new PatternListTC(this);
	}

	@Override
	public PBindAssistantTC createPBindAssistantTC()
	{
		return new PBindAssistantTC(this);
	}

	@Override
	public PMultipleBindAssistantTC createPMultipleBindAssistantTC()
	{
		return new PMultipleBindAssistantTC(this);
	}

	@Override
	public PPatternAssistantTC createPPatternAssistantTC()
	{
		return new PPatternAssistantTC(this);
	}

	@Override
	public PPatternBindAssistantTC createPPatternBindAssistantTC()
	{
		return new PPatternBindAssistantTC(this);
	}

	@Override
	public PPatternListAssistantTC createPPatternListAssistantTC()
	{
		return new PPatternListAssistantTC(this);
	}
	
	//statement

	@Override
	public AAlwaysStmAssistantTC createAAlwaysStmAssistantTC()
	{
		return new AAlwaysStmAssistantTC(this);
	}

	@Override
	public AAssignmentStmAssistantTC createAAssignmentStmAssistantTC()
	{
		return new AAssignmentStmAssistantTC(this);
	}

	@Override
	public ABlockSimpleBlockStmAssistantTC createABlockSimpleBlockStmAssistantTC()
	{
		return new ABlockSimpleBlockStmAssistantTC(this);
	}

	@Override
	public ACallObjectStatementAssistantTC createACallObjectStatementAssistantTC()
	{
		return new ACallObjectStatementAssistantTC(this);
	}

	@Override
	public ACallStmAssistantTC createACallStmAssistantTC()
	{
		return new ACallStmAssistantTC(this);
	}

	@Override
	public ACaseAlternativeStmAssistantTC createACaseAlternativeStmAssistantTC()
	{
		return new ACaseAlternativeStmAssistantTC(this);
	}

	@Override
	public ACasesStmAssistantTC createACasesStmAssistantTC()
	{
		return new ACasesStmAssistantTC(this);
	}

	@Override
	public AElseIfStmAssistantTC createAElseIfStmAssistantTC()
	{
		return new AElseIfStmAssistantTC(this);
	}

	@Override
	public AExitStmAssistantTC createAExitStmAssistantTC()
	{
		return new AExitStmAssistantTC(this);
	}

	@Override
	public AExternalClauseAssistantTC createAExternalClauseAssistantTC()
	{
		return new AExternalClauseAssistantTC(this);
	}

	@Override
	public AForAllStmAssistantTC createAForAllStmAssistantTC()
	{
		return new AForAllStmAssistantTC(this);
	}

	@Override
	public AForIndexStmAssistantTC createAForIndexStmAssistantTC()
	{
		return new AForIndexStmAssistantTC(this);
	}

	@Override
	public AForPatternBindStmAssitantTC createAForPatternBindStmAssitantTC()
	{
		return new AForPatternBindStmAssitantTC(this);
	}

	@Override
	public AIfStmAssistantTC createAIfStmAssistantTC()
	{
		return new AIfStmAssistantTC(this);
	}

	@Override
	public ALetBeStStmAssistantTC createALetBeStStmAssistantTC()
	{
		return new ALetBeStStmAssistantTC(this);
	}

	@Override
	public ANonDeterministicSimpleBlockStmAssistantTC createANonDeterministicSimpleBlockStmAssistantTC()
	{
		return new ANonDeterministicSimpleBlockStmAssistantTC(this);
	}

	@Override
	public AReturnStmAssistantTC createAReturnStmAssistantTC()
	{
		return new AReturnStmAssistantTC(this);
	}

	@Override
	public ATixeStmAssistantTC createATixeStmAssistantTC()
	{
		return new ATixeStmAssistantTC(this);
	}

	@Override
	public ATrapStmAssistantTC createATrapStmAssistantTC()
	{
		return new ATrapStmAssistantTC(this);
	}

	@Override
	public AWhileStmAssistantTC createAWhileStmAssistantTC()
	{
		return new AWhileStmAssistantTC(this);
	}

	@Override
	public PStateDesignatorAssistantTC createPStateDesignatorAssistantTC()
	{
		return new PStateDesignatorAssistantTC(this);
	}

	@Override
	public PStmAssistantTC createPStmAssistantTC()
	{
		return new PStmAssistantTC(this);
	}

	@Override
	public SLetDefStmAssistantTC createSLetDefStmAssistantTC()
	{
		return new SLetDefStmAssistantTC(this);
	}

	@Override
	public SSimpleBlockStmAssistantTC createSSimpleBlockStmAssistantTC()
	{
		return new SSimpleBlockStmAssistantTC(this);
	}


	
	
}
