package org.overturetool.test.examples.vdmj;

import java.io.File;
import java.util.List;

import org.overturetool.test.framework.examples.IMessage;
import org.overturetool.test.framework.examples.Message;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.VDMMessage;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.syntax.SyntaxReader;

public class VdmjFactories
{
	public interface ParserFactory<T extends SyntaxReader, R>
	{
		R read(T reader) throws ParserException, LexException;

		T createReader(LexTokenReader ltr);

		IMessage convertMessage(Object m);

		LexTokenReader createTokenReader(File file);

	}
	
	public final static ParserFactory<ClassReader, List<ClassDefinition>> vdmPpParserfactory = new ParserFactory<ClassReader, List<ClassDefinition>>()
	{

		public List<ClassDefinition> read(ClassReader reader)
				throws ParserException, LexException
		{
			return reader.readClasses();
		}

		public ClassReader createReader(LexTokenReader ltr)
		{
			return new ClassReader(ltr);
		}

		public IMessage convertMessage(Object m)
		{
			VDMMessage msg = (VDMMessage) m;
			return new Message(msg.number,msg.location.startLine,msg.location.endPos,msg.message);
		}

		public LexTokenReader createTokenReader(File file)
		{
			return new LexTokenReader(file, Settings.dialect);
		}
	};
	
	public final static ParserFactory<ModuleReader, List<Module>> vdmSlParserfactory = new ParserFactory<ModuleReader, List<Module>>()
	{

		public List<Module> read(ModuleReader reader)
				throws ParserException, LexException
		{
			return reader.readModules();
		}

		public ModuleReader createReader(LexTokenReader ltr)
		{
			return new ModuleReader(ltr);
		}

		public IMessage convertMessage(Object m)
		{
			VDMMessage msg = (VDMMessage) m;
			return new Message(msg.number,msg.location.startLine,msg.location.endPos,msg.message);
		}

		public LexTokenReader createTokenReader(File file)
		{
			return new LexTokenReader(file, Settings.dialect);
		}
	};
}
