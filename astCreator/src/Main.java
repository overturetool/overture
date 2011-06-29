import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.Generator;
import com.lausdahl.ast.creator.SourceFileWriter;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.CustomClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.analysis.CopyNode2ExtendedNode;
import com.lausdahl.ast.creator.methods.analysis.CopyNode2ExtendedNodeListHelper;

public class Main
{
	private static final String INPUT_FILENAME = "test.astV2";
	private static final String INPUT_FILENAME2 = "testExtended.astV2";
	// private static final String ANALYSIS_PACKAGE_NAME = "org.overture.ast.analysis";
	private static File generated = new File("..\\ast\\src\\");
	static boolean create = true;

	/**
	 * @param args
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(String[] args) throws IOException,
			InstantiationException, IllegalAccessException
	{
		String defaultPackage = "org.overture.ast.node";
		String analysisPackage = "org.overture.ast.analysis";
		Environment env1 = create(INPUT_FILENAME, defaultPackage, analysisPackage, "", generated, true);
		
		defaultPackage = "org.overture.interpreter.ast.node";
		analysisPackage = "org.overture.interpreter.ast.analysis";
		String extendName = "Interpreter";
		Environment env2 = create(INPUT_FILENAME2, defaultPackage, analysisPackage, extendName, generated, true);

		createCopyAdaptor(env1, env2,defaultPackage,extendName,generated);

	}

	public static Environment create(String inputFile, String defaultPackage,
			String analysisPackage, String extendName, File outputBase,
			boolean write) throws IOException, InstantiationException,
			IllegalAccessException
	{
		Environment env = new Generator().generate(inputFile, defaultPackage, analysisPackage);

		String namePostfix = extendName == null ? "" : extendName;
		for (IInterfaceDefinition def : env.getAllDefinitions())
		{
			def.setNamePostfix(namePostfix);
		}
		if (write)
		{
			SourceFileWriter.write(outputBase, env, defaultPackage, analysisPackage);
		}
		return env;
	}
	
	public static void createCopyAdaptor(Environment source, Environment destination, String defaultPackage,String namePostfix,File outputFolder)
	{
		List<Method> methods = new Vector<Method>(); 
		for (CommonTreeClassDefinition c : Generator.getClasses(source.getClasses()))
		{
			if (c.getType() == IClassDefinition.ClassType.Production)
			{
				continue;
			}
			IClassDefinition destDef = null;
			for (IClassDefinition def : destination.getClasses())
			{
				if(def.getName().replace(def.getNamePostfix(), "").equals(c.getName()))
				{
					destDef = def;
				}
			}
			Method m = new CopyNode2ExtendedNode(c, destDef, source,destination);
			m.setClassDefinition(c);
			m.setEnvironment(source);
			methods.add(m);

		}
		
//		System.out.println(methods);

//		String tmpName = answerIntf.getName().substring(1);
//		if (tmpName.contains("<"))
//		{
//			tmpName = tmpName.substring(0, tmpName.indexOf('<')) + "Adaptor"
//					+ answerIntf.getName().substring(tmpName.indexOf('<') + 1);
//		} else
//		{
//			tmpName += "Adaptor";
//		}

		CustomClassDefinition copyAdaptor = new CustomClassDefinition("CopyAdaptor", destination);
		copyAdaptor.setPackageName(defaultPackage);
		copyAdaptor.interfaces.add(source.getTaggedDef(destination.TAG_IAnswer).getSignatureName()+"<"+destination.node.getSignatureName()+">");
		copyAdaptor.methods.addAll(methods);
		copyAdaptor.methods.add(new CopyNode2ExtendedNodeListHelper(source,destination));
		copyAdaptor.imports.addAll(source.getAllDefinitions());
		copyAdaptor.imports.addAll(destination.getAllDefinitions());
		copyAdaptor.setNamePostfix(namePostfix);
		destination.addClass(copyAdaptor);
		SourceFileWriter.write(outputFolder, copyAdaptor);
	}

}
