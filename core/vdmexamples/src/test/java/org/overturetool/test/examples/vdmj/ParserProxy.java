package org.overturetool.test.examples.vdmj;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.overturetool.test.examples.vdmj.VdmjFactories.ParserFactory;
import org.overturetool.test.framework.examples.IMessage;
import org.overturetool.test.framework.examples.Result;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.SyntaxReader;

public class ParserProxy<T extends SyntaxReader, R>
{
	final ParserFactory<T, R> factory;
	final Set<File> files = new HashSet<File>();

	public ParserProxy(ParserFactory<T, R> reader, Set<File> files)
	{
		this.factory = reader;
		this.files.clear();
		this.files.addAll(files);
	}

	public Set<Result<R>> parse() throws Exception
	{
		Set<Result<R>> completeResult = new HashSet<Result<R>>();

		for (File file : files)
		{
			T reader = null;
			R result = null;
			Set<IMessage> warnings = new HashSet<IMessage>();
			Set<IMessage> errors = new HashSet<IMessage>();
			reader = factory.createReader(factory.createTokenReader(file));
			result = factory.read(reader);

			for (VDMError m : reader.getErrors())
			{
				errors.add(factory.convertMessage(m));
			}

			for (VDMWarning m : reader.getWarnings())
			{
				errors.add(factory.convertMessage(m));
			}

			completeResult.add(new Result<R>(result, warnings, errors));
		}
		return completeResult;
	}
}
