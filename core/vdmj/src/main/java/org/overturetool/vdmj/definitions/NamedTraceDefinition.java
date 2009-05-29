/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.definitions;

import java.util.List;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.statements.TraceStatement;
import org.overturetool.vdmj.traces.SequenceTraceNode;
import org.overturetool.vdmj.traces.TestSequence;
import org.overturetool.vdmj.traces.TraceDefinitionTerm;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.OperationValue;

public class NamedTraceDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public final List<String> pathname;
	public final List<TraceDefinitionTerm> terms;

	private StateDefinition state;

	public NamedTraceDefinition(
		LexLocation location, List<String> pathname, List<TraceDefinitionTerm> terms)
	{
		super(Pass.DEFS, location,
			new LexNameToken(
				location.module, Utils.listToString(pathname, "_"), location),
				NameScope.GLOBAL);

		this.pathname = pathname;
		this.terms = terms;

		setAccessSpecifier(new AccessSpecifier(false, false, Token.PUBLIC));
	}

	@Override
	public void implicitDefinitions(Environment base)
	{
		state = base.findStateDefinition();
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		return (sought.equals(name) ? this : null);
	}

	@Override
	public boolean isFunctionOrOperation()
	{
		return true;
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return new DefinitionList(this);
	}

	@Override
	public Type getType()
	{
		return new OperationType(location);		// () ==> ()
	}

	@Override
	public LexNameList getVariableNames()
	{
		return new LexNameList(name);
	}

	@Override
	public NameValuePairList getNamedValues(Context ctxt)
	{
		ExplicitOperationDefinition opdef = new ExplicitOperationDefinition(
			name, new OperationType(location),
			new PatternList(), null, null, new TraceStatement(this));

		OperationValue opval = new OperationValue(opdef, null, null, state);

		return new NameValuePairList(new NameValuePair(name, opval));
	}

	@Override
	public String kind()
	{
		return "trace";
	}

	@Override
	public String toString()
	{
		return pathname + " = " + terms.toString();
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		for (TraceDefinitionTerm term: terms)
		{
			term.typeCheck(base, NameScope.NAMESANDSTATE);
		}
	}

	public TestSequence getTests(Context ctxt)
	{
		SequenceTraceNode traces = new SequenceTraceNode(ctxt);

		for (TraceDefinitionTerm term: terms)
		{
			traces.nodes.add(term.expand(ctxt));
		}

		return traces.getTests();
	}
}
