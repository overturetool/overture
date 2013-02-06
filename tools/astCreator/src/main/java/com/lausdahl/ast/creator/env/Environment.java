package com.lausdahl.ast.creator.env;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.AstCreatorException;
import com.lausdahl.ast.creator.ToStringAddOn;
import com.lausdahl.ast.creator.definitions.AnalysisExceptionDefinition;
import com.lausdahl.ast.creator.definitions.BaseClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;
import com.lausdahl.ast.creator.definitions.PredefinedClassDefinition;
import com.lausdahl.ast.creator.java.definitions.JavaName;

public class Environment extends BaseEnvironment {
	public PredefinedClassDefinition iNode;
	public InterfaceDefinition iToken;

	public final String TAG_IAnalysis = "IAnalysis";
	public final String TAG_IAnswer = "IAnswer";
	public final String TAG_IQuestion = "IQuestion";
	public final String TAG_IQuestionAnswer = "IQuestionAnswer";
	public BaseClassDefinition analysisException;

	private List<ToStringAddOn> toStringAddOn = new Vector<ToStringAddOn>();

	public final Map<IInterfaceDefinition, IInterfaceDefinition> treeNodeInterfaces = new Hashtable<IInterfaceDefinition, IInterfaceDefinition>();

	public final Map<IClassDefinition, ClassType> classToType = new Hashtable<IClassDefinition, ClassType>();

	private String analysisPackage = "org.overture.ast.analysis";

	private String templateAnalysisPackage;

	public String getTemplateDefaultPackage() {
		return templateDefaultPackage;
	}

	public void setTemplateDefaultPackage(String templateDefaultPackage) {
		this.templateDefaultPackage = templateDefaultPackage;
	}

	public String getTemplateAnalysisPackage() {
		return templateAnalysisPackage;
	}

	public void setTemplateAnalysisPackage(String templateAnalysisPackage) {
		this.templateAnalysisPackage = templateAnalysisPackage;
	}

	private Environment(String name) {
		super(name);
	}

	public static Environment getFromBase(Environment base,
			String extAnalysisPackage, String extDefaultPackage) {
		Environment res = new Environment("extended_" + base.name);
		res.toStringAddOn = base.toStringAddOn;
		res.setAnalysisPackages(extAnalysisPackage);
		res.setDefaultPackages(extDefaultPackage);
		res.classes.clear();
		res.iNode = base.iNode;
		res.iToken = base.iToken;
		res.node = base.node;
		res.token = base.token;
		res.nodeList = base.nodeList;
		res.nodeListList = base.nodeListList;
		res.graphNodeList = base.graphNodeList;
		res.graphNodeListList = base.graphNodeListList;
		res.externalNode = base.externalNode;
		res.iToken.supers.add(res.iNode);
		res.node.addInterface(res.iNode);
		res.token.addInterface(res.iToken);
		res.analysisException = base.analysisException;
		res.addCommonTreeInterface(base.node, base.iNode);
		res.addCommonTreeInterface(base.token, base.iToken);
		res.addClass(res.node);
		res.addClass(res.nodeList);
		res.addClass(res.nodeListList);
		res.addClass(res.token);
		res.addClass(res.externalNode);
		res.addClass(base.analysisException);
		return res;
	}

	public static Environment getEmptyInstance(String name) {
		Environment res = new Environment(name);
		return res;
	}

	public static Environment getInstance(String name) {
		Environment res = new Environment(name);
		res.iNode = new PredefinedClassDefinition(res.defaultPackage, "INode");
		res.iToken = new PredefinedClassDefinition(res.defaultPackage, "IToken");
		// iToken.addInterface(iNode);
		res.iToken.supers.add(res.iNode);
		res.node.addInterface(res.iNode);
		res.token.addInterface(res.iToken);
		res.analysisException = new AnalysisExceptionDefinition(
				res.analysisPackage, "AnalysisException", res);
		res.addCommonTreeInterface(res.node, res.iNode);
		res.addCommonTreeInterface(res.token, res.iToken);
		res.addClass(res.analysisException);
		return res;
	}

	public List<IInterfaceDefinition> getInterfaces() {
		return super.interfaces;
	}

