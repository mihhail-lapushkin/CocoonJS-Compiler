package ee.xero.cjscompiler.app.beans;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonFileMinifyVisitor extends SimpleFileVisitor<Path> {
	
	private static final Logger LOG = Logger.getLogger(JsonFileMinifyVisitor.class);
	
	@Autowired
	private JsonFileMinifier jsonMinifier;
	
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) {
    	File file = path.toFile();
 
    	if (file.isDirectory() ||
    		file.isHidden() ||
    		attr.isSymbolicLink() ||
    		!file.getName().endsWith("json")) { return CONTINUE; }
    	
    	try {
			jsonMinifier.minify(file);
		} catch (IOException e) {
			LOG.error(e);
		}
    	
        return CONTINUE;
    }
    
    @Override
    public FileVisitResult postVisitDirectory(Path path, IOException e) {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path path, IOException e) {
        return CONTINUE;
    }
}
