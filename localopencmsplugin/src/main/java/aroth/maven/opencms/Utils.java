package aroth.maven.opencms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.StringUtils;

public class Utils {

	/**
	 * File to store OpenCms PID in
	 */
	private static String PID_FILE_NAME = "/pid.txt";

	/**
	 * Logger to use
	 * 
	 */
	private Log m_logger;

	/**
	 * Public constructor
	 * 
	 * @param logger
	 */
	public Utils(Log logger) {
		this.setLogger(logger);
	}

	/**
	 * Gets a list of java.exe pids
	 * 
	 * @return
	 */
	public List<String> getJavaPids() {

		// Java Pids
		List<String> pids = new ArrayList<String>();

		this.getLogger().info("Getting Java Pids");

		// Handle Windows
		if (OSValidator.isWindows()) {
			// Line
			String line;

			try {
				Process p = Runtime
						.getRuntime()
						.exec(System.getenv("windir")
								+ "\\system32\\"
								+ "tasklist.exe /fo csv /nh /fi \"IMAGENAME eq java.exe\"");

				// Read input
				BufferedReader input = new BufferedReader(
						new InputStreamReader(p.getInputStream()));

				while ((line = input.readLine()) != null) {
					String[] args = line.split(",");
					if (args.length > 1) {
						pids.add(args[1]);
					}
				}

				input.close();
			} catch (IOException e) {
				this.getLogger().error("Error getting java pids in windows", e);
			}

		} else if (OSValidator.isUnix()) {
			// Handle Unix
			// Line
			String line;

			try {
				Process p = Runtime.getRuntime().exec("pidof java");

				// Read input
				BufferedReader input = new BufferedReader(
						new InputStreamReader(p.getInputStream()));

				while ((line = input.readLine()) != null) {
					String[] args = line.split(" ");
					if (args.length > 1) {
						pids.add(args[1]);
					}
				}

				input.close();
			} catch (IOException e) {
				this.getLogger().error("Error getting java pids in linux", e);
			}
		} else if (OSValidator.isMac()) {
			// Handle Mac
			// Line
			String line;

			try {

				String[] command = new String[] { "/bin/sh", "-c",
						"ps axc | grep -i java | awk \"{ print \\$1}\"" };

				Process p = Runtime.getRuntime().exec(command);

				// Read input
				BufferedReader input = new BufferedReader(
						new InputStreamReader(p.getInputStream()));

				while ((line = input.readLine()) != null) {
					this.getLogger().info("Mac pid: " + line);
					pids.add(line.trim());
				}

				input.close();
			} catch (IOException e) {
				this.getLogger().error("Error getting java pids for a mac", e);
			}
		}

		// Return java process ids
		return pids;
	}

	/**
	 * Get difference between two lists
	 * 
	 * @param firstList
	 * @param secondList
	 * @return
	 */
	public List<String> getListDifferences(List<String> firstList,
			List<String> secondList) {
		secondList.removeAll(firstList);
		return secondList;
	}

	/**
	 * Writes the OpenCms PID to a file
	 * 
	 * @param pid
	 * @param targetDirectory
	 * @throws IOException
	 */
	public void writePidToFile(String pid, String targetDirectory)
			throws IOException {

		// Trim PID
		String newPid = pid.replace("\"", "").trim();

		// Get File
		File file = new File(targetDirectory + PID_FILE_NAME);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		// Write to file
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(newPid);
		bw.close();

	}

	/**
	 * Reads the OpenCms PID from a file
	 * 
	 * @return
	 * @throws IOException
	 */
	public String readPidFromFile(String targetDirectory) throws IOException {

		// PID to return
		String pid = "";

		// Buffered Reader
		BufferedReader br = null;

		File pidFile = new File(targetDirectory + PID_FILE_NAME);

		if (pidFile.exists()) {

			try {

				// Setup buffered reader
				br = new BufferedReader(new FileReader(pidFile));

				// Read first line
				String firstLine = br.readLine();

				if (!StringUtils.isEmpty(firstLine)) {
					pid = firstLine.trim();
				}

			} catch (IOException e) {
				this.getLogger().error("error reading pid from file", e);
			} finally {
				try {
					if (br != null)
						br.close();
				} catch (IOException ex) {
					this.getLogger().error("error reading pid from file", ex);
				}
			}
		}
		return pid;
	}

	/**
	 * Stops a process with the specified PID
	 * 
	 * @param pid
	 */
	public void stopOpenCmsProcess(String targetDirectory) {

		// Check to see if a process is specified in the pid.txt file
		this.getLogger().info(
				"Checking to see if OpenCms Process is currently running");

		try {
			String currentlyRunningPid = this.readPidFromFile(targetDirectory);

			if (!StringUtils.isEmpty(currentlyRunningPid)) {

				// Process is running
				this.getLogger().info(
						"OpenCms Process is running, attempting to stop. PID: "
								+ currentlyRunningPid);
				// Handle Windows
				if (OSValidator.isWindows()) {

					// Try to kill task
					Runtime.getRuntime().exec(
							"taskkill /F /PID " + currentlyRunningPid);

					this.getLogger().info("OpenCms Process killed (windows)");

				} else if (OSValidator.isUnix()) {
					// Linux
					// Try to kill task
					Runtime.getRuntime().exec("kill -9 " + currentlyRunningPid);

					this.getLogger().info("OpenCms Process killed (linux)");
				} else if (OSValidator.isMac()) {
					// Mac
					// Try to kill task
					Runtime.getRuntime().exec("kill -9 " + currentlyRunningPid);

					this.getLogger().info("OpenCms Process killed (linux)");
				}

				// clear out pid
				this.writePidToFile("", targetDirectory);

			} else {
				this.getLogger().info("No OpenCms Processes Running");
			}
		} catch (IOException e) {
			this.getLogger().error("Error stopping OpenCms Process", e);
		}

	}

	public Log getLogger() {
		return m_logger;
	}

	public void setLogger(Log m_logger) {
		this.m_logger = m_logger;
	}
}
