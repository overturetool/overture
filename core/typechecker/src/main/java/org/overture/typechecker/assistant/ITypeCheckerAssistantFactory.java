/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.typechecker.assistant;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.patterns.PMultipleBind;
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
import org.overture.typechecker.LexNameTokenAssistant;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AExplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionSet;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.SFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.module.AModuleImportsAssistantTC;
import org.overture.typechecker.assistant.module.AModuleModulesAssistantTC;
import org.overture.typechecker.assistant.pattern.APatternTypePairAssistant;
import org.overture.typechecker.assistant.pattern.ATypeBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;
import org.overture.typechecker.assistant.pattern.PatternListTC;
import org.overture.typechecker.assistant.type.AClassTypeAssistantTC;
import org.overture.typechecker.assistant.type.AFunctionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOperationTypeAssistantTC;
import org.overture.typechecker.assistant.type.ARecordInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;
import org.overture.typechecker.utilities.DefinitionFinder;
import org.overture.typechecker.utilities.DefinitionTypeResolver;
import org.overture.typechecker.utilities.NameFinder;
import org.overture.typechecker.utilities.pattern.AllDefinitionLocator;
import org.overture.typechecker.utilities.pattern.PatternResolver;
import org.overture.typechecker.utilities.type.ConcreateTypeImplementor;
import org.overture.typechecker.utilities.type.PTypeResolver;
import org.overture.typechecker.utilities.type.QualifiedDefinition;

//TODO Add assistant Javadoc
/**
 * The Interface specifying what is offered by the Overture TC assistant functionalities.
 * 
 * @author ldc
 */
public interface ITypeCheckerAssistantFactory extends IAstAssistantFactory
{

//	ABusClassDefinitionAssistantTC createABusClassDefinitionAssistant();

//	ACpuClassDefinitionAssistantTC createACpuClassDefinitionAssistant();

	AExplicitFunctionDefinitionAssistantTC createAExplicitFunctionDefinitionAssistant();

	AExplicitOperationDefinitionAssistantTC createAExplicitOperationDefinitionAssistant();

	AImplicitFunctionDefinitionAssistantTC createAImplicitFunctionDefinitionAssistant();

	AImplicitOperationDefinitionAssistantTC createAImplicitOperationDefinitionAssistant();

	// AImportedDefinitionAssistantTC createAImportedDefinitionAssistant();

	//AInstanceVariableDefinitionAssistantTC createAInstanceVariableDefinitionAssistant();

	//ALocalDefinitionAssistantTC createALocalDefinitionAssistant();

	//AStateDefinitionAssistantTC createAStateDefinitionAssistant();

	// ASystemClassDefinitionAssistantTC createASystemClassDefinitionAssistant();

//	AThreadDefinitionAssistantTC createAThreadDefinitionAssistant();

//	ATypeDefinitionAssistantTC createATypeDefinitionAssistant();

	// AValueDefinitionAssistantTC createAValueDefinitionAssistant();

	PAccessSpecifierAssistantTC createPAccessSpecifierAssistant();

	PDefinitionAssistantTC createPDefinitionAssistant();

	PDefinitionListAssistantTC createPDefinitionListAssistant();

	PDefinitionSet createPDefinitionSet();

//	PTraceDefinitionAssistantTC createPTraceDefinitionAssistant();

	SClassDefinitionAssistantTC createSClassDefinitionAssistant();

	SFunctionDefinitionAssistantTC createSFunctionDefinitionAssistant();
	
	// expression
	//AApplyExpAssistantTC createAApplyExpAssistant();

	//ACaseAlternativeAssistantTC createACaseAlternativeAssistant();

	//PExpAssistantTC createPExpAssistant();

//	SBinaryExpAssistantTC createSBinaryExpAssistant();

	// module

	//AFromModuleImportsAssistantTC createAFromModuleImportsAssistant();

	//AModuleExportsAssistantTC createAModuleExportsAssistant();

	AModuleImportsAssistantTC createAModuleImportsAssistant();

	AModuleModulesAssistantTC createAModuleModulesAssistant();

	//PExportAssistantTC createPExportAssistant();

	//PImportAssistantTC createPImportAssistant();

	// pattern
	// ABooleanPatternAssistantTC createABooleanPatternAssistant();

	// ACharacterPatternAssistantTC createACharacterPatternAssistant();

	// AConcatenationPatternAssistantTC createAConcatenationPatternAssistant();
	
	// AExpressionPatternAssistantTC createAExpressionPatternAssistant();

//	AMapletPatternMapletAssistantTC createAMapletPatternMapletAssistant();

	// AMapPatternAssistantTC createAMapPatternAssistant();

	// AMapUnionPatternAssistantTC createAMapUnionPatternAssistant();

	APatternTypePairAssistant createAPatternTypePairAssistant();

	// ARecordPatternAssistantTC createARecordPatternAssistant();

	// ASeqPatternAssistantTC createASeqPatternAssistant();

	//ASetBindAssistantTC createASetBindAssistant();

	// ASetPatternAssistantTC createASetPatternAssistant();

	// ATuplePatternAssistantTC createATuplePatternAssistant();

	ATypeBindAssistantTC createATypeBindAssistant();

	// AUnionPatternAssistantTC createAUnionPatternAssistant();

	PatternListTC createPatternList();

	PBindAssistantTC createPBindAssistant();

	PMultipleBindAssistantTC createPMultipleBindAssistant();

	PPatternAssistantTC createPPatternAssistant();

	//PPatternBindAssistantTC createPPatternBindAssistant();

