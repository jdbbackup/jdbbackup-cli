package com.fathzer.jdbbackup.cmd;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

import com.fathzer.jdbbackup.JDbBackup;
import com.fathzer.plugin.loader.utils.FileUtils;

import picocli.CommandLine.ITypeConverter;

/** A converter that loads the plugins in a jar file whose path is passed as value.
 */
public class ExtensionSettingsConverter implements ITypeConverter<URLClassLoader> {
	@Override
	public URLClassLoader convert(String value) throws Exception {
		final URL[] urls = FileUtils.getJarFiles(Paths.get(value), 1).stream().map(FileUtils::getURL).toArray(URL[]::new);
		if (urls.length==0) {
			throw new IllegalArgumentException("Found no jar in "+value);
		}
		final URLClassLoader loader = URLClassLoader.newInstance(urls);
		if (!JDbBackup.loadPlugins(loader)) {
			throw new IllegalArgumentException("There is no plugins in "+value);
		}
		return loader;
	}
}
