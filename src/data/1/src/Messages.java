package x.y.z;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "x.y.z.messages"; //$NON-NLS-1$
	public static String M1;
	public static String M2_UNUSED;
	public static String M3;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {}
}
