package org.overture.typechecker.util;

import java.util.Map;

import org.overture.ast.intf.lex.ILexNameToken;

class LexNameTokenEntry<V> implements Map.Entry<ILexNameToken, V>
{

	Map.Entry<LexNameTokenWrapper, V> wrapped;

	public LexNameTokenEntry(Map.Entry<LexNameTokenWrapper, V> wrapped)
	{
		this.wrapped = wrapped;
	}

	public ILexNameToken getKey()
	{
		return wrapped.getKey().token;
	}

	public V getValue()
	{
		return wrapped.getValue();
	}

	public V setValue(V value)
	{
		return wrapped.setValue(value);
	}

	@Override
	public String toString()
	{
		return getKey() + "=" + getValue();
	}

}