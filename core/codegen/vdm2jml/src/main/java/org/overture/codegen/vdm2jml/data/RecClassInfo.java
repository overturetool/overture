package org.overture.codegen.vdm2jml.data;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;

public class RecClassInfo
{
	private List<ADefaultClassDeclIR> recClasses;
	private Set<SDeclIR> members;

	public RecClassInfo()
	{
		this.recClasses = new LinkedList<>();
		this.members = new HashSet<SDeclIR>();
	}

	public void register(SDeclIR acc)
	{
		if (!contains(acc))
		{
			members.add(acc);
		}
	}

	private boolean contains(SDeclIR memberToCheck)
	{
		for (SDeclIR m : members)
		{
			if (m == memberToCheck)
			{
				return true;
			}
		}

		return false;
	}

	public void updateAccessor(AMethodDeclIR oldAcc, AMethodDeclIR newAcc)
	{
		SDeclIR toRemove = null;

		for (SDeclIR m : members)
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

	public boolean isRecField(AFieldDeclIR field)
	{
		return contains(field);
	}

	public boolean inAccessor(INode node)
	{
		AMethodDeclIR anc = node.getAncestor(AMethodDeclIR.class);

		if (anc == null)
		{
			return false;
		}

		return contains(anc);
	}

	public boolean inRecConstructor(INode node)
	{
		if (!inRec(node))
		{
			return false;
		}

		AMethodDeclIR m = node.getAncestor(AMethodDeclIR.class);

		if (m != null)
		{
			return m.getIsConstructor();
		}

		return false;
	}

	public boolean inRec(INode node)
	{
		ADefaultClassDeclIR clazz = node.getAncestor(ADefaultClassDeclIR.class);

		if (clazz == null)
		{
			return false;
		}

		for (ADefaultClassDeclIR r : recClasses)
		{
			if (clazz == r)
			{
				return true;
			}
		}

		return false;
	}

	public void registerRecClass(ADefaultClassDeclIR recClass)
	{
		recClasses.add(recClass);
	}
}