	/**
	 * Sets the analysis package for this environment.
	 * 
	 * Also all interface and class definitions are updated with the package,
	 * only if they are in the old package name.
	 * 
	 * @param analysisPackage
	 */
	public void setAnalysisPackages(String analysisPackage) {
		String oldPackage = this.analysisPackage;
		this.templateAnalysisPackage = this.analysisPackage = analysisPackage;
		for (IClassDefinition c : classes) {
			if (c.getName().getPackageName().equals(oldPackage)) {
				c.getName().setPackageName(analysisPackage);
			}
		}

		for (IInterfaceDefinition c : interfaces) {
			if (c.getName().getPackageName().equals(oldPackage)) {
				c.getName().setPackageName(analysisPackage);
			}
		}
	}

	public String getAnalysisPackage() {
		return this.analysisPackage;
	}

	private static List<IClassDefinition> findClassOfType(String name,
			List<IClassDefinition> l) {

		List<IClassDefinition> res = new LinkedList<IClassDefinition>();

		for (IInterfaceDefinition idef : l)
			if (IClassDefinition.class.isInstance(idef)
					&& idef.getName().toString().contains(name))
				res.add(IClassDefinition.class.cast(idef));

		return res;
	}

	/**
	 * Lookup a path of tag names. E.g. a dot separated list of tags from the
	 * ast file.
	 * 
	 * For example: exp.#Binary.plus
	 * 
	 * @param path
	 *            - the tag path to look up
	 * @param generalize
	 *            - if true: lookupTagPath will generalize to the most general
	 *            class, which must be unique, if not an AstCreatorException is
	 *            thrown.
	 * 
	 *            - if false: lookupTagPath will not generalize however then the
	 *            result may be ambiguous in which case null is returned.
	 * 
	 * @return The unique alternative or production pointed out by the path.
	 * @throws AstCreatorException
	 *             - when a path look up gives an ambiguous result with
	 *             generalize true.
	 */
	public IInterfaceDefinition lookupTagPath(String path, boolean generalize)
			throws AstCreatorException {
		if (path == null)
			return null;
		List<IClassDefinition> possibleResult = null;

		String[] parts = path.split("\\.");

		// okay we have a path a.b.c and so on.
		// Lookup all interface and classes for each a.b.c... and
		// see if we can build a valid path of object e.g. here a is
		// the parent of b being the parant of c etc...
		for (String part : parts) {
			List<IClassDefinition> current = new LinkedList<IClassDefinition>();

			// search the classes
			for (IClassDefinition cdef : classes) {
				if (cdef.getName().getTag().equals(part))
					current.add(cdef);
			}

			if (current.size() == 0)
				throw new AstCreatorException("The path \"" + path
						+ "\" does not exists from " + part, null, true);

			// first iteration, initialize possibleResult rather than search for
			// a valid parent.
			if (possibleResult == null) {
				possibleResult = current;
				List<IClassDefinition> tbr = new LinkedList<IClassDefinition>();
				for (IClassDefinition d : possibleResult)
					if (classToType.containsKey(d))
						switch (classToType.get(d)) {
						case Alternative:
							tbr.add(d);
							break;
						case SubProduction:
						case Production:
							break;
						default:
							break;
						}
				possibleResult.removeAll(tbr);

			}
			// suppose 'a.b.c' is the path and we are in the second
			// iteration.
			// Then 'a' is in possibleResult and 'b' and 'b'' will be in
			// current.
			// now only 'b' (not 'b'') has 'a' as parent and therefore the
			// validContinuation is only 'b'.
			boolean didSomething = false;
			if (possibleResult != current || possibleResult.size() > 1) {
				List<IClassDefinition> validContinuation = new LinkedList<IClassDefinition>();
				for (IClassDefinition parentIdef : possibleResult)
					for (IClassDefinition childIdef : current) {

						if (isSuperTo(parentIdef, childIdef)) {
							didSomething = true;
							validContinuation.add(childIdef);
							if (generalize
									|| (possibleResult.size() == 2
											&& parts.length > 1 && !parentIdef
											.getAstPackage().equals(
													childIdef.getAstPackage())))
								validContinuation.add(parentIdef);
						}
					}
				if (didSomething)
					possibleResult = validContinuation;
			}

		}

		// So we may end up with multiple result that are inheritances of
		// each other, this makes sure we only return the most general one.
		List<IClassDefinition> tbr = new LinkedList<IClassDefinition>();
		if (generalize)
			for (int i = 0; i < possibleResult.size(); i++)
				for (int j = i + 1; j < possibleResult.size(); j++) {
					IClassDefinition ith = possibleResult.get(i);
					IClassDefinition jth = possibleResult.get(j);
					if (ith == jth)
						tbr.add(jth);
					else if (isSuperTo(ith, jth))
						tbr.add(jth);
					else if (isSuperTo(jth, ith))
						tbr.add(ith);
				}

		for (IClassDefinition d : tbr)
			possibleResult.remove(d);

		if (possibleResult.size() == 0)
			return null;
		if (possibleResult.size() == 1) {
			IInterfaceDefinition found = possibleResult.get(0);
			if (generalize) {

				if (treeNodeInterfaces.containsKey(found))
					return treeNodeInterfaces.get(found);
				return found;
			}
			return found;
		}

		StringBuilder message = new StringBuilder();
		message.append("Searching for tag \"" + path
				+ "\" gave multiple possibilities:\n");
		for (IInterfaceDefinition p : possibleResult)
			message.append("\t" + p.toString() + "\n");
		message.append("Typically this means that \""
				+ path
				+ "\" is missing its prefix. E.g. [else]:elseIf in the definition file "
				+ "instead of [else]:exp.elseIf, which would disambiguate elseIf from std.elseIf.");
		throw new AstCreatorException(message.toString(), null, true);
	}

