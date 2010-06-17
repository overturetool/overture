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

package org.overturetool.vdmj.types;

import java.io.Serializable;

import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.values.ValueList;

/**
 * The parent class of all static type checking types.
 */

public abstract class Type implements Comparable<Type>, Serializable, IAstNode
{
	private static final long serialVersionUID = 1L;

	/** The location of the type definition. */
	public final LexLocation location;
	/** True if the type's and its subtype's names have been resolved. */
	public boolean resolved = false;
	/** The type's possible definition(s) (if a named type) */
	public DefinitionList definitions = null;

	/**
	 * Create a new type at the given location.
	 *
	 * @param location
	 */

	public Type(LexLocation location)
	{
		this.location = location;
	}

	abstract protected String toDisplay();

	/** A flag to prevent recursive types from failing toString(). */
	private boolean inToString = false;

	/**
	 * Note that this is synchronized so that multiple threads calling
	 * toString will both get the same string, not "...". This causes
	 * problems with VDM-RT trace logs which are threaded, and use
	 * this method for operation names.
	 */

	@Override
	public synchronized String toString()
	{
		if (inToString)
		{
			return "...";
		}
		else
		{
			inToString = true;
		}

		String s = toDisplay();
		inToString = false;
		return s;
	}

	/**
	 * The type with expanded detail, in the case of record types.
	 *
	 * @return The detailed type string.
	 */

	public String toDetailedString()
	{
		return toString();
	}

	/**
	 * Resolve the type. After syntax checking, all named type references are
	 * created as {@link org.overturetool.vdmj.types.UnresolvedType}, which simply have a name.
	 * The process of resolving a type in a given {@link Environment} will
	 * lookup any UnresolvedTypes and replace them with the type of the actual
	 * definition. This process is performed across all of the subtypes of a
	 * type (eg. in the element types in a SetType).
	 *
	 * @param env The other type names defined in this scope.
	 * @param root The outermost type definition being resolved.
	 */

	public Type typeResolve(Environment env, TypeDefinition root)
	{
		resolved = true;
		return this;
	}

	/**
	 * Clear the recursive "resolved" flag. This does a deep search of a
	 * type structure, clearing the flag. It is used when type checking
	 * errors require multiple passes of the type tree.
	 */

	public void unResolve()
	{
		resolved = false;
	}

	/**
	 * Apply a "type morph" to any type with ParameterType subtypes. This is
	 * used when a polymorphic function is instantiated. Note that the method
	 * does not change the type, but returns a new type representing this type
	 * with parameter type instances of the given name "morphed" to the actual
	 * type given.
	 *
	 * @param pname The name of the type parameter being morphed.
	 * @param actualType The type to morph the parameter to.
	 * @return A new type with the named ParameterType substituted.
	 */

	public Type polymorph(LexNameToken pname, Type actualType)
	{
		return this;
	}

	/**
	 * Remove layers of BracketTypes.
	 */

	public Type deBracket()
	{
		Type r = this;

		while (r instanceof BracketType)
		{
			r = ((BracketType)r).type;
		}

		return r;
	}

	public boolean narrowerThan(AccessSpecifier accessSpecifier)
	{
		if (definitions != null)
		{
			boolean result = false;

			for (Definition d: definitions)
			{
				result = result || d.accessSpecifier.narrowerThan(accessSpecifier);
			}

			return result;
		}
		else
		{
			return false;
		}
	}

	public Type isType(String typename)
	{
		return (toDisplay().equals(typename)) ? this : null;
	}

	public boolean isType(Class<? extends Type> typeclass)
	{
		return typeclass.isInstance(this);
	}

	public boolean isUnion()
	{
		return false;	// Unions, or names of unions etc are not unique.
	}

	public boolean isUnknown()
	{
		return false;	// Parameter types and type check errors are unknown.
	}

	public boolean isSeq()
	{
		return false;
	}

	public boolean isSet()
	{
		return false;
	}

	public boolean isMap()
	{
		return false;
	}

	public boolean isRecord()
	{
		return false;
	}

	public boolean isClass()
	{
		return false;
	}

	public boolean isNumeric()
	{
		return false;
	}

	public boolean isProduct()
	{
		return false;
	}

	public boolean isProduct(@SuppressWarnings("unused") int n)
	{
		return false;
	}

	public boolean isFunction()
	{
		return false;
	}

	public boolean isOperation()
	{
		return false;
	}

	public UnionType getUnion()
	{
		assert false : "Can't getUnion of a non-union";
		return null;
	}

	public SeqType getSeq()
	{
		assert false : "Can't getSeq of a non-sequence";
		return null;
	}

	public SetType getSet()
	{
		assert false : "Can't getSet of a non-set";
		return null;
	}

	public MapType getMap()
	{
		assert false : "Can't getMap of a non-map";
		return null;
	}

	public RecordType getRecord()
	{
		assert false : "Can't getRecord of a non-record";
		return null;
	}

	public ClassType getClassType()
	{
		assert false : "Can't getClassType of a non-class";
		return null;
	}

	public NumericType getNumeric()
	{
		assert false : "Can't getNumeric of a non-numeric";
		return null;
	}

	public ProductType getProduct()
	{
		assert false : "Can't getProduct of a non-product";
		return null;
	}

	public ProductType getProduct(int n)
	{
		assert false : "Can't getProduct of a non-product: " + n;
		return null;
	}

	public FunctionType getFunction()
	{
		assert false : "Can't getFunction of a non-function";
		return null;
	}

	public OperationType getOperation()
	{
		assert false : "Can't getOperation of a non-operation";
		return null;
	}

	public ValueList getAllValues()
	{
		throw new InternalException(4, "Cannot get bind values for type " + this
			+ " " + location);
	}

	@Override
	public boolean equals(Object other)
	{
		while (other instanceof BracketType)
		{
			other = ((BracketType)other).type;
		}

		return this.getClass() == other.getClass();
	}

	public int compareTo(Type o)
	{
		// This is used by the TreeSet to do inserts, not equals!!
		return toString().compareTo(o.toString());
	}

	@Override
	public int hashCode()
	{
		return getClass().hashCode();
	}

	public void report(int number, String msg)
	{
		TypeChecker.report(number, msg, location);
	}

	public void abort(int number, String msg, Context ctxt)
	{
		throw new ContextException(number, msg, location, ctxt);
	}

	public void abort(ValueException ve)
	{
		throw new ContextException(ve, location);
	}

	public void detail(String tag, Object obj)
	{
		TypeChecker.detail(tag, obj);
	}

	public void detail2(String tag1, Object obj1, String tag2, Object obj2)
	{
		TypeChecker.detail2(tag1, obj1, tag2, obj2);
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
