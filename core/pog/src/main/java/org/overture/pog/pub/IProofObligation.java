package org.overture.pog.pub;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.pof.AVdmPoTree;
import org.overture.pog.obligation.POTrivialProof;

public interface IProofObligation extends Comparable<IProofObligation>
{
	
	String getName();
	
	String getUniqueName();
	
	String getIsaName();
	
	String getValue();
	
	AVdmPoTree getValueTree();

	String toString();

	int getNumber();
	
	POType getKind();
	
	POStatus getStatus();
	
	String getKindString();
	
	void setStatus(POStatus status);
	
	POTrivialProof getTrivialProof();

	void setNumber(int i);
	
	ILexLocation getLocation();
	
	INode getNode();
}
