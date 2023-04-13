package com.fathzer.jdbbackup.cmd;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.fathzer.plugin.loader.utils.FileUtils;

import picocli.CommandLine.ITypeConverter;

/** A converter that loads the plugins in a jar file whose path is passed as value.
 */
public class ExtensionSettingsConverter implements ITypeConverter<List<Path>> {
	@Override
	public List<Path> convert(String value) throws Exception {
		final List<Path> paths = FileUtils.getJarFiles(Paths.get(value), 1);
		if (paths.isEmpty()) {
			throw new IllegalArgumentException("Found no jar in "+value);
		}
		return paths;
	}
}
