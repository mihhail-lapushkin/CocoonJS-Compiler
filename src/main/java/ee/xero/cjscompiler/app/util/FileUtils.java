package ee.xero.cjscompiler.app.util;

import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.apache.commons.io.FileUtils.copyDirectoryToDirectory;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class FileUtils {
	
	public static String toPath(String... parts) {
		String path = "";
		
		for (int i = 0; i < parts.length; i++) {
			path += parts[i];
			
			if (i < parts.length - 1) {
				path += File.separator;
			}
		}
		
		return path;
	}

	public static String toString(InputStream is) throws IOException {
		return IOUtils.toString(is, UTF_8);
	}
	
	public static String toString(File f) throws IOException {
		return readFileToString(f, UTF_8);
	}
	
	public static String toString(String f) throws IOException {
		return toString(new File(f));
	}
	
	public static String toString(List<File> files) throws IOException {
		StringBuffer result = new StringBuffer();
		
		for (File file : files) {
			result.append(toString(file));
		}
		
		return result.toString();
	}
	
	public static void toFile(String s, File f) throws IOException {
		writeStringToFile(f, s, UTF_8);
	}
	
	public static void toFile(String s, String f) throws IOException {
		toFile(s, new File(f));
	}
	
	public static void copyDirectory(File from, File to) throws IOException {
		deleteDirectory(to);
		copyDirectoryToDirectory(from, to);
	}
	
	public static void copyDirectory(String from, String to) throws IOException {
		copyDirectory(new File(from), new File(to));
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> toLines(File f) throws IOException {
		return readLines(f, UTF_8);
	}
}
