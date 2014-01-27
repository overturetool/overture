package org.overture.interpreter.messages.rtlog;

import java.io.File;
import java.io.FileNotFoundException;

public interface IRTLogger
{

	public abstract void enable(boolean on);

	public abstract void log(RTMessage message);

	public abstract void setLogfile(File file) throws FileNotFoundException;

	public abstract int getLogSize();

	public abstract void dump(boolean close);

}