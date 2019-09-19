package x.y.z;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.oaklandsw.base.Messages;
import com.oaklandsw.transform.struct.StructNode;

public class Codes extends WizardPage implements InputSourceProvider {

	public WorspaceNodeSourcePage() {
		setTitle(NLS.bind(Messages.M1_SE,
				"xxx"));
			String foo = Messages.M2_SE + "xxx";
			String bar = Messages
							.M3_SE 
								+ "xxx";
			labelText = true 
							? Messages.M3_SE_X 
							: Messages.M7_SE 
						 + "xxx";
			labelText = Messages.getM5() + "xxx";
			se.setText(Messages.M8_SE
					+ _deMap.getExecuteMap().getIdentifyingPath());
			_log.error(Messages.M09_SE
					+ ecint.getInputFile()
					+ Messages.M10 + movedFile
					+ Messages.M11
					+ ecint.getMapRuntime());
		}
			StructNode sa = re.findStruct(folderName + "/Messages/" + folderName);
			if (_state._pm != null) {
				if (_state._pm.isCanceled()) {
					_state._canceled = true;
					throw new IOException(Messages.MX);
				}
				_state._pm.subTask(
						Messages.MY + path.toString());
			}
			MessageDialog.openError(
					Display.getDefault().getActiveShell(),
					Messages.Z,
					noColumnsMsg.toString() + Messages.MapperComponent_noCols_msg);
			throw new AvroExprException(Messages.getMessage("M6", "Bytes"));
	}
}
