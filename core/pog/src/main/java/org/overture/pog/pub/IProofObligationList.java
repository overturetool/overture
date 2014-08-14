package org.overture.pog.pub;

import java.util.List;

public interface IProofObligationList extends List<IProofObligation>
{
	String toString();

	void trivialCheck();

	void renumber();

}
