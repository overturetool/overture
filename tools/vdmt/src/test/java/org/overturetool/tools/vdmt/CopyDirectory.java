package org.overturetool.tools.vdmt;

import java.io.*;

public class CopyDirectory
{



	public void copyDirectory(File srcPath, File dstPath) throws IOException
	{

		if (srcPath.isDirectory()&& !srcPath.getName().toLowerCase().equals(".svn"))
		{

			if (!dstPath.exists())
			{

				dstPath.mkdir();

			}

			String files[] = srcPath.list();

			for (int i = 0; i < files.length; i++)
			{
				copyDirectory(new File(srcPath, files[i]), new File(dstPath,
						files[i]));

			}

		}

		else
		{

			if (!srcPath.exists())
			{

				System.out.println("File or directory does not exist.");

				System.exit(0);

			}

			else if(srcPath.isFile())

			{

				InputStream in = new FileInputStream(srcPath);
				OutputStream out = new FileOutputStream(dstPath);
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];

				int len;

				while ((len = in.read(buf)) > 0)
				{

					out.write(buf, 0, len);

				}

				in.close();

				out.close();

			}

		}

		//System.out.println("Directory copied.");

	}

}