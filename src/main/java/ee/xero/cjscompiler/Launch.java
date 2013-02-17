package ee.xero.cjscompiler;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Launch {
	public static void main(String[] args) {
		new AnnotationConfigApplicationContext(Launch.class.getPackage().getName());
	}
}
