package org.overture.typechecker.assistant;

import java.util.List;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.AstAssistantFactory;
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
import org.overture.typechecker.utilities.CallableOperationChecker;
import org.overture.typechecker.utilities.DefinitionCollector;
import org.overture.typechecker.utilities.DefinitionEqualityChecker;
import org.overture.typechecker.utilities.DefinitionTypeFinder;
import org.overture.typechecker.utilities.Dereferer;
import org.overture.typechecker.utilities.ExitTypeCollector;
import org.overture.typechecker.utilities.FunctionChecker;
import org.overture.typechecker.utilities.ImplicitDefinitionFinder;
import org.overture.typechecker.utilities.KindFinder;
import org.overture.typechecker.utilities.NameFinder;
import org.overture.typechecker.utilities.OperationChecker;
import org.overture.typechecker.utilities.PTypeFunctionChecker;
import org.overture.typechecker.utilities.SelfDefinitionFinder;
import org.overture.typechecker.utilities.TypeFinder;
import org.overture.typechecker.utilities.TypeFinder.Newquestion;
import org.overture.typechecker.utilities.TypeResolver;
import org.overture.typechecker.utilities.TypeResolver.NewQuestion;
import org.overture.typechecker.utilities.UnusedChecker;
import org.overture.typechecker.utilities.UpdatableChecker;
import org.overture.typechecker.utilities.UsedChecker;
import org.overture.typechecker.utilities.UsedMarker;
import org.overture.typechecker.utilities.VariableNameCollector;
import org.overture.typechecker.utilities.pattern.AlwaysMatchingPatternChecker;
import org.overture.typechecker.utilities.pattern.MatchingExpressionFinder;
import org.overture.typechecker.utilities.pattern.PatternUnresolver;
import org.overture.typechecker.utilities.pattern.PossibleTypeFinder;
import org.overture.typechecker.utilities.pattern.SimplePatternChecker;
import org.overture.typechecker.utilities.type.ClassBasisChecker;
import org.overture.typechecker.utilities.type.ClassTypeFinder;
import org.overture.typechecker.utilities.type.ConcreateTypeImplementor;
import org.overture.typechecker.utilities.type.FunctionTypeFinder;
import org.overture.typechecker.utilities.type.MapBasisChecker;
import org.overture.typechecker.utilities.type.MapTypeFinder;
import org.overture.typechecker.utilities.type.NarrowerThanComparator;
import org.overture.typechecker.utilities.type.OperationBasisChecker;
import org.overture.typechecker.utilities.type.OperationTypeFinder;
import org.overture.typechecker.utilities.type.PTypeExtendedChecker;
import org.overture.typechecker.utilities.type.PTypeFinder;
import org.overture.typechecker.utilities.type.PTypeResolver;
import org.overture.typechecker.utilities.type.ProductBasisChecker;
import org.overture.typechecker.utilities.type.ProductExtendedChecker;
import org.overture.typechecker.utilities.type.ProductExtendedTypeFinder;
import org.overture.typechecker.utilities.type.ProductTypeFinder;
import org.overture.typechecker.utilities.type.RecordBasisChecker;
import org.overture.typechecker.utilities.type.RecordTypeFinder;
import org.overture.typechecker.utilities.type.SeqBasisChecker;
import org.overture.typechecker.utilities.type.SeqTypeFinder;
import org.overture.typechecker.utilities.type.SetBasisChecker;
import org.overture.typechecker.utilities.type.SetTypeFinder;
import org.overture.typechecker.utilities.type.TypeDisplayer;
import org.overture.typechecker.utilities.type.TypeEqualityChecker;
import org.overture.typechecker.utilities.type.TypeUnresolver;
import org.overture.typechecker.utilities.type.UnionBasisChecker;
import org.overture.typechecker.utilities.type.UnionTypeFinder;
import org.overture.typechecker.utilities.type.VoidBasisChecker;
import org.overture.typechecker.utilities.type.VoidExistanceChecker;

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

