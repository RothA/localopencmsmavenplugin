package aroth.maven.opencms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public class BaseOpenCmsAbstractMojo extends AbstractMojo {

	/**
	 * Default JVM parameters to use if none are specified
	 */
	protected static String DEFAULT_JVM_PARAMS = "-Xms512M -Xmx2048M -XX:MaxPermSize=128m";

	/**
	 * @parameter Zip file containing the OpenCms Zip File
	 */
	private String openCmsEnvironmentZipFile;

	/**
	 * @parameter Destroy existing OpenCms environment
	 */
	private boolean destroyEnvironment;

	/**
	 * @parameter Target Directory that OpenCms installation will be installed
	 *            to
	 */
	private File targetDirectory;

	/**
	 * @parameter Jar to execute
	 */
	private File startupJar;

	/**
	 * @parameter Parameters to use when creating OpenCms environment JVM
	 */
	private String jvmParameters;

	/**
	 * @parameter The url of the base OpenCms zip file
	 */
	private String baseOpenCmsZipUrl;

	/**
	 * @parameter The location of where the final zip file should be placed
	 */
	private File outputOpenCmsZipFile;

	/**
	 * @parameter List of module files to import
	 */
	private List<String> moduleImportFiles;

	/**
	 * @parameter Path to the OpenCms Root folder
	 */
	private String openCmsRootPath;

	/**
	 * @parameter OpenCms User Name
	 */
	private String openCmsUserName;

	/**
	 * @parameter OpenCms Password
	 */
	private String openCmsPassword;

	public BaseOpenCmsAbstractMojo() {
		this.setDestroyEnvironment(false);
		this.setJvmParameters("");
		this.setModuleImportFiles(new ArrayList<String>());
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub

	}

	public String getOpenCmsEnvironmentZipFile() {
		return openCmsEnvironmentZipFile;
	}

	public void setOpenCmsEnvironmentZipFile(String openCmsEnvironmentZipFile) {
		this.openCmsEnvironmentZipFile = openCmsEnvironmentZipFile;
	}

	public boolean isDestroyEnvironment() {
		return destroyEnvironment;
	}

	public void setDestroyEnvironment(boolean destroyEnvironment) {
		this.destroyEnvironment = destroyEnvironment;
	}

	public File getTargetDirectory() {
		return targetDirectory;
	}

	public void setTargetDirectory(File targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	public File getStartupJar() {
		return startupJar;
	}

	public void setStartupJar(File startupJar) {
		this.startupJar = startupJar;
	}

	public String getJvmParameters() {
		return jvmParameters;
	}

	public void setJvmParameters(String jvmParameters) {
		this.jvmParameters = jvmParameters;
	}

	public String getBaseOpenCmsZipUrl() {
		return baseOpenCmsZipUrl;
	}

	public void setBaseOpenCmsZipUrl(String m_baseOpenCmsZipUrl) {
		this.baseOpenCmsZipUrl = m_baseOpenCmsZipUrl;
	}

	public List<String> getModuleImportFiles() {
		return moduleImportFiles;
	}

	public void setModuleImportFiles(List<String> m_moduleImportFiles) {
		this.moduleImportFiles = m_moduleImportFiles;
	}

	public File getOutputOpenCmsZipFile() {
		return outputOpenCmsZipFile;
	}

	public void setOutputOpenCmsZipFile(File m_outputOpenCmsZipFile) {
		this.outputOpenCmsZipFile = m_outputOpenCmsZipFile;
	}

	public String getOpenCmsRootPath() {
		return openCmsRootPath;
	}

	public void setOpenCmsRootPath(String openCmsRootPath) {
		this.openCmsRootPath = openCmsRootPath;
	}

	public String getOpenCmsUserName() {
		return openCmsUserName;
	}

	public void setOpenCmsUserName(String openCmsUserName) {
		this.openCmsUserName = openCmsUserName;
	}

	public String getOpenCmsPassword() {
		return openCmsPassword;
	}

	public void setOpenCmsPassword(String openCmsPassword) {
		this.openCmsPassword = openCmsPassword;
	}

}
