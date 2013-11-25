Maven Plugin for Managing Local OpenCms Development Environment
=======================

For OpenCms developers that use Maven, I have created a local OpenCms environment running Jetty webserver and Derby embedded for its database.  This allows the entire environment to be portable.  This Maven plugin allows for managing this local environment.

Local Environment Zip Files
---------------------------

I have created two base OpenCms 8.5.1 installations as follows:
		
	1. Targeting JDK v1.6 running Jetty v8: 
	
	2. Targeting JDK v1.7 running Jetty V9: 
	
Maven Goals
-----------
The plugin has the following maven goals.  NOTE: You must be using JDK 1.6 to run these goals.

	1. init
		a. Description: This goal initializes the local OpenCms environment.  It does the following steps:
			i. Downloads the file specifed by the "openCmsEnvironmentZipFile"
			ii. Unzips the file into the directory specified by the "targetDirectory"
		b. Maven Goal:
			i. localopencms:init
	2. start
		a. Description: This goal starts up the local OpenCms environment via the following steps:
			i. Executes the jar file specified by the "startupJar" parameter which starts up Jetty and launches the OpenCms environment
			ii. Writes the PID of the new Java process to the "pid.txt" file in the "targetDirectory" which is used by the "stop" goal to stop the OpenCms environment
		b. Maven Goal:
			i. localopencms:start
	3. stop
		a. Description: This goal stops the local OpenCms environment via the following steps:
			i. Looks for a the PID of the Java process in the "pid.txt" file of the "targetDirectory"
			ii. Uses that PID to run "taskkill" if running on Windows or "kill" on Linux/Macs to stop the OpenCms Java process
		b. Maven Goal:
			i. localopencms:stop
	4. reinstall
		a. Description: This goal reinstalls the the local OpenCms environment.
			i. Destroys the current local environment
			ii. Re-executes the "init" goal to reinstall the local OpenCms environment
		b. Maven Goal:
			i. localopencms:reinstall
	5. installmodules
		a. Description: This goal handles installing custom OpenCms modules to a local environment and then archiving a zip of the new environment
			i. Runs the "init" Maven goal to pull down the OpenCms environment specified by the "baseOpenCmsZipUrl" parameter
			ii. Builds a script out of the modules listed in the "moduleImportFiles"
			iii. Starts up OpenCms via the OpenCms shell
			iv. Runs the script to install the modules into the OpenCms environment
			v. Shuts down the OpenCms shell
			vi. If a value is specified in the "outputOpenCmsZipFile" parameter
				a. Zips up the final directory
				b. Copies the final zip file to the location specified in the "outputOpenCmsZipFile" parameter
		b. Maven Goal:
			i. localopencms:installmodules

Plugin Configuration
--------------------

