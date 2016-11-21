package org.overture.rename;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.AMapSeqStateDesignator;

public class AssignmentOccurenceRenamer extends DepthFirstAnalysisAdaptor {
	private ILexLocation defLoc;
	private Set<AIdentifierStateDesignator> assignmentOccurences;
	private Consumer<RenameObject> function;
	private String newName;
	
	public AssignmentOccurenceRenamer(ILexLocation defLoc, Consumer<RenameObject> f, String newName)
	{
		this.defLoc = defLoc;
		this.assignmentOccurences = new HashSet<AIdentifierStateDesignator>();
		this.function = f;
		this.newName = newName;
	}

	public Set<AIdentifierStateDesignator> getCalls()
	{
		return assignmentOccurences;
	}
	
	@Override
	public void caseAAssignmentStm(AAssignmentStm node) throws AnalysisException {
		node.getExp().apply(THIS);
		super.caseAAssignmentStm(node);
	}
	
	@Override
	public void caseAMapSeqStateDesignator(AMapSeqStateDesignator node) throws AnalysisException {
		node.getMapseq().apply(THIS);
		super.caseAMapSeqStateDesignator(node);
	}
	
	@Override
	public void caseAIdentifierStateDesignator(AIdentifierStateDesignator node) throws AnalysisException {
		if (node.getType().getLocation().getStartLine() == defLoc.getStartLine())
		{
			function.accept(new RenameObject(node.getName(), newName, node::setName));
			assignmentOccurences.add(node);
		}
	}
	
}