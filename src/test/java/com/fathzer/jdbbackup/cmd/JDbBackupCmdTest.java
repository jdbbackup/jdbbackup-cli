package com.fathzer.jdbbackup.cmd;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import com.fathzer.jdbbackup.DestinationManager;
import com.fathzer.jdbbackup.JDbBackup;
import com.fathzer.jdbbackup.SourceManager;
import com.fathzer.jdbbackup.utils.SystemShutup;

import picocli.CommandLine.ExitCode;

class JDbBackupCmdTest {
	@Test
	void test() throws IOException {
		try (SystemShutup su = new SystemShutup(false, true)) {
			try (MockedConstruction<JDbBackup> mock = mockConstruction(JDbBackup.class)) {
				// Missing arguments
				assertEquals (ExitCode.USAGE, JDbBackupCmd.doIt(new String[0]));
				assertEquals (ExitCode.USAGE, JDbBackupCmd.doIt("mysql://a@b:127.0.0.1/test"));
				
				// Two proxies
				assertEquals (ExitCode.USAGE, JDbBackupCmd.doIt("-p", "127.0.0.1:3128", "-p", "127.0.0.1:3128", "mysql://a@b:127.0.0.1/test", "file://backup"));

				// Invalid extension
				assertEquals (ExitCode.USAGE, JDbBackupCmd.doIt("-e", "missing.jar", "mysql://a@b:127.0.0.1/test", "file://backup"));
				
				assertEquals(0,mock.constructed().size());
				
				// Basic call (no extensions, no proxy)
				assertEquals (ExitCode.OK, JDbBackupCmd.doIt("mysql://a@b:127.0.0.1/test", "file://backup"));
				JDbBackup backup = mock.constructed().get(0);
				verify(backup, never()).setProxy(any(), any());
				verify(backup).backup("mysql://a@b:127.0.0.1/test", "file://backup");

				// With proxy
				final String proxyString = "a:b@host:1234";
				assertEquals (ExitCode.OK, JDbBackupCmd.doIt("-p", proxyString, "mysql://a@b:127.0.0.1/test", "file://backup"));
				backup = mock.constructed().get(1);
				ArgumentCaptor<Proxy> proxyCaptor = ArgumentCaptor.forClass(Proxy.class);
				ArgumentCaptor<PasswordAuthentication> loginCaptor = ArgumentCaptor.forClass(PasswordAuthentication.class);
				verify(backup).setProxy(proxyCaptor.capture(), loginCaptor.capture());
				Proxy proxy = proxyCaptor.getValue();
				assertEquals(new InetSocketAddress("host", 1234), proxy.address());
				assertEquals("a", loginCaptor.getValue().getUserName());
				assertArrayEquals(new char[] {'b'}, loginCaptor.getValue().getPassword());
				verify(backup).backup("mysql://a@b:127.0.0.1/test", "file://backup");
			}
		}
	}

	@Test
	void testExtensions() {
		try (SystemShutup su = new SystemShutup(false, true)) {
			final Map<String, SourceManager> sources = new HashMap<>();
			@SuppressWarnings("rawtypes")
			final Map<String, DestinationManager> destinations = new HashMap<>();
			try (MockedConstruction<JDbBackup> mock = mockConstruction(JDbBackup.class, (j,c) -> {
				when(j.getSourceManagers()).thenReturn(sources);
				when(j.getDestinationManagers()).thenReturn(destinations);
			})) {
				assertEquals (ExitCode.OK, JDbBackupCmd.doIt("-e", "src/test/plugins/jdbbackup-fakesource-1.0.0.jar", "-e", "src/test/plugins/jdbbackup-sftp-1.0.0.jar", "fake://x", "file://backup"));
				assertEquals(1, sources.size());
				assertEquals("com.fathzer.jdbbackup.sources.FakeSource", sources.get("fake").getClass().getCanonicalName());
				assertEquals(1, destinations.size());
				assertEquals("com.fathzer.jdbbackup.destinations.sftp.SFTPManager", destinations.get("sftp").getClass().getCanonicalName());

				assertEquals (ExitCode.SOFTWARE, JDbBackupCmd.doIt("-e", "src/test/plugins/empty.jar", "fake://x", "file://backup"));
			}
		}
	}

	@Test
	void testErrors() throws Exception {
		// Test error from JDBBackup
		try (MockedConstruction<JDbBackup> mock = mockConstruction(JDbBackup.class, (j,c) -> {
			doThrow(IllegalArgumentException.class).when(j).backup(any(), any());
		})) {
			// Test setExceptionConsumer is working
			final JDbBackupCmd cmd = new JDbBackupCmd();
			final AtomicReference<Exception> ref = new AtomicReference<>();
			cmd.setExceptionConsumer(e -> {
				ref.set(e);
			});
			assertEquals(ExitCode.USAGE, cmd.call());
			assertNotNull(ref.get());
		}
	}
}
