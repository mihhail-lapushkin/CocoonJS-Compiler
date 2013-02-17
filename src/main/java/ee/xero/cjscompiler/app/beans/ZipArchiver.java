package ee.xero.cjscompiler.app.beans;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class ZipArchiver {

	public void addFilesToZip(String archive, String[] files) throws IOException {
		ZipArchiveOutputStream zos = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(archive)));
		
		for (String file : files) {
			addFileToZip(zos, file, "");
		}
		
		zos.finish();
        zos.close();
	}
	
	private void addFileToZip(ZipArchiveOutputStream zos, String path, String base) throws IOException {
        File f = new File(path);
        String entryName = base + f.getName();
        ZipArchiveEntry zipEntry = new ZipArchiveEntry(f, entryName);
 
        zos.putArchiveEntry(zipEntry);
 
        if (f.isFile()) {
        	processFile(f, zos);
        } else {
        	processDirectory(f, entryName, zos);
        }
    }
	
	private void processFile(File f, ZipArchiveOutputStream zos) throws IOException {
        FileInputStream fInputStream = null;
        try {
            fInputStream = new FileInputStream(f);
            IOUtils.copy(fInputStream, zos);
            zos.closeArchiveEntry();
        } finally {
            IOUtils.closeQuietly(fInputStream);
        }
	}
	
	private void processDirectory(File f, String entryName, ZipArchiveOutputStream zos) throws IOException {
        zos.closeArchiveEntry();
        File[] children = f.listFiles();

        if (children != null) {
            for (File child : children) {
                addFileToZip(zos, child.getAbsolutePath(), entryName + "/");
            }
        }
	}
}
