/*
 * #%~
 * The Overture Abstract Syntax Tree
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ast.preview;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class GraphViz
{
	public class GraphVizException extends Exception
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5213060939651640284L;

		public GraphVizException(String message)
		{
			super(message);
		}

		public GraphVizException(String string, Throwable e)
		{
			super(string, e);
		}
	}

	/**
	 * Where is your dot program located? It will be called externally.
	 */
	private final String dotPath;
	/**
	 * The source of the graph written in dot language.
	 */
	private StringBuilder graph = new StringBuilder();

	/**
	 * Constructor: creates a new GraphViz object that will contain a graph.
	 */
	public GraphViz()
	{
		if (isWindowsPlatform())
		{
			dotPath = "\"c:/Program Files/Graphviz 2.28/bin/dot.exe\"".replace('/', '\\');
		} else
		{
			dotPath = "/usr/bin/dot";
		}
	}

	public GraphViz(File dotPath)
	{
		this.dotPath = dotPath.getPath();
	}

	public static boolean isWindowsPlatform()
	{
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	/**
	 * Returns the graph's source description in dot language.
	 * 
	 * @return Source of the graph in dot language.
	 */
	public String getDotSource()
	{
		return graph.toString();
	}

	/**
	 * Adds a string to the graph's source (without newline).
	 * 
	 * @param line
	 */
	public void add(String line)
	{
		graph.append(line);
	}

	/**
	 * Adds a string to the graph's source (with newline).
	 * 
	 * @param line
	 */
	public void addln(String line)
	{
		graph.append(line + "\n");
	}

	/**
	 * Adds a newline to the graph's source.
	 */
	public void addln()
	{
		graph.append('\n');
	}

	/**
	 * Returns the graph as an image in binary format.
	 * 
	 * @param dot_source
	 *            Source of the graph to be drawn.
	 * @param type
	 *            Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
	 * @return A byte array containing the image of the graph.
	 * @throws GraphVizException
	 */

	public byte[] getGraph(String dot_source, String type)
			throws GraphVizException
	{
		File dot;
		byte[] img_stream;
		try
		{
			dot = writeDotSourceToFile(dot_source);
			if (dot != null)
			{
				img_stream = get_img_stream(dot, type);
				if (!dot.delete())
				{
					throw new GraphVizException("Warning: "
							+ dot.getAbsolutePath() + " could not be deleted!");
				}
				return img_stream;
			}
			return null;

		} catch (IOException ioe)
		{
			return null;
		}

	}

	/**
	 * Writes the graph's image in a file.
	 * 
	 * @param img
	 *            A byte array containing the image of the graph.
	 * @param file
	 *            Name of the file to where we want to write.
	 * @return Success: 1, Failure: -1
	 */
	public int writeGraphToFile(byte[] img, String file)
	{
		File to = new File(file);
		return writeGraphToFile(img, to);
	}

	/**
	 * Writes the graph's image in a file.
	 * 
	 * @param img
	 *            A byte array containing the image of the graph.
	 * @param to
	 *            A File object to where we want to write.
	 * @return Success: 1, Failure: -1
	 */
	public int writeGraphToFile(byte[] img, File to)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(to);
			fos.write(img);
			fos.close();
		} catch (IOException ioe)
		{
			return -1;
		}

		return 1;

	}

	/**
	 * It will call the external dot program, and return the image in binary format.
	 * 
	 * @param dot
	 *            Source of the graph (in dot language).
	 * @param type
	 *            Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
	 * @return The image of the graph in .gif format.
	 * @throws GraphVizException
	 */
	private byte[] get_img_stream(File dot, String type)
			throws GraphVizException
	{
		File img;
		byte[] img_stream;
		try
		{
			img = File.createTempFile("graph_", "." + type);
			Runtime rt = Runtime.getRuntime();
			// patch by Mike Chenault

			String[] args = { dotPath, "-T" + type, dot.getAbsolutePath(),
					"-o", img.getAbsolutePath() };
			Process p = rt.exec(args);
			p.waitFor();
			FileInputStream in = new FileInputStream(img.getAbsolutePath());
			img_stream = new byte[in.available()];
			in.read(img_stream);
			// Close it if we need to
			if (in != null)
			{
				in.close();
			}
			if (!img.delete())
			{
				throw new GraphVizException("Warning: " + img.getAbsolutePath()
						+ " could not be deleted!");
			}
		} catch (IOException ioe)
		{
			throw new GraphVizException("Error: in I/O processing of tempfile in dir. Or in calling external command", ioe);
		} catch (InterruptedException ie)
		{
			throw new GraphVizException("Error: the execution of the external program was interrupted", ie);
		}
		return img_stream;
	}

	/**
	 * Writes the source of the graph in a file, and returns the written file as a File object.
	 * 
	 * @param str
	 *            Source of the graph (in dot language).
	 * @return The file (as a File object) that contains the source of the graph.
	 * @throws GraphVizException
	 */
	private File writeDotSourceToFile(String str) throws java.io.IOException,
			GraphVizException
	{
		File temp;
		try
		{
			temp = File.createTempFile("graph_", ".dot.tmp");
			FileWriter fout = new FileWriter(temp);
			fout.write(str);
			fout.close();
		} catch (Exception e)
		{
			throw new GraphVizException("Error: I/O error while writing the dot source to temp file!");
		}
		return temp;
	}

	/**
	 * Returns a string that is used to start a graph.
	 * 
	 * @return A string to open a graph.
	 */

	public String start_graph()
	{
		return "digraph G {";
	}

	/**
	 * Returns a string that is used to end a graph.
	 * 
	 * @return A string to close a graph.
	 */

	public String end_graph()
	{
		return "}";
	}

	/**
	 * Read a DOT graph from a text file.
	 * 
	 * @param input
	 *            Input text file containing the DOT graph source.
	 * @throws GraphVizException
	 */
	public void readSource(String input) throws GraphVizException
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			FileInputStream fis = new FileInputStream(input);
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String line;
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			dis.close();
		} catch (Exception e)
		{
			throw new GraphVizException("Error: " + e.getMessage());
		}
		this.graph = sb;
	}

}
