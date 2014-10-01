package org.overture.pog.contexts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.utility.Substitution;
import org.overture.pog.utility.UniqueNameGenerator;

public abstract class StatefulContext extends POContext
{
	boolean first = true;

	public StatefulContext()
	{

	}

	public StatefulContext(IPOContextStack ctxt)
	{
		this.last_vars = ctxt.getLast_Vars() == null ? new HashMap<ILexNameToken, AVariableExp>()
				: ctxt.getLast_Vars();
	}

	protected Map<ILexNameToken, AVariableExp> last_vars;
	protected UniqueNameGenerator gen;
	List<Substitution> subs;

	@Override
	public boolean isStateful()
	{
		return true;
	}

	public Map<ILexNameToken, AVariableExp> getLast_vars()
	{
		return last_vars;
	}

}
