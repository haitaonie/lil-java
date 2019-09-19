package com.lil.messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class MessagesManager {
	private static final Logger _log = Logger.getLogger(MessagesManager.class);
	public static void riseUnsedMessagesInJava(
			Map<String, Set<Path>> propMessages,
			Map<String, Set<Path>> javaMessages,
			boolean delete) throws IOException {
		int before = propMessages.size();
		javaMessages.keySet().removeAll(propMessages.keySet());
		int after = propMessages.size();
		_log.info(before + ">>" + after);
		
		_log.info("=========================================");
		javaMessages.forEach((a, b) -> b.forEach(c -> updateMessagesInJava(a, c, delete)));
		_log.info("=========================================");
	}


	public static void riseUnsedMessagesInProp(Map<String, Set<Path>> propMessages, Stream<Path> javaMessages, boolean delete) {
		int before = propMessages.size();
		javaMessages.forEach(s -> propMessages.keySet().removeAll(filterProp(s, propMessages.keySet())));
		int after = propMessages.size();
		_log.info(before + ">>" + after);
		
		_log.info("=========================================");
		propMessages.forEach((a, b) -> b.forEach(c -> updateMessagesInProp(a, c, delete)));
		_log.info("=========================================");
	}
	
	
	public static void riseSpecialMessagesInProp(Stream<Path> propMessages,  String dirty, boolean delete) {
		_log.info("=========================================");
		propMessages.forEach(f -> cleanMessagesInProp(f, dirty, delete));
		_log.info("=========================================");
	}
	
	private static void cleanMessagesInProp(Path path, String dirty, boolean delete) {

		try {
			BufferedReader reader = Files.newBufferedReader(path);
			StringBuffer contentBuilder = new StringBuffer();
			StringBuffer lineBuilder = new StringBuffer();
			int c = 0;
			while (c != -1) {
				c = reader.read();
				lineBuilder.append((char) c);
				if('\n' == (char) c || c == -1) {
					String line = lineBuilder.toString();
					line = line.replaceAll(dirty, "");
					contentBuilder.append(line);
					lineBuilder = new StringBuffer();
				}
			}
			reader.close();

			String content = contentBuilder.toString();
			if(delete)
				FileUtils.write(path.toFile(), content, StandardCharsets.UTF_8.name());
			else
				_log.info(path + "\n" + content);
		} catch (IOException e) {
			throw new RuntimeException("Faild to clean " + dirty + " in " + path);
		}
	
	}


	private static void updateMessagesInJava(String message, Path path, boolean delete) {
		_log.info(message + "\t" + path);
		message = "public static String " + message + ";";
		try {
			BufferedReader reader = Files.newBufferedReader(path);
			StringBuilder contentBuilder = new StringBuilder();
			StringBuilder lineBuilder = new StringBuilder();
			int c = 0;
			while (c != -1) {
				c = reader.read();
				lineBuilder.append((char) c);
				if('\n' == (char) c) {
					String line = lineBuilder.toString();
					if(!line.trim().equals(message))
						contentBuilder.append(line);
					else
						_log.info("Deleting\n" + line);
					lineBuilder = new StringBuilder();
				}
			}
			reader.close();

			String content = contentBuilder.toString();
			if(delete)
				FileUtils.write(path.toFile(), content, StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			throw new RuntimeException("Faild to update " + message + " in " + path);
		}
	}
	


	
	protected static void updateMessagesInProp(String message, Path path, boolean delete) {
		_log.info("Update [" + message + "] @ " + path);
		message = message + "=";
		try {
			BufferedReader reader = Files.newBufferedReader(path);
			StringBuffer contentBuilder = new StringBuffer();
			StringBuffer lineBuilder = new StringBuffer();
			int c = 0;
			while (c != -1) {
				c = reader.read();
				if(c!=-1)
					lineBuilder.append((char) c);
				if('\n' == (char) c || c == -1) {
					String line = lineBuilder.toString();
					if(!line.startsWith(message))
						contentBuilder.append(line);
					lineBuilder = new StringBuffer();
				}
			}
			reader.close();

			String content = contentBuilder.toString();
			if (delete)
				FileUtils.write(path.toFile(), content, StandardCharsets.UTF_8.name());
//			else
//				_log.info(content);
		} catch (IOException e) {
			throw new RuntimeException("Faild to update " + message + " in " + path);
		}
	}
	
	private static Collection<String> filterProp(Path path, Collection<String> propMessages) {
		Collection<String> props = new HashSet<String>();
		try {
			List<String> lines = Files.readAllLines(path);
			StringBuilder content = new StringBuilder();
			for (String line : lines)
				if (line.contains(" extends NLS") || line.contains(" static final ResourceBundle "))
					return Collections.emptySet();
				else
					content.append(line.trim());

			propMessages.stream().forEach(msg -> props.add(isUsedMessageInFile(content.toString(), msg)
					? msg
					: null));
		} catch (IOException e) {
			throw new RuntimeException("Failed to read " + path, e);
		}
		return props;
	}


	protected static boolean isUsedMessageInFile(String content, String msg) {
		return 	content.contains("Messages." + msg) || 
				content.contains("Messages.get" + msg + "(") || 
				content.contains("\"" + msg + "\"");
	}

}
