package org.overture.pog.pub;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.pog.contexts.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.visitors.PogVisitor;

public class ProofObligationGenerator
{
	private ProofObligationGenerator() {
	}

	public static IProofObligationList generateProofObligations(INode root)
			throws AnalysisException
	{
		PogVisitor pog = new PogVisitor();
		IProofObligationList r = root.apply(pog, new POContextStack());
		return r;
	}

	public static IProofObligationList generateProofObligations(
			List<INode> sources) throws AnalysisException
	{
		IProofObligationList r = new ProofObligationList();
		for (INode node : sources)
		{
			r.addAll(node.apply(new PogVisitor(), new POContextStack()));
		}

		return r;
	}

}
