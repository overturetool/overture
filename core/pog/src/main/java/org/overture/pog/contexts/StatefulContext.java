package org.overture.pog.contexts;

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
import org.overture.pog.utility.Substitution;
import org.overture.pog.utility.UniqueNameGenerator;

public abstract class StatefulContext extends POContext
{
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

		AVariableExp var_exp = last_vars.get(var.getName());
		if (var_exp == null)
		{
			var_exp = new AVariableExp();
			var_exp.setName(var.getOldname().clone());
			var_exp.setType(var.getType().clone());
			var_exp.setOriginal(var.getOldname().toString());
		}

		Substitution sub = new Substitution(var.getName().clone(), newVar);
		Substitution sub_old = new Substitution(var.getOldname().clone(), var_exp.clone());

		last_vars.put(var.getName(), newVar);

		subs.add(sub);
		subs.add(sub_old);

		return r;
	}

}
