package com.fathzer.jdbbackup.cmd;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import com.fathzer.jdbbackup.JDbBackup;
import com.fathzer.jdbbackup.utils.Files;

import picocli.CommandLine.ITypeConverter;

/** A converter that loads the plugins in a jar file whose path is passed as value.
 */
public class ExtensionSettingsConverter implements ITypeConverter<URLClassLoader> {
	@Override
	public URLClassLoader convert(String value) throws Exception {
		final URL[] urls = Files.toURL(new File(value), ".jar", 1);
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
