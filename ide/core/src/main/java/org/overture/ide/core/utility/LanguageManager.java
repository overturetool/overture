package org.overture.ide.core.utility;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.overture.ide.core.Activator;
import org.overture.ide.core.ICoreConstants;
import org.overturetool.vdmj.lex.Dialect;

public class LanguageManager
{
	/**
	 * A handle to the unique Singleton instance.
	 */
	static private LanguageManager _instance = null;
	static final List<ILanguage> languages = new Vector<ILanguage>();

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
		List<ILanguage> languages = new Vector<ILanguage>();

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
				if (Activator.DEBUG)
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

	public static boolean isVdmNature(String nature)
	{
		for (ILanguage language : languages)
		{
			if(language.getNature().equals(nature))
				return true;
		}
		return false;
	}
	
	public static ILanguage getLanguage(String nature)
	{
		for (ILanguage language : languages)
		{
			if(language.getNature().equals(nature))
				return language;
		}
		return null;
	}
	
}
