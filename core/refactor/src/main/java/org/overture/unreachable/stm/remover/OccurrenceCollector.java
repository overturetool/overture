package org.overture.unreachable.stm.remover;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexLocation;

public class OccurrenceCollector extends DepthFirstAnalysisAdaptor {

	private ILexLocation loc;
	private boolean foundUsage;

	
	public OccurrenceCollector(){
		this.loc = null;
		foundUsage = false;
	}
	
	public void init(ILexLocation loc){
		this.loc = loc;
		foundUsage = false;
	}
	
	public boolean isFoundUsage() {
		return foundUsage;
	}

	@Override
	public void caseAVariableExp(AVariableExp node) throws AnalysisException {
		if(node.getVardef().getLocation().equals(loc)){
			if(!node.getLocation().equals(loc)){
				foundUsage = true;
			}
		}
	}
	
	
}
