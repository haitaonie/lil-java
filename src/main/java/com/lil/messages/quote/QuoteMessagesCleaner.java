package com.lil.messages.quote;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.lil.messages.FilesScanner;

public class QuoteMessagesCleaner {
	private static final Logger _log = Logger.getLogger(QuoteMessagesCleaner.class);
	
	public QuoteMessagesCleaner(String workspace, String target, String replace) {
		this.workspace = workspace;
		this.srcRegex = target;
		this.destText = replace;
	}

	public static void add(Path path, String msg, Map<String, Set<Path>> allPropMessages) {
		if (null == msg)
			return;
		Set<Path> msgs = allPropMessages.get(msg);
		if (null == msgs)
			msgs = new HashSet<Path>();
		msgs.add(path);
		allPropMessages.put(msg, msgs);
	}
	
	public static void main(String[] args) throws IOException {
		boolean delete = true;
		String workspace = "/home/haitao/ET/03_Workspace/01_TDM-LAST/";
		String folder = "tdm-bd";
//		String workspace = "/home/haitao/ET/03_Workspace/01_TDM-LAST/lil-java/src/data/";
//		String folder = "7";
		QuoteMessagesCleaner cleaner = new QuoteMessagesCleaner(
				workspace,
				"\\\\\\\\",
				"\\\\");
		cleaner.riseQuoteMessagesInProp(folder, delete);
	}
	private String workspace;
	private String srcRegex;
	private String destText;
	public void riseQuoteMessagesInProp(String folder, boolean delete) throws IOException {
		Stream<Path> propMessages = scanProp(workspace + "/" + folder);
		_log.info("============\t'" + workspace + "'\t============");
		_log.info("============\t'" + srcRegex + "' > '" + destText + "'\t============");
		propMessages.forEach(c -> unQuoteMessageInProp(c, delete));
	}
	
	// .exclude("**/tests/**") .exclude("**/test/**")
	public Stream<Path> scanProp(String folder) throws IOException {
		FilesScanner scanner = new FilesScanner()
				.folder(folder)
				.exclude("**/target/**")
				.exclude("**/WEB-INF/**")
				.exclude("**/log4j.properties")
				.exclude("**/build.properties")
				.include( "**/src/**/*.properties");
		return scanner.files();
	}

	int unQuoteMessageInProp(Path path, boolean delete) {
		
		
		int count = 0;
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

					
					lineBuilder = new StringBuffer();

					if(!exclude(line)) {
						String newline = replaceLine(contentBuilder, line);
						contentBuilder.append(newline);
						if(!line.equals(newline))
							count++;
					}
					else
						contentBuilder.append(line);
				}
			}
			reader.close();
			if (count > 0) {
				String content = contentBuilder.toString();
				_log.info(count + " : " + path.toString().replace(workspace, "") + "\n");
//				_log.info(delete ? "" : ("\n" + content));
				if (delete)
					FileUtils.write(path.toFile(), content, StandardCharsets.UTF_8.name());
			}
				
			
			
		} catch (IOException e) {
			throw new RuntimeException("Faild to Un-Quote  " + path, e);
		}
		
		return count;
	}

	protected String replaceLine(StringBuffer contentBuilder, String line) {
		Pattern srcPattern = Pattern.compile(srcRegex);
		
		Matcher matcher = srcPattern.matcher(line);
		boolean has = matcher.find();
		if(!has)
			return line;
		
		_log.info("<<" + line.trim());

		String newline = matcher.replaceAll(destText);
		
//		String newline = line.replaceAll(srcRegex, dstRegex);
		_log.info(">>" + newline);
		if(line.equals(newline))
			throw new RuntimeException("Failed to "
					+ "\n replace \t'" 	+ srcRegex 	+ "'"
					+ "\n with \t'"		+ destText 	+ "'"
					+ "\t on \t'" 		+ line + "'");
		return newline;

	}

	private boolean exclude(String line) {
		return line.contains("CsvRepNode_NewLineMacintoshTypeLabel=") ||
				line.contains("CsvRepNode_NewLineUnixTypeLabel=") ||
				line.contains("CsvRepNode_NewLineMacintoshTypeLabel=") ||
				line.contains("CsvRepNode_NewLineWindowsTypeLabel=") ||
				line.contains("DelimitedRepNode_NlMacintosh=") ||
				line.contains("DelimitedRepNode_NlWindows=") ||
				line.contains("AgConcatNode_DropPasteMapElemsHere=") ||
				line.contains("AgNumericNode_DropPasteMapElemsHere=") ||
				line.contains("AnyConcatNode_DropPasteMapElemsHere=") ||
				line.contains("ConcatNode_DropPasteMapElemsHere=") ||
				line.contains("THConvertFileDialog_separatorFieldMissing");
	}
	
	
}
