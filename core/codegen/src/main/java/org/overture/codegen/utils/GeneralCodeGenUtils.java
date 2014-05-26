package org.overture.codegen.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.logging.Logger;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class GeneralCodeGenUtils
{
	public static TypeCheckResult<List<SClassDefinition>> validateFile(File file) throws AnalysisException
	{
		if (!file.exists() || !file.isFile())
		{
			throw new AnalysisException("Could not find file: "
					+ file.getAbsolutePath());
		}

		ParserResult<List<SClassDefinition>> parseResult = ParserUtil.parseOo(file);

		if (parseResult.errors.size() > 0)
		{
			throw new AnalysisException("File did not parse: "
					+ file.getAbsolutePath());
		}

		TypeCheckResult<List<SClassDefinition>> typeCheckResult = TypeCheckerUtil.typeCheckPp(file);

		if (typeCheckResult.errors.size() > 0)
		{
			throw new AnalysisException("File did not pass the type check: "
					+ file.getName());
		}
		
		return typeCheckResult;

	}
	
	
	public static TypeCheckResult<PExp> validateExp(String exp) throws AnalysisException
	{
		if (exp == null || exp.isEmpty())
		{
			throw new AnalysisException("No expression to generate from");
		}

		ParserResult<PExp> parseResult = null;
		try
		{
			parseResult = ParserUtil.parseExpression(exp);
		} catch (Exception e)
		{
			throw new AnalysisException("Unable to parse expression: " + exp
					+ ". Message: " + e.getMessage());
		}

		if (parseResult.errors.size() > 0)
		{
			throw new AnalysisException("Unable to parse expression: " + exp);
		}

		TypeCheckResult<PExp> typeCheckResult = null;
		try
		{
			typeCheckResult = TypeCheckerUtil.typeCheckExpression(exp);
		} catch (Exception e)
		{
			throw new AnalysisException("Unable to type check expression: "
					+ exp + ". Message: " + e.getMessage());
		}
		
		return typeCheckResult;
	}
	
	public static void replaceInFile(String filePath, String regex, String replacement)
	{
		try
		{
			File file = new File(filePath);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			while ((line = reader.readLine()) != null)
			{
				oldtext += line + "\n";
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
	
	public static void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException
	{
		if(!targetLocation.exists())
		{
			targetLocation.getParentFile().mkdirs();
		}
	
		if (sourceLocation.isDirectory())
		{
			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++)
			{
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} else
		{
			targetLocation.createNewFile();
			
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
