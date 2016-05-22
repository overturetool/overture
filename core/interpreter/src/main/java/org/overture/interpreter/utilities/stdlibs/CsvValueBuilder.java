package org.overture.interpreter.utilities.stdlibs;

import org.overture.interpreter.values.Value;

public interface CsvValueBuilder
{
	public Value createValue(String value) throws Exception;
}
