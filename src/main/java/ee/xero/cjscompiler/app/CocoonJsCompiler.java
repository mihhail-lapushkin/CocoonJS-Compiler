package ee.xero.cjscompiler.app;

import static ee.xero.cjscompiler.app.util.FileUtils.copyDirectory;
import static ee.xero.cjscompiler.app.util.FileUtils.toFile;
import static ee.xero.cjscompiler.app.util.FileUtils.toPath;
import static java.nio.file.Files.walkFileTree;
import static org.apache.commons.io.FileUtils.deleteDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ee.xero.cjscompiler.app.beans.ClosureCompilerClient;
import ee.xero.cjscompiler.app.beans.HtmlParser;
import ee.xero.cjscompiler.app.beans.JsonFileMinifyVisitor;
import ee.xero.cjscompiler.app.beans.ZipArchiver;
import ee.xero.cjscompiler.app.util.FileUtils;

@Component
public class CocoonJsCompiler {
	
	private static final Logger LOG = Logger.getLogger(CocoonJsCompiler.class);
	
	private static final String ARCHIVE_FILE_NAME = "archive.zip";
	
	private static final String HTML_FILE_NAME = "index.html";
	
	private static final String AUDIO_RESOURCES_FOLDER_NAME = "aud";
	
	private static final String RESOURCES_FOLDER_NAME = "resources";
	
	private static final String TARGET_FOLDER_NAME = "target";
	
	private static final String TARGET_HTML_PATH = toPath(TARGET_FOLDER_NAME, HTML_FILE_NAME);
	private static final String TARGET_ARCHIVE_PATH = toPath(TARGET_FOLDER_NAME, ARCHIVE_FILE_NAME);
	private static final String TARGET_RESOURCES_PATH = toPath(TARGET_FOLDER_NAME, RESOURCES_FOLDER_NAME);
	private static final String AUDIO_RESOURCES_PATH = toPath(TARGET_RESOURCES_PATH, AUDIO_RESOURCES_FOLDER_NAME);
	
	@Value("${resources.dir.sound}")
	private String soundDir;
	
	@Autowired
	private HtmlParser htmlParser;
	
	@Autowired
	private ClosureCompilerClient compiler;
	
	@Autowired
	private JsonFileMinifyVisitor jsonVisitor;
	
	@Autowired
	private ZipArchiver zipArchiver;

	@PostConstruct
	public void run() throws IOException {
		copyResourcesToTarget();
		
		String mergedJs = parseHtmlFile();
		
		compileJs(mergedJs, TARGET_HTML_PATH);
		
		minifyJsonFiles(TARGET_RESOURCES_PATH);
		
		deleteUneededSoundFolders(AUDIO_RESOURCES_PATH);
		
		packageToZip(TARGET_ARCHIVE_PATH, new String[] { TARGET_RESOURCES_PATH, TARGET_HTML_PATH });
		
		LOG.info("[ DONE ]");
	}
	
	private void copyResourcesToTarget() throws IOException {
		LOG.info("[1] Copying resources to target");
		
		copyDirectory(RESOURCES_FOLDER_NAME, TARGET_FOLDER_NAME);
	}
	
	private String parseHtmlFile() throws IOException {
		LOG.info("[2] Parsing HTML file");
		
		List<File> scripts = htmlParser.findJsFiles(HTML_FILE_NAME);
		return FileUtils.toString(scripts);
	}
	
	private void compileJs(String mergedJs, String targetHtmlPath) throws IOException {
		LOG.info("[3] Compiling JavaScript");
		
		String compiledJs = compiler.compile(mergedJs);
		
		toFile("<script>" + compiledJs + "</script>", targetHtmlPath);
	}
	
	private void minifyJsonFiles(String targetResourcesPath) throws IOException {
		LOG.info("[4] Minifying JSON files");
		
		walkFileTree(new File(targetResourcesPath).toPath(), jsonVisitor);
	}
	
	private void deleteUneededSoundFolders(String audioResourcesPath) throws IOException {
		LOG.info("[5] Deleting unneeded sound folders");
		
		for (File f : new File(audioResourcesPath).listFiles()) {
			if (!f.getName().equals(soundDir)) {
				if (f.isDirectory()) {
					deleteDirectory(f);
				} else {
					f.delete();
				}
			}
		}
	}
	
	private void packageToZip(String targetArchivePath, String[] files) throws IOException {
		LOG.info("[6] Packaging to ZIP");
		
		zipArchiver.addFilesToZip(targetArchivePath, files);
	}
}
