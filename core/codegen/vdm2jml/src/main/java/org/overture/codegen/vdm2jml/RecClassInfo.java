package org.overture.codegen.vdm2jml;

import java.util.HashSet;
import java.util.Set;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.logging.Logger;

public class RecClassInfo
{
	private Set<AMethodDeclCG> accessors;

	public RecClassInfo()
	{
		this.accessors = new HashSet<>();
	}

	public void register(AMethodDeclCG acc)
	{
		if (!contains(acc))
		{
			accessors.add(acc);
		}
	}

	private boolean contains(AMethodDeclCG acc)
	{
		for (AMethodDeclCG a : accessors)
		{
			if (a == acc)
			{
				return true;
			}
		}

		return false;
	}

	public void updateAccessor(AMethodDeclCG oldAcc, AMethodDeclCG newAcc)
	{
		AMethodDeclCG toRemove = null;

		for (AMethodDeclCG a : accessors)
		{
			if (a == oldAcc)
			{
				toRemove = a;
				break;
			}
		}

		if (toRemove != null)
		{
			accessors.remove(toRemove);
			accessors.add(newAcc);
		}
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