//	@Override
//	public ABracketTypeAssistantTC createABracketTypeAssistant()
//	{
//		return new ABracketTypeAssistantTC(this);
//	}

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
	public ARecordInvariantTypeAssistantTC createARecordInvariantTypeAssistant()
	{
		return new ARecordInvariantTypeAssistantTC(this);
	}

	@Override
	public ASetTypeAssistantTC createASetTypeAssistant()
	{
		return new ASetTypeAssistantTC(this);
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

	@Override
	public IQuestionAnswer<NameFinder.Newquestion, PDefinition> getNameFinder()
	{
		return new NameFinder(this);
	}

	@Override
	public AnswerAdaptor<Boolean> getFunctionChecker()
	{
		return new FunctionChecker(this);
	}

	@Override
	public IAnswer<Boolean> getOperationChecker()
	{
		return new OperationChecker(this);
	}

	@Override
	public IAnswer<String> getKindFinder()
	{
		return new KindFinder(this);
	}

	@Override
	public IAnswer<Boolean> getUpdatableChecker()
	{
		return new UpdatableChecker(this);
	}

	@Override
	public IAnswer<Boolean> getCallableOperationChecker()
	{
		return new CallableOperationChecker(this);
	}

	@Override
	public AnalysisAdaptor getUsedMarker()
	{
		return new UsedMarker(this);
	}

	@Override
	public IQuestion<Environment> getImplicitDefinitionFinder()
	{
		return new ImplicitDefinitionFinder(this);
	}

	@Override
	public IAnswer<Boolean> getUsedChecker()
	{
		return new UsedChecker(this);
	}

	@Override
	public IAnswer<Boolean> getPTypeFunctionChecker()
	{
		return new PTypeFunctionChecker(this);
	}

	@Override
	public AnalysisAdaptor getUnusedChecker()
	{
		return new UnusedChecker(this);
	}

	@Override
	public IAnswer<PDefinition> getDereferer()
	{
		return new Dereferer(this);
	}

	@Override
	public IQuestion<NewQuestion> getTypeResolver()
	{
		return new TypeResolver(this);
	}

	@Override
	public AnswerAdaptor<Boolean> getMapBasisChecker()
	{
		return new MapBasisChecker(this);
	}

	@Override
	public IAnswer<SMapType> getMapTypeFinder()
	{
		return new MapTypeFinder(this);
	}

	@Override
	public IAnswer<SSeqType> getSeqTypeFinder()
	{
		return new SeqTypeFinder(this);
	}

	@Override
	public AnswerAdaptor<Boolean> getSeqBasisChecker()
	{
		return new SeqBasisChecker(this);
	}

	@Override
	public IAnswer<AOperationType> getOperationTypeFinder()
	{
		return new OperationTypeFinder(this);
	}

	@Override
	public AnswerAdaptor<Boolean> getOperationBasisChecker()
	{
		return new OperationBasisChecker(this);
	}

	@Override
	public AnswerAdaptor<Boolean> getSetBasisChecker()
	{
		return new SetBasisChecker(this);
	}

	@Override
	public IAnswer<ASetType> getSetTypeFinder()
	{
		return new SetTypeFinder(this);
	}

	@Override
	public AnswerAdaptor<Boolean> getRecordBasisChecker()
	{
		return new RecordBasisChecker(this);
	}

	@Override
	public IAnswer<ARecordInvariantType> getRecordTypeFinder()
	{
		return new RecordTypeFinder(this);
	}

	@Override
	public AnswerAdaptor<Boolean> getClassBasisChecker()
	{
		return new ClassBasisChecker(this);
	}

	@Override
	public IAnswer<AClassType> getClassTypeFinder()
	{
		return new ClassTypeFinder(this);
	}

	@Override
	public IAnswer<AProductType> getProductTypeFinder()
	{
		return new ProductTypeFinder(this);
	}

	@Override
	public AnswerAdaptor<Boolean> getProductBasisChecker()
	{
		return new ProductBasisChecker(this);
	}

	@Override
	public IAnswer<AUnionType> getUnionTypeFinder()
	{
		return new UnionTypeFinder(this);
	}

	@Override
	public IQuestionAnswer<Object, Boolean> getTypeEqualityChecker()
	{
		return new TypeEqualityChecker(this);
	}

	@Override
	public IAnswer<String> getTypeDisplayer()
	{
		return new TypeDisplayer(this);
	}

	@Override
	public AnalysisAdaptor getTypeUnresolver()
	{
		return new TypeUnresolver(this);
	}

	@Override
	public IQuestionAnswer<AAccessSpecifierAccessSpecifier, Boolean> getNarrowerThanComparator()
	{
		return new NarrowerThanComparator(this);
	}
	
	@Override
	public AnswerAdaptor<Boolean> getUnionBasisChecker()
	{
		return new UnionBasisChecker(this);
	}
	
	@Override
	public IAnswer<AFunctionType> getFunctionTypeFinder()
	{
		return new FunctionTypeFinder(this);
	}
	
	@Override
	public IQuestionAnswer<org.overture.typechecker.utilities.type.ConcreateTypeImplementor.Newquestion, PType> getConcreateTypeImplementor()
	{
		return new ConcreateTypeImplementor(this);
	}
	
	@Override
	public IQuestionAnswer<org.overture.typechecker.utilities.type.PTypeResolver.Newquestion, PType> getPTypeResolver()
	{
		return new PTypeResolver(this);
	}
	
	@Override
	public IQuestionAnswer<String, PType> getPTypeFinder()
	{
		return new PTypeFinder(this);
	}
	
	@Override
	public IQuestionAnswer<Integer, Boolean> getProductExtendedChecker()
	{
		return new ProductExtendedChecker(this);
	}
	
	@Override
	public IQuestionAnswer<Integer, AProductType> getProductExtendedTypeFinder()
	{
		return new ProductExtendedTypeFinder(this);
	}
	
	@Override
	public IQuestionAnswer<Class<? extends PType>, Boolean> getPTypeExtendedChecker()
	{
		return new PTypeExtendedChecker(this);
	}
	
	@Override
	public IAnswer<Boolean> getVoidExistanceChecker()
	{
		return new VoidExistanceChecker(this);
	}
	
	@Override
	public IAnswer<Boolean> getVoidBasisChecker()
	{
		return new VoidBasisChecker(this);
	}
	
	@Override
	public IAnswer<PType> getPossibleTypeFinder()
	{
		return new PossibleTypeFinder(this);
	}
	
	@Override
	public IAnswer<PExp> getMatchingExpressionFinder()
	{
		return new MatchingExpressionFinder(this);
	}
	
	@Override
	public IAnswer<Boolean> getSimplePatternChecker()
	{
		return new SimplePatternChecker(this);
	}
	
	@Override
	public IAnswer<Boolean> getAlwaysMatchingPatternChecker()
	{
		return new AlwaysMatchingPatternChecker(this);
	}
	
	@Override
	public AnalysisAdaptor getPatternUnresolver()
	{
		return new PatternUnresolver(this);
	}
}
