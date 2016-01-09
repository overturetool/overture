package org.overture.codegen.vdm2jml.data;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;

public class RecClassInfo
{
	private List<ADefaultClassDeclCG> recClasses;
	private Set<SDeclCG> members;

	public RecClassInfo()
	{
		this.recClasses = new LinkedList<>();
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
			return false;
		}

		return contains(anc);
	}
	
	public boolean inRecConstructor(INode node)
	{
		if(!inRec(node))
		{
			return false;
		}
		
		AMethodDeclCG m = node.getAncestor(AMethodDeclCG.class);
		
		if(m != null)
		{
			return m.getIsConstructor();
		}
		
		return false;
	}
	
	public boolean inRec(INode node)
	{
		ADefaultClassDeclCG clazz = node.getAncestor(ADefaultClassDeclCG.class);
		
		if(clazz == null)
		{
			return false;
		}
		
		for(ADefaultClassDeclCG r : recClasses)
		{
			if(clazz == r)
			{
				return true;
			}
		}
		
		return false;
	}

	public void registerRecClass(ADefaultClassDeclCG recClass)
	{
		recClasses.add(recClass);
	}
}
