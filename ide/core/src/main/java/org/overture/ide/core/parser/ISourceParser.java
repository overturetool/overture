package org.overture.ide.core.parser;

import org.eclipse.core.resources.IFile;

public interface ISourceParser {

	void parse(char[] charArray, char[] source, IFile file);
	void parse(IFile file);
	void parse(IFile file,String data);

}
