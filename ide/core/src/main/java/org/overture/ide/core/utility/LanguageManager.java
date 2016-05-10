/*
 * #%~
 * org.overture.ide.core
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.core.utility;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.ICoreConstants;
import org.overture.ast.lex.Dialect;

public class LanguageManager
{
	/**
	 * A handle to the unique Singleton instance.
	 */
	static private LanguageManager _instance = null;
	final List<ILanguage> languages = new Vector<ILanguage>();

	/**
	 * @return The unique instance of this class.
	 */
	static public LanguageManager getInstance()
	{
		if (null == _instance)
		{
			_instance = new LanguageManager();
		}
		return _instance;
	}

	public LanguageManager() {
		languages.addAll(getLoadLanguages());
	}

	public Collection<? extends ILanguage> getLanguages()
	{
		return languages;
	}

	private List<ILanguage> getLoadLanguages()
	{
		List<ILanguage> languages = new ArrayList<ILanguage>();

		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(ICoreConstants.EXTENSION_LANGUAGE_ID);

		for (IConfigurationElement e : config)
		{
			Language language = new Language();
			language.setName(e.getAttribute("name"));
			language.setNature(e.getAttribute("nature"));
			try
			{
				language.setDialect(Dialect.valueOf(e.getAttribute("dialect")));
			} catch (Exception exception)
			{
				if (VdmCore.DEBUG)
				{
					System.err.println("Cannot parse dialect of language extension: "
							+ language.getName());
				}
				continue;
			}

			for (IConfigurationElement contentTypeElement : e.getChildren("ContentType"))
			{
				language.addContentType(contentTypeElement.getAttribute("id"));
			}
			languages.add(language);
		}

		return languages;
	}

	public boolean isVdmNature(String nature)
	{
		for (ILanguage language : languages)
		{
			if(language.getNature().equals(nature))
				return true;
		}
		return false;
	}
	
	public ILanguage getLanguage(String nature)
	{
		for (ILanguage language : languages)
		{
			if(language.getNature().equals(nature))
				return language;
		}
		return null;
	}
	
}
