package org.overture.interpreter.runtime;


import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.definitions.*;
import org.overture.ast.definitions.traces.*;
import org.overture.ast.expressions.*;
import org.overture.ast.intf.lex.*;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.modules.*;
import org.overture.ast.node.INode;
import org.overture.ast.node.IToken;
import org.overture.ast.node.tokens.TAsync;
import org.overture.ast.node.tokens.TStatic;
import org.overture.ast.patterns.*;
import org.overture.ast.statements.*;
import org.overture.ast.typechecker.ClassDefinitionSettings;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.typechecker.Pass;
import org.overture.ast.types.*;
import org.overture.ast.util.ClonableFile;
import org.overture.ast.util.ClonableString;
import org.overture.interpreter.values.ObjectValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

public class CoverageToXML implements IAnalysis{
    private Document doc;
    private Element rootElement;
    private Element currentElement;
    private Context ctx;

    public CoverageToXML(){
        DocumentBuilder db = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        this.doc = db.newDocument();
        this.rootElement = doc.createElement("file");
        this.currentElement = rootElement;
        this.doc.appendChild(rootElement);
        this.ctx = null;
    }

    public void setContext(Context context){
        this.ctx=context;
    }

    public static void fill_source_file_location(Element and, ILexLocation local) {
        and.setAttribute("start_line", Integer.toString(local.getStartLine()));
        and.setAttribute("start_column", Integer.toString(local.getStartPos()));
        and.setAttribute("end_line", Integer.toString(local.getEndLine()));
        and.setAttribute("end_column", Integer.toString(local.getEndPos()));
    }

