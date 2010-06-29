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

package org.overturetool.vdmj.definitions;

import java.io.Serializable;

import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.messages.LocatedException;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

/**
 * The abstract parent of all definitions. A definition can represent a data
 * type, a value (constant), implicit or explicit functions, implicit or
 * explicit operations, module state, as well as various sorts of local variable
 * definition.
 */

public abstract class Definition implements Serializable, IAstNode
{
	private static final long serialVersionUID = 1L;

	/** The textual location of the definition */
	public final LexLocation location;
	/** The name of the object being defined. */
	public final LexNameToken name;
	/** The scope of the definition name. */
	public final NameScope nameScope;
	/** The pass to cover this definition in type checking. */
	public final Pass pass;

	/** True if the definition has been used by the rest of the code. */
	public boolean used = false;

	/** A public/private/protected/static specifier, if any. */
	public AccessSpecifier accessSpecifier = null;
	/** A pointer to the enclosing class definition, if any. */
	public ClassDefinition classDefinition = null;

	/**
	 * Create a new definition of a particular name and location.
	 */

	public Definition(Pass pass, LexLocation location, LexNameToken name, NameScope scope)
	{
		this.pass = pass;
		this.location = location;
		this.name = name;
		this.nameScope = scope;
		this.accessSpecifier = AccessSpecifier.DEFAULT;
		LexLocation.addAstNode(getLocation(), this);
	}

	@Override
	abstract public String toString();

	@Override
	public boolean equals(Object other)		// Used for sets of definitions.
	{
		if (other instanceof Definition)
		{
			Definition odef = (Definition)other;
			return name != null && odef.name != null && name.equals(odef.name);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();		// Used for sets of definitions (see equals).
	}

	/** A string with the informal kind of the definition, like "operation". */
	abstract public String kind();

	/**
	 * Perform a static type check of this definition. The actions performed
	 * depend on the type of definition and are entirely defined in the
	 * subclass. The type checker is passed an Environment object which
	 * contains a list of other definitions, and a scope which indicates
	 * what sort of names from the environment can be used (state etc).
	 *
	 * @param base	Named definitions potentially in scope
	 * @param scope The types of names in scope
	 */

	abstract public void typeCheck(Environment base, NameScope scope);

	/**
	 * Resolve the unresolved types in the definition. Type resolution means
	 * looking up type names (which are all that is known after parsing, unless
	 * the types are primitive) and replacing them with the {@link Type} value
	 * from the definition of the named type in the {@link Environment} passed.
	 * <p>
	 * This method is defined for Definition subclasses which have a
	 * {@link org.overturetool.vdmj.typechecker.Pass Pass} value of
	 * {@link org.overturetool.vdmj.typechecker.Pass Pass.TYPES}.
	 *
	 * @param env
	 */

	public void typeResolve(Environment env)
	{
		return;
	}

	/**
	 * Return a list of all the definitions created by this definition. A
	 * definition may create lower level definitions if it defines more than one
	 * "name" which can be used in its scope. For example, a function may define
	 * pre and post conditions, which cause implicit functions to be defined in
	 * addition to the main definition for the function itself.
	 *
	 * @return A list of definitions.
	 */

	abstract public DefinitionList getDefinitions();

	/**
	 * Return a list of variable names that would be defined by the definition.
	 */

	abstract public LexNameList getVariableNames();

	/**
	 * Return a list of external values that are read by the definition.
	 * @param ctxt The context in which to evaluate the expressions.
	 * @return A list of values read.
	 */

	public ValueList getValues(Context ctxt)
	{
		return new ValueList();
	}

	/**
	 * Return the static type of the definition. For example, the type of a
	 * function or operation definition would be its parameter/result signature;
	 * the type of a value definition would be that value's type; the type of a
	 * type definition is the underlying type being defined.
	 * <p>
	 * Note that for Definitions which define multiple inner definitions (see
	 * {@link #getDefinitions}), this method returns the primary type - eg.
	 * the type of a function, not the types of its pre/post definitions.
	 *
	 * @return The primary type of this definition.
	 */

	abstract public Type getType();

	/**
	 * Get a list of proof obligations for the definition.
	 *
	 * @param ctxt The call context.
	 * @return A list of POs.
	 */

	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return new ProofObligationList();
	}

	/**
	 * Complete the generation of implicit definitions for this definition.
	 * Some definition types create implicit definitions, such as pre_ and
	 * post_ functions, inv_ and init_ functions. These definitions can be
	 * referred to by name, so they must be created shortly after the parse
	 * of the explicit definitions.
	 *
	 * @param base The environment defining all global definitions.
	 */

