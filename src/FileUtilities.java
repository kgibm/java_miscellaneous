import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author kevin.grigorenko@us.ibm.com
 */
public class FileUtilities {
	public static byte[] readBytes(String file) throws IOException {
		File f = new File(file);
		byte[] buffer = new byte[(int) f.length()];
		InputStream ios = null;
		try {
			ios = new FileInputStream(file);
			if (ios.read(buffer) == -1) {
				throw new EOFException();
			}
			return buffer;
		} finally {
			if (ios != null) {
				ios.close();
			}
		}
	}

	public static void writeBytes(byte[] data, String file) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(file);
		try {
			fos.write(data);
			fos.flush();
		} finally {
			fos.close();
		}
	}
}
