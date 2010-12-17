package org.overture.ide.core.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.overture.ide.core.VdmCore;
import org.overture.ide.internal.core.ResourceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModelPath
{
	final IVdmProject vdmProject;
	final IProject project;
	final File modelPathFile;

	List<IContainer> srcPaths = new Vector<IContainer>();
	IContainer output;

	public ModelPath(IVdmProject project)
	{
		this.vdmProject = project;

		this.project = (IProject) this.vdmProject.getAdapter(IProject.class);
		IPath base = this.project.getLocation();
		base = base.append(".modelpath");
		this.modelPathFile = base.toFile();
		parse();
	}

	private boolean hasModelPath()
	{
		return this.modelPathFile.exists();
	}

	private IContainer getDefaultModelSrcPath()
	{
		return this.project;
	}

	public List<IContainer> getModelSrcPaths()
	{
		if (!hasModelPath())
		{
			srcPaths.add(getDefaultModelSrcPath());
			return srcPaths;
		}

		return srcPaths;
	}

	public IContainer getOutput()
	{
		return this.output;
	}

	private void parse()
	{
		if(!hasModelPath())
		{
			return;
		}
		try
		{
			File file = this.modelPathFile;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("modelpathentry");
			for (int s = 0; s < nodeLst.getLength(); s++)
			{
				Node fstNode = nodeLst.item(s);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Node kindAttribute = fstNode.getAttributes().getNamedItem("kind");
					String kindValue = kindAttribute.getNodeValue();
					if (kindValue != null)
					{
						if (kindValue.equals("src"))
						{
							Node pathAttribute = fstNode.getAttributes().getNamedItem("path");
							String pathValue = pathAttribute.getNodeValue();
							srcPaths.add(this.project.getFolder(pathValue));
						} else if (kindValue.equals("output"))
						{
							Node pathAttribute = fstNode.getAttributes().getNamedItem("path");
							String pathValue = pathAttribute.getNodeValue();
							output = this.project.getFolder(pathValue);
						}
					}
				}

			}
		} catch (Exception e)
		{
			VdmCore.log("Faild to parse .modelpath file", e);
		}
	}

	public void save(List<IContainer> srcPaths, IContainer output)
			throws CoreException
	{
		StringBuffer sb = new StringBuffer();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<modelpath>\n");

		for (IContainer src : srcPaths)
		{
			sb.append("\t<modelpathentry kind=\"src\" path=\""
					+ src.getProjectRelativePath() + "\"/>\n");
		}

		if (output != null)
		{
			sb.append("\t<modelpathentry kind=\"output\" path=\""
					+ output.getProjectRelativePath() + "\"/>\n");
		}
		sb.append("</modelpath>");

		PrintWriter out = null;
		try
		{
			FileWriter outFile = new FileWriter(this.modelPathFile);
			out = new PrintWriter(outFile);
			out.println(sb.toString());

		} catch (IOException e)
		{
			VdmCore.log("Faild to save .modelpath file", e);
		} finally
		{
			out.close();
		}
		ResourceManager.getInstance().syncBuildPath(vdmProject);

	}
}
