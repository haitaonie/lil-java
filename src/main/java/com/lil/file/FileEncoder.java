package com.lil.file;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileEncoder {
	public static void main(String[] args) throws IOException {
		writeEncodedFiles("/tmp/encoding/mock", "ôùéè");
//		writeEncodedFiles("/tmp/encoding/csv", "a\nô\nù\né\nè");
//		writeEncodedFiles("/tmp/encoding/xml", "<x><a>ô</a><a>ù</a><a>é</a><a>è</a></x>");

	}

	protected static void writeEncodedFiles(String dir, String input) throws IOException {
		writeEncodedFile(Paths.get(dir, "iso"), input, "ISO_8859-1");
		writeEncodedFile(Paths.get(dir, "win"), input, "windows-1252");
		writeEncodedFile(Paths.get(dir, "utf"), input, "UTF-8");
	}

	protected static void writeEncodedFile(Path path, String input, String encoding) throws IOException {
		Charset charset = Charset.forName(encoding);
		BufferedWriter writer = Files.newBufferedWriter(path, charset);
		
		// JOptionPane.showInputDialog("Enter any string in Unicode");
		writer.write(input);
		writer.flush();
		writer.close();
	}

}
