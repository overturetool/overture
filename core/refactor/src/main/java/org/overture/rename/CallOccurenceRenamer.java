package org.overture.rename;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.ACallStm;

public class CallOccurenceRenamer  extends DepthFirstAnalysisAdaptor {
	private ILexLocation defLoc;
	private Set<ACallStm> callOccurences;
	private Consumer<RenameObject> function;
	private String newName;
	
	public CallOccurenceRenamer(ILexLocation defLoc, Consumer<RenameObject> f, String newName)
	{
		this.defLoc = defLoc;
		this.callOccurences = new HashSet<ACallStm>();
		this.function = f;
		this.newName = newName;
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
			function.accept(new RenameObject(node.getName(), newName, node::setName));
			callOccurences.add(node);
		}
	}

}