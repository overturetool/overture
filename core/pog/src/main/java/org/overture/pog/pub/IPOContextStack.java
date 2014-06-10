package org.overture.pog.pub;

import org.overture.ast.expressions.PExp;
import org.overture.ast.types.PType;
import org.overture.pog.utility.UniqueNameGenerator;

public interface IPOContextStack
{
	
	public IPOContext push(IPOContext context);
	
	public IPOContext pop();
	
	public int size();

	PType checkType(PExp exp, PType expected);

	void noteType(PExp exp, PType PType);

	public abstract PExp getPredWithContext(PExp initialPredicate);
	
	public abstract String getName();

	public abstract String getObligation(String root);

	public void clearStateContexts();

	public void setGenerator(UniqueNameGenerator gen);
	
	public UniqueNameGenerator getGenerator();

}