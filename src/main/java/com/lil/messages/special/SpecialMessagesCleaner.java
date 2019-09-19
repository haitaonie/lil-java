package com.lil.messages.special;

import static com.lil.messages.FilesScanner.aPropSpecialScanner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.lil.messages.MessagesManager;

public class SpecialMessagesCleaner {
	protected void riseSpecialMessagesInProp(String folder, boolean delete) throws IOException {
		Stream<Path> propMessages = aPropSpecialScanner(folder).files();
		MessagesManager.riseSpecialMessagesInProp(propMessages, "￿", delete);

	}
}