	public IInterfaceDefinition lookupByTag(String tag) {
		if ("".equals(tag))
			return null;

		for (IInterfaceDefinition idef : classes) {
			if (tag.equals(idef.getTag()))
				return idef;
			JavaName jn = idef.getName();
			if (tag.equals(jn.getTag()))
				return idef;
		}
		return null;
	}

	public IInterfaceDefinition lookUpType(String name) {
		IInterfaceDefinition res = lookUpInterface(name);
		if (res == null) {
			return lookUp(name);
		}
		return res;
	}

	public IClassDefinition lookUpPreferSameContext(String name, String ctxt) {

		// Find all classes designated name
		List<IClassDefinition> cdefs = new LinkedList<IClassDefinition>();
		for (IClassDefinition cdef : classes) {
			if (isClassNamesEqual(cdef.getName().getName(), name))
				cdefs.add(cdef);
		}

		// Only one, no choice, fine let them have it
		if (cdefs.size() == 1)
			return cdefs.get(0);

		// Nothing, well too bad
		if (cdefs.size() == 0)
			return null;

		// More than one, weed out the ones are not in the given context
		List<IClassDefinition> tbr = new LinkedList<IClassDefinition>();
		for (IClassDefinition cdef : cdefs) {
			if (!cdef.getName().getPackageName().startsWith(ctxt))
				tbr.add(cdef);
		}
		cdefs.removeAll(tbr);

		// Now that is better
		if (cdefs.size() == 1)
			return cdefs.get(0);

		// Nothing after weeding out the ones no from the context
		if (cdefs.isEmpty())
			throw new RuntimeException("After removing classes not in \""
					+ ctxt + "\" nothing is left.");

		// The name we are looking for defined multiple times in the given
		// context :(
		if (cdefs.size() > 1) {
			System.out.println("WARNING! \"" + name
					+ "\" is defined multiple times.");
			return cdefs.get(0);
		}
		// throw new RuntimeException("Name \"" + name
		// + "\" is defined multiple times in this context "
		// + ctxt.getAstPackage() + " which makes lookups ambigious.");

		// Actually this should not happen
		return null;
	}

	public IClassDefinition lookUp(String name) {
		for (IClassDefinition cl : classes) {
			if (isClassNamesEqual(cl.getName().getName(), name)) {
				return cl;
			}
		}
		return null;
	}

	public IInterfaceDefinition lookUpInterface(String name) {
		for (IInterfaceDefinition cl : interfaces) {
			if (isClassNamesEqual(cl.getName().getName(), name)) {
				return cl;
			}
		}
		return null;
	}

	private boolean isClassNamesEqual(String a, String b) {
		if (a.contains("<")) {
			a = a.substring(0, a.indexOf('<'));
		}
		if (b.contains("<")) {
			b = b.substring(0, b.indexOf('<'));
		}
		return a.equals(b);
	}

	public boolean isSuperTo(IClassDefinition superClass,
			IClassDefinition selectedClass) {
		return !selectedClass.equals(superClass) && selectedClass.hasSuper()
				&& selectedClass.getSuperDef().equals(superClass);
	}

	public IClassDefinition getSuperClass(IClassDefinition selectedClass) {
		for (IClassDefinition cl : classes) {
			if (isSuperTo(cl, selectedClass)) {
				return cl;
			}
		}
		return null;
	}

