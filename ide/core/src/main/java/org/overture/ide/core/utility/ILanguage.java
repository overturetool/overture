package org.overture.ide.core.utility;

import java.util.List;

import org.overturetool.vdmj.lex.Dialect;

public interface ILanguage
{
	String getNature();

	String getName();

	List<String> getContentTypes();

	Dialect getDialect();
}
