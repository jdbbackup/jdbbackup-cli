package com.fathzer.jdbbackup.cmd;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.concurrent.Callable;

import com.fathzer.jdbbackup.JDbBackup;
import com.fathzer.jdbbackup.utils.ProxySettings;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/** A command line tool to perform backup.
 */
@Command(name="java com.fathzer.jdbbackup.cmd.JDbBackupCmd", mixinStandardHelpOptions = true, description = "Saves a database to one or more destinations", usageHelpWidth = 160)
public class JDbBackupCmd implements Callable<Integer>, CommandLineSupport {
	@Parameters(index="0", description="Data base address (for example mysql://user:pwd@host:port/db")
    private String db;
	@Parameters(index="1", arity = "1..*", description = "Destinations (example sftp://user:pwd@host/filepath)")
    private String[] dest;
	@Option(names={"-p","--proxy"}, description="The proxy used for the backup, format is [user[:pwd]@]host:port", converter = ProxySettingsConverter.class)
	private ProxySettings proxy;
	@Option(names={"-e","--extension"}, description="A jar file that contains an extension (DestinationManager or DBDumper), or a folder containing such jars",arity = "1..*",
			converter = ExtensionSettingsConverter.class)
	private URLClassLoader[] extensions;
	
	/** Launches the command.
	 * @param args The command arguments. Run the class without any arguments to know what are the available arguments.
	 */
	public static void main(String... args) {
		System.exit(doIt(args));
    }

	static int doIt(String... args) {
		return new CommandLine(new JDbBackupCmd()).execute(args);
	}
	
	@Override
	public Integer call() throws Exception {
		try {
			loadExtensions();
			new JDbBackup().backup(proxy, db, dest);
			return ExitCode.OK;
        } catch (IllegalArgumentException e) {
        	err(e);
        	return ExitCode.USAGE;
        } catch (IOException e) {
        	err(e);
        	return ExitCode.SOFTWARE;
        }
	}

	private void loadExtensions() {
		for (URLClassLoader extension:extensions) {
			System.out.println("We are here");
			JDbBackup.loadPlugins(extension);
		}
	}
}
