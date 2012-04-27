package com.lausdahl.ast.creator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.GenericArgumentedIInterfceDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.PredefinedClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.extend.ExtensionGenerator;
import com.lausdahl.ast.creator.extend.ExtensionGenerator.ExtendMode;
import com.lausdahl.ast.creator.java.definitions.JavaName;
import com.lausdahl.ast.creator.methods.CheckCacheMethod;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.visitors.copy.CopyNode2ExtendedNode;
import com.lausdahl.ast.creator.methods.visitors.copy.CopyNode2ExtendedNodeListHelper;
import com.lausdahl.ast.creator.methods.visitors.copy.CopyNode2ExtendedNodeListListHelper;
import com.lausdahl.ast.creator.utils.ClassFactory;

public class Main
{
	public enum RunType
	{
		OvertureII, OvertureII_Interpreter, Test, TestInterpreter, TestInterpreterBase, TestInterpreterExtend, Nested
	}

	/**
	 * Set this to false to generate the overture II AST
	 */
	public static boolean test = false;

	private static final String INPUT_FILENAME_OVERTURE_II = "..\\..\\core\\ast\\src\\main\\resources\\overtureII.astv2".replace('\\', File.separatorChar);
	private static final String INPUT_FILENAME_OVERTURE_II_INTERPRETER = "..\\..\\core\\interpreter\\src\\main\\resources\\overtureII.astv2".replace('\\', File.separatorChar);
	private static final String INPUT_FILENAME_TEST = "src\\main\\resources\\testdata\\nested1.ast";
	// private static final String INPUT_FILENAME = "src\\main\\resources\\testdata\\extend\\t1.ast";
	//
	private static final String TESTDATA_BASE = "src\\main\\resources\\testdata\\";
	// private static final String INPUT_FILENAME2 = "src\\main\\resources\\testdata\\extend\\t2.ast";
	// private static final String ANALYSIS_PACKAGE_NAME = "org.overture.ast.analysis";
	private static File generated = null;

