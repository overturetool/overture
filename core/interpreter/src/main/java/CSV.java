import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.IntegerValue;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ExpressionReader;

/**
 * Basic CSV file support for VDM
 * 
 * @author kela
 */
public class CSV extends IO implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static String lastError = "";

	public static Value ferror()
	{
		return new SeqValue(lastError);
	}

	/**
	 * Writes a seq of ? in a CSV format to the file specified
	 * 
	 * @param fval
	 *            the filename
	 * @param tval
	 *            the sequence to write
	 * @param dval
	 *            append to or start a new file
	 * @return
	 */
	public static Value fwriteval(Value fval, Value tval, Value dval)
	{
		File file = getFile(fval);
		String fdir = dval.toString(); // <start>|<append>
		StringBuffer text = new StringBuffer();
		if (tval instanceof SeqValue)
		{
			for (Value val : ((SeqValue) tval).values)
			{
				text.append(val.toString());
				text.append(",");
			}

		}

		if (text.length() > 0 && text.charAt(text.length() - 1) == ',')
		{
			text.deleteCharAt(text.length() - 1);
		}
		text.append('\n');

		try
		{
			FileOutputStream fos = new FileOutputStream(file, fdir.equals("<append>"));

			fos.write(text.toString().getBytes(Console.charset));
			fos.close();
		} catch (IOException e)
		{
			lastError = e.getMessage();
			return new BooleanValue(false);
		}

		return new BooleanValue(true);
	}

	/**
	 * Read a CSV live as a seq of ? in VDM
	 * 
	 * @param fval
	 *            name of the file to read from
	 * @param indexVal
	 *            the line index
	 * @return true + seq of ? or false and nil
	 */
	public static Value freadval(Value fval, Value indexVal)
	{
		ValueList result = new ValueList();

		try
		{
			File file = getFile(fval);
			long index = indexVal.intValue(null);
			SeqValue lineCells = new SeqValue();

			boolean success = false;
			try
			{
				for (String value : getLine(file, index))
				{
					lineCells.values.add(createValue("CSV", "freadval", value));
				}
				success = true;
			} catch (Exception e)
			{
				success = false;
				lastError = e.getMessage();
				// OK
			}

			result.add(new BooleanValue(success));
			result.add(lineCells);
		} catch (Exception e)
		{
			lastError = e.toString();
			result = new ValueList();
			result.add(new BooleanValue(false));
			result.add(new NilValue());
		}

		return new TupleValue(result);
	}

	/**
	 * Gets the line count of the CSV file
	 * 
	 * @param fval
	 *            name of the file
	 * @return int value with count
	 */
	public static Value flinecount(Value fval)
	{
		ValueList result = new ValueList();

		try
		{
			File file = getFile(fval);
			long count = getLineCount(file);

			result.add(new BooleanValue(true));
			result.add(new IntegerValue(count));
		} catch (Exception e)
		{
			lastError = e.toString();
			result = new ValueList();
			result.add(new BooleanValue(false));
			result.add(new NilValue());
		}

		return new TupleValue(result);
	}

	private static int getLineCount(File file) throws IOException
	{
		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		int lines = 0;
		try
		{
			while (bufRdr.readLine() != null)
			{
				lines++;
			}
		} finally
		{
			bufRdr.close();
		}
		return lines;
	}

	private static List<String> getLine(File file, long index)
			throws IOException
	{
		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = null;
		int lineIndex = 0;
		List<String> cells = new ArrayList<String>();

		if (index < 1)
		{
			bufRdr.close();
			throw new IOException("CSV line index before first entry");
		}

		try
		{
			while ((line = bufRdr.readLine()) != null)
			{
				lineIndex++;
				if (lineIndex == index)
				{
					StringTokenizer st = new StringTokenizer(line, ",");
					while (st.hasMoreTokens())
					{
						cells.add(st.nextToken());
					}
					break;
				}

			}
		} finally
		{
			bufRdr.close();
		}

		if (cells.isEmpty())
		{
			throw new IOException("CSV no data read. Empty line.");
		}

		return cells;
	}

	private static Value createValue(String module, String method, String value)
			throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(value, Dialect.VDM_PP);
		ExpressionReader reader = new ExpressionReader(ltr);
		reader.setCurrentModule(module);
		PExp exp = reader.readExpression();

		Interpreter ip = Interpreter.getInstance();
		ip.typeCheck(exp, ip.getGlobalEnvironment());

		Context ectxt = new Context(ip.getAssistantFactory(), null, method, null);
		ectxt.setThreadState(null, CPUValue.vCPU);
		return exp.apply(VdmRuntime.getExpressionEvaluator(), ectxt);
	}
}
