package org.overture.ide.plugins.idedebughelper.actions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

public class Profiler {
	public static byte[] sizeOf(Object obj) throws java.io.IOException {
		ByteArrayOutputStream byteObject = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteObject);
		objectOutputStream.writeObject(obj);
		objectOutputStream.flush();
		objectOutputStream.close();
		byteObject.close();

		return byteObject.toByteArray();
	}

	public static double sizeOfInKb(Object obj) {
		try {
			byte[] bytes = sizeOf(obj);
			// System.out.println("Size: "+ bytes.length/((double)1000) +
			// " kB");
			return bytes.length / ((double) 1000);
		} catch (NotSerializableException e) {
			return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
}
