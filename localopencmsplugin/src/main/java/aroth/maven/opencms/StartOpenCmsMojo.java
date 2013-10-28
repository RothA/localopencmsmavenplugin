package aroth.maven.opencms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Start the local OpenCms Environment
 * 
 * @goal start
 * 
 */
public class StartOpenCmsMojo extends BaseOpenCmsAbstractMojo {

	/** Utils object to use */
	private Utils utils;

	public Utils getUtils() {
		return utils;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	public StartOpenCmsMojo() {
		this.setUtils(new Utils(this.getLog()));
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		this.getLog().info("Starting Local OpenCms Environment...");

		List<String> commandList = new ArrayList<String>();

		// Get Java executable
		String javaExec = System.getProperty("java.home") + "/bin/java";
		commandList.add(javaExec);

		// Add Parameters
		String params = DEFAULT_JVM_PARAMS;

		if (!StringUtils.isEmpty(this.getJvmParameters())) {
			params = this.getJvmParameters();
		}

		String[] jvmParams = params.split(" ");
		for (int i = 0; i < jvmParams.length; i++) {
			commandList.add(jvmParams[i]);
		}

		// Add -jar
		commandList.add("-jar");

		// Add startup jar
		commandList.add(this.getStartupJar().getPath());

		try {

			// Stop any current process
			this.getUtils().stopOpenCmsProcess(
					this.getTargetDirectory().getPath());

			// Get Java Pids List
			List<String> beforePids = this.getUtils().getJavaPids();

			this.getLog().info("Java Pids before launch: " + beforePids);

			// Run the process

			this.getLog().info("Command list: " + commandList);

			Process proc = new ProcessBuilder(commandList).directory(
					this.getTargetDirectory()).start();

			this.getLog().info("OpenCms starting...");

			// Get After Java Pids
			List<String> afterPids = this.getUtils().getJavaPids();

			this.getLog().info("Java Pids after launch: " + afterPids);

			List<String> finalPids = this.getUtils().getListDifferences(
					beforePids, afterPids);

			this.getLog().info("OpenCms Pid: " + finalPids);

			if (finalPids.size() > 0) {
				// Write first PID to file
				this.getUtils().writePidToFile(finalPids.get(0),
						this.getTargetDirectory().getPath());

				this.getLog().info(
						"OpenCms Pid Written to File: " + finalPids.get(0));
			}

		} catch (IOException e) {
			this.getLog().error("Error running startup jar", e);
			throw new MojoExecutionException("Error running startup jar", e);
		}

	}
}