	public final static RunType run = RunType.OvertureII;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		String input1 = null;
		String input2 = null;
		String output = null;
		switch (run)
		{
			case OvertureII:
				output = "..\\..\\core\\ast\\src\\main\\java\\";
				input1 = INPUT_FILENAME_OVERTURE_II;
				input2 = null;
				break;
			case OvertureII_Interpreter:
				output = "..\\..\\core\\interpreter\\src\\main\\java\\";
				input1 = INPUT_FILENAME_OVERTURE_II;
				input2 = INPUT_FILENAME_OVERTURE_II_INTERPRETER;
				break;
			case Test:
				output = "..\\..\\astTest\\srcNew\\";
				input1 = INPUT_FILENAME_TEST;
				input2 = null;
				test = true;
				break;
			case TestInterpreter:
				output = "..\\..\\astTest\\srcInterpreter\\";
				input1 = TESTDATA_BASE + "extend\\t1.astv2";
				input2 = TESTDATA_BASE + "extend\\t2.astv2";
				test = false;
				break;
			case TestInterpreterExtend:
				output = "..\\..\\astTest\\srcInterpreterExtend2\\";
				input1 = TESTDATA_BASE + "extend\\t1.astv2";
				input2 = TESTDATA_BASE + "extend\\t2.astv2";
				test = true;
				break;
			case TestInterpreterBase:
				output = "..\\..\\astTest\\srcInterpreterBase2\\";
				input1 = TESTDATA_BASE + "extend\\t1.astv2";
				input2 = null;
				test = true;
				break;
			case Nested:
				output = "..\\..\\astTest\\srcNested\\";
				input1 = TESTDATA_BASE + "nested1.astv2";
				input2 = null;
				test = true;
				break;

		}
		try
		{
			input1 = input1.replace('/', File.separatorChar).replace('\\', File.separatorChar);
			if (input2 != null)
			{
				input2 = input2.replace('/', File.separatorChar).replace('\\', File.separatorChar);
			}
			generated = new File(output.replace('/', File.separatorChar).replace('\\', File.separatorChar));

			System.out.println("Output location set to: "
					+ generated.getAbsolutePath());
			switch (run)
			{
				case OvertureII:
				case TestInterpreterBase:
				case Nested:
				{
					System.out.println("Generator starting with input: "
							+ input1);

					Environment env1 = create(input1, generated, true);
					System.out.println("\n\nGenerator completed with "
							+ env1.getAllDefinitions().size()
							+ " generated files.\n\n");
				}
					break;
				case OvertureII_Interpreter:
				case TestInterpreter:
				case TestInterpreterExtend:
				{
					System.out.println("Generator starting with input: "
							+ input1);
					Main.create(new File(input1), new File(input2), generated, "Interpreter");
					System.out.println("Done.");
				}
					break;
				case Test:
				{
					System.out.println("TESTING...");
					Environment env1 = create(input1, generated, true);
					System.out.println(env1);
					// Main.create(new File(INPUT_FILENAME), new File(INPUT_FILENAME2), generated, "Interpreter");
					// System.out.println("Generator starting with input: "
					// + INPUT_FILENAME);
					// // String defaultPackage = "org.overture.ast.node";
					// // String analysisPackage = "org.overture.ast.analysis";
					// Environment env1 = create(INPUT_FILENAME, generated, true);
					//
					// if (extend)
					// {
					// // defaultPackage = "org.overture.interpreter.ast.node";
					// // analysisPackage = "org.overture.interpreter.ast.analysis";
					// String extendName = "Interpreter";
					// // Environment env2 = create(INPUT_FILENAME2, defaultPackage, analysisPackage, extendName,
					// // generated, true);
					// Generator generator = new Generator();
					// Environment env2 = generator.generate(INPUT_FILENAME);
					// Environment env2Extension = generator.generate(INPUT_FILENAME2);
					// env2 = env2.extendWith(env2Extension);
					// generator.runPostGeneration(env2);
					// setExtendName(env2, extendName);
					//
					// SourceFileWriter.write(generated, env2);
					//
					// createCopyAdaptor(env1, env2, extendName, generated);
					// }
					System.out.println("TESTING...DONE.");
				}
					break;

			}

		} catch (AstCreatorException e)
		{
			System.err.println();
			System.err.println(e.getMessage());
		}

	}

	public static Environment create(String inputFile, File outputBase,
			boolean write) throws IOException, InstantiationException,
			IllegalAccessException, AstCreatorException
	{
		Generator generator = new Generator();
		Environment env = generator.generate(inputFile,"Base");
		generator.runPostGeneration(env);

		if (write)
		{
			SourceFileWriter.write(outputBase, env);
		}
		return env;
	}

	public static void create(File ast1, File ast2, File generated,
			String extendName) throws Exception
	{
		System.out.println("TESTING...");
		test = false;
		System.out.println("Generator starting with input: " + ast1);
		Environment env1 = create(ast1.getAbsolutePath(), generated, false);
		
//		System.out.println("Source");
		
		
		
		System.out.println("Generator starting with input: " + ast2);
		Generator generator = new Generator();

		Environment env2 = generator.generate(ast1.getAbsolutePath(),"Extension Base");
		generator.runPostGeneration(env2);
		System.out.println(env2.getInheritanceToString());
		
		Environment env2Extension = generator.generate(ast2.getAbsolutePath(),"Entension");
		test = true;
		System.out.println("------------------------------------------------------------------------");
//		System.out.println("Extended Source");
		System.out.println(env2Extension.getInheritanceToString());
		// generator.createInterfacesForNodePoints(env2);
		env2 =ExtensionGenerator.extendWith(env2,ExtendMode.Standalone, env2Extension);

		setExtendName(env2, extendName);
		generator.runPostGeneration(env2);
		setExtendName(env2, extendName);

		if (test)
		{
			Environment envOrigin = create(ast1.getAbsolutePath(), generated, false);
			ExtensionGenerator.extendWith(env2,ExtendMode.Extend, envOrigin);
		}

		SourceFileWriter.write(generated, env2);

		createCopyAdaptor(env1, env2, extendName, generated);

	}

	public static void setExtendName(Environment env, String extendName)
	{
		String namePostfix = extendName == null ? "" : extendName;
		for (IInterfaceDefinition def : env.getAllDefinitions())
		{
			def.getName().setPostfix(namePostfix);
		}
	}

	public static void createCopyAdaptor(Environment source,
			Environment destination, String namePostfix, File outputFolder)
			throws Exception
	{
		IClassDefinition convertFactory = ClassFactory.createCustom(new JavaName(destination.getDefaultPackage(), "", "ConvertFactory", namePostfix), destination);
		// convertFactory.getName().setPostfix(namePostfix);
		// convertFactory.getName().setPackageName(destination.getDefaultPackage());
		convertFactory.setAbstract( true);

		List<Method> methods = new Vector<Method>();
		for (IClassDefinition c : Generator.getClasses(source.getClasses(), source))
		{
			if (source.classToType.get(c) == IClassDefinition.ClassType.Production
			/* || c.getType() == ClassType.SubProduction */)
			{
				continue;
			}
			IClassDefinition destDef = null;
			for (IClassDefinition def : destination.getClasses())
			{
				if (def.getName().getName().replace(def.getName().getPostfix(), "").equals(c.getName()))
				{
					destDef = def;
				}
			}
			if (destDef == null)
			{
				System.err.println("Source class: " + c.getName()
						+ " has no match in target environment.");
				System.err.println("Target Environment:");
				System.err.println(destination);
				throw new Exception("Tree match error on copy");
			}
			Method m = new CopyNode2ExtendedNode(c, destDef, source, destination, convertFactory);
			m.setClassDefinition(c);
			m.setEnvironment(source);
			methods.add(m);

		}

		IClassDefinition copyAdaptor =ClassFactory.createCustom(new JavaName(destination.getDefaultPackage(), "", "CopyAdaptor", namePostfix), destination);
		Field converFactoryField = new Field(destination);
		converFactoryField.name = "factory";
		converFactoryField.type = convertFactory;
		copyAdaptor.addField(converFactoryField);

		Field cacheField = new Field(destination);
		cacheField.name = "cache";
		cacheField.setType("Hashtable");
		destination.addClass(new PredefinedClassDefinition("java.util", "Hashtable", true));
		copyAdaptor.addField(cacheField);

		copyAdaptor.setAnnotation("@SuppressWarnings({\"unused\",\"unchecked\",\"rawtypes\"})");
		// copyAdaptor.setPackageName(destination.getDefaultPackage());
		copyAdaptor.addInterface(new GenericArgumentedIInterfceDefinition(source.getTaggedDef(destination.TAG_IAnswer), destination.node.getName().getName()));
		copyAdaptor.getMethods().addAll(methods);
		copyAdaptor.addMethod(new CopyNode2ExtendedNodeListHelper(source, destination));
		copyAdaptor.addMethod(new CopyNode2ExtendedNodeListListHelper(source, destination));
		copyAdaptor.addMethod(new CheckCacheMethod(copyAdaptor, destination));
		// copyAdaptor.methods.add(new ConstructorMethod(copyAdaptor,destination));
		// copyAdaptor.imports.addAll(source.getAllDefinitions());
		// copyAdaptor.imports.addAll(destination.getAllDefinitions());
		// copyAdaptor.setNamePostfix(namePostfix);
//		destination.addClass(copyAdaptor);
		SourceFileWriter.write(outputFolder, copyAdaptor);
		SourceFileWriter.write(outputFolder, convertFactory);

	}

}
