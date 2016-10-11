package org.overture.signature;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.INode;

public class ApplyOccurrenceSignatureChanger extends DepthFirstAnalysisAdaptor{
	private ILexLocation defLoc;
	private Set<AApplyExp> applyOccurrences;
	private Consumer<SignatureChangeObject> function;
	private String newParamName;
	
	public ApplyOccurrenceSignatureChanger(ILexLocation defLoc, Consumer<SignatureChangeObject> function, String newParamName){
		this.defLoc = defLoc;
		this.function = function;
		this.newParamName = newParamName;
		this.applyOccurrences = new HashSet<AApplyExp>();		
	}
	
	public Set<AApplyExp> getApplications(){
		return applyOccurrences;
	}
	
	@Override
	public void caseAApplyExp(AApplyExp node) throws AnalysisException {
		AVariableExp root = (AVariableExp) node.getRoot();
		
		if(root == null){
			return;
		}
		
		AExplicitOperationDefinition rootOperationDef = (AExplicitOperationDefinition) root.getVardef();
		ILexLocation operationLoc = rootOperationDef.getLocation();
				
		if (operationLoc.equals(defLoc)){
			String operationName = "";
			INode operationNode = node.parent().parent().parent();
			if(operationNode instanceof AExplicitOperationDefinition){
				operationName = ((AExplicitOperationDefinition) operationNode).getName().getName();
			}
			
			String newParamNameStr = newParamName;
			LinkedList<PExp> paramListOfParent = node.getArgs();
			ILexLocation lastLoc = paramListOfParent.getLast().getLocation();
			LexLocation newLastLoc = new LexLocation(lastLoc.getFile(),lastLoc.getModule(),lastLoc.getStartLine(),lastLoc.getEndPos()+2,lastLoc.getEndLine(),lastLoc.getEndPos()+2+String.valueOf(newParamNameStr).length(),lastLoc.getStartOffset(), lastLoc.getEndOffset());
			
			ILexNameToken newParamName = new LexNameToken(root.getName().getModule(), newParamNameStr, newLastLoc);
			
			function.accept(new SignatureChangeObject(newParamName.getLocation(), newParamName, paramListOfParent ,operationName));
			
			applyOccurrences.add(node);
		}
	}
}