package org.overture.prettyprinter;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.ACallStm;

public class CallOccurenceCollector extends DepthFirstAnalysisAdaptor {
	private ILexLocation defLoc;
	private Set<ACallStm> callOccurences;
	
	public CallOccurenceCollector(ILexLocation defLoc)
	{
		this.defLoc = defLoc;
		this.callOccurences = new HashSet<ACallStm>();
	}

	public Set<ACallStm> getCalls()
	{
		return callOccurences;
	}
	
	@Override
	public void caseACallStm(ACallStm node) throws AnalysisException
	{
		if (node.getRootdef() == null)
		{
			return;
		}

		if (node.getRootdef().getLocation().equals(defLoc))
		{
			callOccurences.add(node);
		}
	}

}
