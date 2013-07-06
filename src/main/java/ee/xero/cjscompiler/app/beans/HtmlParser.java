package ee.xero.cjscompiler.app.beans;

import static ee.xero.cjscompiler.app.util.FileUtils.toLines;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HtmlParser {
	
	private static final String SRC_EXTRACTION_PATTERN = ".+src=['\"]([^'\"]+)['\"].+";
	
	private static final String SCRIPT_TAG_NAME = "script";

	@Value("${js.ignore}")
	private String[] ignoreJS;
	
	@Value("${js.path.partsToStrip}")
	private String[] partsToStrip;
	
	private boolean isIgnored(String line) {
		for (String js : ignoreJS) {
			if (line.contains(js + ".js")) {
				return true;
			}
		}
		
		return false;
	}
	
	private String stripPathParts(String url) {
		for (String part : partsToStrip) {
			url = url.replace(part, "");
		}
		
		return url;
	}
	
	public List<File> findJsFiles(String html) throws IOException {
		return findJsFiles(new File(html));
	}
	
	public List<File> findJsFiles(File html) throws IOException {
		Pattern jsPattern = Pattern.compile(SRC_EXTRACTION_PATTERN);
		
		List<File> scripts = new ArrayList<File>();
		
		for (String line : toLines(html)) {
			if (line.contains(SCRIPT_TAG_NAME)) {
				if (isIgnored(line)) {
					continue;
				}

				Matcher m = jsPattern.matcher(line);

				if (m.find()) {
					scripts.add(new File(stripPathParts(m.group(1))));
				}
			}
		}
		
		return scripts;
	}
}
