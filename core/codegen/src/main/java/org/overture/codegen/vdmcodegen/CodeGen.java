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

import org.apache.velocity.app.Velocity;
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
import org.overture.codegen.visitor.OoAstGenerator;

import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;

public class CodeGen
{
	private OoAstGenerator generator;

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
		this.generator = new OoAstGenerator(log);
	}

	private void initVelocity()
	{
		Velocity.setProperty("runtime.log.logsystem.class" , "org.apache.velocity.runtime.log.NullLogSystem");
		Velocity.init();
	}

	public GeneratedModule generateQuotes()
	{
		try
		{
			MergeVisitor mergeVisitor = new MergeVisitor();
			StringWriter writer = new StringWriter();

			AInterfaceDeclCG quotesInterface = generator.getQuotes();

			if (quotesInterface.getFields().size() == 0)
				return null; // Nothing to generate

			quotesInterface.apply(mergeVisitor, writer);
			String code = writer.toString();

			return new GeneratedModule(quotesInterface.getName(), formatCode(code));

		} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
		{
			Logger.getLog().printErrorln("Error when formatting quotes: "
					+ e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public List<GeneratedModule> generateCode(
			List<SClassDefinition> mergedParseLists) throws AnalysisException
	{
		List<AClassDeclCG> classes = new ArrayList<AClassDeclCG>();

		for (SClassDefinition classDef : mergedParseLists)
		{
			classes.add(generator.generateFrom(classDef));
		}

		MergeVisitor mergeVisitor = new MergeVisitor();

		List<GeneratedModule> generated = new ArrayList<GeneratedModule>();
		for (AClassDeclCG classCg : classes)
		{
			StringWriter writer = new StringWriter();
			try
			{
				classCg.apply(mergeVisitor, writer);
				String code = writer.toString();

				generated.add(new GeneratedModule(classCg.getName(), formatCode(code)));

			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Error when generating code for class "
						+ classCg.getName() + ": " + e.getMessage());
				Logger.getLog().printErrorln("Skipping class..");
				e.printStackTrace();
			}
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
			Logger.getLog().printErrorln("Error when generating CG utils");
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
			Logger.getLog().printErrorln("Could not generate expression: "
					+ exp);
			e.printStackTrace();
			return null;
		}
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
			Logger.getLog().printErrorln("Error when saving class file: "
					+ javaFileName);
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
			Logger.getLog().printErrorln("Error replacing characters in file: "
					+ filePath);
			ioe.printStackTrace();
		}
	}

	private String formatCode(String code)
	{
		StringBuffer b = new StringBuffer();
		try
		{
			File tempFile = new File("temp.java");
			FileWriter xwriter = new FileWriter(tempFile);
			xwriter.write(code.toString());
			xwriter.flush();

			Jalopy jalopy = new Jalopy();
			jalopy.setFileFormat(FileFormat.DEFAULT);
			jalopy.setInput(tempFile);
			jalopy.setOutput(b);
			jalopy.format();

			xwriter.close();
			tempFile.delete();

			if (jalopy.getState() == Jalopy.State.OK
					|| jalopy.getState() == Jalopy.State.PARSED)
				return b.toString();
			else if (jalopy.getState() == Jalopy.State.WARN)
				return null;// formatted with warnings
			else if (jalopy.getState() == Jalopy.State.ERROR)
				return null; // could not be formatted

		} catch (Exception e)
		{
			Logger.getLog().printErrorln("Could not format code: "
					+ e.toString());
			e.printStackTrace();
		}

		return null;// could not be formatted
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
