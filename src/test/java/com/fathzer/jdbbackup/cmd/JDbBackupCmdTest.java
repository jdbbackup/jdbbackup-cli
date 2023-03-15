package com.fathzer.jdbbackup.cmd;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.fathzer.jdbbackup.JDbBackup;
import com.fathzer.jdbbackup.utils.SystemShutup;

import picocli.CommandLine.ExitCode;

class JDbBackupCmdTest {
	@Test
	void test() {
		try (MockedConstruction<JDbBackup> mock = mockConstruction(JDbBackup.class)) {
			try (SystemShutup su = new SystemShutup(false, true)) {
				// Missing arguments
				assertEquals (ExitCode.USAGE, JDbBackupCmd.doIt(new String[0]));
				assertEquals (ExitCode.USAGE, JDbBackupCmd.doIt("mysql://a@b:127.0.0.1/test"));
				
				// Two proxies
				assertEquals (ExitCode.USAGE, JDbBackupCmd.doIt("mysql://a@b:127.0.0.1/test", "file://backup", "-p", "127.0.0.1:3128", "-p", "127.0.0.1:3128"));

				// Invalid extension
				assertEquals (ExitCode.USAGE, JDbBackupCmd.doIt("mysql://a@b:127.0.0.1/test", "file://backup", "-e", "missing.jar"));
			}
			assertEquals(0,mock.constructed().size());
		}
	}

}
