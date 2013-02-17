package ee.xero.cjscompiler.app.beans;

import static ee.xero.cjscompiler.app.beans.ClosureCompilerClient.CompilationLevel.SIMPLE_OPTIMIZATIONS;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ee.xero.cjscompiler.app.util.FileUtils;

@Component
public class ClosureCompilerClient {
	
	public static enum CompilationLevel { WHITESPACE_ONLY, SIMPLE_OPTIMIZATIONS, ADVANCED_OPTIMIZATIONS }
	
	private static final String URL = "http://closure-compiler.appspot.com/compile";
	
	private static final String LINE_BREAK_PATTERN = "[\n\f\r]";
	
	@Autowired
	private HttpClient http;
	
	public String compile(String code) throws IOException {
		return compile(code, SIMPLE_OPTIMIZATIONS);
	}
	
	public String compile(String code, CompilationLevel level) throws IOException {
		HttpResponse response = http.execute(composeRequest(code, level));
		
		return stripBreakes(toString(response));
	}
	
	private String toString(HttpResponse response) throws IOException {
		return FileUtils.toString(response.getEntity().getContent());
	}
	
	private String stripBreakes(String str) {
		return str.replaceAll(LINE_BREAK_PATTERN, "");
	}
	
	private HttpPost composeRequest(String code, CompilationLevel level) throws UnsupportedEncodingException {
		HttpPost post = new HttpPost(URL);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(param("output_info", "compiled_code"));
		nameValuePairs.add(param("output_format", "text"));
		nameValuePairs.add(param("js_code", code));
		nameValuePairs.add(param("compilation_level", level));

		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		return post;
	}
	
	private NameValuePair param(String name, CompilationLevel value) {
		return param(name, value.name());
	}
	
	private NameValuePair param(String name, String value) {
		return new BasicNameValuePair(name, value);
	}
}
