package org.overture.ide.internal.core.resources;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

import org.overture.ide.core.resources.IOptionGroup;
import org.overture.ide.core.resources.Options;

public class OptionGroup implements Serializable, IOptionGroup
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3114626907232232185L;
	public String id;
	final Map<String,Object> attributes = new Hashtable<String,Object>();
	private transient Options parent;

	public OptionGroup()
	{
	}

	public OptionGroup(String key, Options opt)
	{
		this.id = key;
		this.parent = opt;
	}
	
	public void setParent(Options options)
	{
		this.parent = options;
	}
	
	public Map<String,Object> getAttributes()
	{
		return attributes;
	}
	
	/* (non-Javadoc)
	 * @see org.overture.ide.internal.core.resources.IOptionGroup#getOptions()
	 */
	public Options getOptions()
	{
		return this.parent;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof OptionGroup)
		{
			return ((OptionGroup) obj).id.equals(id);
		}
		return super.equals(obj);
	}
	
	/* (non-Javadoc)
	 * @see org.overture.ide.internal.core.resources.IOptionGroup#getAttribute(java.lang.String, java.lang.String)
	 */
	public synchronized String getAttribute(String key, String defaultValue)
	{
		if(attributes.containsKey(key))
		{
			return attributes.get(key).toString();
		}
		return defaultValue;
	}
	
	/* (non-Javadoc)
	 * @see org.overture.ide.internal.core.resources.IOptionGroup#getAttribute(java.lang.String, boolean)
	 */
	public synchronized boolean getAttribute(String key, boolean defaultValue)
	{
		if(attributes.containsKey(key))
		{
			Object obj = attributes.get(key);
			if(obj instanceof Boolean)
			{
				return (Boolean) obj;
			}
		}
		return defaultValue;
	}
	
	
	/* (non-Javadoc)
	 * @see org.overture.ide.internal.core.resources.IOptionGroup#setAttribute(java.lang.String, java.lang.String)
	 */
	public synchronized void setAttribute(String key, String value)
	{
		if(attributes.containsKey(key))
		{
			attributes.remove(key);
		}
		attributes.put(key, value);
	}
	
	/* (non-Javadoc)
	 * @see org.overture.ide.internal.core.resources.IOptionGroup#setAttribute(java.lang.String, boolean)
	 */
	public synchronized void setAttribute(String key, Boolean value)
	{
		if(attributes.containsKey(key))
		{
			attributes.remove(key);
		}
		attributes.put(key, value);
	}

	

}
