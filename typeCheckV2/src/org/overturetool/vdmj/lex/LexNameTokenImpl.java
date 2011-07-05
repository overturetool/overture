/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.lex;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.overture.ast.analysis.IAnalysis;
import org.overture.ast.analysis.IAnswer;
import org.overture.ast.analysis.IQuestion;
import org.overture.ast.analysis.IQuestionAnswer;
import org.overture.ast.node.Node;
import org.overture.ast.node.NodeEnum;
import org.overture.ast.types.PType;
import org.overture.runtime.TypeComparator;
import org.overturetool.vdmj.messages.InternalException;


public class LexNameTokenImpl extends LexNameToken implements Serializable, Comparable<LexNameToken>
{
	private static final long serialVersionUID = 1L;

	public final String module;
	public final String name;
	public final boolean old;
	public final boolean explicit;	// Name has an explicit module/class

	public List<PType> typeQualifier = null;

	private int hashcode = 0;

	public LexNameTokenImpl(
		String module, String name, LexLocation location,
		boolean old, boolean explicit)
	{
		super(location, VDMToken.NAME);
		this.module = module;
		this.name = name;
		this.old = old;
		this.explicit = explicit;
	}

	public LexNameTokenImpl(String module, String name, LexLocation location)
	{
		this(module, name, location, false, false);
	}

	public LexNameTokenImpl(String module, LexIdentifierToken id)
	{
		super(id.getLocation(), VDMToken.NAME);
		this.module = module;
		this.name = id.getName();
		this.old = id.isOld();
		this.explicit = false;
	}

	public LexIdentifierToken getIdentifier()
	{
		return new LexIdentifierTokenImpl(name, old, location);
	}

	public LexNameToken getExplicit(boolean ex)
	{
		return new LexNameTokenImpl(module, name, location, old, ex);
	}

	public LexNameToken getOldName()
	{
		return new LexNameTokenImpl(module,
			new LexIdentifierTokenImpl(name, true, location));
	}

	public String getName()
	{
		// Flat specifications have blank module names
		return (explicit ? (module.length() > 0 ? module + "`" : "") : "") +
				name + (old ? "~" : "");	// NB. No qualifier
	}

	public LexNameToken getPreName(LexLocation l)
	{
		return new LexNameTokenImpl(module, "pre_" + name, l);
	}

	public LexNameToken getPostName(LexLocation l)
	{
		return new LexNameTokenImpl(module, "post_" + name, l);
	}

	public LexNameToken getInvName(LexLocation l)
	{
		return new LexNameTokenImpl(module, "inv_" + name, l);
	}

	public LexNameToken getInitName(LexLocation l)
	{
		return new LexNameTokenImpl(module, "init_" + name, l);
	}

	public LexNameToken getModifiedName(String classname)
	{
		LexNameToken mod = new LexNameTokenImpl(classname, name, location);
		mod.setTypeQualifier(typeQualifier);
		return mod;
	}

	public LexNameToken getSelfName()
	{
		if (module.equals("CLASS"))
		{
			return new LexNameTokenImpl(name, "self", location);
		}
		else
		{
			return new LexNameTokenImpl(module, "self", location);
		}
	}

	public LexNameToken getThreadName()
	{
		if (module.equals("CLASS"))
		{
			return new LexNameTokenImpl(name, "thread", location);
		}
		else
		{
			return new LexNameTokenImpl(module, "thread", location);
		}
	}

	public static LexNameToken getThreadName(LexLocation loc)
	{
		return new LexNameTokenImpl(loc.module, "thread", loc);
	}

	public LexNameToken getPerName(LexLocation loc)
	{
		return new LexNameTokenImpl(module, "per_" + name, loc);
	}

	public LexNameToken getClassName()
	{
		return new LexNameTokenImpl("CLASS", name, location);
	}

	public void setTypeQualifier(List<PType> types)
	{
		if (hashcode != 0)
		{
			if ((typeQualifier == null && types != null) ||
				(typeQualifier != null && !typeQualifier.equals(types)))
			{
				throw new InternalException(
					2, "Cannot change type qualifier: " + this + " to " + types);
			}
		}

		typeQualifier = types;
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof LexNameToken))
		{
			return false;
		}

		LexNameToken lother = (LexNameToken)other;

		if (typeQualifier != null && lother.getTypeQualifier() != null)
		{
			if (!TypeComparator.compatible(typeQualifier, lother.getTypeQualifier()))
			{
				return false;
			}
		}
		else if ((typeQualifier != null && lother.getTypeQualifier() == null) ||
				 (typeQualifier == null && lother.getTypeQualifier() != null))
		{
			return false;
		}

		return matches(lother);
	}

	public boolean matches(LexNameToken other)
	{
		return module.equals(other.getModule()) &&
				name.equals(other.getName()) &&
				old == other.isOld();
	}

	@Override
	public int hashCode()
	{
		if (hashcode == 0)
		{
			hashcode = module.hashCode() + name.hashCode() + (old ? 1 : 0) +
				(typeQualifier == null ? 0 : typeQualifier.hashCode());
		}

		return hashcode;
	}

	@Override
	public String toString()
	{
		return getName() + (typeQualifier == null ? "" : typeQualifier);
	}

	public LexNameToken copy()
	{
		LexNameToken c = new LexNameTokenImpl(module, name, location, old, explicit);
		c.setTypeQualifier(typeQualifier);
		return c;
	}

	public int compareTo(LexNameToken o)
	{
		return toString().compareTo(o.toString());
	}

	@Override
	public LexLocation getLocation() {
		return location;
	}

	@Override
	public String getModule() {
		return module;
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node clone(Map<Node, Node> oldToNewMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeEnum kindNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeChild(Node child) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void apply(IAnalysis analysis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <A> A apply(IAnswer<A> caller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PType> getTypeQualifier() {
		return typeQualifier;
	}

	@Override
	public boolean isOld() {
		return old;
	}
}
