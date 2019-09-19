package com.lil.messages.spaceend;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.lil.messages.FilesScanner;

public class SpaceEndMessagesCleaner {
	private static final Logger _log = Logger.getLogger(SpaceEndMessagesCleaner.class);

	public static void add(Path path, String msg, Map<String, Set<Path>> allPropMessages) {
		if (null == msg)
			return;
		Set<Path> msgs = allPropMessages.get(msg);
		if (null == msgs)
			msgs = new HashSet<Path>();
		msgs.add(path);
		allPropMessages.put(msg, msgs);
	}
	
	public void riseSpaceEndMessagesInProp(String folder, boolean delete) throws IOException {
		Map<String, Set<Path>> propMessages = scanProp(folder);
		Map<String, Set<Path>> javaMessages = scanJava(folder);
		riseSpaceEndMessagesInProp(propMessages, javaMessages, delete);
	}

	public void riseSpaceEndMessagesInJava(String folder, boolean delete) throws IOException {
		Map<String, Set<Path>> propMessages = scanProp(folder);
		Map<String, Set<Path>> javaMessages = scanJava(folder);

		riseSpaceEndMessagesInJava(propMessages, javaMessages, delete);
	}
	
	public static void main(String[] args) throws IOException {
		boolean delete = false;
		String folder = "/home/haitao/ET/03_Workspace/01_TDM-LAST/tdm-bd";

		SpaceEndMessagesCleaner cleaner = new SpaceEndMessagesCleaner();
//		cleaner.riseSpaceEndMessagesInJava(folder, delete);
		cleaner.riseSpaceEndMessagesInProp(folder, delete);
	}
	
	// ----------------------------
	// JAVA
	// ----------------------------
	protected Map<String, Set<Path>> scanJava(String folder) throws IOException {
		Map<String, Set<Path>> javaMessages = new HashMap<String, Set<Path>>();
		FilesScanner scanner = new FilesScanner()
				.folder(folder)
				.exclude("**/target/**")
				.exclude("**/SapIDocsImporterTest.java")
				// .exclude("**/tests/**")
				// .exclude("**/test/**")
				.include("**/src/**/*.java");

		scanner.files().forEach(s -> readJava(s, javaMessages));
		return javaMessages;
	}

	public static final Pattern pattern = Pattern.compile("Messages\\.([^\\s\\\\+;,]*)\\s*\\+");

	private void readJava(Path path, Map<String, Set<Path>> allPropMessages) {
		try {
			StringBuffer content = new StringBuffer();
			Files.readAllLines(path).stream().forEach(l -> content.append(l.trim()));

			Matcher matcher = pattern.matcher(content.toString());
			// _log.info("reading " + " " + path);

			while (matcher.find()) {
				String msg = matcher.group(1);
				int i = msg.indexOf('(');
				if (-1 != i) {
					msg = msg.substring(0, i);
					if (msg.startsWith("get"))
						msg = msg.substring(3);
					else
						throw new RuntimeException("[" + matcher.group(1) + "] is not supported in ");
				}
				// _log.info(msg + " +");
				add(path, msg, allPropMessages);
			}

		} catch (Exception e) {
			throw new RuntimeException("Failed to read " + path, e);
		}

	}




	void riseSpaceEndMessagesInJava(
			Map<String, Set<Path>> propMessages,
			Map<String, Set<Path>> javaMessages,
			boolean delete) {
		// javaMessages.forEach(s -> propMessages.keySet().removeAll(filterProp(s, propMessages.keySet())));
		int before = propMessages.size();
		propMessages.keySet().retainAll(javaMessages.keySet());
		int after = propMessages.size();
		_log.info(before + ">>" + after);
		_log.info("=========================================");
		propMessages.forEach((a, b) -> b.forEach(c -> bindMesageInProp(a, c, delete)));
		_log.info("=========================================");

		before = javaMessages.size();
		javaMessages.keySet().retainAll(propMessages.keySet());
		after = javaMessages.size();
		_log.info(before + ">>" + after);
		_log.info("=========================================");
		javaMessages.forEach((a, b) -> b.forEach(c -> bindMessageInJava(a, c, delete)));
		_log.info("=========================================");
	}

	void bindMesageInProp(String message, Path path, boolean delete) {
		_log.info("Trim [" + message + "] @ " + path);
		message = message + "=";
		try {
			BufferedReader reader = Files.newBufferedReader(path);
			StringBuffer contentBuilder = new StringBuffer();
			StringBuffer lineBuilder = new StringBuffer();
			int c = 0;
			while (c != -1) {
				c = reader.read();
				if (c != -1)
					lineBuilder.append((char) c);
				if ('\n' == (char) c || c == -1) {
					String line = lineBuilder.toString();
					if (line.startsWith(message) && line.endsWith(" \n"))
						line = line.trim() + " {0}\n";
					contentBuilder.append(line);
					lineBuilder = new StringBuffer();
				}
			}
			reader.close();

			String content = contentBuilder.toString();
			if (delete)
				FileUtils.write(path.toFile(), content, StandardCharsets.UTF_8.name());
			else
				_log.info(content);
		} catch (IOException e) {
			throw new RuntimeException("Faild to trim " + message + " in " + path);
		}
	}

