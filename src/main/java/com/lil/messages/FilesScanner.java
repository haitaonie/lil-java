package com.lil.messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.apache.tools.ant.DirectoryScanner;

public class FilesScanner {
	private static final Logger _log = Logger.getLogger(FilesScanner.class);

	private static final Collection<String> SPECIALS= Arrays.asList(
//			"M4",
			"ExporterAvro_SchemaBuildError",
			"ExporterAvro_InvalidMapGroupElementError",
			"CobolExporterAvro_InvalidOccursMaxTimesError",
			"ExporterAvro_InvalidDefaultValue"
			);

	/** Java codes files without "extends NLS" and "static final ResourceBundle"*/
	private static final Predicate<Path> JAVA_CODES_FILE_VALIDATOR = new Predicate<Path>() {
		@Override
		public boolean test(Path path) {
			try {
				List<String> lines = Files.readAllLines(path);
				for (String line : lines)
					if (line.contains(" extends NLS") || line.contains(" static final ResourceBundle "))
						return false;
				return true;
			} catch (IOException e) {
				throw new RuntimeException("Failed to read " + path, e);
			}

		}
	};

	/** Java messages files with "extends NLS" and "static final ResourceBundle"*/
	private static final Predicate<Path> JAVA_MSG_FILE_VALIDATOR = new Predicate<Path>() {
		@Override
		public boolean test(Path path) {
			try {
				List<String> lines = Files.readAllLines(path);
				for (String line : lines)
					if (line.contains(" extends NLS") || line.contains("ResourceBundle"))
						return true;
				return false;
			} catch (IOException e) {
				throw new RuntimeException("Failed to read " + path, e);
			}
		}
	};

	private static final Function<String, String> JAVA_MSG_MATCHER = new Function<String, String>() {
		// public static String MSG;
		// NLS.bind(Messages.MSG
		String p1 = ".*\\s*public\\s*static\\s*String\\s*([^\\s\\/]*);.*";
		String p2 = ".*NLS\\.bind\\(Messages.([^\\s\\/,]*),";
		String p = p1 + "|" + p2;
				
		Pattern pattern = Pattern.compile(p1);

		@Override
		public String apply(String line) {
			Matcher matcher = pattern.matcher(line);
			
			String msg = matcher.matches()
					? matcher.group(1)
					: null;
			return SPECIALS.contains(msg) ? null : msg;
		}
	};
	
	private static final Function<String, String> BIND_JAVA_MSG_MATCHER = new Function<String, String>() {
		// public static String MSG;
		// NLS.bind(Messages.MSG
		String p1 = ".*Messages\\.([^\\s\\\\+]*)\\s*\\+.*";


				
		Pattern pattern = Pattern.compile(p1);

		@Override
		public String apply(String line) {
			Matcher matcher = pattern.matcher(line);
			
			String msg = matcher.matches()
					? matcher.group(1)
					: null;
			if(null != msg && msg.startsWith("get") && msg.contains("(")) {
				msg = msg.substring(3);
				int end = msg.indexOf("(");
				msg = msg.substring(0, end);
			}
			return msg;
		}
	};

	public static final Function<String, String> PROP_MSG_MATCHER = new Function<String, String>() {
		Pattern pattern = Pattern.compile("([^=]+)=.*");

		@Override
		public String apply(String line) {
			if(line.startsWith("#"))
				return null;
			Matcher matcher = pattern.matcher(line);
			String msg = matcher.matches()
					? matcher.group(1)
					: null;
			return SPECIALS.contains(msg) ? null : msg;
		}
	};
	

	
	
	public static final FilesScanner aPropMessagesScanner(String folder) {
		return new FilesScanner()
				.folder(folder)
				.exclude("**/target/**")
//				.exclude("**/tests/**")
//				.exclude("**/test/**")
				.exclude("**/WEB-INF/**")
				.exclude("**/log4j.properties")
				.exclude("**/build.properties")
				.include("**/src/**/*messages.properties")
				.messageMatcher(PROP_MSG_MATCHER);
	}
	
	public static final FilesScanner aPropSpecialScanner(String folder) {
		return new FilesScanner()
				.folder(folder)
				.exclude("**/target/**")
//				.exclude("**/tests/**")
//				.exclude("**/test/**")
				.exclude("**/WEB-INF/**")
				.exclude("**/log4j.properties")
				.exclude("**/build.properties")
				.include("**/src/**/*messages*.properties");
	}
	

	
	
	public static final FilesScanner aJavaMessagesScanner(String folder) {
		return new FilesScanner()
				.folder(folder)
				.exclude("**/target/**")
				.exclude("**/SapIDocsImporterTest.java")
//				.exclude("**/tests/**")
//				.exclude("**/test/**")
				.include("**/src/**/*.java")
				.messageFileValidator(JAVA_MSG_FILE_VALIDATOR)
				.messageMatcher(JAVA_MSG_MATCHER);
	}
	
	public static final FilesScanner aJavaCodesScanner(String folder) {
		return new FilesScanner()
				.folder(folder)
				.exclude("**/target/**")
				.exclude("**/SapIDocsImporterTest.java")
//				.exclude("**/tests/**")
//				.exclude("**/test/**")
				.include("**/src/**/*.java")
				.messageFileValidator(JAVA_CODES_FILE_VALIDATOR)
				.messageMatcher(JAVA_MSG_MATCHER);
	}
	
	private Collection<String> includeFilters = new ArrayList<String>();
	private Collection<String> excludeFilters = new ArrayList<String>();
	private String folder;
	private Predicate<Path> messageFileValidator;
	private Function<String, String> messageMatcher;


	
	public FilesScanner folder(String value) {
		this.folder = value;
		return this;
	}

	public FilesScanner include(String value) {
		this.includeFilters.add(value);
		return this;
	}

	public FilesScanner exclude(String value) {
		this.excludeFilters.add(value);
		return this;
	}

	public FilesScanner messageFileValidator(Predicate<Path> value) {
		this.messageFileValidator = value;
		return this;
	}

	public FilesScanner messageMatcher(Function<String, String> value) {
		this.messageMatcher = value;
		return this;
	}

	public Stream<Path> files() throws IOException {
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setIncludes(includeFilters.toArray(new String[includeFilters.size()]));
		scanner.setExcludes(excludeFilters.toArray(new String[excludeFilters.size()]));
		scanner.setBasedir(folder);
		scanner.setCaseSensitive(false);
		scanner.scan();
		return Arrays
				.stream(scanner.getIncludedFiles())
				.map(s -> Paths.get(folder, s));
	}
	
	public Map<String, Set<Path>> scan() throws IOException {
		Map<String, Set<Path>> allPropMessages = new HashMap<String, Set<Path>>();
		files()
				.filter(null != this.messageFileValidator
						? this.messageFileValidator
						: s -> true)
				.forEach(s -> read(s, this.messageMatcher, allPropMessages));
		return allPropMessages;
	}
	
	
	
	private void read(Path path, Function<String, String> messageMatcher, Map<String, Set<Path>> allPropMessages) {
		try {
			Files
					.readAllLines(path)
					.stream().forEach(l -> add(path, messageMatcher.apply(l), allPropMessages));
		} catch (Exception e) {
			throw new RuntimeException("Failed to read " + path, e);
		}

	}

	private void add(Path path, String msg, Map<String, Set<Path>> allPropMessages) {
		if(null == msg)
			return;
		Set<Path> msgs = allPropMessages.get(msg);
		if(null==msgs)
			msgs = new HashSet<Path>();
		msgs.add(path);
		allPropMessages.put(msg, msgs);
	}

}