	PPatternListAssistantTC createPPatternListAssistant();

	// statement
	// AAlwaysStmAssistantTC createAAlwaysStmAssistant();
	// AAssignmentStmAssistantTC createAAssignmentStmAssistant();
//	ABlockSimpleBlockStmAssistantTC createABlockSimpleBlockStmAssistant();

//	ACallObjectStatementAssistantTC createACallObjectStatementAssistant();

//	ACallStmAssistantTC createACallStmAssistant();

	// ACaseAlternativeStmAssistantTC createACaseAlternativeStmAssistant();
	// ACasesStmAssistantTC createACasesStmAssistant();
	// AElseIfStmAssistantTC createAElseIfStmAssistant();
	// AExitStmAssistantTC createAExitStmAssistant();
//	AExternalClauseAssistantTC createAExternalClauseAssistant();

	// AForAllStmAssistantTC createAForAllStmAssistant();
	// AForIndexStmAssistantTC createAForIndexStmAssistant();
	// AForPatternBindStmAssitantTC createAForPatternBindStmAssitant();
	// AIfStmAssistantTC createAIfStmAssistant();
	// ALetBeStStmAssistantTC createALetBeStStmAssistant();
//	ANonDeterministicSimpleBlockStmAssistantTC createANonDeterministicSimpleBlockStmAssistant();

	// AReturnStmAssistantTC createAReturnStmAssistant();
	// ATixeStmAssistantTC createATixeStmAssistant();
	// ATrapStmAssistantTC createATrapStmAssistant();
	// AWhileStmAssistantTC createAWhileStmAssistant();
//	PStateDesignatorAssistantTC createPStateDesignatorAssistant();

	//PStmAssistantTC createPStmAssistant();

	// SLetDefStmAssistantTC createSLetDefStmAssistant();
	// SSimpleBlockStmAssistantTC createSSimpleBlockStmAssistant();

	// Type
//	AApplyObjectDesignatorAssistantTC createAApplyObjectDesignatorAssistant();

	// ABracketTypeAssistantTC createABracketTypeAssistant();

	AClassTypeAssistantTC createAClassTypeAssistant();

	AFunctionTypeAssistantTC createAFunctionTypeAssistant();

	AOperationTypeAssistantTC createAOperationTypeAssistant();

//	APatternListTypePairAssistantTC createAPatternListTypePairAssistant();

	ARecordInvariantTypeAssistantTC createARecordInvariantTypeAssistant();

//	AUnionTypeAssistantTC createAUnionTypeAssistant();

	PTypeAssistantTC createPTypeAssistant();

	// visitors

	// SSeqTypeAssistantTC createSSeqTypeAssistant();

	// stuff to delete ends here

	// visitor getters that we will actually keep

	IAnswer<List<PDefinition>> getDefinitionCollector();

	IAnswer<PType> getDefinitionTypeFinder();

	IQuestionAnswer<Object, Boolean> getDefinitionEqualityChecker();

	AnswerAdaptor<Boolean> getMapBasisChecker();

	IAnswer<LexNameList> getVariableNameCollector();

	IAnswer<PDefinition> getSelfDefinitionFinder();

	IAnswer<PTypeSet> getExitTypeCollector();

	IQuestionAnswer<DefinitionFinder.Newquestion, PDefinition> getDefinitionFinder();

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

	IQuestion<DefinitionTypeResolver.NewQuestion> getDefinitionTypeResolver();

	IAnswer<SMapType> getMapTypeFinder();

	IAnswer<SSeqType> getSeqTypeFinder();

	AnswerAdaptor<Boolean> getSeqBasisChecker();

	IAnswer<AOperationType> getOperationTypeFinder();

	AnswerAdaptor<Boolean> getOperationBasisChecker();

	AnswerAdaptor<Boolean> getSetBasisChecker();

	IAnswer<ASetType> getSetTypeFinder();

	AnswerAdaptor<Boolean> getRecordBasisChecker();

	AnswerAdaptor<Boolean> getTagBasisChecker();

	IAnswer<ARecordInvariantType> getRecordTypeFinder();

	AnswerAdaptor<Boolean> getClassBasisChecker(Environment env);

	IAnswer<AClassType> getClassTypeFinder(Environment env);

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

	IAnswer<Boolean> getSimplePatternChecker();

	IAnswer<Boolean> getAlwaysMatchingPatternChecker();

	AnalysisAdaptor getPatternUnresolver();

	IQuestion<PatternResolver.NewQuestion> getPatternResolver();

	IQuestionAnswer<AllDefinitionLocator.NewQuestion, List<PDefinition>> getAllDefinitionLocator();

	IAnswer<PType> getPossibleBindTypeFinder();

	IAnswer<List<PMultipleBind>> getMultipleBindLister();

	IAnswer<ILexNameToken> getPreNameFinder();

	IQuestionAnswer<LinkedList<PDefinition>, Collection<? extends PDefinition>> getExportDefinitionFinder();

	IAnswer<Collection<? extends PDefinition>> getExportDefinitionListFinder();

	IQuestionAnswer<AModuleModules, List<PDefinition>> getImportDefinitionFinder();

	IAnswer<PTypeList> getComposeTypeCollector();

	TypeComparator getTypeComparator();

	LexNameTokenAssistant getLexNameTokenAssistant();

	IQuestionAnswer<TypeCheckInfo, List<QualifiedDefinition>> getQualificationVisitor();
	
	IAnswer<Boolean> getInstanceVariableChecker();
}
