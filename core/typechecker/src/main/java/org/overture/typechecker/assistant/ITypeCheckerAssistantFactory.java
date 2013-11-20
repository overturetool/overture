package org.overture.typechecker.assistant;

import java.util.List;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.Environment;
import org.overture.typechecker.assistant.definition.ABusClassDefinitionAssistantTC;
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
import org.overture.typechecker.assistant.definition.AStateDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ASystemClassDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AThreadDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ATypeDefinitionAssistantTC;
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
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOperationTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOptionalTypeAssistantTC;
import org.overture.typechecker.assistant.type.AParameterTypeAssistantTC;
import org.overture.typechecker.assistant.type.APatternListTypePairAssistantTC;
import org.overture.typechecker.assistant.type.AProductTypeAssistantTC;
import org.overture.typechecker.assistant.type.ARecordInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.ASetTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnknownTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnresolvedTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;
import org.overture.typechecker.assistant.type.SMapTypeAssistantTC;
import org.overture.typechecker.assistant.type.SNumericBasicTypeAssistantTC;
import org.overture.typechecker.assistant.type.SSeqTypeAssistantTC;
import org.overture.typechecker.utilities.NameFinder;
import org.overture.typechecker.utilities.TypeFinder;
import org.overture.typechecker.utilities.TypeResolver;
import org.overture.typechecker.utilities.type.ConcreateTypeImplementor;
import org.overture.typechecker.utilities.type.PTypeResolver;

public interface ITypeCheckerAssistantFactory extends IAstAssistantFactory
{
	// Definition
	// AAssignmentDefinitionAssistantTC createAAssignmentDefinitionAssistant();
	ABusClassDefinitionAssistantTC createABusClassDefinitionAssistant();

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

	AStateDefinitionAssistantTC createAStateDefinitionAssistant();

	ASystemClassDefinitionAssistantTC createASystemClassDefinitionAssistant();

	AThreadDefinitionAssistantTC createAThreadDefinitionAssistant();

	ATypeDefinitionAssistantTC createATypeDefinitionAssistant();

	AValueDefinitionAssistantTC createAValueDefinitionAssistant();

	PAccessSpecifierAssistantTC createPAccessSpecifierAssistant();

	PDefinitionAssistantTC createPDefinitionAssistant();

	PDefinitionListAssistantTC createPDefinitionListAssistant();

	PDefinitionSet createPDefinitionSet();

	PTraceDefinitionAssistantTC createPTraceDefinitionAssistant();

	SClassDefinitionAssistantTC createSClassDefinitionAssistant();

	// expression
	AApplyExpAssistantTC createAApplyExpAssistant();

	ACaseAlternativeAssistantTC createACaseAlternativeAssistant();

	PExpAssistantTC createPExpAssistant();

	SBinaryExpAssistantTC createSBinaryExpAssistant();

	// module
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

	// pattern
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

	// statement
	// AAlwaysStmAssistantTC createAAlwaysStmAssistant();
	// AAssignmentStmAssistantTC createAAssignmentStmAssistant();
	ABlockSimpleBlockStmAssistantTC createABlockSimpleBlockStmAssistant();

	ACallObjectStatementAssistantTC createACallObjectStatementAssistant();

	ACallStmAssistantTC createACallStmAssistant();

	// ACaseAlternativeStmAssistantTC createACaseAlternativeStmAssistant();
	// ACasesStmAssistantTC createACasesStmAssistant();
	// AElseIfStmAssistantTC createAElseIfStmAssistant();
	// AExitStmAssistantTC createAExitStmAssistant();
	AExternalClauseAssistantTC createAExternalClauseAssistant();

	// AForAllStmAssistantTC createAForAllStmAssistant();
	// AForIndexStmAssistantTC createAForIndexStmAssistant();
	// AForPatternBindStmAssitantTC createAForPatternBindStmAssitant();
	// AIfStmAssistantTC createAIfStmAssistant();
	// ALetBeStStmAssistantTC createALetBeStStmAssistant();
	ANonDeterministicSimpleBlockStmAssistantTC createANonDeterministicSimpleBlockStmAssistant();

	// AReturnStmAssistantTC createAReturnStmAssistant();
	// ATixeStmAssistantTC createATixeStmAssistant();
	// ATrapStmAssistantTC createATrapStmAssistant();
	// AWhileStmAssistantTC createAWhileStmAssistant();
	PStateDesignatorAssistantTC createPStateDesignatorAssistant();

	PStmAssistantTC createPStmAssistant();

	// SLetDefStmAssistantTC createSLetDefStmAssistant();
	// SSimpleBlockStmAssistantTC createSSimpleBlockStmAssistant();

	// Type
	AApplyObjectDesignatorAssistantTC createAApplyObjectDesignatorAssistant();

	ABracketTypeAssistantTC createABracketTypeAssistant();

	AClassTypeAssistantTC createAClassTypeAssistant();

