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
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.utils.NameUtil;

public class SourceFileWriter
{
	public static void write(File outputFolder, Environment env,
			boolean generateVdm)
	{
		if (generateVdm)
		{
			File generatedVdm = new File(new File(new File(outputFolder, "vdm"), "generated"), "node");
			generatedVdm.mkdirs();
		}
		outputFolder.mkdirs();

		System.out.println("Copying base classes to destination...");
		copyBaseClasses(outputFolder, env.getDefaultPackage(), env.getAnalysisPackage(), env);
		System.out.println("Writing source files.:");
		long startTime = System.currentTimeMillis();
		int i = 80;
		for (IInterfaceDefinition def : env.getAllDefinitions())
		{
			if (def instanceof PredefinedClassDefinition
					|| def instanceof ExternalJavaClassDefinition)
			{
				continue;
			}
			System.out.print(/* def.getSignatureName()+"..." */".");
			// System.out.println(def.getName());
			System.out.flush();
			i--;
			if (i == 0)
			{
				i = 80;
				System.out.println();
			}
			SourceFileWriter.write(outputFolder, def);
			// SourceFileWriter.write(generatedVdm, def, false);
		}
		long endTime = System.currentTimeMillis();
		System.out.println();
		System.out.println("File write completed in " + (endTime - startTime)
				/ 1000 + " secs");

	}

	private static void copyBaseClasses(File generated, String defaultPackage,
			String analysisPackageName, Environment env)
	{
		Map<String, String> replace = new Hashtable<String, String>();
		replace.put("//COPYRIGHT", IInterfaceDefinition.copurightHeader);

		replace.put("%INode%", env.iNode.getName().getName());
		replace.put("%Node%", env.node.getName().getName());
		replace.put("%IToken%", env.iToken.getName().getName());
		replace.put("%Token%", env.token.getName().getName());
		replace.put("%NodeList%", env.nodeList.getName().getName());
		replace.put("%GraphNodeList%", env.graphNodeList.getName().getName());

		replace.put("%NodeListList%", env.nodeListList.getName().getName());
		replace.put("%GraphNodeListList%", env.graphNodeListList.getName().getName());
		replace.put("%ExternalNode%", env.externalNode.getName().getName());
		replace.put("%generated.node%", defaultPackage);

		replace.put("%org.overture.ast.analysis%", analysisPackageName);

		replace.put("%org.overture.ast.analysis.IAnalysis%", env.getTaggedDef(env.TAG_IAnalysis).getName().getCanonicalName());
		replace.put("%org.overture.ast.analysis.IAnswer%", env.getTaggedDef(env.TAG_IAnswer).getName().getCanonicalName());
		replace.put("%org.overture.ast.analysis.IQuestion%", env.getTaggedDef(env.TAG_IQuestion).getName().getCanonicalName());
		replace.put("%org.overture.ast.analysis.IQuestionAnswer%", env.getTaggedDef(env.TAG_IQuestionAnswer).getName().getCanonicalName());

		replace.put("%IAnalysis%", env.getTaggedDef(env.TAG_IAnalysis).getName().getName());
		replace.put("%IAnswer<A>%", NameUtil.getGenericName(env.getTaggedDef(env.TAG_IAnswer)));
		replace.put("%IQuestion<Q>%", NameUtil.getGenericName(env.getTaggedDef(env.TAG_IQuestion)));
		replace.put("%IQuestionAnswer<Q,A>%", NameUtil.getGenericName(env.getTaggedDef(env.TAG_IQuestionAnswer)));

		replace.put("%IAnswer%", env.getTaggedDef(env.TAG_IAnswer).getName().getName());
		replace.put("%IQuestion%", env.getTaggedDef(env.TAG_IQuestion).getName().getName());
		replace.put("%IQuestionAnswer%", env.getTaggedDef(env.TAG_IQuestionAnswer).getName().getName());

		replace.put("%NodeEnum%", "NodeEnum"
				+ env.node.getName().getName().replace("Node", ""));

		copy(generated, "INode.java", replace, defaultPackage);
		copy(generated, "Node.java", replace, defaultPackage);
		copy(generated, "IToken.java", replace, defaultPackage);
		copy(generated, "Token.java", replace, defaultPackage);
		copy(generated, "NodeList.java", replace, defaultPackage);
		copy(generated, "NodeListList.java", replace, defaultPackage);
		copy(generated, "GraphNodeList.java", replace, defaultPackage);
		copy(generated, "GraphNodeListList.java", replace, defaultPackage);
		copy(generated, "ExternalNode.java", replace, defaultPackage);
	}

	private static void copy(File generated, String name,
			Map<String, String> replaceTemplates, String packageName)
	{
		InputStream fis = null;

		try
		{

			fis = Generator.class.getResourceAsStream("/" + name);

			if (fis == null)
			{
				fis = new FileInputStream(name);

			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

			String text = null;
			StringBuffer buf = new StringBuffer();

			// repeat until all lines is read
			while ((text = reader.readLine()) != null)
			{
				buf.append(text + "\n");
			}

			String data = buf.toString();
			for (Entry<String, String> entry : replaceTemplates.entrySet())
			{
				data = data.replaceAll(entry.getKey(), entry.getValue());
			}

			String outputFileName = "";
			if (data.contains(" class "))
			{
				outputFileName = data.substring(data.indexOf(" class ")
						+ " class ".length());
			} else if (data.contains("public interface "))
			{
				outputFileName = data.substring(data.indexOf(" interface ")
						+ " interface ".length());
			} else
			{
				System.err.println("Unable to determin file name for:\n\n______________________________________________________________\n"
						+ data);
			}
			outputFileName = outputFileName.substring(0, outputFileName.indexOf(' ')).trim();
			if (outputFileName.contains("<"))
			{
				outputFileName = outputFileName.substring(0, outputFileName.indexOf('<'));
			}

			if (outputFileName.contains("{"))
			{
				outputFileName = outputFileName.substring(0, outputFileName.indexOf('{')).replace('\n', ' ').replace('\r', ' ').trim();
			}

			File output = createFolder(generated, packageName);
			output.mkdirs();

			OutputStream out = new FileOutputStream(new File(output, outputFileName
					+ ".java"));
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
			File output = createFolder(generated, def.getName().getPackageName());
			if (writeJava)
			{
				name = def.getName().getName();
				content = def.getJavaSourceCode(new StringBuilder());
			} else
			{
				InterfaceDefinition.VDM = true;
				String tmp = Field.fieldPrefic;
				Field.fieldPrefic = "m_";
				content = def.getVdmSourceCode(new StringBuilder());
				Field.fieldPrefic = tmp;
				name = def.getName().getName();
				InterfaceDefinition.VDM = false;
			}

			if (content == null || content.isEmpty())
			{
				return;
			}

			FileWriter outFile = new FileWriter(new File(output, getFileName(name)
					+ (writeJava ? ".java" : ".vdmpp")));
			PrintWriter out = new PrintWriter(outFile);

			out.write(content);
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
			if (output == null)
			{
				output = new File(src, s);
			} else
			{
				output = new File(output, s);
			}
		}

		if (output == null)
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