	void bindMessageInJava(String message, Path path, boolean delete) {
		_log.info(message + "\t" + path);
		try {
			StringBuffer before = new StringBuffer();
			Files.readAllLines(path).stream().forEach(l -> before.append(l + "\n"));
			String p ="(?<msg>(GuiMessages|Messages)\\." + message + ")\\s*[^\\w]";
			String after = before.toString().replaceAll(p, "NLS.bind(${msg},\n");
			
//			BufferedReader reader = Files.newBufferedReader(path);
//			StringBuilder contentBuilder = new StringBuilder();
//			StringBuilder lineBuilder = new StringBuilder();
//			int c = 0;
//			while (c != -1) {
//				c = reader.read();
//				lineBuilder.append((char) c);
//				if ('\n' == (char) c) {
//					
//					String line = lineBuilder.toString();
//					
//					line = line.replaceAll(p, "NLS.bind(${msg},\n");
//					
//					
//
//						contentBuilder.append(line);
//					lineBuilder = new StringBuilder();
//				}
//			}
//			reader.close();
//
//			String content = contentBuilder.toString();
			if (delete)
				FileUtils.write(path.toFile(), after, StandardCharsets.UTF_8.name());
			else
				_log.info(path + "\n" + after);
		} catch (IOException e) {
			throw new RuntimeException("Faild to update " + message + " in " + path);
		}
	}

	// ----------------------------
	// PROP
	// ----------------------------
	public static final Function<String, String> SPACE_END_PROP_MSG_MATCHER = new Function<String, String>() {
		Pattern pattern = Pattern.compile("([^=]+)=.*");

		@Override
		public String apply(String line) {
			if (line.startsWith("#"))
				return null;
			Matcher matcher = pattern.matcher(line);
			String msg = (line.endsWith(" ") && matcher.matches())
					? matcher.group(1)
					: null;
			return msg;
		}
	};

	public Map<String, Set<Path>> scanProp(String folder) throws IOException {
		Map<String, Set<Path>> allPropMessages = new HashMap<String, Set<Path>>();
		FilesScanner scanner = new FilesScanner().folder(folder).exclude("**/target/**")
		// .exclude("**/tests/**")
		// .exclude("**/test/**")
				.exclude("**/WEB-INF/**")
				.exclude("**/log4j.properties")
				.exclude("**/build.properties")
				.include("**/src/**/*messages*.properties");

		scanner.files().forEach(p -> readProp(p, SPACE_END_PROP_MSG_MATCHER, allPropMessages));
		return allPropMessages;
	}

	private void readProp(
			Path p,
			Function<String, String> messageMatcher,
			Map<String, Set<Path>> allPropMessages) {
		try {
			Files.readAllLines(p).stream().forEach(l -> add(p, messageMatcher.apply(l), allPropMessages));
		} catch (Exception e) {
			throw new RuntimeException("Failed to read " + p, e);
		}

	}
	
	void riseSpaceEndMessagesInProp(
			Map<String, Set<Path>> propMessages,
			Map<String, Set<Path>> javaMessages,
			boolean delete) {
		// javaMessages.forEach(s -> propMessages.keySet().removeAll(filterProp(s, propMessages.keySet())));
		int before = propMessages.size();
		propMessages.keySet().removeAll(javaMessages.keySet());
		int after = propMessages.size();
		_log.info(before + ">>" + after);
		_log.info("=========================================");
		propMessages.forEach((a, b) -> b.forEach(c -> trimMessageInProp(a, c, delete)));
		_log.info("=========================================");

		_log.info("=========================================");
		// javaMessages.forEach((a, b) -> b.forEach(c -> _log.info("[" + a +"]"+ "\t@ " + c)));
		_log.info("=========================================");
	}

	void trimMessageInProp(String message, Path path, boolean delete) {
		_log.info("Trim [" + message + "] @ " + path);
		message = message + "=";
		try {
			BufferedReader reader = Files.newBufferedReader(path);
			StringBuffer contentBuilder = new StringBuffer();
			StringBuffer lineBuilder = new StringBuffer();
			int c = 0;
			while (c != -1) {
				c = reader.read();
				if (c != -1)
					lineBuilder.append((char) c);
				if ('\n' == (char) c || c == -1) {
					String line = lineBuilder.toString();
					if (line.startsWith(message))
						line = line.trim() + "\n";
					contentBuilder.append(line);
					lineBuilder = new StringBuffer();
				}
			}
			reader.close();

			String content = contentBuilder.toString();
			if (delete)
				FileUtils.write(path.toFile(), content, StandardCharsets.UTF_8.name());
			else
				_log.info(content);
		} catch (IOException e) {
			throw new RuntimeException("Faild to trim " + message + " in " + path);
		}
	}
}
