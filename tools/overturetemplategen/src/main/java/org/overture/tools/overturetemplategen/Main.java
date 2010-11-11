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
