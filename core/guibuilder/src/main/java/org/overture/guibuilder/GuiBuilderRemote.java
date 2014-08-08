/*******************************************************************************
 * Copyright (c) 2009, 2013 Overture Team and others.
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
package org.overture.guibuilder;

import java.awt.EventQueue;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;

import org.overture.config.Settings;
import org.overture.guibuilder.generated.swixml.schema.ObjectFactory;
import org.overture.guibuilder.internal.ToolSettings;
import org.overture.guibuilder.internal.UiInterface;
import org.overture.guibuilder.internal.VdmjVdmInterpreterWrapper;
import org.overture.guibuilder.internal.ir.IVdmClassReader;
import org.overture.guibuilder.internal.ir.VdmjVdmClassReader;
import org.overture.interpreter.debug.RemoteControl;
import org.overture.interpreter.debug.RemoteInterpreter;
import org.overture.interpreter.runtime.ClassInterpreter;

public class GuiBuilderRemote implements RemoteControl
{

	// "my.swixml.schema"
	public static final String GENERATED_PACKAGE = org.overture.guibuilder.generated.swixml.schema.Applet.class.getPackage().getName();

	static class PW extends VdmjVdmInterpreterWrapper
	{

		private RemoteInterpreter interpreter;

		public PW(RemoteInterpreter intterpreter) throws Exception
		{
			super(Settings.dialect);
			interpreter = intterpreter;
		}

		@Override
		public void parseFilesAndTypeCheck(
				@SuppressWarnings("rawtypes") List filenames)
		{
		}

		@Override
		public void initInterpreter() throws Exception
		{
			interpreter.init();
		}

		@Override
		public String getValueOf(String line) throws Exception
		{
			return execute(line);
		}

		@Override
		public String execute(String cmd) throws Exception
		{
			return interpreter.execute(cmd);
		}

		@Override
		public void createInstance(String var, String expr) throws Exception
		{
			interpreter.create(var, expr);
		}

	}

	@Override
	public void run(final RemoteInterpreter interpreter) throws Exception
	{
		// Object d = new SwiXMLGenerator();
		{
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			JAXBContext jaxbContext = JAXBContext.newInstance(GENERATED_PACKAGE);
			/* Marshaller marshaller = */jaxbContext.createMarshaller();
			/* ObjectFactory objFactory = */new ObjectFactory();
		}
		EventQueue.invokeLater(new Runnable()
		{

			public void run()
			{
				try
				{
					IVdmClassReader reader = new VdmjVdmClassReader(((ClassInterpreter) interpreter.getInterpreter()).getClasses());
					Vector<File> files = new Vector<File>();
					for (File file : interpreter.getInterpreter().getSourceFiles())
					{
						if (file.getAbsolutePath().contains(File.separatorChar
								+ "lib" + File.separatorChar))
						{
							continue;
						}
						files.add(file);
					}
					reader.readFiles(files, interpreter.getInterpreter().getAssistantFactory());
					UiInterface ui = new UiInterface(new PW(interpreter));
					if (ToolSettings.GENERATE.booleanValue())
					{
						// if (ToolSettings.SAVE_XML.booleanValue())
						ui.buildAndRender(reader, "Unknown", files.get(0).getParentFile().getAbsolutePath()
								+ File.separatorChar);
						// else
						// ui.buildAndRender(reader, "Unknown");
					}
					ui.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
