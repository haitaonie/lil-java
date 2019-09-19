package com.lil.messages.unused;

import static com.lil.messages.FilesScanner.aJavaCodesScanner;
import static com.lil.messages.FilesScanner.aJavaMessagesScanner;
import static com.lil.messages.FilesScanner.aPropMessagesScanner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.lil.messages.MessagesManager;

public class UnusedMessagesCleaner {
	
	public static void main(String[] args) throws IOException {
		boolean delete = false;
		String folder = "/home/haitao/ET/03_Workspace/01_TDM-LAST/tdm-bd";
		// folder = folder + "/org.talend.transform.parser.xsd";

		UnusedMessagesCleaner cleaner = new UnusedMessagesCleaner();

		cleaner.riseUnsedMessagesInProp(folder, delete);
		cleaner.riseUnsedMessagesInJava(folder, delete);

	}
	
	
	public void riseUnsedMessagesInProp(String folder, boolean delete) throws IOException {
		Map<String, Set<Path>> propMessages = aPropMessagesScanner(folder).scan();
		Stream<Path> javaMessages = aJavaCodesScanner(folder).files();

		MessagesManager.riseUnsedMessagesInProp(propMessages, javaMessages, delete);

	}
	
	public void riseUnsedMessagesInJava(String folder, boolean delete) throws IOException {
		Map<String, Set<Path>> propMessages = aPropMessagesScanner(folder).scan();
		Map<String, Set<Path>> javaMessages = aJavaMessagesScanner(folder).scan();

		MessagesManager.riseUnsedMessagesInJava(propMessages, javaMessages, delete);
	}
}
