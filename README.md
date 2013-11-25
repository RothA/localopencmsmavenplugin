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

Refer to the [Example Pom.xml](pom_example.xml)

The plugin exposes the various configuration options:

	1. openCmsEnvironmentZipFile
		a. Description: This is the location of the OpenCms Environment zip file that you want to use for your local environment.  This will correspond to the branch of the code you are currently working on so that the correct Epic7 modules are installed in the environment.
		b. Example: http://testserver/opencms_branch_august_2013.zip
		c. Used by Maven Goals:
			i. init
			ii. reinstall
	2. targetDirectory
		a. Description:	This is the location of the directory for the local OpenCms environment (ie. the location the environment zip file should be extracted to). 
		b. Example: ${project.build.directory}/opencms_local/
		c. Used by Maven Goals:
			i. init
			ii. installmodules
			iii. reinstall
			iv. start
			v. stop
	3. startupJar
		a. Description:	This is the path to the Jetty "start.jar".  This is how the OpenCms environment is started up.  This file is also checked for to determine if the OpenCms Environment Zip File should be pulled down again when running the init Maven Goal. 
		b. Example: ${project.build.directory}/opencms_local/start.jar
		c. Used by Maven Goals:
			i. init
			ii. installmodules
			iii. reinstall
			iv. start
	4. jvmParameters (OPTIONAL)
		a. Description:	This allows you to specify different parameters to use when starting up the JVM for the local OpenCms environment.  This is an optional parameter and only should be used if you need to override the default configuration below.
		b. Default: -Xms512M -Xmx2048M -XX:MaxPermSize=128m
		c. Used by Maven Goals:
			i. installmodules
			ii. start
		d. DEBUGGING FROM ECLIPSE
			i. To debug from Eclipse set this option to the following in your localopencms/pom.xml file:
				a.​​-Xms512M -Xmx2048M -XX:MaxPermSize=128m -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n
				b. Information on debugging in Eclipse:	​http://manish-eclipseremotedebugging.blogspot.com/​
	​
	5. baseOpenCmsZipUrl (OPTIONAL)
		a. Description: The location of the base OpenCms environment zip file.  This should point to an environment that does NOT have any custom Dispatch modules already installed in it. NOTE: If this parameter is not set it will use the zip file specified by the "openCmsEnvironmentZipFile" parameter.
		b. Example: http://testserver/base_opencms_8_5_1_jetty8.zip
		c. Used by Maven Goals:
			i. installmodules
	6. outputOpenCmsZipFile (OPTIONAL)
		a. Description:	This is the final destination of the OpenCms environment zip file once all custom modules have been installed in the base OpenCms installation.  NOTE: If you do provide this parameter then the process will simply install the modules to the local OpenCms install and not zip up the final directory.
		b. Example: ${project.build.directory}/opencms_dispatch.zip
		c. Used by Maven Goals:
			i. installmodules
	7. openCmsRootPath
		a. Description:	This is the path to the OpenCms root.  This must be specified properly so the module import can occur correctly.
		b. Example: ${project.build.directory}/opencms_local/webapps/opencms/WEB-INF/
		c. Used by Maven Goals:
			i. installmodules
	8. openCmsUserName
		a. Description:	The OpenCms user to use for install modules via the OpenCms Shell.  NOTE: This must be an OpenCms administrator account!
		b. Example: User
		c. Used by Maven Goals:
			i. installmodules
	9. openCmsPassword
		a. Description:	The OpenCms user's password to use for install modules via the OpenCms Shell.  NOTE: This must be an OpenCms administrator account!
		b. Example: Dispatch1
		c. Used by Maven Goals:
			i. installmodules
	10. moduleImportFiles 
		a. Description: This is a list of all of the modules to import into the local OpenCms environment via the OpenCms shell.
		b. Example:
			<moduleImportFiles>
			      <moduleImportFile>${project.parent.basedir}/framework/target/com.dispatch.framework${opencmsModuleSuffix}</moduleImportFile>
			      <moduleImportFile>${project.parent.basedir}/advertising/target/com.dispatch.advertising${opencmsModuleSuffix}</moduleImportFile>
			</moduleImportFiles> 
		c. Used by Maven Goals:
			i. installmodules
