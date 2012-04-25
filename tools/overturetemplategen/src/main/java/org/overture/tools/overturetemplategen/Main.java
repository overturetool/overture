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
package org.overture.tools.overturetemplategen;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;


public class Main
{

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		Reader reader = new Reader();
		
		TemplateList list = reader.read(new File(args[0]));
		System.out.println(list);
		
//		System.out.println(list.getPpList().getTemplateFileContent());

		File tmp = new File("tmp");
		
		writeFile(new File(new File(tmp,"sl"),"templates.xml"), list.getSlList().getTemplateFileContent());
		writeFile(new File(new File(tmp,"pp"),"templates.xml"), list.getPpList().getTemplateFileContent());
		writeFile(new File(new File(tmp,"rt"),"templates.xml"), list.getRtList().getTemplateFileContent());
		
		
		System.out.println("------------------------------");
		System.out.println(list.toLatexTable());
	}

	public static void writeFile(File file, String content)
	{
		 Writer writer = null;

	        try
	        {
	           file.getParentFile().mkdirs();
	            writer = new BufferedWriter(new FileWriter(file));
	            writer.write(content);
	        } catch (FileNotFoundException e)
	        {
	            e.printStackTrace();
	        } catch (IOException e)
	        {
	            e.printStackTrace();
	        } finally
	        {
	            try
	            {
	                if (writer != null)
	                {
	                    writer.close();
	                }
	            } catch (IOException e)
	            {
	                e.printStackTrace();
	            }
	        }
	}
}
