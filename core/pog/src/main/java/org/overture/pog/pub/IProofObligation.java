package org.overture.pog.pub;

import java.io.Serializable;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.pof.AVdmPoTree;
import org.overture.pog.obligation.POStatus;
import org.overture.pog.obligation.POTrivialProof;
import org.overture.pog.obligation.POType;

public interface IProofObligation extends Comparable<IProofObligation>, Serializable
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