	public void implicitDefinitions(Environment base)
	{
		return;
	}

	/**
	 * Find whether this Definition contains a definition of a name. Since some
	 * definitions contain inner definitions (eg. function definitions contain
	 * further definitions for their pre and post conditions), this cannot in
	 * general compare the name passed with the name of this object.
	 *
	 * This method is used for finding Definitions that are not types. That can
	 * include constants, functions, operations and state definitions. The
	 * TypeDefinition implementation of this method checks for any type
	 * invariant function definition that it has created, but does not return a
	 * match for the name of the type itself. The {@link #findType findType}
	 * method is used for this.
	 *
	 * The implementation in the base class compares the name with this.name,
	 * setting the "used" flag if the names match. Subclasses perform a similar
	 * check on any extra definitions they have created.
	 *
	 * Definitions which include state fields are sometimes in scope (from
	 * within operation bodies) and sometimes not (in function bodies). So the
	 * scope parameter is used to indicate what sorts of definitions should be
	 * considered in the lookup. The {@link StateDefinition} subclass uses this
	 * to decide whether to consider its state definitions as in scope.
	 *
	 * @param sought The name of the definition sought.
	 * @param scope The sorts of definitions which may be considered.
	 * @return A definition object, or null if not found.
	 */

	public Definition findName(LexNameToken sought, NameScope scope)
	{
		if (name.equals(sought))
		{
			if ((nameScope == NameScope.STATE && !scope.matches(NameScope.STATE)) ||
				(nameScope == NameScope.OLDSTATE && !scope.matches(NameScope.OLDSTATE)))
			{
				sought.report(3302,
					"State variable '" + sought.name + "' cannot be accessed from this context");
			}

			markUsed();
			return this;
		}

		return null;
	}

	/**
	 * Set the "used" flag.
	 */

	public void markUsed()
	{
		used = true;
	}

	/**
	 * Test the "used" flag.
	 */

	protected boolean isUsed()
	{
		return used;
	}

	/**
	 * Find whether this definition contains a definition of a type name. This
	 * is very similar to {@link #findName findName}, except that there is no
	 * need for a scope parameter since state definitions' types are always in
	 * scope. The method is implemented by the {@link TypeDefinition} class, by
	 * {@link StateDefinition} (when module state is referred to as a
	 * record type), and by {@link org.overturetool.vdmj.modules.ImportedType ImportedType}
	 * definitions.
	 *
	 * @param sought The name of the type definition being sought.
	 * @return The type definition or null.
	 */

	public Definition findType(LexNameToken sought)
	{
		return null;
	}

	/**
	 * Locate a {@link Statement} that starts on the line number indicated.
	 * This is used by the debugger to set breakpoints.
	 *
	 * @param lineno The line number to look for.
	 * @return A statement that starts on the line, or null if there is none.
	 */

	public Statement findStatement(int lineno)
	{
		return null;
	}

	/**
	 * Locate an {@link Expression} that starts on the line number indicated.
	 * This is used by the debugger to set breakpoints.
	 *
	 * @param lineno The line number to look for.
	 * @return An expression that starts on the line, or null if there is none.
	 */

	public Expression findExpression(int lineno)
	{
		return null;
	}

	/**
	 * Check whether this definition has ever been used. This method is called
	 * when a definition goes out of scope. If the "used" flag has not been set,
	 * then nothing has referenced the variable during its lifetime and an
	 * "unused variable" warning is given. The "used" flag is set after the
	 * first warning to prevent repeat warnings.
	 */

	public void unusedCheck()
	{
		if (!isUsed())
		{
			warning(5000, "Definition '" + name + "' not used");
			markUsed();		// To avoid multiple warnings
		}
	}

	/**
	 * Return the names and values in the runtime environment created by this
	 * definition. For example, a value definition would return its name(s),
	 * coupled with the value(s) derived from the evaluation of the value of
	 * the definition using the Context object passed in.
	 *
	 * In many definition types, the method has to deal with pattern
	 * substitution which may yield several variables, which is why it returns a
	 * list of name/values.
	 *
	 * @param ctxt The execution context in which to evaluate the definition.
	 * @return A possibly empty list of names and values.
	 */

	public NameValuePairList getNamedValues(Context ctxt)
	{
		return new NameValuePairList();		// Overridden
	}

	/**
	 * Set the definition's AccessSpecifier. This is used in VDM++ definitions
	 * to hold  static and public/protected/private settings.
	 *
	 * @param access The AccessSpecifier to set.
	 */

	public void setAccessSpecifier(AccessSpecifier access)
	{
		accessSpecifier = access;
	}

	/**
	 * Test access specifier. An empty specifier defaults to PRIVATE.
	 */

