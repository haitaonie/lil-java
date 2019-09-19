package x.y.z;

public class Codes extends WizardPage implements InputSourceProvider {

	protected void doRename(MapExecutionContextInternal ecint, String outDir) {

		if (!FileUtils.renameFileTo(ecint.getInputFile(), movedFile)) {
			_log.error(NLS.bind(Messages.M1,
 ecint.getInputFile()
					+ NLS.bind(Messages.M2,
 movedFile
					+ NLS.bind(Messages.M3,
 ecint.getMapRuntime());
		}
	}
		
}
