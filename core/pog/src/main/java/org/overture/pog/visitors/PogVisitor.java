package org.overture.pog.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.AProtectedAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.PAccess;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.PAlternative;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.PModifier;
import org.overture.ast.modules.AAllExport;
import org.overture.ast.modules.AFunctionExport;
import org.overture.ast.modules.AModuleExports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.AOperationExport;
import org.overture.ast.modules.ATypeExport;
import org.overture.ast.modules.AValueExport;
import org.overture.ast.modules.PExport;
import org.overture.ast.modules.PExports;
import org.overture.ast.modules.PImports;
import org.overture.ast.modules.PModules;
import org.overture.ast.node.AFalseBooleanConst;
import org.overture.ast.node.ATrueBooleanConst;
import org.overture.ast.node.Node;
import org.overture.ast.node.PBooleanConst;
import org.overture.ast.node.Token;
import org.overture.ast.node.tokens.TAndAnd;
import org.overture.ast.node.tokens.TAsync;
import org.overture.ast.node.tokens.TBool;
import org.overture.ast.node.tokens.TBoolLiteral;
import org.overture.ast.node.tokens.TChar;
import org.overture.ast.node.tokens.TCharLiteral;
import org.overture.ast.node.tokens.TFalse;
import org.overture.ast.node.tokens.TInt;
import org.overture.ast.node.tokens.TNat;
import org.overture.ast.node.tokens.TNatOne;
import org.overture.ast.node.tokens.TNumbersLiteral;
import org.overture.ast.node.tokens.TOrOr;
import org.overture.ast.node.tokens.TPlus;
import org.overture.ast.node.tokens.TQuoteLiteral;
import org.overture.ast.node.tokens.TRat;
import org.overture.ast.node.tokens.TReal;
import org.overture.ast.node.tokens.TRealLiteral;
import org.overture.ast.node.tokens.TStatic;
import org.overture.ast.node.tokens.TStringLiteral;
import org.overture.ast.node.tokens.TTokenLiteral;
import org.overture.ast.node.tokens.TTrue;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.PPatternBind;
import org.overture.ast.statements.AApplyObjectDesignator;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.statements.AFieldObjectDesignator;
import org.overture.ast.statements.AFieldStateDesignator;
import org.overture.ast.statements.AIdentifierObjectDesignator;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.AMapSeqStateDesignator;
import org.overture.ast.statements.ANewObjectDesignator;
import org.overture.ast.statements.ASelfObjectDesignator;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.PAlternativeStm;
import org.overture.ast.statements.PCase;
import org.overture.ast.statements.PClause;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.PStmtAlternative;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PField;
import org.overture.ast.types.PType;
import org.overture.pog.assistants.PDefinitionAssistantPOG;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.ProofObligationList;
import org.overturetool.util.ClonableFile;
import org.overturetool.vdmj.lex.LexBooleanToken;
import org.overturetool.vdmj.lex.LexCharacterToken;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexQuoteToken;
import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.lex.LexStringToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.typechecker.ClassDefinitionSettings;
import org.overturetool.vdmj.typechecker.NameScope;

/**
 * This is the proof obligation visitor climbs through the AST and builds the
 * list of proof obligations the given program exhibits.
 * 
 * References:
 * 
 * [1] http://wiki.overturetool.org/images/9/95/VDM10_lang_man.pdf for BNF
 * definitions.
 * 
 * This work is based on previous work by Nick Battle in the VDMJ package.
 * 
 * @author Overture team
 * @since 1.0
 */