	public boolean isAccess(Token kind)
	{
		switch (kind)
		{
			case STATIC:
				return accessSpecifier.isStatic;

			default:
				return accessSpecifier.access == kind;
		}
	}

	/**
	 * Test for a static access specifier.
	 */

	public boolean isStatic()
	{
		return accessSpecifier.isStatic;
	}

	/**
	 * Return true if the definition is of an implicit or explicit function
	 * or operation.
	 */

	public boolean isFunctionOrOperation()
	{
		return isFunction() || isOperation();
	}

	public boolean isFunction()
	{
		return false;
	}

	public boolean isOperation()
	{
		return false;
	}

	/**
	 * Return true if the definition is an operation that defines a body.
	 */

	public boolean isCallableOperation()
	{
		return false;
	}

	/**
	 * Return true if the definition is an instance variable.
	 */

	public boolean isInstanceVariable()
	{
		return false;
	}

	/**
	 * Return true if the definition is a type definition.
	 */

	public boolean isTypeDefinition()
	{
		return false;
	}

	/**
	 * Return true if the definition is a value definition.
	 */

	public boolean isValueDefinition()
	{
		return false;
	}

	/**
	 * Return true if the definition generates a value which should be used
	 * at runtime. For example, TypeDefinitions don't.
	 */

	public boolean isRuntime()
	{
		return true;	// Most are!
	}

	/**
	 * Return true if the definition generates a value which is updatable,
	 * like a state value, an instance variable, or a dcl declaration.
	 */

	public boolean isUpdatable()
	{
		return false;	// Most aren't!
	}

	/**
	 * Set the associated ClassDefinition for this definition. This is used with
	 * VDM++ where {@link ClassDefinition} instances contain definitions for all
	 * the contained functions, operations, types, values and instance variables.
	 *
	 * @param def
	 */

	public void setClassDefinition(ClassDefinition def)
	{
		classDefinition = def;
	}

	/**
	 * Generate a local definition for "self". Note that this assumes the
	 * setClassDefinition method has been called.
	 *
	 * @return a LocalDefinition for "self".
	 */

	protected Definition getSelfDefinition()
	{
		return classDefinition.getSelfDefinition();
	}

	/**
	 * Report the message as a type checking error, increment the error count,
	 * and continue.
	 */

	public void report(int number, String msg)
	{
		TypeChecker.report(number, msg, location);
	}

	/**
	 * Report the message as a type checking warning, increment the warning count,
	 * and continue.
	 */

	public void warning(int number, String msg)
	{
		TypeChecker.warning(number, msg, location);
	}

	/**
	 * Add detail to a type checking error or warning. For example, the report
	 * method might be used to indicate that an unexpected type was used, but
	 * the detail method can be used to indicate what the expected type was.
	 *
	 * @param tag	The message.
	 * @param obj	The value associated with the message.
	 */

	public void detail(String tag, Object obj)
	{
		TypeChecker.detail(tag, obj);
	}

	/**
	 * Add detail to a type checking error or warning. As {@link #detail},
	 * except two tag/value pairs can be passed (eg. "Actual" x, "Expected" y).
	 */

	public void detail2(String tag1, Object obj1, String tag2, Object obj2)
	{
		TypeChecker.detail2(tag1, obj1, tag2, obj2);
	}

	/**
	 * Abort the runtime interpretation of the definition, throwing a
	 * ContextException to indicate the call stack.
	 * @param number The error number.
	 * @param msg The error message, without location information.
	 * @param ctxt The runtime execution context of the problem.
	 */

	public void abort(int number, String msg, Context ctxt)
	{
		throw new ContextException(number, msg, location, ctxt);
	}

	/**
	 * Abort the runtime interpretation of the definition, throwing a
	 * ContextException to indicate the call stack. The information is
	 * based on that in the ValueException passed.
	 *
	 * @param ve	The ValueException that caused the problem.
	 */

	public Value abort(ValueException ve)
	{
		throw new ContextException(ve, location);
	}

	/**
	 * Abort the runtime interpretation of the definition, throwing a
	 * ContextException to indicate the call stack. The information is
	 * based on that in the Exception passed.
	 *
	 * @param e		The Exception that caused the problem.
	 * @param ctxt	The runtime context that caught the exception.
	 */

	public Value abort(LocatedException e, Context ctxt)
	{
		throw new ContextException(e.number, e.getMessage(), e.location, ctxt);
	}
	
	
	public String getName()
	{
		if(name!=null)
		{
			return name.name;
		}
		return null;
	}
	
	public LexLocation getLocation()
	{
		return location;
	}
}
