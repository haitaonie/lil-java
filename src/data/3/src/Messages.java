package x.y.z;

import org.eclipse.osgi.util.NLS;

import com.oaklandsw.transform.exporting.Messages;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "x.y.z.messages"; //$NON-NLS-1$
	public static String M1_SE;
	public static String M2_SE;
	public static String M3_SE;
	public static String M4_SN;
	public static String M5;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {}
	
	public static String foo(Object... objects) {
		return Messages.M4_SN + objects;
	}
	
	public static String getM5(Object... objects) {
		return NLS.bind(Messages.M5, objects);
	}
}
