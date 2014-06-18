package org.overture.pog.contexts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.utility.Substitution;
import org.overture.pog.utility.UniqueNameGenerator;

public abstract class StatefulContext extends POContext
{

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

	private boolean isLast = false;

	public void lastStmt()
	{
		isLast = true;
	}

	public boolean isLast()
	{
		return isLast;
	}

	protected PMultipleBind introduceFreshVar(AInstanceVariableDefinition var)
	{
		ATypeMultipleBind r = new ATypeMultipleBind();

		List<PPattern> pats = new LinkedList<PPattern>();
		AIdentifierPattern idPat = new AIdentifierPattern();

		idPat.setName(gen.getUnique(var.getName().getName()));
		pats.add(idPat);

		r.setPlist(pats);
		r.setType(var.getType().clone());

		AVariableExp newVar = new AVariableExp();
		newVar.setName(idPat.getName().clone());
		newVar.setOriginal(idPat.getName().getFullName());

		AVariableExp old_var = last_vars.get(var.getName());
		if (old_var != null)
		{
			Substitution sub_old = new Substitution(var.getOldname().toString(), old_var);
			subs.add(sub_old);
		}

		Substitution sub = new Substitution(var.getName().clone(), newVar);
		last_vars.put(var.getName(), newVar);
		subs.add(sub);

		return r;
	}

}
