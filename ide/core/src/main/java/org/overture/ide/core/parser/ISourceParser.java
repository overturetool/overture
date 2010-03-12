package org.overture.ide.core.parser;

import org.eclipse.core.resources.IFile;

public interface ISourceParser {

	/**
	 * Parse a single file
	 * @param file the file to be parsed
	 */
	void parse(IFile file);
	
	/**
	 * Parse a single file where the content is parsed and the file is set as the source file
	 * @param file the file to be set as source
	 * @param content the content to be parsed
	 */
	void parse(IFile file,String content);

}
