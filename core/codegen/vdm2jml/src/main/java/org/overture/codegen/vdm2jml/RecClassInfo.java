package org.overture.codegen.vdm2jml;

import java.util.HashSet;
import java.util.Set;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.logging.Logger;

public class RecClassInfo
{
	private Set<SDeclCG> members;

	public RecClassInfo()
	{
		this.members = new HashSet<SDeclCG>();
	}

	public void register(SDeclCG acc)
	{
		if (!contains(acc))
		{
			members.add(acc);
		}
	}

	private boolean contains(SDeclCG memberToCheck)
	{
		for (SDeclCG m : members)
		{
			if (m == memberToCheck)
			{
				return true;
			}
		}

		return false;
	}

	public void updateAccessor(AMethodDeclCG oldAcc, AMethodDeclCG newAcc)
	{
		SDeclCG toRemove = null;

		for (SDeclCG m : members)
		{
			if (m == oldAcc)
			{
				toRemove = m;
				break;
			}
		}

		if (toRemove != null)
		{
			members.remove(toRemove);
			members.add(newAcc);
		}
	}
	
	public boolean isRecField(AFieldDeclCG field)
	{
		return contains(field);
	}

	public boolean inAccessor(INode node)
	{
		AMethodDeclCG anc = node.getAncestor(AMethodDeclCG.class);

		if (anc == null)
		{
			Logger.getLog().printErrorln("Expected " + node
					+ " to be enclosed by a method in '"
					+ this.getClass().getSimpleName() + "'");
			return false;
		}

		return contains(anc);
	}
}
