/*
 * #%~
 * AST Pretty Printer
 * %%
 * Copyright (C) 2008 - 2016 Overture
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
package org.overture.prettyprinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACardinalityUnaryExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ADomainResByBinaryExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.APreExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExpBase;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.AMapSeqStateDesignator;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASkipStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.codegen.analysis.vdm.NameCollector;
import org.overture.codegen.ir.TempVarNameGen;
import org.overture.core.npp.IPrettyPrinter;
import org.overture.core.npp.ISymbolTable;
import org.overture.core.npp.IndentTracker;
import org.overture.core.npp.Utilities;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

class ASTPrettyPrinter extends QuestionAnswerAdaptor < IndentTracker, String >
    implements IPrettyPrinter {

	private ITypeCheckerAssistantFactory af;
	private static String space = " ";
	private PDefinition enclosingDef;
	private Stack<ILexNameToken> localDefsInScope;
	private int enclosingCounter;
	private Set<String> namesToAvoid;
	private boolean operationFlag;
	private boolean functionFlag;
	private boolean valuesFlag;
	private boolean stateFlag;
	private int outerScopeCounter = -1;
	private List<ArrayList<String>> stringOuterStack;
	private static String NODE_NOT_FOUND = "ERROR: Node not found";
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	protected ISymbolTable mytable;
	protected IPrettyPrinter rootNpp;
	
	public ASTPrettyPrinter(IPrettyPrinter root, ISymbolTable nst, ITypeCheckerAssistantFactory af,
			Map<AIdentifierStateDesignator, PDefinition> idDefs)
	{
		this.rootNpp = root;
		this.mytable = nst;
		this.af = af;
		this.enclosingDef = null;
		this.localDefsInScope = new Stack<ILexNameToken>();
		this.enclosingCounter = 0;
		this.namesToAvoid = new HashSet<String>();
		new TempVarNameGen();
		this.stringOuterStack = new ArrayList<ArrayList<String>>();
		this.operationFlag = false;
		this.functionFlag = false;
		this.valuesFlag = false;
		this.stateFlag = false;
	}

	@Override
	public String caseAModuleModules(AModuleModules node, IndentTracker question) throws AnalysisException
	{
		if (enclosingDef != null)
		{
			return null;
		}
		
		this.operationFlag = false;
		this.functionFlag = false;
		this.valuesFlag = false;
		this.stateFlag = false;
		
		incrementOuterScopeCounter();
		if(!node.getName().getName().equals("DEFAULT")){
			insertIntoStringStack("module " + node.getName().getName() + "\n");
		}
		question.incrIndent();
		visitModuleDefs(node.getDefs(), node, question);
		question.decrIndent();
		if(!node.getName().getName().equals("DEFAULT")){
			insertIntoStringStack("end " + node.getName().getName() + ";\n");
		}
		return node.getName().getName();
	}

	@Override
	public String caseAClassClassDefinition(AClassClassDefinition node, IndentTracker question)
			throws AnalysisException
	{
		if (enclosingDef != null)
		{
			return null;
		}
		insertIntoStringStack(node.getName().getFullName());
		visitModuleDefs(node.getDefinitions(), node, question);
		return node.getName().getFullName();
	}

	// For operations and functions it works as a single pattern
	// Thus f(1,mk_(2,2),5) will fail
	// public f : nat * (nat * nat) * nat -> nat
	// f (b,mk_(b,b), a) == b;

	@Override
	public String caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, IndentTracker question) throws AnalysisException
	{

		if (!proceed(node))
		{
			return null;
		}
		if(!operationFlag){
			insertIntoStringStack("\n");
			insertIntoStringStack("operations");
			insertIntoStringStack("\n\n");
			operationFlag = true;
		}
		
		StringBuilder strBuilder = new StringBuilder();
		String opName = node.getName().getFullName();
		strBuilder.append(opName);
		strBuilder.append(" : ");
		
		PType typeDef = node.getType();
		LinkedList<? extends PType> typeDefs = new LinkedList<AOperationType>();
		
		if(typeDef instanceof AOperationType){
			typeDefs = ((AOperationType) typeDef).getParameters();
		}
		
		parametertypesPrinter(strBuilder, typeDefs);
		
		strBuilder.append(" ==> ");
		AOperationType defOp = node.getType().getAncestor(AOperationType.class);
		if (defOp != null){
			strBuilder.append(defOp.getResult().toString());
		}
		
		// Top part done:" op: nat ==> nat "
		strBuilder.append("\n");
		strBuilder.append(question.getIndentation() + opName + "(");
		
		LinkedList<PPattern> patterns = node.getParameterPatterns();
		
		parameterNamePrinter(strBuilder, patterns);
		if(node.getBody() instanceof ABlockSimpleBlockStm){
			strBuilder.append(" (\n");
		}else{
			strBuilder.append("\n");
		}		
		
		insertIntoStringStack(question.getIndentation() + strBuilder.toString());
		question.incrIndent();
		node.getBody().apply(this, question);
		question.decrIndent();
		
		boolean lastSemiColonFixed = false;
		
		if(node.getBody() instanceof ABlockSimpleBlockStm){
			insertIntoStringStack(question.getIndentation() + ")");
		}
		if(node.getPrecondition() != null){
			lastSemiColonFixed = removeLastSemiColon(lastSemiColonFixed);
			insertIntoStringStack("pre ");
			node.getPrecondition().apply(this, question);
		}
		if(node.getPostcondition() != null){
			lastSemiColonFixed = removeLastSemiColon(lastSemiColonFixed);
			insertIntoStringStack("post ");
			node.getPostcondition().apply(this, question);
		}
		finishElementInStack();
		return node.getName().getFullName();
	}

	
	
	@Override
	public String caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node, IndentTracker question)
			throws AnalysisException {
		if (!proceed(node))
		{
			return null;
		}
		if(!functionFlag){
			insertIntoStringStack("\n");
			insertIntoStringStack("functions");
			insertIntoStringStack("\n\n");
			functionFlag = true;
		}
		
		StringBuilder strBuilder = new StringBuilder();
		String funcName = node.getName().getFullName();
		strBuilder.append(funcName);
		strBuilder.append(" : ");
		
		PType typeDef = node.getType();
		LinkedList<? extends PType> typeDefs = new LinkedList<AFunctionType>();
		
		if(typeDef instanceof AFunctionType){
			typeDefs = ((AFunctionType) typeDef).getParameters();
		}
		
		parametertypesPrinter(strBuilder, typeDefs);
		
		strBuilder.append(" -> ");
		AFunctionType defOp = node.getType().getAncestor(AFunctionType.class);
		if (defOp != null){
			strBuilder.append(defOp.getResult().toString());
		}
		
		// Top part done:" op: nat ==> nat "
		strBuilder.append("\n");
		strBuilder.append(question.getIndentation() + funcName + "(");
		
		List<PPattern> patterns = node.getParamPatternList().getFirst();
		
		parameterNamePrinter(strBuilder, patterns);
		if(node.getBody() instanceof ABlockSimpleBlockStm){
			strBuilder.append(" (\n");
		}else{
			strBuilder.append("\n");
		}		
		
		insertIntoStringStack(question.getIndentation() + strBuilder.toString());
		question.incrIndent();
		node.getBody().apply(this, question);
		question.decrIndent();

		if(node.getBody() instanceof ABlockSimpleBlockStm){
			insertIntoStringStack(question.getIndentation() + ");");
		}
		
		finishElementInStack();
		return node.getName().getFullName();
	}

	private void parameterNamePrinter(StringBuilder strBuilder, List<PPattern> patterns) {
		for (PPattern def : patterns){
			if(def != patterns.get(0)){
				strBuilder.append(", ");
			}	
			strBuilder.append(def.toString());
		}
		strBuilder.append(") ==");
	}

	private void parametertypesPrinter(StringBuilder strBuilder, LinkedList<? extends PType> typeDefs) {
		if(typeDefs != null && typeDefs.size() > 0){	
			for (PType def : typeDefs){
				if(def != typeDefs.get(0)){
					strBuilder.append("*");
				}	
				strBuilder.append(def.toString());
			}
		} else {
			strBuilder.append("()");
		}
	}
	
	@Override
	public String caseABlockSimpleBlockStm(ABlockSimpleBlockStm node, IndentTracker question)
			throws AnalysisException
	{
		if (!proceed(node))
		{
			return "";
		}
		//TODO visit assigmentdefs
		visitStms(node.getStatements(), question);
		return "";
	}
	@Override
	public String caseALetStm(ALetStm node, IndentTracker question) throws AnalysisException
	{
		if (!proceed(node))
		{
			return null;
		}
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append(question.getIndentation() + "let ");
		
		VDMDefinitionInfo defInfo = new VDMDefinitionInfo(node.getLocalDefs(), af);

		visitDefs(defInfo.getNodeDefs(), question);

		List<? extends PDefinition> nodeDefs = defInfo.getNodeDefs();

		for (PDefinition parentDef : nodeDefs)
		{
			if(parentDef != nodeDefs.get(0)){
				strBuilder.append(", ");
			}
			List<? extends PDefinition> localDefs = defInfo.getLocalDefs(parentDef);

			for (PDefinition localDef : localDefs){
				strBuilder.append(localDef.getName().getName() + " = ");
			}
			insertIntoStringStack(strBuilder.toString());
			AValueDefinition defVal = parentDef.getAncestor(AValueDefinition.class);
			
			if (defVal != null){
				defVal.getExpression().apply(this, question);
			}
			strBuilder = new StringBuilder();
		}
		insertIntoStringStack("\n" + question.getIndentation() + "in\n");
		question.incrIndent();
		node.getStatement().apply(this, question);
		question.decrIndent();
		return node.toString();
	}
	
	@Override
	public String caseAReturnStm(AReturnStm node, IndentTracker question) throws AnalysisException {
		insertIntoStringStack(question.getIndentation() + "return ");
		node.getExpression().apply(this, question);
		insertIntoStringStack(";\n");
		return node.toString();
	}
	
	@Override
	public String caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		return expressionWriter(node, mytable.getPLUS(), question);
	}
	
	@Override
	public String caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		return expressionWriter(node, mytable.getMINUS(), question);
	}
	
	@Override
	public String caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		return expressionWriter(node, mytable.getTIMES(), question);
	}
	
	@Override
	public String caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		return expressionWriter(node, mytable.getDIVIDE(), question);
	}
	
	@Override
	public String caseAModNumericBinaryExp(AModNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		return expressionWriter(node, mytable.getMOD(), question);
	}
	
	@Override
	public String caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		return expressionWriter(node, mytable.getDIV(), question);
	}
	
	@Override
	public String caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		return expressionWriter(node, mytable.getIMPLIES(), question);
	}
	
	private String expressionWriter(SBinaryExpBase exp, String op, IndentTracker question){
		StringBuilder sb = new StringBuilder();
		insertIntoStringStack("(");		
		try {
			exp.getLeft().apply(THIS, question);
		} catch (AnalysisException e) {
			e.printStackTrace();
		}
		sb.append(space);
		sb.append(op);
		sb.append(space);
		insertIntoStringStack(sb.toString());
		try {
			exp.getRight().apply(THIS, question);
		} catch (AnalysisException e) {
			e.printStackTrace();
		}
		insertIntoStringStack(")");		
		
		return Utilities.wrap(sb.toString());
	}
	
	public String basicLiteralExpPrinter(String value, IndentTracker question, INode parent){
		
		StringBuilder sb = new StringBuilder();
		if(parent instanceof AExplicitFunctionDefinition || 
				parent instanceof AExplicitOperationDefinition){
			sb.append(question.getIndentation());
		}
		sb.append(value);
		insertIntoStringStack(sb.toString());
		return sb.toString();
	}
	
	@Override
	public String caseAIntLiteralExp(AIntLiteralExp node, IndentTracker question)
			throws AnalysisException
	{
		return basicLiteralExpPrinter(Long.toString(node.getValue().getValue()),question,node.parent());
	}
	
	@Override
	public String caseARealLiteralExp(ARealLiteralExp node,
			IndentTracker question) throws AnalysisException
	{
		return basicLiteralExpPrinter(Double.toString(node.getValue().getValue()),question,node.parent());
	}
	
	@Override
	public String caseAVariableExp(AVariableExp node, IndentTracker question)
			throws AnalysisException
	{
		String var = node.getOriginal();
		insertIntoStringStack(var);
		return var;
	}
	
	@Override
	public String caseABooleanConstExp(ABooleanConstExp node, IndentTracker question) throws AnalysisException {
		return basicLiteralExpPrinter(String.valueOf(node.getValue().getValue()),question,node.parent());
	}
	
	@Override
	public String caseACharLiteralExp(ACharLiteralExp node, IndentTracker question) throws AnalysisException {
		return basicLiteralExpPrinter(String.valueOf(node.getValue().getValue()),question,node.parent());
	}
	
	@Override
	public String caseACallStm(ACallStm node, IndentTracker question) throws AnalysisException {
		if(node.getName() != null){
		insertIntoStringStack(question.getIndentation() + node.getName().getFullName() + "(");
		printArgsList(node.getArgs(), question);
		insertIntoStringStack(");");
		insertIntoStringStack("\n");
		}
		return "";
	}
	
	@Override
	public String caseAApplyExp(AApplyExp node, IndentTracker question) throws AnalysisException {
		AVariableExp printNode = node.getRoot().getAncestor(AVariableExp.class);
		if(printNode != null){
			insertIntoStringStack(printNode.getName().getFullName() + "(");
			printArgsList(node.getArgs(), question);
			insertIntoStringStack(")");
		}
		return "";
	}
	
	@Override
	public String caseAIfStm(AIfStm node, IndentTracker question) throws AnalysisException {
		insertIntoStringStack(question.getIndentation() + "if");
		node.getIfExp().apply(this, question);
		
		insertIntoStringStack("\n" + question.getIndentation() + "then\n");
		question.incrIndent();
		node.getThenStm().apply(this, question);
		question.decrIndent();
		
		if(node.getElseStm() != null){
			insertIntoStringStack("\n" + question.getIndentation() + "else\n");
			question.incrIndent();
			node.getElseStm().apply(this, question);
			question.decrIndent();
		}
			
		return "";
	}
	
	@Override
	public String caseAIfExp(AIfExp node, IndentTracker question) throws AnalysisException {
		insertIntoStringStack(question.getIndentation() + "if");
		node.getTest().apply(this, question);
		
		insertIntoStringStack("\n" + question.getIndentation() + "then\n");
		question.incrIndent();
		node.getThen().apply(this, question);
		question.decrIndent();
		
		if(node.getElse() != null){
			insertIntoStringStack("\n" + question.getIndentation() + "else\n");
			question.incrIndent();
			node.getElse().apply(this, question);
			question.decrIndent();
		}
			
		return "";
	}
	
	@Override
	public String caseAPlusPlusBinaryExp(APlusPlusBinaryExp node, IndentTracker question) throws AnalysisException {
		return expressionWriter(node, node.getOp().toString() ,question);
	}
	
	@Override
	public String caseAMapEnumMapExp(AMapEnumMapExp node, IndentTracker question) throws AnalysisException {
		// TODO Auto-generated method stub
		insertIntoStringStack("{");
		for(AMapletExp item : node.getMembers()){
			item.apply(this, question);			
		}
		insertIntoStringStack("}");
		return super.caseAMapEnumMapExp(node, question);
	}
	
	@Override
	public String caseAMapletExp(AMapletExp node, IndentTracker question) throws AnalysisException {
		node.getLeft().apply(this, question);
		insertIntoStringStack(" |-> ");
		node.getRight().apply(this, question);
		return "";
	}
	
	@Override
	public String caseADomainResByBinaryExp(ADomainResByBinaryExp node, IndentTracker question)
			throws AnalysisException {
		return expressionWriter(node, node.getOp().toString() ,question);
	}
	
	@Override
	public String caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node, IndentTracker question) throws AnalysisException {
		return expressionWriter(node, node.getOp().toString() ,question);
	}
	
	@Override
	public String caseAEqualsBinaryExp(AEqualsBinaryExp node, IndentTracker question) throws AnalysisException {
		return expressionWriter(node, mytable.getEQUALS() ,question);
	}
	
	@Override
	public String caseAInSetBinaryExp(AInSetBinaryExp node, IndentTracker question) throws AnalysisException {
		return expressionWriter(node, node.getOp().toString() ,question);
	}
	@Override
	public String caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node, IndentTracker question)
			throws AnalysisException {
		return expressionWriter(node, node.getOp().toString() ,question);
	}
	
	@Override
	public String caseAMapDomainUnaryExp(AMapDomainUnaryExp node, IndentTracker question) throws AnalysisException {
		insertIntoStringStack("dom ");
		node.getExp().apply(this, question);
		return node.toString();
	}
	
	@Override
	public String caseACardinalityUnaryExp(ACardinalityUnaryExp node, IndentTracker question) throws AnalysisException {
		insertIntoStringStack("card ");
		node.getExp().apply(this, question);
		return node.toString();
	}
	
	@Override
	public String caseASkipStm(ASkipStm node, IndentTracker question) throws AnalysisException {
		insertIntoStringStack(question.getIndentation() + "skip");
		return "";
	}
	
	@Override
	public String caseAAssignmentStm(AAssignmentStm node, IndentTracker question) throws AnalysisException {
		node.getTarget().apply(this, question);
		insertIntoStringStack(" := ");
		node.getExp().apply(this, question); 
		insertIntoStringStack(";\n"); // make global check for ; used for indent
		return "";
	}
	
	@Override
	public String caseAMapSeqStateDesignator(AMapSeqStateDesignator node, IndentTracker question)
			throws AnalysisException {
		node.getMapseq().apply(this, question); 
		insertIntoStringStack("(");
		node.getExp().apply(this, question); 
		insertIntoStringStack(")");
		return super.caseAMapSeqStateDesignator(node, question);
	}
	

	
	@Override
	public String caseAIdentifierStateDesignator(AIdentifierStateDesignator node, IndentTracker question)
			throws AnalysisException {
		insertIntoStringStack(question.getIndentation() + node.getName().getName());
		return node.getName().getName();
	}
	
	@Override
	public String caseAMkTypeExp(AMkTypeExp node, IndentTracker question) throws AnalysisException {
		insertIntoStringStack("mk_" + node.getTypeName().getName() + "("); //TODO maybe question.getIndentation() +
	    printArgsList(node.getArgs(), question);
	    insertIntoStringStack(")");
		return "";
	}

	@Override
	public String caseASetUnionBinaryExp(ASetUnionBinaryExp node, IndentTracker question) throws AnalysisException {
		return expressionWriter(node, node.getOp().toString() ,question);
	}
	
	private void printArgsList(LinkedList<PExp> list, IndentTracker question) throws AnalysisException {
		for(PExp stm : list){
			
			if(stm != list.getFirst()){
				insertIntoStringStack(", ");
			}
			stm.apply(THIS,question);
		}
	}
	
	@Override
	public String caseASetEnumSetExp(ASetEnumSetExp node, IndentTracker question) throws AnalysisException {
		insertIntoStringStack("{");
		printArgsList(node.getMembers(), question);
		insertIntoStringStack("}");
		return "";
	}
	
	@Override
	public String caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node, IndentTracker question)
			throws AnalysisException {
		return expressionWriter(node, node.getOp().toString() ,question);
	}
	
	@Override
	public String caseAPreExp(APreExp node, IndentTracker question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAPreExp(node, question);
	}
	
	@Override
	public String caseAPreOpExp(APreOpExp node, IndentTracker question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAPreOpExp(node, question);
	}
	
	private void visitModuleDefs(List<PDefinition> defs, INode module, IndentTracker question)
			throws AnalysisException
	{
		VDMDefinitionInfo defInfo = getStateDefs(defs, module);
		
		
		if (defInfo != null)
		{
			
			if(!defInfo.getTypeDefs().isEmpty()){
				insertIntoStringStack("\n");
				insertIntoStringStack("types");
				insertIntoStringStack("\n\n");
			}
			
			for (ATypeDefinition typeDef : defInfo.getTypeDefs()) // check if it matches position
			{		
				insertIntoStringStack(question.getIndentation() + typeDef.getName().getFullName() + " = " + getTypeDefAncestor(typeDef) + ";\n");
			}

			for (PDefinition localDef : defInfo.getAllLocalDefs()) // check if it matches position
			{
				if(localDef.parent() instanceof AValueDefinition){
					if(!valuesFlag){
						insertIntoStringStack("\n");
						insertIntoStringStack("values");
						insertIntoStringStack("\n\n");
						valuesFlag = true;
					}
					insertIntoStringStack(question.getIndentation() + localDef.parent().toString() + ";\n");
				}
				if(localDef.parent() instanceof AStateDefinition){
					if(!stateFlag){
						insertIntoStringStack("\n");
						insertIntoStringStack("state");
						insertIntoStringStack("\n\n");
						stateFlag = true;
					}
//					insertIntoStringStack(question.getIndentation() + localDef.parent().toString() + ";\n"); //TODO fix state
				}
			}
			
			handleExecutables(defs,question);
		} else
		{
			handleExecutables(defs,question);
		}
	}

	private String getTypeDefAncestor(ATypeDefinition node){
		
		ANamedInvariantType defInvType = node.getType().getAncestor(ANamedInvariantType.class);
		if (defInvType != null){
			return defInvType.getType().toString();
		}else{
			//ANamedInvariantType def = node.getType().getAncestor(ANamedInvariantType.class);
		}
			
		return  "";
	}
	
	private void handleExecutables(List<PDefinition> defs, IndentTracker question)
			throws AnalysisException
	{
		for (PDefinition def : defs)
		{
			if (def instanceof SOperationDefinition
					|| def instanceof SFunctionDefinition
					|| def instanceof ANamedTraceDefinition)
			{
				enclosingDef = def;
				enclosingCounter = 0;
				setNamesToAvoid(def);
				new TempVarNameGen();

				def.apply(this,question);
			}
		}
	}

	private VDMDefinitionInfo getStateDefs(List<PDefinition> defs, INode module)
	{
		if (module instanceof AModuleModules)
		{
			List<PDefinition> fieldDefs = new LinkedList<PDefinition>();
			List<ATypeDefinition> typeDefs = new LinkedList<ATypeDefinition>();
			AStateDefinition stateDef = getStateDef(defs);
		
			if (stateDef != null)
			{
				fieldDefs.addAll(findFieldDefs(stateDef.getStateDefs(), stateDef.getFields()));
			}
			
			for (PDefinition def : defs)
			{
				if (def instanceof ATypeDefinition)
				{
					typeDefs.add((ATypeDefinition) def);
				}
			}
			
			for (PDefinition def : defs)
			{
				if (def instanceof AValueDefinition)
				{
					fieldDefs.add(def);
				}
			}

			return new VDMDefinitionInfo(fieldDefs, typeDefs, af);
		} else if (module instanceof SClassDefinition)
		{
			SClassDefinition classDef = (SClassDefinition) module;
			List<PDefinition> allDefs = new LinkedList<PDefinition>();

			LinkedList<PDefinition> enclosedDefs = classDef.getDefinitions();
			LinkedList<PDefinition> inheritedDefs = classDef.getAllInheritedDefinitions();

			allDefs.addAll(enclosedDefs);
			allDefs.addAll(inheritedDefs);

			List<PDefinition> fields = new LinkedList<PDefinition>();

			for (PDefinition def : allDefs)
			{
				if (def instanceof AInstanceVariableDefinition
						|| def instanceof AValueDefinition)
				{
					fields.add(def);
				}
			}

			return new VDMDefinitionInfo(fields, af);
		} else
		{
			log.error("Expected module or class definition. Got: " + module);
			return null;
		}
	}

	private AStateDefinition getStateDef(List<PDefinition> defs)
	{
		for (PDefinition def : defs)
		{
			if (def instanceof AStateDefinition)
			{
				return (AStateDefinition) def;
			}
		}

		return null;
	}
	
	private List<PDefinition> findFieldDefs(List<PDefinition> stateDefs,
			List<AFieldField> fields)
	{
		List<PDefinition> fieldDefs = new LinkedList<PDefinition>();

		for (PDefinition d : stateDefs)
		{
			for (AFieldField f : fields)
			{
				if (f.getTagname().equals(d.getName()))
				{
					fieldDefs.add(d);
					break;
				}
			}
		}
		return fieldDefs;
	}

	private void setNamesToAvoid(PDefinition def) throws AnalysisException
	{
		NameCollector collector = new NameCollector();
		def.apply(collector);
		namesToAvoid = collector.namesToAvoid();
	}

	public void init(boolean clearRenamings)
	{
		this.enclosingDef = null;
		this.enclosingCounter = 0;
		this.namesToAvoid.clear();
		new TempVarNameGen();
	}

	private boolean proceed(INode node)
	{
		if (node == enclosingDef)
		{
			enclosingCounter++;
		}

		if (enclosingCounter > 1)
		{
			// To protect against recursion
			return false;
		}

		PDefinition def = node.getAncestor(SOperationDefinition.class);

		if (def == null)
		{
			def = node.getAncestor(SFunctionDefinition.class);

			if (def == null)
			{
				def = node.getAncestor(ANamedTraceDefinition.class);

				if (def == null)
				{
					def = node.getAncestor(AValueDefinition.class);

					if (def == null)
					{
						def = node.getAncestor(AInstanceVariableDefinition.class);

						if (def == null)
						{
							def = node.getAncestor(ATypeDefinition.class);

							if (def == null)
							{
								def = node.getAncestor(AStateDefinition.class);
							}
						}
					}
				}
			}
		}

		if (def == null)
		{
			log.error("Got unexpected definition: " + enclosingDef);
		}

		return enclosingDef == def;
	}

	public void removeLocalDefFromScope(PDefinition localDef)
	{
		localDefsInScope.remove(localDef.getName());
	}

	private void visitDefs(List<? extends PDefinition> defs, IndentTracker question)
			throws AnalysisException
	{
		for (PDefinition def : defs)
		{
			def.apply(this, question);
		}
	}

	private void visitStms(List<? extends PStm> stms, IndentTracker question) throws AnalysisException
	{
		for (PStm stm : stms)
		{
			stm.apply(this, question);
		}
	}

	public String getVDMText(){
		StringBuilder strBuilder = new StringBuilder();
		for(ArrayList<String> list : stringOuterStack){
			
		    for(String item : list)
			{
				if(item == list.get(0) && !item.equals("DEFAULT") && item != list.get(0)){
					strBuilder.append("module ");
				}
				strBuilder.append(item);
			}
			strBuilder.append("\n");
		}
		
		return strBuilder.toString();
	}
	
	public void insertIntoStringStack(String str){
		stringOuterStack.get(outerScopeCounter).add(str);
	}
	
	private boolean removeLastSemiColon(boolean lastSemiColonFixed){
		if(!lastSemiColonFixed){
			List<String> strList = stringOuterStack.get(outerScopeCounter);
			String lastStr = strList.get(strList.size()-1);
			strList.set(strList.size()-1, removeAllCharUntil(lastStr) + "\n");
		}
		return true;
	}
	
	private static String removeAllCharUntil(String str) {
        return str.substring(0,str.lastIndexOf(";"));
    }
	
	public void finishElementInStack(){
		 String lastInput = stringOuterStack.get(outerScopeCounter).get(stringOuterStack.get(outerScopeCounter).size() - 1);
		 
		 removeLastSemiColon(true);
		 char lastChar = lastInput.charAt(lastInput.length() - 1);
		 if(lastChar == '\n'){
			 insertIntoStringStack("\n");
		 }
		 else if (lastChar == ')') {
			 insertIntoStringStack(";\n\n");
		 } 
		 else{
			 insertIntoStringStack("\n\n");
		 }
	}
	
	public void incrementOuterScopeCounter(){
		stringOuterStack.add(new ArrayList<String>());
		outerScopeCounter++;
		operationFlag = false;
	}

	@Override
	public void setInsTable(ISymbolTable it)
	{
		mytable = it;
	}
	
	@Override
	public String createNewReturnValue(INode node, IndentTracker question)
			throws AnalysisException
	{
		return NODE_NOT_FOUND;
	}

	@Override
	public String createNewReturnValue(Object node, IndentTracker question)
			throws AnalysisException
	{
		return NODE_NOT_FOUND;
	}

}