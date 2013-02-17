package ee.xero.cjscompiler.app.beans;

import static ee.xero.cjscompiler.app.beans.ClosureCompilerClient.CompilationLevel.WHITESPACE_ONLY;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ee.xero.cjscompiler.app.util.FileUtils;


@Component
public class JsonFileMinifier {
	
	private static final String FAKE_VARIABLE = "fakevar=";

	@Autowired
	private ClosureCompilerClient compiler;
	
    public void minify(File file) throws IOException {
    	String json = FileUtils.toString(file);
    	
    	String minified = compiler.compile(addFakeVariable(json), WHITESPACE_ONLY);
    	
    	minified = removeSemicolons(removeFakeVariable(minified));
    	
    	FileUtils.toFile(minified, file);
    }
    
    private String addFakeVariable(String json) {
    	return FAKE_VARIABLE + json;
    }
    
    private String removeFakeVariable(String json) {
    	return json.substring(FAKE_VARIABLE.length());
    }
    
    private String removeSemicolons(String json) {
    	return json.replaceAll(";", "");
    }
}
