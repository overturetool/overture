package org.overture.pog.pub;

import java.io.Serializable;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.pof.AVdmPoTree;

public interface IProofObligation extends Comparable<IProofObligation>,
		Serializable
{

	String getName();

	String getUniqueName();

	String getIsaName();

	String getFullPredString();

	String getDefPredString();

	AVdmPoTree getValueTree();

	String toString();

	int getNumber();

	POType getKind();

	POStatus getStatus();

	String getKindString();

	void setStatus(POStatus status);

	void setNumber(int i);

	ILexLocation getLocation();

	INode getNode();

	String getLocale();
}