    public void saveCoverageXml(File coverage,String  filename) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(coverage.getPath() + File.separator + filename + ".xml"));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void caseILexToken(ILexToken node) throws AnalysisException {

    }

    @Override
    public void caseILexNameToken(ILexNameToken node) throws AnalysisException {

    }

    @Override
    public void caseILexIdentifierToken(ILexIdentifierToken node) throws AnalysisException {

    }

    @Override
    public void caseILexBooleanToken(ILexBooleanToken node) throws AnalysisException {

    }

    @Override
    public void caseILexCharacterToken(ILexCharacterToken node) throws AnalysisException {

    }

    @Override
    public void caseILexIntegerToken(ILexIntegerToken node) throws AnalysisException {

    }

    @Override
    public void caseILexQuoteToken(ILexQuoteToken node) throws AnalysisException {

    }

    @Override
    public void caseILexRealToken(ILexRealToken node) throws AnalysisException {

    }

    @Override
    public void caseILexStringToken(ILexStringToken node) throws AnalysisException {

    }

    @Override
    public void caseILexLocation(ILexLocation node) throws AnalysisException {

    }

    @Override
    public void caseClonableFile(ClonableFile node) throws AnalysisException {

    }

    @Override
    public void caseClonableString(ClonableString node) throws AnalysisException {

    }

    @Override
    public void caseClassDefinitionSettings(ClassDefinitionSettings node) throws AnalysisException {

    }

    @Override
    public void caseNameScope(NameScope node) throws AnalysisException {

    }

    @Override
    public void casePass(Pass node) throws AnalysisException {

    }

    @Override
    public void caseBoolean(Boolean node) throws AnalysisException {

    }

    @Override
    public void caseInteger(Integer node) throws AnalysisException {

    }

    @Override
    public void caseString(String node) throws AnalysisException {

    }

    @Override
    public void caseLong(Long node) throws AnalysisException {

    }

    @Override
    public void caseTStatic(TStatic node) throws AnalysisException {

    }

    @Override
    public void caseTAsync(TAsync node) throws AnalysisException {

    }

    @Override
    public void defaultPExp(PExp node) throws AnalysisException {

    }

    @Override
    public void caseAApplyExp(AApplyExp node) throws AnalysisException {

    }

    @Override
    public void caseANarrowExp(ANarrowExp node) throws AnalysisException {

    }

    @Override
    public void defaultSUnaryExp(SUnaryExp node) throws AnalysisException {

    }

    @Override
    public void defaultSBinaryExp(SBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseABooleanConstExp(ABooleanConstExp node) throws AnalysisException {

    }

    @Override
    public void caseACasesExp(ACasesExp node) throws AnalysisException {

    }

    @Override
    public void caseACharLiteralExp(ACharLiteralExp node) throws AnalysisException {

    }

    @Override
    public void caseAElseIfExp(AElseIfExp node) throws AnalysisException {

    }

    @Override
    public void caseAExists1Exp(AExists1Exp node) throws AnalysisException {

    }

    @Override
    public void caseAExistsExp(AExistsExp node) throws AnalysisException {

    }

    @Override
    public void caseAFieldExp(AFieldExp node) throws AnalysisException {

    }

    @Override
    public void caseAFieldNumberExp(AFieldNumberExp node) throws AnalysisException {

    }

    @Override
    public void caseAForAllExp(AForAllExp node) throws AnalysisException {

    }

    @Override
    public void caseAFuncInstatiationExp(AFuncInstatiationExp node) throws AnalysisException {

    }

    @Override
    public void caseAHistoryExp(AHistoryExp node) throws AnalysisException {

    }

    @Override
    public void caseAIfExp(AIfExp node) throws AnalysisException {

    }

    @Override
    public void caseAIntLiteralExp(AIntLiteralExp node) throws AnalysisException {

    }

    @Override
    public void caseAIotaExp(AIotaExp node) throws AnalysisException {

    }

    @Override
    public void caseAIsExp(AIsExp node) throws AnalysisException {

    }

    @Override
    public void caseAIsOfBaseClassExp(AIsOfBaseClassExp node) throws AnalysisException {

    }

    @Override
    public void caseAIsOfClassExp(AIsOfClassExp node) throws AnalysisException {

    }

    @Override
    public void caseALambdaExp(ALambdaExp node) throws AnalysisException {

    }

    @Override
    public void caseALetBeStExp(ALetBeStExp node) throws AnalysisException {

    }

    @Override
    public void caseALetDefExp(ALetDefExp node) throws AnalysisException {

    }

    @Override
    public void caseADefExp(ADefExp node) throws AnalysisException {

    }

    @Override
    public void defaultSMapExp(SMapExp node) throws AnalysisException {

    }

    @Override
    public void caseAMapletExp(AMapletExp node) throws AnalysisException {

    }

    @Override
    public void caseAMkBasicExp(AMkBasicExp node) throws AnalysisException {

    }

    @Override
    public void caseAMkTypeExp(AMkTypeExp node) throws AnalysisException {

    }

    @Override
    public void caseAMuExp(AMuExp node) throws AnalysisException {

    }

    @Override
    public void caseANewExp(ANewExp node) throws AnalysisException {

    }

    @Override
    public void caseANilExp(ANilExp node) throws AnalysisException {

    }

    @Override
    public void caseANotYetSpecifiedExp(ANotYetSpecifiedExp node) throws AnalysisException {

    }

    @Override
    public void caseAPostOpExp(APostOpExp node) throws AnalysisException {

    }

    @Override
    public void caseAPreExp(APreExp node) throws AnalysisException {

    }

    @Override
    public void caseAPreOpExp(APreOpExp node) throws AnalysisException {

    }

    @Override
    public void caseAQuoteLiteralExp(AQuoteLiteralExp node) throws AnalysisException {

    }

    @Override
    public void caseARealLiteralExp(ARealLiteralExp node) throws AnalysisException {

    }

    @Override
    public void caseASameBaseClassExp(ASameBaseClassExp node) throws AnalysisException {

    }

    @Override
    public void caseASameClassExp(ASameClassExp node) throws AnalysisException {

    }

    @Override
    public void caseASelfExp(ASelfExp node) throws AnalysisException {

    }

    @Override
    public void defaultSSeqExp(SSeqExp node) throws AnalysisException {

    }

    @Override
    public void defaultSSetExp(SSetExp node) throws AnalysisException {

    }

    @Override
    public void caseAStateInitExp(AStateInitExp node) throws AnalysisException {

    }

    @Override
    public void caseAStringLiteralExp(AStringLiteralExp node) throws AnalysisException {

    }

    @Override
    public void caseASubclassResponsibilityExp(ASubclassResponsibilityExp node) throws AnalysisException {

    }

    @Override
    public void caseASubseqExp(ASubseqExp node) throws AnalysisException {

    }

    @Override
    public void caseAThreadIdExp(AThreadIdExp node) throws AnalysisException {

    }

    @Override
    public void caseATimeExp(ATimeExp node) throws AnalysisException {

    }

    @Override
    public void caseATupleExp(ATupleExp node) throws AnalysisException {

    }

    @Override
    public void caseAUndefinedExp(AUndefinedExp node) throws AnalysisException {

    }

    @Override
    public void caseAVariableExp(AVariableExp node) throws AnalysisException {
        ILexLocation local=node.getLocation();
        Element eval=doc.createElement("evaluation");
        eval.setTextContent(ctx.lookup(node.getName()).toString());
        System.out.println("-->"+ctx.lookup(node.getName()));
        eval.setAttribute("n","1");
        currentElement.appendChild(eval);
    }

    @Override
    public void caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseACardinalityUnaryExp(ACardinalityUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseADistConcatUnaryExp(ADistConcatUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseADistIntersectUnaryExp(ADistIntersectUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseADistMergeUnaryExp(ADistMergeUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseADistUnionUnaryExp(ADistUnionUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAElementsUnaryExp(AElementsUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAFloorUnaryExp(AFloorUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAHeadUnaryExp(AHeadUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAIndicesUnaryExp(AIndicesUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseALenUnaryExp(ALenUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAMapDomainUnaryExp(AMapDomainUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAMapInverseUnaryExp(AMapInverseUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAMapRangeUnaryExp(AMapRangeUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseANotUnaryExp(ANotUnaryExp node) throws AnalysisException {
        Element not=doc.createElement("not");
        ILexLocation local=node.getLocation();
        fill_source_file_location(not, local);
        PExp expression = node.getExp();
        currentElement.appendChild(not);
        currentElement = not;
        expression.apply(this);
    }

    @Override
    public void caseAPowerSetUnaryExp(APowerSetUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAReverseUnaryExp(AReverseUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseATailUnaryExp(ATailUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node) throws AnalysisException {

    }

    @Override
    public void defaultSBooleanBinaryExp(SBooleanBinaryExp node) throws AnalysisException {
        Element op=doc.createElement(node.getOp().toString());
        ILexLocation local=node.getLocation();
        fill_source_file_location(op, local);
        PExp left = node.getLeft();
        PExp right = node.getRight();
        currentElement.appendChild(op);
        currentElement = op;
        left.apply(this);
        currentElement = op;
        right.apply(this);
    }

    @Override
    public void caseACompBinaryExp(ACompBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseADomainResByBinaryExp(ADomainResByBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseADomainResToBinaryExp(ADomainResToBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAEqualsBinaryExp(AEqualsBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAInSetBinaryExp(AInSetBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAMapUnionBinaryExp(AMapUnionBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseANotEqualBinaryExp(ANotEqualBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseANotInSetBinaryExp(ANotInSetBinaryExp node) throws AnalysisException {

    }

    @Override
    public void defaultSNumericBinaryExp(SNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAPlusPlusBinaryExp(APlusPlusBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseARangeResByBinaryExp(ARangeResByBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseARangeResToBinaryExp(ARangeResToBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseASeqConcatBinaryExp(ASeqConcatBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseASetIntersectBinaryExp(ASetIntersectBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseASetUnionBinaryExp(ASetUnionBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAStarStarBinaryExp(AStarStarBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseASubsetBinaryExp(ASubsetBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAEquivalentBooleanBinaryExp(AEquivalentBooleanBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseADivNumericBinaryExp(ADivNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseADivideNumericBinaryExp(ADivideNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAGreaterEqualNumericBinaryExp(AGreaterEqualNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseALessEqualNumericBinaryExp(ALessEqualNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseALessNumericBinaryExp(ALessNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAModNumericBinaryExp(AModNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAPlusNumericBinaryExp(APlusNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseARemNumericBinaryExp(ARemNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseATimesNumericBinaryExp(ATimesNumericBinaryExp node) throws AnalysisException {

    }

    @Override
    public void caseAMapCompMapExp(AMapCompMapExp node) throws AnalysisException {

    }

    @Override
    public void caseAMapEnumMapExp(AMapEnumMapExp node) throws AnalysisException {

    }

    @Override
    public void caseASeqCompSeqExp(ASeqCompSeqExp node) throws AnalysisException {

    }

    @Override
    public void caseASeqEnumSeqExp(ASeqEnumSeqExp node) throws AnalysisException {

    }

    @Override
    public void caseASetCompSetExp(ASetCompSetExp node) throws AnalysisException {

    }

    @Override
    public void caseASetEnumSetExp(ASetEnumSetExp node) throws AnalysisException {

    }

    @Override
    public void caseASetRangeSetExp(ASetRangeSetExp node) throws AnalysisException {

    }

    @Override
    public void defaultPModifier(PModifier node) throws AnalysisException {

    }

    @Override
    public void caseARecordModifier(ARecordModifier node) throws AnalysisException {

    }

    @Override
    public void defaultPAlternative(PAlternative node) throws AnalysisException {

    }

    @Override
    public void caseACaseAlternative(ACaseAlternative node) throws AnalysisException {

    }

    @Override
    public void defaultPType(PType node) throws AnalysisException {

    }

    @Override
    public void defaultSBasicType(SBasicType node) throws AnalysisException {

    }

    @Override
    public void caseABracketType(ABracketType node) throws AnalysisException {

    }

    @Override
    public void caseAClassType(AClassType node) throws AnalysisException {

    }

    @Override
    public void caseAFunctionType(AFunctionType node) throws AnalysisException {

    }

    @Override
    public void defaultSInvariantType(SInvariantType node) throws AnalysisException {

    }

    @Override
    public void defaultSMapType(SMapType node) throws AnalysisException {

    }

    @Override
    public void caseAOperationType(AOperationType node) throws AnalysisException {

    }

    @Override
    public void caseAOptionalType(AOptionalType node) throws AnalysisException {

    }

    @Override
    public void caseAParameterType(AParameterType node) throws AnalysisException {

    }

    @Override
    public void caseAProductType(AProductType node) throws AnalysisException {

    }

    @Override
    public void caseAQuoteType(AQuoteType node) throws AnalysisException {

    }

    @Override
    public void defaultSSeqType(SSeqType node) throws AnalysisException {

    }

    @Override
    public void caseASetType(ASetType node) throws AnalysisException {

    }

    @Override
    public void caseAUndefinedType(AUndefinedType node) throws AnalysisException {

    }

    @Override
    public void caseAUnionType(AUnionType node) throws AnalysisException {

    }

    @Override
    public void caseAUnknownType(AUnknownType node) throws AnalysisException {

    }

    @Override
    public void caseAUnresolvedType(AUnresolvedType node) throws AnalysisException {

    }

    @Override
    public void caseAVoidReturnType(AVoidReturnType node) throws AnalysisException {

    }

    @Override
    public void caseAVoidType(AVoidType node) throws AnalysisException {

    }

    @Override
    public void caseASeqSeqType(ASeqSeqType node) throws AnalysisException {

    }

    @Override
    public void caseASeq1SeqType(ASeq1SeqType node) throws AnalysisException {

    }

    @Override
    public void caseAInMapMapType(AInMapMapType node) throws AnalysisException {

    }

    @Override
    public void caseAMapMapType(AMapMapType node) throws AnalysisException {

    }

    @Override
    public void caseANamedInvariantType(ANamedInvariantType node) throws AnalysisException {

    }

    @Override
    public void caseARecordInvariantType(ARecordInvariantType node) throws AnalysisException {

    }

    @Override
    public void caseABooleanBasicType(ABooleanBasicType node) throws AnalysisException {

    }

    @Override
    public void caseACharBasicType(ACharBasicType node) throws AnalysisException {

    }

    @Override
    public void defaultSNumericBasicType(SNumericBasicType node) throws AnalysisException {

    }

    @Override
    public void caseATokenBasicType(ATokenBasicType node) throws AnalysisException {

    }

    @Override
    public void caseAIntNumericBasicType(AIntNumericBasicType node) throws AnalysisException {

    }

    @Override
    public void caseANatOneNumericBasicType(ANatOneNumericBasicType node) throws AnalysisException {

    }

    @Override
    public void caseANatNumericBasicType(ANatNumericBasicType node) throws AnalysisException {

    }

    @Override
    public void caseARationalNumericBasicType(ARationalNumericBasicType node) throws AnalysisException {

    }

    @Override
    public void caseARealNumericBasicType(ARealNumericBasicType node) throws AnalysisException {

    }

    @Override
    public void defaultPField(PField node) throws AnalysisException {

    }

    @Override
    public void caseAFieldField(AFieldField node) throws AnalysisException {

    }

    @Override
    public void defaultPAccessSpecifier(PAccessSpecifier node) throws AnalysisException {

    }

    @Override
    public void caseAAccessSpecifierAccessSpecifier(AAccessSpecifierAccessSpecifier node) throws AnalysisException {

    }

    @Override
    public void defaultPAccess(PAccess node) throws AnalysisException {

    }

    @Override
    public void caseAPublicAccess(APublicAccess node) throws AnalysisException {

    }

    @Override
    public void caseAProtectedAccess(AProtectedAccess node) throws AnalysisException {

    }

    @Override
    public void caseAPrivateAccess(APrivateAccess node) throws AnalysisException {

    }

    @Override
    public void defaultPPattern(PPattern node) throws AnalysisException {

    }

    @Override
    public void caseABooleanPattern(ABooleanPattern node) throws AnalysisException {

    }

    @Override
    public void caseACharacterPattern(ACharacterPattern node) throws AnalysisException {

    }

    @Override
    public void caseAConcatenationPattern(AConcatenationPattern node) throws AnalysisException {

    }

    @Override
    public void caseAExpressionPattern(AExpressionPattern node) throws AnalysisException {

    }

    @Override
    public void caseAIdentifierPattern(AIdentifierPattern node) throws AnalysisException {

    }

    @Override
    public void caseAIgnorePattern(AIgnorePattern node) throws AnalysisException {

    }

    @Override
    public void caseAIntegerPattern(AIntegerPattern node) throws AnalysisException {

    }

    @Override
    public void caseANilPattern(ANilPattern node) throws AnalysisException {

    }

    @Override
    public void caseAQuotePattern(AQuotePattern node) throws AnalysisException {

    }

    @Override
    public void caseARealPattern(ARealPattern node) throws AnalysisException {

    }

    @Override
    public void caseARecordPattern(ARecordPattern node) throws AnalysisException {

    }

    @Override
    public void caseASeqPattern(ASeqPattern node) throws AnalysisException {

    }

    @Override
    public void caseASetPattern(ASetPattern node) throws AnalysisException {

    }

    @Override
    public void caseAStringPattern(AStringPattern node) throws AnalysisException {

    }

    @Override
    public void caseATuplePattern(ATuplePattern node) throws AnalysisException {

    }

    @Override
    public void caseAUnionPattern(AUnionPattern node) throws AnalysisException {

    }

    @Override
    public void caseAMapPattern(AMapPattern node) throws AnalysisException {

    }

    @Override
    public void caseAMapUnionPattern(AMapUnionPattern node) throws AnalysisException {

    }

    @Override
    public void caseAObjectPattern(AObjectPattern node) throws AnalysisException {

    }

    @Override
    public void defaultPMaplet(PMaplet node) throws AnalysisException {

    }

    @Override
    public void caseAMapletPatternMaplet(AMapletPatternMaplet node) throws AnalysisException {

    }

    @Override
    public void defaultPPair(PPair node) throws AnalysisException {

    }

    @Override
    public void caseAPatternTypePair(APatternTypePair node) throws AnalysisException {

    }

    @Override
    public void caseAPatternListTypePair(APatternListTypePair node) throws AnalysisException {

    }

    @Override
    public void caseANamePatternPair(ANamePatternPair node) throws AnalysisException {

    }

    @Override
    public void defaultPBind(PBind node) throws AnalysisException {

    }

    @Override
    public void caseASetBind(ASetBind node) throws AnalysisException {

    }

    @Override
    public void caseATypeBind(ATypeBind node) throws AnalysisException {

    }

    @Override
    public void defaultPMultipleBind(PMultipleBind node) throws AnalysisException {

    }

    @Override
    public void caseASetMultipleBind(ASetMultipleBind node) throws AnalysisException {

    }

    @Override
    public void caseATypeMultipleBind(ATypeMultipleBind node) throws AnalysisException {

    }

    @Override
    public void defaultPPatternBind(PPatternBind node) throws AnalysisException {

    }

    @Override
    public void caseADefPatternBind(ADefPatternBind node) throws AnalysisException {

    }

    @Override
    public void defaultPDefinition(PDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAAssignmentDefinition(AAssignmentDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAInstanceVariableDefinition(AInstanceVariableDefinition node) throws AnalysisException {

    }

    @Override
    public void defaultSClassDefinition(SClassDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAClassInvariantDefinition(AClassInvariantDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAEqualsDefinition(AEqualsDefinition node) throws AnalysisException {

    }

    @Override
    public void defaultSFunctionDefinition(SFunctionDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAExternalDefinition(AExternalDefinition node) throws AnalysisException {

    }

    @Override
    public void defaultSOperationDefinition(SOperationDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAImportedDefinition(AImportedDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAInheritedDefinition(AInheritedDefinition node) throws AnalysisException {

    }

    @Override
    public void caseALocalDefinition(ALocalDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAMultiBindListDefinition(AMultiBindListDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAMutexSyncDefinition(AMutexSyncDefinition node) throws AnalysisException {

    }

    @Override
    public void caseANamedTraceDefinition(ANamedTraceDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAPerSyncDefinition(APerSyncDefinition node) throws AnalysisException {

    }

    @Override
    public void caseARenamedDefinition(ARenamedDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAStateDefinition(AStateDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAThreadDefinition(AThreadDefinition node) throws AnalysisException {

    }

    @Override
    public void caseATypeDefinition(ATypeDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAUntypedDefinition(AUntypedDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAValueDefinition(AValueDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAImplicitFunctionDefinition(AImplicitFunctionDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAExplicitOperationDefinition(AExplicitOperationDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAImplicitOperationDefinition(AImplicitOperationDefinition node) throws AnalysisException {

    }

    @Override
    public void defaultPTerm(PTerm node) throws AnalysisException {

    }

    @Override
    public void caseATraceDefinitionTerm(ATraceDefinitionTerm node) throws AnalysisException {

    }

    @Override
    public void defaultPTraceDefinition(PTraceDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAInstanceTraceDefinition(AInstanceTraceDefinition node) throws AnalysisException {

    }

    @Override
    public void caseALetBeStBindingTraceDefinition(ALetBeStBindingTraceDefinition node) throws AnalysisException {

    }

    @Override
    public void caseALetDefBindingTraceDefinition(ALetDefBindingTraceDefinition node) throws AnalysisException {

    }

    @Override
    public void caseARepeatTraceDefinition(ARepeatTraceDefinition node) throws AnalysisException {

    }

    @Override
    public void defaultPTraceCoreDefinition(PTraceCoreDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAApplyExpressionTraceCoreDefinition(AApplyExpressionTraceCoreDefinition node) throws AnalysisException {

    }

    @Override
    public void caseABracketedExpressionTraceCoreDefinition(ABracketedExpressionTraceCoreDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAConcurrentExpressionTraceCoreDefinition(AConcurrentExpressionTraceCoreDefinition node) throws AnalysisException {

    }

    @Override
    public void caseABusClassDefinition(ABusClassDefinition node) throws AnalysisException {

    }

    @Override
    public void caseACpuClassDefinition(ACpuClassDefinition node) throws AnalysisException {

    }

    @Override
    public void caseASystemClassDefinition(ASystemClassDefinition node) throws AnalysisException {

    }

    @Override
    public void caseAClassClassDefinition(AClassClassDefinition node) throws AnalysisException {

    }

    @Override
    public void defaultPModules(PModules node) throws AnalysisException {

    }

    @Override
    public void caseAModuleModules(AModuleModules node) throws AnalysisException {

    }

    @Override
    public void defaultPImports(PImports node) throws AnalysisException {

    }

    @Override
    public void caseAModuleImports(AModuleImports node) throws AnalysisException {

    }

    @Override
    public void caseAFromModuleImports(AFromModuleImports node) throws AnalysisException {

    }

    @Override
    public void defaultPImport(PImport node) throws AnalysisException {

    }

    @Override
    public void caseAAllImport(AAllImport node) throws AnalysisException {

    }

    @Override
    public void caseATypeImport(ATypeImport node) throws AnalysisException {

    }

    @Override
    public void defaultSValueImport(SValueImport node) throws AnalysisException {

    }

    @Override
    public void caseAValueValueImport(AValueValueImport node) throws AnalysisException {

    }

    @Override
    public void caseAFunctionValueImport(AFunctionValueImport node) throws AnalysisException {

    }

    @Override
    public void caseAOperationValueImport(AOperationValueImport node) throws AnalysisException {

    }

    @Override
    public void defaultPExports(PExports node) throws AnalysisException {

    }

    @Override
    public void caseAModuleExports(AModuleExports node) throws AnalysisException {

    }

    @Override
    public void defaultPExport(PExport node) throws AnalysisException {

    }

    @Override
    public void caseAAllExport(AAllExport node) throws AnalysisException {

    }

    @Override
    public void caseAFunctionExport(AFunctionExport node) throws AnalysisException {

    }

    @Override
    public void caseAOperationExport(AOperationExport node) throws AnalysisException {

    }

    @Override
    public void caseATypeExport(ATypeExport node) throws AnalysisException {

    }

    @Override
    public void caseAValueExport(AValueExport node) throws AnalysisException {

    }

    @Override
    public void defaultPStm(PStm node) throws AnalysisException {

    }

    @Override
    public void caseAAlwaysStm(AAlwaysStm node) throws AnalysisException {

    }

    @Override
    public void caseAAssignmentStm(AAssignmentStm node) throws AnalysisException {

    }

    @Override
    public void caseAAtomicStm(AAtomicStm node) throws AnalysisException {

    }

    @Override
    public void caseACallObjectStm(ACallObjectStm node) throws AnalysisException {

    }

    @Override
    public void caseACallStm(ACallStm node) throws AnalysisException {

    }

    @Override
    public void caseACasesStm(ACasesStm node) throws AnalysisException {

    }

    @Override
    public void caseAClassInvariantStm(AClassInvariantStm node) throws AnalysisException {

    }

    @Override
    public void caseACyclesStm(ACyclesStm node) throws AnalysisException {

    }

    @Override
    public void caseADurationStm(ADurationStm node) throws AnalysisException {

    }

    @Override
    public void caseAElseIfStm(AElseIfStm node) throws AnalysisException {

    }

    @Override
    public void caseAErrorStm(AErrorStm node) throws AnalysisException {

    }

    @Override
    public void caseAExitStm(AExitStm node) throws AnalysisException {

    }

    @Override
    public void caseAForAllStm(AForAllStm node) throws AnalysisException {

    }

    @Override
    public void caseAForIndexStm(AForIndexStm node) throws AnalysisException {

    }

    @Override
    public void caseAForPatternBindStm(AForPatternBindStm node) throws AnalysisException {

    }

    @Override
    public void caseAIfStm(AIfStm node) throws AnalysisException {
        ILexLocation local=node.getLocation();

        Element if_statement =doc.createElement("if_statement");
        rootElement.appendChild(if_statement);
        fill_source_file_location(if_statement, local);

        PExp exp=node.getIfExp();
        Element expression=doc.createElement("expression");
        if_statement.appendChild(expression);
        currentElement = expression;
        exp.apply(this);
    }

    @Override
    public void caseALetBeStStm(ALetBeStStm node) throws AnalysisException {

    }

    @Override
    public void caseALetStm(ALetStm node) throws AnalysisException {

    }

    @Override
    public void caseANotYetSpecifiedStm(ANotYetSpecifiedStm node) throws AnalysisException {

    }

    @Override
    public void caseAReturnStm(AReturnStm node) throws AnalysisException {

    }

    @Override
    public void defaultSSimpleBlockStm(SSimpleBlockStm node) throws AnalysisException {

    }

    @Override
    public void caseASkipStm(ASkipStm node) throws AnalysisException {

    }

    @Override
    public void caseASpecificationStm(ASpecificationStm node) throws AnalysisException {

    }

    @Override
    public void caseAStartStm(AStartStm node) throws AnalysisException {

    }

    @Override
    public void caseAStopStm(AStopStm node) throws AnalysisException {

    }

    @Override
    public void caseASubclassResponsibilityStm(ASubclassResponsibilityStm node) throws AnalysisException {

    }

    @Override
    public void caseATixeStm(ATixeStm node) throws AnalysisException {

    }

    @Override
    public void caseATrapStm(ATrapStm node) throws AnalysisException {

    }

    @Override
    public void caseAWhileStm(AWhileStm node) throws AnalysisException {

    }

    @Override
    public void caseAPeriodicStm(APeriodicStm node) throws AnalysisException {

    }

    @Override
    public void caseASporadicStm(ASporadicStm node) throws AnalysisException {

    }

    @Override
    public void caseABlockSimpleBlockStm(ABlockSimpleBlockStm node) throws AnalysisException {

    }

    @Override
    public void caseANonDeterministicSimpleBlockStm(ANonDeterministicSimpleBlockStm node) throws AnalysisException {

    }

    @Override
    public void defaultPStateDesignator(PStateDesignator node) throws AnalysisException {

    }

    @Override
    public void caseAFieldStateDesignator(AFieldStateDesignator node) throws AnalysisException {

    }

    @Override
    public void caseAIdentifierStateDesignator(AIdentifierStateDesignator node) throws AnalysisException {

    }

    @Override
    public void caseAMapSeqStateDesignator(AMapSeqStateDesignator node) throws AnalysisException {

    }

    @Override
    public void defaultPObjectDesignator(PObjectDesignator node) throws AnalysisException {

    }

    @Override
    public void caseAApplyObjectDesignator(AApplyObjectDesignator node) throws AnalysisException {

    }

    @Override
    public void caseAFieldObjectDesignator(AFieldObjectDesignator node) throws AnalysisException {

    }

    @Override
    public void caseAIdentifierObjectDesignator(AIdentifierObjectDesignator node) throws AnalysisException {

    }

    @Override
    public void caseANewObjectDesignator(ANewObjectDesignator node) throws AnalysisException {

    }

    @Override
    public void caseASelfObjectDesignator(ASelfObjectDesignator node) throws AnalysisException {

    }

    @Override
    public void defaultPAlternativeStm(PAlternativeStm node) throws AnalysisException {

    }

    @Override
    public void caseACaseAlternativeStm(ACaseAlternativeStm node) throws AnalysisException {

    }

    @Override
    public void defaultPStmtAlternative(PStmtAlternative node) throws AnalysisException {

    }

    @Override
    public void caseATixeStmtAlternative(ATixeStmtAlternative node) throws AnalysisException {

    }

    @Override
    public void defaultPClause(PClause node) throws AnalysisException {

    }

    @Override
    public void caseAExternalClause(AExternalClause node) throws AnalysisException {

    }

    @Override
    public void defaultPCase(PCase node) throws AnalysisException {

    }

    @Override
    public void caseAErrorCase(AErrorCase node) throws AnalysisException {

    }

    @Override
    public void defaultINode(INode node) throws AnalysisException {

    }

    @Override
    public void defaultIToken(IToken node) throws AnalysisException {

    }
}
