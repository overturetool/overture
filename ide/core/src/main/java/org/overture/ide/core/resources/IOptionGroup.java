package org.overture.ide.core.resources;


public interface IOptionGroup
{

	public abstract Options getOptions();

	public abstract String getAttribute(String key, String defaultValue);

	public abstract boolean getAttribute(String key, boolean defaultValue);

	public abstract void setAttribute(String key, String value);

	public abstract void setAttribute(String key, Boolean value);

}