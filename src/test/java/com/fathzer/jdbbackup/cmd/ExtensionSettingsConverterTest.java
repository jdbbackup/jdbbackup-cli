package com.fathzer.jdbbackup.cmd;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExtensionSettingsConverterTest {

	@Test
	void test(@TempDir Path dir) throws Exception {
		final ExtensionSettingsConverter c = new ExtensionSettingsConverter();
		final String dirPath = dir.toString();
		assertThrows(IllegalArgumentException.class, () -> c.convert(dirPath));
		final Path jar1 = dir.resolve("x.jar");
		final Path jar2 = dir.resolve("y.jar");
		Files.createFile(jar1);
		Files.createFile(jar2);
		final List<Path> files = c.convert(dirPath);
		assertEquals(2, files.size());
		assertTrue(files.contains(jar1));
		assertTrue(files.contains(jar2));
	}

}
