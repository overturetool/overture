package org.overture.dead.model.part.removal;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.refactoring.RefactoringUtils;

public class OccurrenceCollector extends DepthFirstAnalysisAdaptor {

	private ILexLocation loc;
	private boolean foundUsage;
	
	public OccurrenceCollector(ILexLocation loc){
		this.loc = loc;
		foundUsage = false;
	}
	
	public boolean isFoundUsage() {
		return foundUsage;
	}
	
	@Override
	public void caseAVariableExp(AVariableExp node) throws AnalysisException {
		
		if(node.getVardef() != null){
			if(RefactoringUtils.compareNodeLocations(loc, node.getVardef().getLocation()) && 
					!RefactoringUtils.compareNodeLocations(loc, node.getLocation())){
				foundUsage = true;
			}
		}
	}

}
