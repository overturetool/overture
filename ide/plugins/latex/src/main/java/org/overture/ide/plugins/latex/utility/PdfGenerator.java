package org.overture.ide.plugins.latex.utility;

public interface PdfGenerator
{

	void setFail(boolean b);

	void start();

	boolean isFinished();

	boolean hasFailed();

	void kill();

}
