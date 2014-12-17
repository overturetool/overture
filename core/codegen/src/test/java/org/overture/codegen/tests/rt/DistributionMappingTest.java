package org.overture.codegen.tests.rt;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Test;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.vdm2java.rt.DistributionMapping;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

/*
 * This is a test of the VDM AST visitor which extracts the connection map,
 * distribution map, number of CPUs and number of deployed objects
 * The VDM AST is the backbone in order to generate the correct 
 * remote contracts, implementation, local system class and the
 * look up of correct remote and local objects. For this reason, it needs to
 * be tested.
 */

public class DistributionMappingTest {

	public static final String ROOT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar
			+ "rt_codegen";

	
	public DistributionMappingTest()
	{
		Settings.dialect = Dialect.VDM_RT;
		Settings.release = Release.VDM_10;
	}
	
	// Test for no distribution, boundary condition
	@Test
	public void testNoDistribution() {

		List<SClassDefinition> classes = new LinkedList<SClassDefinition>();
		
		DistributionMapping distMapping = new DistributionMapping(classes);
		distMapping.run();
		
		
		int deployedObjCounter = distMapping.getDeployedObjCounter();
		Assert.assertTrue("Expected number of deployed objects to be 0 but got " + deployedObjCounter, deployedObjCounter == 0);
		Assert.assertTrue("Expected no deployed classes to be found", distMapping.getDeployedClasses().isEmpty());
	}
	
	// Test for null arguments, boundary condition
	@Test
	public void testNullArgForDistributionMapping() throws Exception {
		
		DistributionMapping distMapping = new DistributionMapping(null);
		distMapping.run();
		
		Assert.assertTrue("Expected no deployed objects", distMapping.getDeployedObjects().isEmpty());
	}
	
	@Test
	public void testTwoConnectedCPUsWithOneDeployedObject() throws ParserException, LexException {

		
		List<SClassDefinition> classes = readClasses("SimpleDistribution1");
		
		DistributionMapping distMapping = new DistributionMapping(classes);
		distMapping.run();
		
		// Check correct system name, number of deployed objects, and number of CPUs 
		makeBasicAssertions(distMapping, "SimpleSys", 2, 2);
		
		Map<String, Set<AVariableExp>> deploymentMap = distMapping.getCpuToDeployedObject();
		
		// Test deployment map
		checkNamesExist(deploymentMap, "cpu1", new String[]{"a1"});
		checkNamesExist(deploymentMap, "cpu2", new String[]{"a2"});
		
		// Test connection map
		Map<String, Set<String>> connectionMap = distMapping.cpuToConnectedCPUs();
		checkConnectionMap(connectionMap, "cpu1", new String[]{"cpu2"});
		checkConnectionMap(connectionMap, "cpu2", new String[]{"cpu1"});
		
	}
	
	@Test
	public void testTwoConnectedCPUsWithTwoDeployedObjectsEach() throws Exception {
		
		List<SClassDefinition> classes = readClasses("SimpleDistribution2");
		
		DistributionMapping distMapping = new DistributionMapping(classes);
		distMapping.run();
		
		// Test correct system name, number of deployed objects, and number of CPUs 
		makeBasicAssertions(distMapping, "MySys", 4 , 2);
		
		Map<String, Set<AVariableExp>> deploymentMap = distMapping.getCpuToDeployedObject();
		
		// Test deployment map
		checkNamesExist(deploymentMap, "cpu1", new String[]{"x1", "x2"});
		checkNamesExist(deploymentMap, "cpu2", new String[]{"y1", "y2"});

		// Test connection map
		Map<String, Set<String>> connectionMap = distMapping.cpuToConnectedCPUs();
		checkConnectionMap(connectionMap, "cpu1", new String[]{"cpu2"});
		checkConnectionMap(connectionMap, "cpu2", new String[]{"cpu1"});
	}

	@Test
	public void testFourConnectedCPUsWithOneDeployedObjectEach() throws Exception {
		
		List<SClassDefinition> classes = readClasses("ComplexDistribution1");
		
		DistributionMapping distMapping = new DistributionMapping(classes);
		distMapping.run();
		
		// Test correct system name, number of deployed objects, and number of CPUs 
		makeBasicAssertions(distMapping, "DistSys", 4, 4);
		
		Map<String, Set<AVariableExp>> deploymentMap = distMapping.getCpuToDeployedObject();
		
		// Test deployment map
		checkNamesExist(deploymentMap, "cpu1", new String[]{"a1"});
		checkNamesExist(deploymentMap, "cpu2", new String[]{"a2"});
		checkNamesExist(deploymentMap, "cpu3", new String[]{"a3"});
		checkNamesExist(deploymentMap, "cpu4", new String[]{"a4"});
		
		// Test connection map
		Map<String, Set<String>> connectionMap = distMapping.cpuToConnectedCPUs();
		checkConnectionMap(connectionMap, "cpu1", new String[]{"cpu2", "cpu3", "cpu4"});
		checkConnectionMap(connectionMap, "cpu2", new String[]{"cpu1", "cpu3"});
		checkConnectionMap(connectionMap, "cpu3", new String[]{"cpu1", "cpu2"});
		checkConnectionMap(connectionMap, "cpu2", new String[]{"cpu1", "cpu3"});
	}
	
	
	// Methods used for testing
	// Method to test correct system name, number of deployed objects and number of CPUs in the system
	private void makeBasicAssertions(DistributionMapping distMapping, String superName, int deployedObjectCount, int CPUnum) {
		Assert.assertTrue("Expected system class name to be SimpleSys", distMapping.getSystemName().equals(superName));
		Assert.assertTrue("Expected: ", distMapping.getDeployedObjects().size() == deployedObjectCount);
		Assert.assertTrue("Expected: ", distMapping.cpuToConnectedCPUs().size() == CPUnum);

	}

	// Type check a file
	private List<SClassDefinition> readClasses(String fileName) throws ParserException,
			LexException {
		File modelFile = new File(ROOT, fileName);
		
		TypeCheckResult<List<SClassDefinition>> tcResult = TypeCheckerUtil.typeCheckRt(modelFile);
		
		Assert.assertTrue("Expected no type errors in SimpleDistribution1 model", tcResult.errors.isEmpty());
		
		List<SClassDefinition> classes = tcResult.result;
		return classes;
	}
	
	// Method for testing the deployment map
	private void checkNamesExist(Map<String, Set<AVariableExp>> deploymentMap, String cpuName, String[] allowedNames)
	{
		List<String> allowedNamesList = Arrays.asList(allowedNames);
		Set<AVariableExp> cpu1Objects = deploymentMap.get(cpuName);

		for(AVariableExp varExp : cpu1Objects)
		{
			String name = varExp.getName().getName();
			String errorMsg = String.format("Expected deployed object on %s to have a named contained in %s but got %s", cpuName, allowedNamesList, name);
			Assert.assertTrue(errorMsg + name, allowedNamesList.contains(name));
		}
	}
	
	// Method for testing the connection map
	private void checkConnectionMap(Map<String, Set<String>> connectionMap, String cpuName, String[] allowedNames)
	{
		List<String> allowedNamesList = Arrays.asList(allowedNames);
		Set<String> cpu1Objects = connectionMap.get(cpuName);

		
		for(String varExp : cpu1Objects)
		{
			String name = varExp;
			String errorMsg = String.format("Expected a connected CPU on %s to have a named contained in %s but got %s", cpuName, allowedNamesList, name);
			Assert.assertTrue(errorMsg + name, allowedNamesList.contains(name));
		}
	}
}
