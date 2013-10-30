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
import org.overture.ast.expressions.PExp;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.constants.IText;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.visitor.CodeGenerator;

public class CodeGen
{
	private CodeGenerator generator;

	public CodeGen()
	{
		init(null);
	}

	public CodeGen(ILogger log)
	{
		init(log);
	}

	private void init(ILogger log)
	{
		initVelocity();
		this.generator = new CodeGenerator(log);
	}

	private void initVelocity()
	{
		
		//String propertyPath = CodeGenUtil.getVelocityPropertiesPath("velocity.properties");
		Velocity.init();
		//Velocity.init(propertyPath);
	}

	public GeneratedModule generateQuotes()
	{
		try
		{
			MergeVisitor mergeVisitor = new MergeVisitor();
			CodeFormatter codeFormatter = constructCodeFormatter();
			StringWriter writer = new StringWriter();
			
			AInterfaceDeclCG quotesInterface = generator.getQuotes();
			
			if(quotesInterface.getFields().size() == 0)
				return null; //Nothing to generate
			
			quotesInterface.apply(mergeVisitor, writer);
			String code = writer.toString();
			
			TextEdit textEdit = codeFormatter.format(CodeFormatter.K_UNKNOWN, code, 0, code.length(), 0, null);
			IDocument doc = new Document(code);
			try
			{
				textEdit.apply(doc);
				return new GeneratedModule(quotesInterface.getName(), doc.get());
			} catch (MalformedTreeException e)
			{
				e.printStackTrace();
			} catch (BadLocationException e)
			{
				e.printStackTrace();
			}

		} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<GeneratedModule> generateCode(List<SClassDefinition> mergedParseLists) throws AnalysisException
	{
		List<AClassDeclCG> classes = new ArrayList<AClassDeclCG>();
		CodeFormatter codeFormatter = constructCodeFormatter();

		for (SClassDefinition classDef : mergedParseLists)
		{
			classes.add(generator.generateFrom(classDef));
		}
		
		MergeVisitor mergeVisitor = new MergeVisitor();
		StringWriter writer = new StringWriter();

		List<GeneratedModule> generated = new ArrayList<GeneratedModule>();
		for (AClassDeclCG classCg : classes)
		{
			try
			{
				classCg.apply(mergeVisitor, writer);
				String code = writer.toString();
				
				TextEdit textEdit = codeFormatter.format(CodeFormatter.K_UNKNOWN, code, 0, code.length(), 0, null);
				IDocument doc = new Document(code);
				try
				{
					if(textEdit == null)
					{
						Logger.getLog().printErrorln("Could not format generated code for class: " + classCg.getName());
						break;
					}
					
					textEdit.apply(doc);
					generated.add(new GeneratedModule(classCg.getName(), doc.get()));

				} catch (MalformedTreeException e)
				{
					e.printStackTrace();
				} catch (BadLocationException e)
				{
					e.printStackTrace();
				}

			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Error when generating code for class: " + classCg.getName());
				Logger.getLog().printErrorln("Skipping class..");
			}
			
			writer = new StringWriter();
		}
		
		return generated;
	}

	public void generateSourceFiles(List<GeneratedModule> generatedClasses)
	{
		for (GeneratedModule classCg : generatedClasses)
		{
			saveClass(classCg.getName() + ".java", classCg.getContent());
		}
	}

	public void generateCodeGenUtils()
	{
		String utilsPath = "src\\main\\java\\org\\overture\\codegen\\generated\\collections";
		String targetr = "target\\sources";

		try
		{
			copyDirectory(new File(utilsPath), new File(targetr));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		replaceInFile(targetr + "\\Utils.java", "package org.overture.codegen.generated.collections;", "");
	}

	public String generateCode(PExp exp) throws AnalysisException
	{
		PExpCG expCg = generator.generateFrom(exp);

		MergeVisitor mergeVisitor = new MergeVisitor();
		StringWriter writer = new StringWriter();

		try
		{
			expCg.apply(mergeVisitor, writer);
			String code = writer.toString();

			return code;
		} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public CodeFormatter constructCodeFormatter()
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

		return ToolFactory.createCodeFormatter(options);

	}

	private void saveClass(String javaFileName, String code)
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

	private void replaceInFile(String filePath, String regex, String replacement)
	{
		try
		{
			File file = new File(filePath);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			while ((line = reader.readLine()) != null)
			{
				oldtext += line + IText.NEW_LINE;
			}
			reader.close();
			String newtext = oldtext.replaceAll(regex, replacement);

			FileWriter writer = new FileWriter(filePath);
			writer.write(newtext);
			writer.close();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	private void copyDirectory(File sourceLocation, File targetLocation)
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
}
