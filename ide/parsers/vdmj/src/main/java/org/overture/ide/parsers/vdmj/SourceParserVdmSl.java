/*
 * #%~
 * org.overture.ide.parsers.vdmj
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
package org.overture.ide.parsers.vdmj;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.messages.InternalException;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.util.modules.ModuleList;
import org.overture.config.Settings;
import org.overture.ide.core.parser.AbstractParserParticipant;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.parser.config.Properties;
import org.overture.parser.lex.BacktrackInputReader.ReaderType;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ModuleReader;

public class SourceParserVdmSl extends AbstractParserParticipant
{

	@Override
	protected ParseResult startParse(IVdmSourceUnit file, String source,
			String charset)
	{
		file.setType(IVdmSourceUnit.VDM_MODULE_SPEC);
		Settings.dialect = Dialect.VDM_SL;
		System.setProperty("VDM_SL", "1");
		try
		{
			Settings.release = file.getProject().getLanguageVersion();
		} catch (CoreException e1)
		{
			if (Activator.DEBUG)
			{
				e1.printStackTrace();
			}
		}

		Properties.init();
		Properties.parser_tabstop = 1;

		ModuleList modules = new ModuleList();
		modules.clear();
		LexLocation.resetLocations();

		ModuleReader reader = null;
		ParseResult result = new ParseResult();
		try
		{
			ReaderType streamReaderType = AbstractParserParticipant.findStreamReaderType(file.getFile());

			LexTokenReader ltr = new LexTokenReader(source, Settings.dialect, file.getSystemFile(), charset, streamReaderType);
			reader = new ModuleReader(ltr);
			modules.addAll(reader.readModules());

			List<INode> nodes = new Vector<INode>();
			for (AModuleModules module : modules)
			{
				nodes.add(module);
			}
			if (nodes.size() > 0)
			{
				result.setAst(nodes);
			} else
			{
				result.setFatalError(new Exception("No VDM source in file"));
			}

		} catch (InternalException e)
		{
			result.setFatalError(e);
		} catch (Throwable e)
		{
			e.printStackTrace();
			result.setFatalError(e);
		}finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}

		if (reader != null && reader.getErrorCount() > 0)
		{
			result.setErrors(reader.getErrors());
		}

		if (reader != null && reader.getWarningCount() > 0)
		{
			result.setWarnings(reader.getWarnings());
		}

		return result;
	}

}
