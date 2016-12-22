package org.overture.add.parameter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
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
	private Consumer<AddParameterObject> function;
	private String newParamName;
	private String newParamPlaceholder;
	private String newParamType;
	private boolean isParamListEmpty;
	
	public ApplyOccurrenceSignatureChanger(ILexLocation defLoc, Consumer<AddParameterObject> function, 
			String newParamName, String newParamPlaceholder, String newParamType, boolean isParamListEmpty){
		this.defLoc = defLoc;
		this.function = function;
		this.newParamName = newParamName;
		this.newParamPlaceholder = newParamPlaceholder;
		this.newParamType = newParamType;
		this.isParamListEmpty = isParamListEmpty;
		this.applyOccurrences = new HashSet<AApplyExp>();		
	}
	
	public Set<AApplyExp> getApplications(){
		return applyOccurrences;
	}
	
	@Override
	public void caseAApplyExp(AApplyExp node) throws AnalysisException {
		AVariableExp root = null;		
		if(node.getRoot() instanceof AVariableExp){
			root = (AVariableExp) node.getRoot();
		} else{
			return;
		}		
		
		if(root == null || !(root.getVardef() instanceof AExplicitOperationDefinition)){
			return;
		}
		
		AExplicitOperationDefinition rootOperationDef = (AExplicitOperationDefinition) root.getVardef();;
		ILexLocation operationLoc = rootOperationDef.getLocation();
				
		if (operationLoc.equals(defLoc)){
			String operationName = "";
			INode operationNode = node.parent().parent().parent();
			if(operationNode instanceof AExplicitOperationDefinition){
				operationName = ((AExplicitOperationDefinition) operationNode).getName().getName();
			}
			
			String newParamNameStr = newParamName;
			LinkedList<PExp> paramListOfParent = node.getArgs();
			LexLocation newLastLoc = new LexLocation();
			
			if(!isParamListEmpty){
				ILexLocation lastLoc = paramListOfParent.getLast().getLocation();
				newLastLoc = AddParameterUtil.calculateNewParamLocationWhenNotEmptyList(lastLoc, newParamPlaceholder);
			} else{
				newLastLoc = AddParameterUtil.calculateParamLocationInCallWhenEmptyList(node.getLocation(), newParamPlaceholder);
			}
			ILexNameToken newParamName = new LexNameToken(root.getName().getModule(), newParamNameStr, newLastLoc);
			
			function.accept(new AddParameterObject(newParamName.getLocation(), newParamName, 
					paramListOfParent ,operationName, newParamType));
			
			applyOccurrences.add(node);
		}
	}
}