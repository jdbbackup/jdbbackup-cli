package com.fathzer.jdbbackup.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SystemShutup implements AutoCloseable {
	private final PrintStream originalOut;
	private final PrintStream originalErr;
	
	public SystemShutup(boolean out, boolean err) {
		originalOut = out ? System.out : null; 
		originalErr = err ? System.err : null;
		if (out) {
			System.setOut(new PrintStream(new ByteArrayOutputStream()));
		}
		if (err) {
			System.setErr(new PrintStream(new ByteArrayOutputStream()));
		}
	}

	@Override
	public void close() {
		if (originalErr!=null) {
			System.setErr(originalErr);
		}
		if (originalOut!=null) {
			System.setOut(originalOut);
		}
	}
	
}
