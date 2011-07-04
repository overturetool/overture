package com.lausdahl.ast.creator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import com.lausdahl.ast.creator.definitions.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;
import com.lausdahl.ast.creator.definitions.PredefinedClassDefinition;

public class SourceFileWriter
{
	public static void write(File outputFolder, Environment env,String defaultPackage,String analysisPackageName)
	{
		File generatedVdm = new File(new File(new File(outputFolder, "vdm"), "generated"), "node");
		outputFolder.mkdirs();
		generatedVdm.mkdirs();
		
		System.out.println("Copying base classes to destination...");
		copyBaseClasses(outputFolder,defaultPackage,analysisPackageName,env);
		System.out.println("Writing source files.:");
		int i=80;
		for (IInterfaceDefinition def : env.getAllDefinitions())
		{
			if (def instanceof PredefinedClassDefinition
					|| def instanceof ExternalJavaClassDefinition)
			{
				continue;
			}
			System.out.print(/*def.getSignatureName()+"..."*/".");
			i--;
			if(i==0)
			{
				i=80;
				System.out.println();
			}
			SourceFileWriter.write(outputFolder, def);
//			SourceFileWriter.write(generatedVdm, def, false);
		}
		System.out.println();
	}
	
	private static void copyBaseClasses(File generated,String defaultPackage, String analysisPackageName,Environment env)
	{
		Map<String,String> replace = new Hashtable<String, String>();
		replace.put("%Node%", env.node.getName());
		replace.put("%Token%", env.token.getName());
		replace.put("%NodeList%", env.nodeList.getName());
		replace.put("%ExternalNode%", env.externalNode.getName());
		replace.put("%generated.node%",defaultPackage );
		
		replace.put("%org.overture.ast.analysis%",analysisPackageName );
		replace.put("%IAnalysis%",env.getTaggedDef(env.TAG_IAnalysis).getName() );
		replace.put("%IAnswer<A>%",env.getTaggedDef(env.TAG_IAnswer).getName());
		replace.put("%IQuestion<Q>%",env.getTaggedDef(env.TAG_IQuestion).getName());
		replace.put("%IQuestionAnswer<Q,A>%",env.getTaggedDef(env.TAG_IQuestionAnswer).getName());
		
		replace.put("%IAnswer%",env.getTaggedDef(env.TAG_IAnswer).getSignatureName());
		replace.put("%IQuestion%",env.getTaggedDef(env.TAG_IQuestion).getSignatureName());
		replace.put("%IQuestionAnswer%",env.getTaggedDef(env.TAG_IQuestionAnswer).getSignatureName());
		
		replace.put("%NodeEnum%","NodeEnum"+env.node.getName().replace("Node", ""));
		
		
		
		
		
		copy(generated, "Node.java",replace,defaultPackage);
		copy(generated, "Token.java",replace,defaultPackage);
		copy(generated, "NodeList.java",replace,defaultPackage);
		copy(generated, "ExternalNode.java",replace,defaultPackage);
	}
	
	private static void copy(File generated, String name,Map<String,String> replaceTemplates, String packageName)
	{
//		File output = new File(new File(generated, "generated"), "node");
		
		InputStream fis = null;

		try
		{

			fis = Generator.class.getResourceAsStream("/"+ name);

			if (fis == null)
			{
				fis = new FileInputStream(name);

			}
			
			
//			byte[] buffer = new byte[4096];
//			for (int n; (n = fis.read(buffer)) != -1;)
//			{
//				out.write(buffer, 0, n);
//			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			
			String text = null;
			StringBuffer buf = new StringBuffer();

            // repeat until all lines is read
            while ((text = reader.readLine()) != null) {
               buf.append(text+"\n");
            }
            
            String data = buf.toString();
            for (Entry<String, String> entry : replaceTemplates.entrySet())
			{
				data = data.replaceAll(entry.getKey(), entry.getValue());
			}
            
            String outputFileName = "";
            if(data.contains("class "))
            {            
            	outputFileName=   data.substring(data.indexOf(" class ")+" class ".length());
            }else
            {
            	outputFileName=	data.substring(data.indexOf(" interface ")+" interface ".length());
            }
            outputFileName = outputFileName.substring(0,outputFileName.indexOf(' ')).trim();
            if(outputFileName.contains("<"))
            {
            	outputFileName=outputFileName.substring(0,outputFileName.indexOf('<'));
            }
            
            
            File output = createFolder(generated, packageName);
            output.mkdirs();
            
            OutputStream out = new FileOutputStream(new File(output, outputFileName+".java"));
			out.write(data.getBytes());
			
			out.close();
			fis.close();

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void write(File generated, IInterfaceDefinition def)
	{
		write(generated, def, true);
	}
	
	private static void write(File generated, IInterfaceDefinition def,
			boolean writeJava)
	{
		try
		{
			String name = null;
			String content = "";
			File output = createFolder(generated, def.getPackageName());
			if (writeJava)
			{
				name = def.getName();
				content = def.getJavaSourceCode();
			} else
			{
				InterfaceDefinition.VDM = true;
				String tmp = Field.fieldPrefic;
				Field.fieldPrefic = "m_";
				content = def.getVdmSourceCode();
				Field.fieldPrefic = tmp;
				name = def.getName();
				InterfaceDefinition.VDM = false;
			}

			if (content == null || content.trim().length() == 0)
			{
				return;
			}

			FileWriter outFile = new FileWriter(new File(output, getFileName(name)
					+ (writeJava ? ".java" : ".vdmpp")));
			PrintWriter out = new PrintWriter(outFile);

			out.println(content);
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	private static File createFolder(File src, String packageName)
	{
		File output = null;
		for (String s : packageName.split("\\."))
		{
			if(output==null)
			{
			output = new File(src,s);
			}else
			{
				output = new File(output,s);
			}
		}
		
		if(output == null)
		{
			output = src;
		}
		output.mkdirs();
		return output;
	}
	
	private static String getFileName(String name)
	{
		if (name.contains("<"))
		{
			return name.substring(0, name.indexOf('<'));
		}

		return name;
	}
}
