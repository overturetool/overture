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

package org.overturetool.vdmj.expressions;

import java.io.Serializable;

import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Breakpoint;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

/**
 *	The parent class of all VDM expressions.
 */

public abstract class Expression implements Serializable,IAstNode
{
	private static final long serialVersionUID = 1L;

	/** The textual location of the expression. */
	public final LexLocation location;

	/** The expression's breakpoint, if any. */
	public Breakpoint breakpoint;

	/**
	 * Generate an expression at the given location.
	 *
	 * @param location	The location of the new expression.
	 */

	public Expression(LexLocation location)
	{
		this.location = location;
		this.breakpoint = new Breakpoint(location);

		location.executable(true);
		LexLocation.addAstNode(getLocation(), this);
	}

	/**
	 * Generate an expression at the same location as the expression passed.
	 * This is used when a compound expression, comprising several
	 * subexpressions, is being constructed. The expression passed "up" is
	 * considered the location of the overall expression. For example, a
	 * function application involves an expression for the function to apply,
	 * plus a list of expressions for the arguments. The location of the
	 * expression for the function (often just a variable name) is considered
	 * the location of the entire function application.
	 *
	 * @param exp The expression containing the location.
	 */

	public Expression(Expression exp)
	{
		this(exp.location);
	}

	@Override
	public abstract String toString();

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Expression)
		{
			Expression oe = (Expression)other;
			return toString().equals(oe.toString());	// For now...
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	/**
	 * A short descriptive string for the expression. An example would
	 * be "forall" or "map comprehension".
	 *
	 * @return A short name for the expression.
	 */

	public abstract String kind();

	/**
	 * Perform a type check of the expression. The method returns the
	 * {@link Type} of the expression it checked, and is used
	 * recursively across an expression tree. For example, a boolean "and"
	 * expression would type check its left and right hand sides, check that the
	 * types returned were both compatible with {@link org.overturetool.vdmj.types.BooleanType}
	 * and then return a BooleanType (regardless of any errors).
	 * <p>
	 * The qualifiers argument is passed when checking function and operation
	 * application. It contains the list of argument types of the application,
	 * and is used to choose between overloaded function/operation definitions.
	 *
	 * @param env The static environment for resolving names.
	 * @param qualifiers The argument type qualifiers for overloading.
	 * @param scope The scope of applicable names from the environment.
	 * @return The type of the expression.
	 */

	public abstract Type typeCheck(Environment env, TypeList qualifiers, NameScope scope);

	/**
	 * Evaluate the expression in the given runtime context. The {@link Value} object
	 * returned can hold any type of value (int, bool, sequences, sets etc).
	 *
	 * @param ctxt	The context in which to evaluate the expression.
	 * @return	The value of the expression.
	 */

	abstract public Value eval(Context ctxt);

	/**
	 * Get a list of proof obligations from the expression.
	 *
	 * @param ctxt The call context.
	 * @return The list of proof obligations.
	 */

	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return new ProofObligationList();
	}

	/**
	 * Find an expression starting on the given line. Single expressions just
	 * compare their location to lineno, but expressions with sub-expressions
	 * iterate over their branches.
	 *
	 * @param lineno The line number to locate.
	 * @return An expression starting on the line, or null.
	 */

	public Expression findExpression(int lineno)
	{
		return (location.startLine == lineno) ? this : null;
	}

	/**
	 * Return a list of all the updatable values read by the expression. This
	 * is used to add listeners to values that affect permission guards, so
	 * that the guard may be efficiently re-evaluated when the values change.
	 *
	 * @param ctxt	The context in which to search for values.
	 * @return  A list of values read by the expression.
	 */

	public ValueList getValues(Context ctxt)
	{
		return new ValueList();  // Default, for expressions with no variables
	}
	
	/**
	 * Return a list of sub-expressions of this expression.
	 */
	
	public ExpressionList getSubExpressions()
	{
		ExpressionList subs = new ExpressionList();
		subs.add(this);
		return subs;
	}

	/**
	 * Get the name of the precondition function of this expression, if it is
	 * a function expression that identifies a function with a precondition.
	 * This is used during proof obligation generation. It is implemented in
	 * the VariableExpression class.
	 */

	public String getPreName()
	{
		return null;
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#report
	 */

	public void report(int number, String msg)
	{
		TypeChecker.report(number, msg, location);
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#warning
	 */

	public void warning(int number, String msg)
	{
		TypeChecker.warning(number, msg, location);
	}

	/**
	 * This is used when a problem would be an error if the type it applies
	 * to is unambiguous, or a warning otherwise.
	 * @param serious True if this should be an error
	 * @param number The error number.
	 * @param msg The problem.
	 */

	public void concern(boolean serious, int number, String msg)
	{
		if (serious)
		{
			TypeChecker.report(number, msg, location);
		}
		else
		{
			TypeChecker.warning(number, msg, location);
		}
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#abort
	 */

	public Value abort(int number, String msg, Context ctxt)
	{
		throw new ContextException(number, msg, location, ctxt);
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#abort
	 */

	public Value abort(ValueException ve)
	{
		throw new ContextException(ve, location);
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#detail
	 */

	public void detail(String tag, Object obj)
	{
		TypeChecker.detail(tag, obj);
	}

	public void detail(boolean serious, String tag, Object obj)
	{
		if (serious)
		{
			TypeChecker.detail(tag, obj);
		}
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#detail2
	 */

	public void detail2(String tag1, Object obj1, String tag2, Object obj2)
	{
		TypeChecker.detail2(tag1, obj1, tag2, obj2);
	}

	public void detail2(
		boolean serious, String tag1, Object obj1, String tag2, Object obj2)
	{
		if (serious)
		{
			TypeChecker.detail2(tag1, obj1, tag2, obj2);
		}
	}
	
	public String getName()
	{
		return location.module;
	}
	
	public LexLocation getLocation()
	{
		return location;
	}
}
