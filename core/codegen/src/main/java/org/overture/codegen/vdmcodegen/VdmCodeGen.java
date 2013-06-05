package org.overture.codegen.vdmcodegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.Velocity;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.codegen.cgast.AClassTypeDeclCG;
import org.overture.codegen.constants.IText;
import org.overture.codegen.logging.DefaultLogger;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.visitor.CodeGenVisitor;

public class VdmCodeGen
{
	private ILogger log;

	public VdmCodeGen()
	{
		init(null);
	}

	public VdmCodeGen(ILogger log)
	{
		init(log);
	}

	private void init(ILogger log)
	{
		initVelocity();

		if (log == null)
			this.log = new DefaultLogger();
		else
			this.log = log;
	}

	private void initVelocity()
	{
		String propertyPath = VdmCodeGenUtil.getVelocityPropertiesPath("velocity.properties");
		Velocity.init(propertyPath);
	}

	public void generateCode(List<SClassDefinition> mergedParseLists)
			throws AnalysisException
	{
		// take default Eclipse formatting options
		@SuppressWarnings("unchecked")
		Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

		// initialize the compiler settings to be able to format 1.5 code
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_7);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);

		// change the option to wrap each enum constant on a new line
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS, DefaultCodeFormatterConstants.createAlignmentValue(true, DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE, DefaultCodeFormatterConstants.INDENT_ON_COLUMN));

		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);

		CodeGenVisitor codeGenVisitor = new CodeGenVisitor(log);

		for (INode node : mergedParseLists)
		{
			node.apply(codeGenVisitor);
		}

		ArrayList<AClassTypeDeclCG> classes = codeGenVisitor.getClasses();
		MergeVisitor mergeVisitor = new MergeVisitor();

		String utilsPath = "src\\main\\java\\org\\overture\\codegen\\generated\\collections";
		String targetr = "target\\sources";
		
		for (AClassTypeDeclCG classCg : classes)
		{
			try
			{
				StringWriter writer = new StringWriter();
				classCg.apply(mergeVisitor, writer);
				String code = writer.toString();

				System.out.println("File: " + classCg.getName());

				TextEdit textEdit = codeFormatter.format(CodeFormatter.K_UNKNOWN, code, 0, code.length(), 0, null);
				IDocument doc = new Document(code);
				try
				{
					textEdit.apply(doc);
					System.out.println(doc.get());
					saveClass(classCg.getName() + ".java", doc.get());
				} catch (MalformedTreeException e)
				{
					e.printStackTrace();
				} catch (BadLocationException e)
				{
					e.printStackTrace();
				}

				copyDirectory(new File(utilsPath), new File(targetr));
				
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		replaceInFile(targetr + "\\Utils.java", "package org.overture.codegen.generated.collections;", "");
	}

	public void saveClass(String javaFileName, String code)
	{
		try
		{
			new File("target\\sources\\").mkdirs();
			String file_name = "target\\sources\\" + javaFileName;
			FileWriter file = new FileWriter(file_name);
			BufferedWriter out = new BufferedWriter(file);
			out.write(code);
			out.close();

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void replaceInFile(String filePath, String regex, String replacement)
	{
        try
        {
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = "", oldtext = "";
        while((line = reader.readLine()) != null)
            {
            oldtext += line + IText.NEW_LINE;
        }
        reader.close();
        // replace a word in a file
        //String newtext = oldtext.replaceAll("drink", "Love");
        //To replace a line in a file
        String newtext = oldtext.replaceAll(regex, replacement);
       
        FileWriter writer = new FileWriter(filePath);
        writer.write(newtext);
        writer.close();
    }
    catch (IOException ioe)
        {
        ioe.printStackTrace();
    }
	}

	public void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException
	{
		if (sourceLocation.isDirectory())
		{
			if (!targetLocation.exists())
			{
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++)
			{
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} else
		{

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	// public void save(CodeGenContextMap contextMap)
	// {
	//
	// java.net.URI absolutePath = vdmProject.getModelBuildPath().getOutput().getLocationURI();//
	// iFile.getLocationURI();
	// URL url;
	// try
	// {
	// url = FileLocator.toFileURL(absolutePath.toURL());
	// File file = new File(url.toURI());
	// } catch (IOException e)
	// {
	// e.printStackTrace();
	// } catch (URISyntaxException e)
	// {
	// e.printStackTrace();
	// }

	// ********** COMMENT BELOW BACK IN:

	// Set<String> set = contextMap.getContextKeys();
	//
	// Template template = Vdm2CppUtil.getTemplate("class.vm");
	//
	// if (template == null)
	// {
	// return;
	// }
	// PrintWriter out = new PrintWriter(System.out);
	// for (String classDef : set)
	// {
	// template.merge(contextMap.getContext(classDef).getVelocityContext(), out);
	// out.flush();
	// out.println();
	// }
	// }

}
