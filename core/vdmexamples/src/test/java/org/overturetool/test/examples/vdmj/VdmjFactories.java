/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overturetool.test.examples.vdmj;

import java.io.File;
import java.util.List;

import org.overturetool.test.framework.examples.IMessage;
import org.overturetool.test.framework.examples.IResultCombiner;
import org.overturetool.test.framework.examples.Message;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.VDMMessage;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.syntax.SyntaxReader;

public class VdmjFactories
{
	public interface ParserFactory<T extends SyntaxReader, R> extends IMessageConverter
	{
		R read(T reader) throws ParserException, LexException;

		T createReader(LexTokenReader ltr);

		LexTokenReader createTokenReader(File file);

	}
	
	public interface IMessageConverter{
		IMessage convertMessage(Object m);
	}

	public final static ParserFactory<ClassReader, List<ClassDefinition>> vdmPpParserfactory = new ParserFactory<ClassReader, List<ClassDefinition>>()
	{

		public List<ClassDefinition> read(ClassReader reader)
				throws ParserException, LexException
		{
			return reader.readClasses();
		}

		public ClassReader createReader(LexTokenReader ltr)
		{
			return new ClassReader(ltr);
		}

		public IMessage convertMessage(Object m)
		{
			VDMMessage msg = (VDMMessage) m;
			return new Message(msg.location.file.getName(),msg.number, msg.location.startLine, msg.location.endPos, msg.message);
		}

		public LexTokenReader createTokenReader(File file)
		{
			return new LexTokenReader(file, Settings.dialect);
		}
	};

	public final static ParserFactory<ModuleReader, List<Module>> vdmSlParserfactory = new ParserFactory<ModuleReader, List<Module>>()
	{

		public List<Module> read(ModuleReader reader) throws ParserException,
				LexException
		{
			return reader.readModules();
		}

		public ModuleReader createReader(LexTokenReader ltr)
		{
			return new ModuleReader(ltr);
		}

		public IMessage convertMessage(Object m)
		{
			VDMMessage msg = (VDMMessage) m;
			return new Message(msg.location.file.getName(),msg.number, msg.location.startLine, msg.location.endPos, msg.message);
		}

		public LexTokenReader createTokenReader(File file)
		{
			return new LexTokenReader(file, Settings.dialect);
		}
	};

	public static IResultCombiner<List<ClassDefinition>> vdmPpParserResultCombiner = new IResultCombiner<List<ClassDefinition>>()
	{

		public List<ClassDefinition> combine(List<ClassDefinition> a,
				List<ClassDefinition> b)
		{
			if(a== null)
			{
				return b;
			}
			a.addAll(b);
			return a;
		}
	};

	public static IResultCombiner<List<Module>> vdmSlParserResultCombiner = new IResultCombiner<List<Module>>()
	{

		public List<Module> combine(List<Module> a, List<Module> b)
		{
			if(a== null)
			{
				return b;
			}
			a.addAll(b);
			
			return a;
		}
	};
}
