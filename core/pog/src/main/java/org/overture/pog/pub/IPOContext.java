package org.overture.pog.pub;

import org.overture.ast.expressions.PExp;
import org.overture.ast.types.PType;

public interface IPOContext
{
	String getContext();

	PExp getContextNode(PExp stitch);

	String getName();

	boolean isScopeBoundary();

	void noteType(PExp exp, PType type);

	PType checkType(PExp exp);

	boolean isStateful();

	public void lastStmt();

}
