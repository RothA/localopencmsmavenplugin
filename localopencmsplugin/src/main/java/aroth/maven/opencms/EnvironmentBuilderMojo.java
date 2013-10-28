package aroth.maven.opencms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Handles installing recently created modules into a default OpenCms
 * environment then creating an environment zip file
 * 
 * @author aroth
 * 
 * @goal installmodules
 * 
 */
public class EnvironmentBuilderMojo extends InitOpenCmsMojo {

	/**
	 * Deploy script file name
	 */
	private static String DEPLOY_SCRIPT_FILE_NAME = "/deploy_script.txt";

	/**
	 * Public constructor
	 */
	public EnvironmentBuilderMojo() {

		super();

	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// Override OpenCms Environment with Base OpenCms Zip Url
		if (this.getBaseOpenCmsZipUrl() != null) {
			this.setOpenCmsEnvironmentZipFile(this.getBaseOpenCmsZipUrl());
		}

		// Execute OpenCms init
		super.execute();

		try {
			this.getLog().info("Starting environment builder mojo...");
			this.getLog().info(
					"Base Zip File: "
							+ this.getBaseOpenCmsZipUrl()
							+ ", Output Zip File: "
							+ this.getOutputOpenCmsZipFile()
							+ ", Modules to Import: "
							+ Arrays.toString(this.getModuleImportFiles()
									.toArray(new String[0])));

			// Execute shell
			File scriptFile = new File(this.getTargetDirectory()
					.getAbsolutePath() + DEPLOY_SCRIPT_FILE_NAME);

			// Delete script file if it already exists
			if (scriptFile.exists()) {
				scriptFile.delete();
			}

			this.getLog()
					.info("Starting script creation: "
							+ scriptFile.getAbsolutePath());

			// Only continue if script file is not null
			if (scriptFile != null) {

				// Write to file
				FileWriter fw = new FileWriter(scriptFile.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);

				// Get contents
				String fileContents = this.generateInstallScriptContents();

				// Write to file
				bw.write(fileContents);

				// Close file writer
				bw.close();
			}

			this.getLog().info("End of script creation...");

			// Get execution command list
			List<String> commandList = this
					.getExecutableCommandList(scriptFile);

			this.getLog().info(
					"Running commands: "
							+ Arrays.toString(commandList
									.toArray(new String[0])));

			// Run script
			this.executeScript(commandList);

			this.getLog().info("Deploy complete...");

			// Check that output file is created
			if (this.getOutputOpenCmsZipFile() != null) {

				this.getLog().info("Creating Zip file ...");

				// Zip up directory
				ZipFile zipFile = new ZipFile(this.getOutputOpenCmsZipFile());

				// Initiate Zip Parameters which define various properties such
				// as compression method, etc.
				ZipParameters parameters = new ZipParameters();

				// set compression method to store compression
				parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

				// Set the compression level
				parameters
						.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

				// Exclue root folder
				parameters.setIncludeRootFolder(false);

				zipFile.addFolder(this.getTargetDirectory(), parameters);

				this.getLog().info(
						"Zip file created: "
								+ zipFile.getFile().getAbsolutePath());
			}

		} catch (Exception ec) {
			this.getLog().error(
					"Error running OpenCms environment builder mojo", ec);

			// Throw Mojo Execution Exception
			MojoExecutionException mojoException = new MojoExecutionException(
					"Error executing OpenCms environment builder mojo");

			mojoException.initCause(ec);

			throw mojoException;
		}

	}

	/**
	 * Executes the script and prints the script output to the log
	 * 
	 * @param commandList
	 */
	protected void executeScript(List<String> commandList) {

		Process proc;
		try {

			// Run process
			proc = new ProcessBuilder(commandList).directory(
					this.getTargetDirectory()).start();

			// Get process input stream
			InputStreamReader instream = new InputStreamReader(
					proc.getInputStream());

			// get buffered reader for output of process
			BufferedReader dstream = new BufferedReader(instream);

			String currentLine;

			while ((currentLine = dstream.readLine()) != null) {

				// Log output
				this.getLog().info(currentLine);
			}

		} catch (IOException e) {
			this.getLog().error("Error running script", e);
		}

	}

	/**
	 * Generates the commands for running the executable
	 * 
	 * @param scriptFile
	 * @return
	 */
	protected List<String> getExecutableCommandList(File scriptFile) {
		// Execute shell script
		List<String> commandList = new ArrayList<String>();
		commandList.add(System.getProperty("java.home") + "/bin/java");

		// Add Parameters
		String params = DEFAULT_JVM_PARAMS;

		if (!StringUtils.isEmpty(this.getJvmParameters())) {
			params = this.getJvmParameters();
		}

		String[] jvmParams = params.split(" ");
		for (int i = 0; i < jvmParams.length; i++) {
			commandList.add(jvmParams[i]);
		}

		commandList.add("-Djava.ext.dirs=" + this.getOpenCmsRootPath()
				+ "\\lib;" + this.getTargetDirectory().getAbsolutePath()
				+ "\\lib;" + this.getTargetDirectory().getAbsolutePath()
				+ "\\lib\\jsp;");
		commandList.add("-classpath");
		commandList.add(this.getOpenCmsRootPath() + "\\classes");
		commandList.add("org.opencms.main.CmsShell");
		commandList.add("-script=" + scriptFile.getAbsolutePath());
		commandList.add("-base=" + getOpenCmsRootPath());

		return commandList;
	}

	/**
	 * Generates the install script contents
	 * 
	 * @return
	 */
	protected String generateInstallScriptContents() {

		// Contents of the script file
		StringBuilder contents = new StringBuilder();

		// Line separator
		String lineSeparator = System.getProperty("line.separator");

		// Write username/pass
		contents.append("loginUser " + this.getOpenCmsUserName() + " "
				+ this.getOpenCmsPassword());
		contents.append(lineSeparator);

		// Add module imports
		for (String file : this.getModuleImportFiles()) {
			File f = new File(file);
			if (f.exists()) {
				String im = "importModule \""
						+ f.getAbsolutePath().replace("\\", "/") + "\"";
				this.getLog().info(im);
				contents.append(im);
				contents.append(lineSeparator);
			}
		}

		contents.append("purgeJspRepository");
		contents.append(lineSeparator);
		contents.append("exit");
		contents.append(lineSeparator);

		return contents.toString();
	}
}