	public boolean isSuperTo(IInterfaceDefinition superClass,
			IInterfaceDefinition selectedClass) {
		Set<IInterfaceDefinition> selectedSupers = new HashSet<IInterfaceDefinition>();
		if (selectedClass instanceof InterfaceDefinition) {
			selectedSupers.addAll(((InterfaceDefinition) selectedClass).supers);
		} else if (selectedClass instanceof IClassDefinition) {
			selectedSupers.addAll(((IClassDefinition) selectedClass)
					.getInterfaces());
			selectedSupers
					.add(((IClassDefinition) selectedClass).getSuperDef());
		}

		for (IInterfaceDefinition intf : selectedSupers) {
			if (superClass.getName().equals(intf.getName())
					|| isSuperTo(superClass, intf)) {
				return true;
			}
		}
		return false;
	}

	public List<IClassDefinition> getSubClasses(IClassDefinition c) {
		List<IClassDefinition> subclasses = new Vector<IClassDefinition>();
		for (IClassDefinition sub : classes) {
			// if (sub.getSuperClassDefinition() != null
			// && sub.getSuperClassDefinition().equals(c))
			// {
			if (isSuperTo(c, sub)) {
				subclasses.add(sub);
			}
		}
		return subclasses;
	}

	public IInterfaceDefinition getTaggedDef(String tag) {
		for (IInterfaceDefinition def : interfaces) {
			if (def.getTag().equals(tag))

			{
				return def;
			}
		}
		return null;
	}

	public void addToStringAddOn(ToStringAddOn addon) {
		toStringAddOn.add(addon);
	}

	public List<ToStringAddOn> getToStringAddOns() {
		return this.toStringAddOn;
	}

	public void addCommonTreeInterface(IInterfaceDefinition source,
			IInterfaceDefinition intf) {
		addInterface(intf);
		treeNodeInterfaces.put(source, intf);
	}

	public IInterfaceDefinition getInterfaceForCommonTreeNode(
			IInterfaceDefinition node) {
		if (treeNodeInterfaces.containsKey(node)) {
			return treeNodeInterfaces.get(node);
		}
		/*
		 * System.out.println("Error getting interface for common tree node:\n\t"
		 * + node.getName() +
		 * "\nThe mapping is as defined as follows in environment \"" +
		 * this.name + "\": "); Set<Entry<IInterfaceDefinition,
		 * IInterfaceDefinition>> l = treeNodeInterfaces .entrySet(); for
		 * (Entry<IInterfaceDefinition, IInterfaceDefinition> e : l) {
		 * System.out.println("\t" + e.getKey().getName() + " ==> " +
		 * e.getValue().getName()); }
		 */
		return null;
	}

	public boolean isTreeNode(IClassDefinition c) {
		if (classToType.containsKey(c)) {
			switch (classToType.get(c)) {
			case Alternative:
			case Production:
			case SubProduction:
			case Token:
				return true;
			}
		}
		return false;
	}

	public String getInheritanceToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("* \n* " + name + "\n*\n");
		for (IInterfaceDefinition cl : interfaces) {
			sb.append(pad("interface " + cl.getName().getName(), 40)
					+ pad(cl.getName().getPackageName(), 35)
					+ inheritanceString(cl) + "\n");

		}
		for (IClassDefinition cl : classes) {
			sb.append(pad("class " + cl.getName().getName(), 40)
					+ pad(cl.getName().getPackageName(), 35)
					+ inheritanceString(cl) + "\n");

		}
		return sb.toString();
	}

	private String inheritanceString(IInterfaceDefinition def) {
		if (def instanceof IClassDefinition) {
			String tmp = "";
			if (!((IClassDefinition) def).getInterfaces().isEmpty()) {
				tmp += "(";
				for (Iterator<IInterfaceDefinition> itr = ((IClassDefinition) def)
						.getInterfaces().iterator(); itr.hasNext();) {
					IInterfaceDefinition type = itr.next();
					tmp += type.getName().getName();
				}
				tmp += ")";
			}
			if (((IClassDefinition) def).hasSuper()) {
				return (tmp.length() > 0 ? ": " + tmp : "")
						+ " <- "
						+ ((IClassDefinition) def).getSuperDef().getName()
								.getName()
						+ inheritanceString(((IClassDefinition) def)
								.getSuperDef());
			}
			return tmp.length() > 0 ? ": " + tmp : "";
		}

		if (def instanceof InterfaceDefinition) {
			if (!((InterfaceDefinition) def).supers.isEmpty()) {
				String tmp = "";
				for (IInterfaceDefinition intf : ((InterfaceDefinition) def).supers) {
					tmp += " <- " + intf.getName().getName();
				}
				return tmp;

			}
			return "";
		}

		return "";
	}

}
