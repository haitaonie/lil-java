package com.lil.json.gen;

import static java.nio.file.StandardOpenOption.APPEND;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonGdeltGenApp {

	protected int loop;
	protected String headFile = "/gdelt/1-head";
	protected String bodyFile = "/gdelt/2-body";
	protected String footFile = "/gdelt/3-foot";
	protected String jsonDir;

	public JsonGdeltGenApp(String dir, String size) {
		this.jsonDir = dir;
		this.loop = Integer.valueOf(size);
	}

	private String readFromInputStream(String path) throws IOException {
		InputStream inputStream = JsonGdeltGenApp.class.getResourceAsStream(path);
	
		
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}

	protected Path generateJson(String type, boolean isLoopOnly) throws Exception {
		Path jsonFile = new File(jsonDir + "/i-" + type + ".json").toPath();
		String head =isLoopOnly
						? "[\n"
						: readFromInputStream(headFile).replace("xxx", ""+ loop);
		String body = readFromInputStream(bodyFile);
		String foot =  isLoopOnly
						? "\n]"
						: readFromInputStream(footFile);

		head = isLoopOnly
				? "[\n"
				: head.replace("\"total\": xxx,", "\"total\": " + loop + ",");
			
		Files.write(jsonFile, head.getBytes());
		
		int i = 1;
		for (; i < loop; i++) {
			Files.write(jsonFile, body.getBytes(), APPEND);
			Files.write(jsonFile, ",\t\n".getBytes(), APPEND);
		}
		if (i <= loop)
			Files.write(jsonFile, body.getBytes(), APPEND);

		Files.write(jsonFile, foot.getBytes(), APPEND);
		return jsonFile;
	}

	public static void main(String[] args) throws Exception {
		JsonGdeltGenApp genApp = new JsonGdeltGenApp(args[0], args[2]);
	
		Path jsonFile = null;
		String type = args[1];
		
		if("obj".equals(type))
			jsonFile = genApp.generateJson(type, false);
		else if("arr".equals(type))
			jsonFile = genApp.generateJson(type, true);
		else 
			throw new IllegalAccessError(type);
		System.out.println("END@" + jsonFile);
	}
}
