package x.y.z;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.talend.transform.expression.language.interpreter.AvroExprException;
import org.talend.transform.expression.language.interpreter.Messages;

public class Codes extends WizardPage implements InputSourceProvider {
	private IFolder _folder;

	public WorspaceNodeSourcePage() {
		setTitle(NLS.bind(Messages.M1,
				"xxx"));
	}

	private void addWorkspaceNode() {

		String labelText = NLS
					.bind
						(
								Messages
									.M2,
				"xxx");
	}

	private void handleResourceSelection(Object[] objs) {
		String labelText = null;
		_inputSource = null;
		IResource res = (objs == null || objs.length < 1) ? null
				: (IResource) objs[0];
		if (res == null) {
			labelText = Messages.foo(); // M-4 
		} else {
			labelText = Messages.getM5();
		}
		throw new AvroExprException(Messages.getMessage("M6", "Bytes"));
	}
}