public class PogVisitor extends
		QuestionAnswerAdaptor<POContextStack, ProofObligationList> {

	private PogExpVisitor pogExpVisitor = new PogExpVisitor(this);
	private PogStmVisitor pogStmVisitor = new PogStmVisitor(this);
	private PogDefinitionVisitor pogDefinitionVisitor = new PogDefinitionVisitor(
			this);
	private PogImportVisitor pogImportVisitor = new PogImportVisitor(this);
	private PogTypeVisitor pogTypeVisitor = new PogTypeVisitor(this);
	private PogPatternVisitor pogPatternVisitor = new PogPatternVisitor(this);

	@Override
	// See [1] pg. 167 for the definition
	public ProofObligationList caseAModuleModules(AModuleModules node,
			POContextStack question) {
		return PDefinitionAssistantPOG.getProofObligations(node.getDefs(),
				pogDefinitionVisitor, question);

	}

	@Override
	public ProofObligationList caseLexToken(LexToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexToken(node, question);
	}

	@Override
	public ProofObligationList caseLexNameToken(LexNameToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexNameToken(node, question);
	}

	@Override
	public ProofObligationList caseLexIdentifierToken(LexIdentifierToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexIdentifierToken(node, question);
	}

	@Override
	public ProofObligationList caseLexBooleanToken(LexBooleanToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexBooleanToken(node, question);
	}

	@Override
	public ProofObligationList caseLexCharacterToken(LexCharacterToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexCharacterToken(node, question);
	}

	@Override
	public ProofObligationList caseLexIntegerToken(LexIntegerToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexIntegerToken(node, question);
	}

	@Override
	public ProofObligationList caseLexQuoteToken(LexQuoteToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexQuoteToken(node, question);
	}

	@Override
	public ProofObligationList caseLexRealToken(LexRealToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexRealToken(node, question);
	}

	@Override
	public ProofObligationList caseLexStringToken(LexStringToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexStringToken(node, question);
	}

	@Override
	public ProofObligationList caseClonableFile(ClonableFile node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseClonableFile(node, question);
	}

	@Override
	public ProofObligationList caseClassDefinitionSettings(
			ClassDefinitionSettings node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseClassDefinitionSettings(node, question);
	}

	@Override
	public ProofObligationList caseLexLocation(LexLocation node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexLocation(node, question);
	}

	@Override
	public ProofObligationList caseTPlus(TPlus node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTPlus(node, question);
	}

	@Override
	public ProofObligationList caseBoolean(Boolean node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseBoolean(node, question);
	}

	@Override
	public ProofObligationList caseInteger(Integer node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseInteger(node, question);
	}

	@Override
	public ProofObligationList caseString(String node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseString(node, question);
	}

	@Override
	public ProofObligationList caseLong(Long node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLong(node, question);
	}

	@Override
	public ProofObligationList caseNameScope(NameScope node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseNameScope(node, question);
	}

	@Override
	public ProofObligationList caseTBool(TBool node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTBool(node, question);
	}

	@Override
	public ProofObligationList caseTChar(TChar node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTChar(node, question);
	}

	@Override
	public ProofObligationList caseTInt(TInt node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTInt(node, question);
	}

	@Override
	public ProofObligationList caseTNatOne(TNatOne node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTNatOne(node, question);
	}

	@Override
	public ProofObligationList caseTNat(TNat node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTNat(node, question);
	}

	@Override
	public ProofObligationList caseTRat(TRat node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTRat(node, question);
	}

	@Override
	public ProofObligationList caseTReal(TReal node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTReal(node, question);
	}

	@Override
	public ProofObligationList caseTTrue(TTrue node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTTrue(node, question);
	}

	@Override
	public ProofObligationList caseTFalse(TFalse node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTFalse(node, question);
	}

	@Override
	public ProofObligationList caseTAndAnd(TAndAnd node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTAndAnd(node, question);
	}

	@Override
	public ProofObligationList caseTOrOr(TOrOr node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTOrOr(node, question);
	}

	@Override
	public ProofObligationList caseTNumbersLiteral(TNumbersLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTNumbersLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTCharLiteral(TCharLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTCharLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTQuoteLiteral(TQuoteLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTQuoteLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTRealLiteral(TRealLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTRealLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTStringLiteral(TStringLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTStringLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTTokenLiteral(TTokenLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTTokenLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTBoolLiteral(TBoolLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTBoolLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTStatic(TStatic node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTStatic(node, question);
	}

	@Override
	public ProofObligationList caseTAsync(TAsync node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTAsync(node, question);
	}

	@Override
	public ProofObligationList defaultPExp(PExp node, POContextStack question) {

		return node.apply(pogExpVisitor, question);
	}

	@Override
	public ProofObligationList defaultPModifier(PModifier node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPModifier(node, question);
	}

	@Override
	public ProofObligationList caseARecordModifier(ARecordModifier node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARecordModifier(node, question);
	}

	@Override
	public ProofObligationList defaultPAlternative(PAlternative node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPAlternative(node, question);
	}

	@Override
	public ProofObligationList caseACaseAlternative(ACaseAlternative node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACaseAlternative(node, question);
	}

	@Override
	public ProofObligationList defaultPBooleanConst(PBooleanConst node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPBooleanConst(node, question);
	}

	@Override
	public ProofObligationList caseATrueBooleanConst(ATrueBooleanConst node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATrueBooleanConst(node, question);
	}

	@Override
	public ProofObligationList caseAFalseBooleanConst(AFalseBooleanConst node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFalseBooleanConst(node, question);
	}

	@Override
	public ProofObligationList defaultPType(PType node, POContextStack question) {

		return node.apply(pogTypeVisitor, question);
	}

	@Override
	public ProofObligationList defaultPField(PField node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPField(node, question);
	}

	@Override
	public ProofObligationList caseAFieldField(AFieldField node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFieldField(node, question);
	}

	@Override
	public ProofObligationList defaultPAccessSpecifier(PAccessSpecifier node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPAccessSpecifier(node, question);
	}

	@Override
	public ProofObligationList caseAAccessSpecifierAccessSpecifier(
			AAccessSpecifierAccessSpecifier node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAccessSpecifierAccessSpecifier(node, question);
	}

	@Override
	public ProofObligationList defaultPAccess(PAccess node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPAccess(node, question);
	}

	@Override
	public ProofObligationList caseAPublicAccess(APublicAccess node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPublicAccess(node, question);
	}

	@Override
	public ProofObligationList caseAProtectedAccess(AProtectedAccess node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAProtectedAccess(node, question);
	}

	@Override
	public ProofObligationList caseAPrivateAccess(APrivateAccess node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPrivateAccess(node, question);
	}

	@Override
	public ProofObligationList defaultPPattern(PPattern node,
			POContextStack question) {
		return node.apply(pogPatternVisitor, question);
	}

	@Override
	public ProofObligationList defaultPPair(PPair node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPPair(node, question);
	}

	@Override
	public ProofObligationList caseAPatternTypePair(APatternTypePair node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPatternTypePair(node, question);
	}

	@Override
	public ProofObligationList caseAPatternListTypePair(
			APatternListTypePair node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPatternListTypePair(node, question);
	}

	@Override
	public ProofObligationList defaultPBind(PBind node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPBind(node, question);
	}

	@Override
	public ProofObligationList caseASetBind(ASetBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetBind(node, question);
	}

	@Override
	public ProofObligationList caseATypeBind(ATypeBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATypeBind(node, question);
	}

	@Override
	public ProofObligationList defaultPMultipleBind(PMultipleBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPMultipleBind(node, question);
	}

	@Override
	public ProofObligationList caseASetMultipleBind(ASetMultipleBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetMultipleBind(node, question);
	}

	@Override
	public ProofObligationList caseATypeMultipleBind(ATypeMultipleBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATypeMultipleBind(node, question);
	}

	@Override
	public ProofObligationList defaultPPatternBind(PPatternBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPPatternBind(node, question);
	}

	@Override
	public ProofObligationList caseADefPatternBind(ADefPatternBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADefPatternBind(node, question);
	}

	@Override
	public ProofObligationList defaultPDefinition(PDefinition node,
			POContextStack question) {

		return node.apply(pogExpVisitor, question);
	}

	@Override
	public ProofObligationList defaultPModules(PModules node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPModules(node, question);
	}

	@Override
	public ProofObligationList defaultPImports(PImports node,
			POContextStack question) {

		return node.apply(pogImportVisitor, question);
	}

	@Override
	public ProofObligationList defaultPExports(PExports node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPExports(node, question);
	}

	@Override
	public ProofObligationList caseAModuleExports(AModuleExports node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAModuleExports(node, question);
	}

	@Override
	public ProofObligationList defaultPExport(PExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPExport(node, question);
	}

	@Override
	public ProofObligationList caseAAllExport(AAllExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAllExport(node, question);
	}

	@Override
	public ProofObligationList caseAFunctionExport(AFunctionExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFunctionExport(node, question);
	}

	@Override
	public ProofObligationList caseAOperationExport(AOperationExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAOperationExport(node, question);
	}

	@Override
	public ProofObligationList caseATypeExport(ATypeExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATypeExport(node, question);
	}

	@Override
	public ProofObligationList caseAValueExport(AValueExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAValueExport(node, question);
	}

	@Override
	public ProofObligationList defaultPStm(PStm node, POContextStack question) {

		return node.apply(pogStmVisitor, question);
	}

	@Override
	public ProofObligationList defaultPStateDesignator(PStateDesignator node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPStateDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAFieldStateDesignator(
			AFieldStateDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFieldStateDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAIdentifierStateDesignator(
			AIdentifierStateDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIdentifierStateDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAMapSeqStateDesignator(
			AMapSeqStateDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMapSeqStateDesignator(node, question);
	}

	@Override
	public ProofObligationList defaultPObjectDesignator(PObjectDesignator node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAApplyObjectDesignator(
			AApplyObjectDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAApplyObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAFieldObjectDesignator(
			AFieldObjectDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFieldObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAIdentifierObjectDesignator(
			AIdentifierObjectDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIdentifierObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList caseANewObjectDesignator(
			ANewObjectDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANewObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList caseASelfObjectDesignator(
			ASelfObjectDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASelfObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList defaultPAlternativeStm(PAlternativeStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPAlternativeStm(node, question);
	}

	@Override
	public ProofObligationList caseACaseAlternativeStm(
			ACaseAlternativeStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACaseAlternativeStm(node, question);
	}

	@Override
	public ProofObligationList defaultPStmtAlternative(PStmtAlternative node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPStmtAlternative(node, question);
	}

	@Override
	public ProofObligationList caseATixeStmtAlternative(
			ATixeStmtAlternative node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATixeStmtAlternative(node, question);
	}

	@Override
	public ProofObligationList defaultPClause(PClause node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPClause(node, question);
	}

	@Override
	public ProofObligationList caseAExternalClause(AExternalClause node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAExternalClause(node, question);
	}

	@Override
	public ProofObligationList defaultPCase(PCase node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPCase(node, question);
	}

	@Override
	public ProofObligationList caseAErrorCase(AErrorCase node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAErrorCase(node, question);
	}

	@Override
	public ProofObligationList defaultNode(Node node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultNode(node, question);
	}

	@Override
	public ProofObligationList defaultToken(Token node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultToken(node, question);
	}

}
