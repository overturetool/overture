import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.overture.interpreter.ast.node.ConvertFactoryInterpreter;
import org.overture.interpreter.ast.node.CopyAdaptorInterpreter;
import org.overture.interpreter.ast.node.NodeInterpreter;
import org.overture.interpreter.util.ClonableFile;
import org.overture.interpreter.vdmj.lex.LexBooleanToken;
import org.overture.interpreter.vdmj.lex.LexCharacterToken;
import org.overture.interpreter.vdmj.lex.LexIdentifierToken;
import org.overture.interpreter.vdmj.lex.LexIntegerToken;
import org.overture.interpreter.vdmj.lex.LexLocation;
import org.overture.interpreter.vdmj.lex.LexNameToken;
import org.overture.interpreter.vdmj.lex.LexQuoteToken;
import org.overture.interpreter.vdmj.lex.LexRealToken;
import org.overture.interpreter.vdmj.lex.LexStringToken;
import org.overture.interpreter.vdmj.lex.LexToken;
import org.overture.vdmj.lex.LexException;
import org.overture.vdmj.syntax.ParserException;
import org.overture.vdmj.typechecker.ClassDefinitionSettings;
import org.overture.vdmj.typechecker.NameScope;

public class ConvertAst extends BasicTypeCheckTestCase
{
	public ConvertAst(String string)
	{
		super("test");

	}

	public void test() throws ParserException, LexException
	{
		List<org.overture.ast.node.Node> exp = parse(ParserType.Module, "module A \ndefinitions \nfunctions\na : int -> int\na(b) == b;\nend A");

		@SuppressWarnings("rawtypes")
		CopyAdaptorInterpreter visitor = new CopyAdaptorInterpreter(new Factory(),new Hashtable());
		NodeInterpreter node = exp.get(0).apply(visitor);
		System.out.println(node);

	}

	public static class Factory extends ConvertFactoryInterpreter
	{

		@Override
		public LexToken convert(org.overture.vdmj.lex.LexToken node)
		{
			return new LexToken(convert(node.location), node.type);
		}

		@Override
		public LexNameToken convert(org.overture.vdmj.lex.LexNameToken node)
		{
			return new LexNameToken(node.module, node.name, convert(node.location), node.old, node.explicit);
		}

		@Override
		public LexIdentifierToken convert(
				org.overture.vdmj.lex.LexIdentifierToken node)
		{
			return new LexIdentifierToken(node.name, node.old, convert(node.location));
		}

		@Override
		public LexBooleanToken convert(
				org.overture.vdmj.lex.LexBooleanToken node)
		{
			return new LexBooleanToken(node.value, convert(node.location));
		}

		@Override
		public LexCharacterToken convert(
				org.overture.vdmj.lex.LexCharacterToken node)
		{
			return new LexCharacterToken(node.unicode, convert(node.location));
		}

		@Override
		public LexIntegerToken convert(
				org.overture.vdmj.lex.LexIntegerToken node)
		{
			return new LexIntegerToken(node.value, convert(node.location));
		}

		@Override
		public LexQuoteToken convert(
				org.overture.vdmj.lex.LexQuoteToken node)
		{
			return new LexQuoteToken(node.value, convert(node.location));
		}

		@Override
		public LexRealToken convert(org.overture.vdmj.lex.LexRealToken node)
		{
			return new LexRealToken(node.value, convert(node.location));
		}

		@Override
		public LexStringToken convert(
				org.overture.vdmj.lex.LexStringToken node)
		{
			return new LexStringToken(node.value, convert(node.location));
		}

		@Override
		public LexLocation convert(org.overture.vdmj.lex.LexLocation node)
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
				List<? extends org.overture.util.ClonableFile> node)
		{
			List<ClonableFile> files = new ArrayList<ClonableFile>();
			for (org.overture.util.ClonableFile clonableFile : node)
			{
				files.add(new ClonableFile(clonableFile));
			}
			return files;
		}

	}
}
