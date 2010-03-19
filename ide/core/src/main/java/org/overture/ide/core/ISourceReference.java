package org.overture.ide.core;

import org.overturetool.vdmj.lex.LexLocation;

public interface ISourceReference extends IVdmElement
{
	LexLocation getSourceRange() throws VdmModelException;
}
