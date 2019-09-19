package x.y.z;

import com.oaklandsw.transform.struct.StructNode;

public class Codes extends WizardPage implements InputSourceProvider {

	public WorspaceNodeSourcePage() {
		setTitle(NLS.bind(Messages.M1,
				"xxx"));
			String labelText = Messages.M2 + "xxx";
		
			labelText = Messages.foo() + "xxx"; // M-4 
			labelText = Messages.getM5() + "xxx";
			StructNode sa = re.findStruct(folderName + "/Messages/" + folderName);
		throw new AvroExprException(Messages.getMessage("M6", "Bytes"));
	}
}
