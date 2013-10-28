package aroth.maven.opencms;

import java.io.File;
import java.net.URL;

import net.lingala.zip4j.core.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Initialize a local OpenCms Environment
 * 
 * @goal init
 * 
 */

public class InitOpenCmsMojo extends BaseOpenCmsAbstractMojo {

	/** Utils object to use */
	private Utils utils;

	public InitOpenCmsMojo() {
		this.setUtils(new Utils(this.getLog()));
	}

	/**
	 * Execute this Mojo goal
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {

		// Temp file
		File tempFile = null;

		try {

			this.getLog().info("Starting OpenCms Local Mojo...");
			this.getLog().info(
					"OpenCms Zip File: " + this.getOpenCmsEnvironmentZipFile());
			this.getLog().info(
					"Destory Environment: " + this.isDestroyEnvironment());
			this.getLog()
					.info("Target Directory: " + this.getTargetDirectory());

			// Check to see if environment should be destroyed
			if (this.isDestroyEnvironment()) {

				// Delete existing directory
				if (this.getTargetDirectory().exists()) {

					this.getLog().info(
							"Destroying Local OpenCms Environment...");

					// Clean out the directory
					FileUtils.cleanDirectory(this.getTargetDirectory());

					this.getLog().info(
							"Target Directory contents deleted: "
									+ this.getTargetDirectory().getPath());
				}

			}

			// Check that output directory is a folder and exists
			if (!this.getTargetDirectory().exists()) {
				if (!this.getTargetDirectory().mkdir()) {
					throw new Exception(
							"Creating target directory was not successfull: "
									+ this.getTargetDirectory().getPath());
				}
			}

			// Check if starting jar already exists

			if (!this.getStartupJar().exists()) {

				// Create Temp FIle
				tempFile = File.createTempFile("ocm", null,
						this.getTargetDirectory());

				if (tempFile != null) {
					this.getLog().info(
							"Downloading OpenCms Zip File ("
									+ this.getOpenCmsEnvironmentZipFile()
									+ ") to " + tempFile.getPath() + "...");

					// Download
					FileUtils.copyURLToFile(
							new URL(this.getOpenCmsEnvironmentZipFile()),
							tempFile);

					this.getLog().info("OpenCms Zip File Download Complete!");

					this.getLog().info(
							"Unzipping " + tempFile.getPath() + " to "
									+ this.getTargetDirectory().getPath());

					// Unzip to target directory
					ZipFile zipFile = new ZipFile(tempFile);
					zipFile.extractAll(this.getTargetDirectory().getPath());

					this.getLog().info(
							"Unzipping " + tempFile.getPath() + " to "
									+ this.getTargetDirectory().getPath()
									+ " complete!");
				} else {
					this.getLog().error("Error creating temp file");

				}
			} else {
				this.getLog()
						.info("Startup Jar already exists so not redownloading environment zip file");
			}

		} catch (Exception ec) {
			this.getLog().error("Error executing OpenCms Environment mojo", ec);

			// Throw Mojo Execution Exception
			MojoExecutionException mojoException = new MojoExecutionException(
					"Error executing OpenCms Environment mojo");

			mojoException.initCause(ec);

			throw mojoException;
		} finally {

			// Delete temp file
			if (tempFile != null) {
				if (tempFile.exists()) {
					// Delete
					tempFile.delete();
					this.getLog().info(
							"Temp file deleted: " + tempFile.getAbsolutePath());
				}
			}
		}
	}

	public Utils getUtils() {
		return utils;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

}