	AFieldFieldAssistantTC createAFieldFieldAssistant();

	AFunctionTypeAssistantTC createAFunctionTypeAssistant();

	ANamedInvariantTypeAssistantTC createANamedInvariantTypeAssistant();

	AOperationTypeAssistantTC createAOperationTypeAssistant();

	AOptionalTypeAssistantTC createAOptionalTypeAssistant();

	AParameterTypeAssistantTC createAParameterTypeAssistant();

	APatternListTypePairAssistantTC createAPatternListTypePairAssistant();

	AProductTypeAssistantTC createAProductTypeAssistant();

	ARecordInvariantTypeAssistantTC createARecordInvariantTypeAssistant();

	ASetTypeAssistantTC createASetTypeAssistant();

	AUnionTypeAssistantTC createAUnionTypeAssistant();

	AUnknownTypeAssistantTC createAUnknownTypeAssistant();

	AUnresolvedTypeAssistantTC createAUnresolvedTypeAssistant();

	PTypeAssistantTC createPTypeAssistant();

	SMapTypeAssistantTC createSMapTypeAssistant();

	SNumericBasicTypeAssistantTC createSNumericBasicTypeAssistant();

	SSeqTypeAssistantTC createSSeqTypeAssistant();

	// visitors
	IAnswer<List<PDefinition>> getDefinitionCollector();

	IAnswer<PType> getDefinitionTypeFinder();

	IQuestionAnswer<Object, Boolean> getDefinitionEqualityChecker();

	AnswerAdaptor<Boolean> getMapBasisChecker();

	IAnswer<LexNameList> getVariableNameCollector();

	IAnswer<PDefinition> getSelfDefinitionFinder();

	IAnswer<PTypeSet> getExitTypeCollector();

	IQuestionAnswer<TypeFinder.Newquestion, PDefinition> getTypeFinder();

	IQuestionAnswer<NameFinder.Newquestion, PDefinition> getNameFinder();

	AnswerAdaptor<Boolean> getFunctionChecker();

	IAnswer<Boolean> getOperationChecker();

	IAnswer<String> getKindFinder();

	IAnswer<Boolean> getUpdatableChecker();

	IAnswer<Boolean> getCallableOperationChecker();

	AnalysisAdaptor getUsedMarker();

	IQuestion<Environment> getImplicitDefinitionFinder();

	IAnswer<Boolean> getUsedChecker();

	IAnswer<Boolean> getPTypeFunctionChecker();

	AnalysisAdaptor getUnusedChecker();

	IAnswer<PDefinition> getDereferer();

	IQuestion<TypeResolver.NewQuestion> getTypeResolver();

	IAnswer<SMapType> getMapTypeFinder();

	IAnswer<SSeqType> getSeqTypeFinder();

	AnswerAdaptor<Boolean> getSeqBasisChecker();

	IAnswer<AOperationType> getOperationTypeFinder();

	AnswerAdaptor<Boolean> getOperationBasisChecker();

	AnswerAdaptor<Boolean> getSetBasisChecker();

	IAnswer<ASetType> getSetTypeFinder();

	AnswerAdaptor<Boolean> getRecordBasisChecker();

	IAnswer<ARecordInvariantType> getRecordTypeFinder();

	AnswerAdaptor<Boolean> getClassBasisChecker();

	IAnswer<AClassType> getClassTypeFinder();

	IAnswer<AProductType> getProductTypeFinder();

	AnswerAdaptor<Boolean> getProductBasisChecker();

	IAnswer<String> getTypeDisplayer();

	AnalysisAdaptor getTypeUnresolver();

	IQuestionAnswer<AAccessSpecifierAccessSpecifier, Boolean> getNarrowerThanComparator();

	IAnswer<AUnionType> getUnionTypeFinder();

	IQuestionAnswer<Object, Boolean> getTypeEqualityChecker();
	
	AnswerAdaptor<Boolean> getUnionBasisChecker();
	
	IAnswer<AFunctionType> getFunctionTypeFinder();
	
	IQuestionAnswer<PTypeResolver.Newquestion, PType> getPTypeResolver();
	
	IQuestionAnswer<ConcreateTypeImplementor.Newquestion, PType> getConcreateTypeImplementor();
	
	IQuestionAnswer<String, PType> getPTypeFinder();
	
	IQuestionAnswer<Integer, Boolean> getProductExtendedChecker();
	
	IQuestionAnswer<Integer, AProductType> getProductExtendedTypeFinder();
	
	IQuestionAnswer<Class<? extends PType>, Boolean> getPTypeExtendedChecker();
	
	IAnswer<Boolean> getVoidExistanceChecker();
	
	IAnswer<Boolean> getVoidBasisChecker();
	
	IAnswer<PType> getPossibleTypeFinder();
	
	IAnswer<PExp> getMatchingExpressionFinder();
	

}
