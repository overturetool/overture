package org.overture.add.remove.parameter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.statements.ACallStm;

public class CallOccurrenceSignatureChanger extends DepthFirstAnalysisAdaptor {
	private ILexLocation defLoc;
	private Set<ACallStm> callOccurences;
	private Consumer<SignatureChangeObject> function;
	private String newParamName;
	private String newParamPlaceholder;
	private String newParamType;
	private boolean isParamListEmpty;
	
	public CallOccurrenceSignatureChanger(ILexLocation defLoc, Consumer<SignatureChangeObject> f, 
			String newParamName, String newParamPlaceholder, String newParamType, boolean isParamListEmpty){
		this.defLoc = defLoc;
		this.newParamName = newParamName;
		this.newParamPlaceholder = newParamPlaceholder;
		this.newParamType = newParamType;
		this.isParamListEmpty = isParamListEmpty;
		this.callOccurences = new HashSet<ACallStm>();
		this.function = f;
	}

	public Set<ACallStm> getCalls(){
		return callOccurences;
	}
	
	@Override
	public void caseACallStm(ACallStm node) throws AnalysisException{
		AExplicitOperationDefinition root = (AExplicitOperationDefinition) node.getRootdef();
		
		if (root == null){
			return;
		}
		if (root.getLocation().equals(defLoc)){
			String newParamNameStr = newParamName;
			
			AExplicitOperationDefinition parent = node.getAncestor(AExplicitOperationDefinition.class);
			if(parent != null){
				LinkedList<PExp> paramListOfParent = node.getArgs();
				LexLocation newLastLoc = new LexLocation();
				
				if(!isParamListEmpty){
					ILexLocation lastLoc = paramListOfParent.getLast().getLocation();
					newLastLoc = SignatureChangeUtil.calculateNewParamLocationWhenNotEmptyList(lastLoc, newParamPlaceholder);
				} else{
					newLastLoc = SignatureChangeUtil.calculateParamLocationInCallWhenEmptyList(node.getLocation(), newParamPlaceholder);
				}		
				
				ILexNameToken newParamName = new LexNameToken(root.getName().getModule(), newParamNameStr, newLastLoc);				
				
				function.accept(new SignatureChangeObject(newParamName.getLocation(), newParamName, 
						paramListOfParent, parent.getName().getName(), newParamType));
			
				callOccurences.add(node);
			}
		}
	}
}
