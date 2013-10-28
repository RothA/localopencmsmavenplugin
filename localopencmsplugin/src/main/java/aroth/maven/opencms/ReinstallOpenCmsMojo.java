package aroth.maven.opencms;

/**
 * Reinstall a local OpenCms Environment
 * 
 * @goal reinstall
 * 
 */
public class ReinstallOpenCmsMojo extends InitOpenCmsMojo {

	public ReinstallOpenCmsMojo() {
		this.setDestroyEnvironment(true);
	}
}
