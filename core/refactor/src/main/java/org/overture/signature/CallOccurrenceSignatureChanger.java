package org.overture.signature;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.ACallStm;

public class CallOccurrenceSignatureChanger extends DepthFirstAnalysisAdaptor {
	private ILexLocation defLoc;
	private Set<ACallStm> callOccurences;
	private Consumer<SignatureChangeObject> function;
	
	public CallOccurrenceSignatureChanger(ILexLocation defLoc, Consumer<SignatureChangeObject> f)
	{
		this.defLoc = defLoc;
		this.callOccurences = new HashSet<ACallStm>();
		this.function = f;
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
			function.accept(new SignatureChangeObject(node.getName(), node.getArgs()));
			callOccurences.add(node);
		}
	}
}
