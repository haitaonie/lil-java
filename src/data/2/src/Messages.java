package x.y.z;

import org.eclipse.osgi.util.NLS;

import com.oaklandsw.transform.exporting.Messages;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "x.y.z.messages"; //$NON-NLS-1$
	public static String M1;
	public static String M2;
	public static String M3_UNUSED;
	public static String M4;
	public static String M5;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {}
	
	public static String foo(Object... objects) {
		return NLS.bind(Messages.M4, objects);
	}
	
	public static String getM5(Object... objects) {
		return NLS.bind(Messages.M5, objects);
	}
}
