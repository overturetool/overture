package org.overturetool.util;

import java.util.List;
import java.util.Vector;

import org.overture.interpreter.ast.node.ConvertFactoryInterpreter;
import org.overturetool.interpreter.util.ClonableFile;
import org.overturetool.interpreter.vdmj.lex.LexBooleanToken;
import org.overturetool.interpreter.vdmj.lex.LexCharacterToken;
import org.overturetool.interpreter.vdmj.lex.LexIdentifierToken;
import org.overturetool.interpreter.vdmj.lex.LexIntegerToken;
import org.overturetool.interpreter.vdmj.lex.LexLocation;
import org.overturetool.interpreter.vdmj.lex.LexNameToken;
import org.overturetool.interpreter.vdmj.lex.LexQuoteToken;
import org.overturetool.interpreter.vdmj.lex.LexRealToken;
import org.overturetool.interpreter.vdmj.lex.LexStringToken;
import org.overturetool.interpreter.vdmj.lex.LexToken;
import org.overturetool.vdmj.typechecker.ClassDefinitionSettings;
import org.overturetool.vdmj.typechecker.NameScope;

public class CopyFactory extends ConvertFactoryInterpreter
{

	@Override
	public LexToken convert(org.overturetool.vdmj.lex.LexToken node)
	{
		return new LexToken(convert(node.location), node.type);
	}

	@Override
	public LexNameToken convert(org.overturetool.vdmj.lex.LexNameToken node)
	{
		return new LexNameToken(node.module, node.name, convert(node.location), node.old, node.explicit);
	}

	@Override
	public LexIdentifierToken convert(
			org.overturetool.vdmj.lex.LexIdentifierToken node)
	{
		return new LexIdentifierToken(node.name, node.old, convert(node.location));
	}

	@Override
	public LexBooleanToken convert(
			org.overturetool.vdmj.lex.LexBooleanToken node)
	{
		return new LexBooleanToken(node.value, convert(node.location));
	}

	@Override
	public LexCharacterToken convert(
			org.overturetool.vdmj.lex.LexCharacterToken node)
	{
		return new LexCharacterToken(node.unicode, convert(node.location));
	}

	@Override
	public LexIntegerToken convert(
			org.overturetool.vdmj.lex.LexIntegerToken node)
	{
		return new LexIntegerToken(node.value, convert(node.location));
	}

	@Override
	public LexQuoteToken convert(
			org.overturetool.vdmj.lex.LexQuoteToken node)
	{
		return new LexQuoteToken(node.value, convert(node.location));
	}

	@Override
	public LexRealToken convert(org.overturetool.vdmj.lex.LexRealToken node)
	{
		return new LexRealToken(node.value, convert(node.location));
	}

	@Override
	public LexStringToken convert(
			org.overturetool.vdmj.lex.LexStringToken node)
	{
		return new LexStringToken(node.value, convert(node.location));
	}

	@Override
	public LexLocation convert(org.overturetool.vdmj.lex.LexLocation node)
	{
		return new LexLocation(node.file, node.module, node.startLine, node.startPos, node.endLine, node.endPos);
	}

	@Override
	public NameScope convert(NameScope node)
	{
		return node;
	}

	@Override
	public ClassDefinitionSettings convert(ClassDefinitionSettings node)
	{
		return node;
	}

	@Override
	public List<? extends ClonableFile> convert(
			List<? extends org.overturetool.util.ClonableFile> node)
	{
		List<ClonableFile> files = new Vector<ClonableFile>();
		for (org.overturetool.util.ClonableFile clonableFile : node)
		{
			files.add(new ClonableFile(clonableFile));
		}
		return files;
	}

}