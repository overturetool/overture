package org.overture.ide.plugins.latex.utility;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.overture.ast.lex.Dialect;

public interface PdfBuilder
{

	public abstract void prepare(IProject project, Dialect dialect)
			throws IOException;

	public abstract void saveDocument(IProject project, File projectRoot,
			String name) throws IOException;

	public abstract void addInclude(String path);

}