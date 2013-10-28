package aroth.maven.opencms;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Stop the local OpenCms Environment
 * 
 * @goal stop
 * 
 */
public class StopOpenCmsMojo extends BaseOpenCmsAbstractMojo {

	/** Utils object to use */
	private Utils utils;

	public Utils getUtils() {
		return utils;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	/**
	 * Public constructor
	 */
	public StopOpenCmsMojo() {
		this.setUtils(new Utils(this.getLog()));
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		this.getLog().info("Stopping Local OpenCms Environment...");

		this.getUtils().stopOpenCmsProcess(this.getTargetDirectory().getPath());

		this.getLog().info("OpenCms Process Stopped...");
	}

}
