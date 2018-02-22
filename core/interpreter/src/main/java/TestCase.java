import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.interpreter.runtime.*;
import org.overture.interpreter.values.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;


public class TestCase {
    private final static String xmlReportTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n\t";
    private final static String xmlReportSuiteTemplate = "<testsuite xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"https://maven.apache.org/surefire/maven-surefire-plugin/xsd/surefire-test-report.xsd\" name=\"%s\" time=\"0.0\" tests=\"0\" errors=\"0\" skipped=\"0\" failures=\"0\"/>";
    private final static String vdmUnitReportEnable = "vdm.unit.report";

    public static Value reflectionRunTest(Value obj, Value name)
            throws Exception {
        String methodName = name.toString().replaceAll("\"", "").trim();

        ObjectValue instance = (ObjectValue) obj;
        for (NameValuePair p : instance.members.asList()) {
            if (p.name.getName().equals(methodName)) {
                if (p.value instanceof OperationValue) {
                    OperationValue opVal = (OperationValue) p.value;
                    Context mainContext = new StateContext(Interpreter.getInstance().getAssistantFactory(), p.name.getLocation(), "reflection scope");

                    mainContext.putAll(ClassInterpreter.getInstance().initialContext);
                    // mainContext.putAll(ClassInterpreter.getInstance().);
                    mainContext.setThreadState(ClassInterpreter.getInstance().initialContext.threadState.dbgp, ClassInterpreter.getInstance().initialContext.threadState.CPU);

                    long timerStart = System.nanoTime();
                    boolean success = false;
                    ExitException error = null;
                    try {
                        opVal.eval(p.name.getLocation(), new ValueList(), mainContext);
                        success = true;
                    } catch (Exception e) {
                        if (e instanceof ExitException) {
                            if (((ExitException) e).value.objectValue(null).type.getName().equals("AssertionFailedError")) {
                                success = false;
                            }
                            throw e;
                        }

                        try {
                            return ClassInterpreter.getInstance().evaluate("Error`throw(\""
                                    + e.getMessage().replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\'", "\\\'")
                                    + "\")", mainContext);
                        } catch (ExitException e2) {
                            error = e2;
                            throw e2;
                        }
                    } finally {
                        long totalExecTime = System.nanoTime() - timerStart;

                        if (System.getProperty(vdmUnitReportEnable) != null) {

                            String containerName = "";

                            if (obj instanceof ObjectValue) {
                                containerName = ((ObjectValue) obj).type.getName().getName();
                            }

                            recordTestResults(containerName, methodName, success, error, totalExecTime);
                        }
                    }
                }
            }
        }
        return new VoidValue();

    }

    public static boolean reflectionRunTest(AModuleModules module, AExplicitOperationDefinition opDef) throws Exception {
        String moduleName = module.getName().getName();
        String testName = opDef.getName().getName();

        long timerStart = System.nanoTime();
        boolean success = false;
        ExitException error = null;
        try {
            ModuleInterpreter.getInstance().evaluate(moduleName + "`" + testName + "()"
                    , ModuleInterpreter.getInstance().initialContext);
            success = true;
        } catch (Exception e) {
            if (e instanceof ExitException) {
                success = false;
            }
            throw e;
        } finally {
            long totalExecTime = System.nanoTime() - timerStart;

            if (System.getProperty(vdmUnitReportEnable) != null) {
                recordTestResults(moduleName, testName, success, error, totalExecTime);
            }
        }
        return success;
    }

    private static void recordTestResults(String containerName, String methodName, boolean success, ExitException error, long totalExecTime) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {

        File report = new File("TEST-" + containerName + ".xml");

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = null;

        if (report.exists()) {
            doc = docBuilder.parse(report);

        } else {
            doc = docBuilder.parse(new ByteArrayInputStream(String.format(xmlReportTemplate + xmlReportSuiteTemplate, containerName).getBytes()));
        }

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/testsuite[@name='" + containerName + "']");

        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        if (nl.getLength() > 0) {
            Element testSuiteNode = (Element) nl.item(0);

            expr = xpath.compile("testcase[@name='" + methodName + "']");
            NodeList nlT = (NodeList) expr.evaluate(testSuiteNode, XPathConstants.NODESET);
            Element n = null;

            if (nlT.getLength() > 0) {
                n = (Element) nlT.item(0);
            } else {
                n = doc.createElement("testcase");
            }

            while (n.getFirstChild() != null) {
                n.removeChild(n.getFirstChild());
            }

            n.setAttribute("name", methodName);
            n.setAttribute("classname", containerName);
            n.setAttribute("time", totalExecTime * 1E-9 + "");
            testSuiteNode.appendChild(n);

            testSuiteNode.setAttribute("tests", String.valueOf(Integer.parseInt(testSuiteNode.getAttribute("tests")) + 1));

            if (error != null) {
                testSuiteNode.setAttribute("error", String.valueOf(Integer.parseInt(testSuiteNode.getAttribute("errors")) + 1));
                Element errorElement = doc.createElement("error");
                errorElement.setAttribute("message", error.number + "");
                errorElement.setAttribute("type", "ERROR");
                StringWriter strOut = new StringWriter();
                error.ctxt.printStackTrace(new PrintWriter(strOut), true);
                errorElement.setTextContent(strOut.toString());
                n.appendChild(errorElement);
            } else if (!success) {
                testSuiteNode.setAttribute("failures", String.valueOf(Integer.parseInt(testSuiteNode.getAttribute("failures")) + 1));
                Element failureElement = doc.createElement("failure");
                failureElement.setAttribute("message", methodName);
                failureElement.setAttribute("type", "WARNING");
                failureElement.setAttribute("time", totalExecTime * 1E-9 + "");
                n.appendChild(failureElement);
            }

            testSuiteNode.setAttribute("time", String.valueOf(Double.parseDouble(testSuiteNode.getAttribute("time")) + (totalExecTime * 1E-9)));

        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(report);
        Source input = new DOMSource(doc);

        transformer.transform(input, output);


    }
}
