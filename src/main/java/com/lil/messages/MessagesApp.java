package com.lil.messages;

import java.io.IOException;

import com.lil.messages.spaceend.SpaceEndMessagesCleaner;
import com.lil.messages.unused.UnusedMessagesCleaner;

public class MessagesApp {
	public static void main(String[] args) throws IOException {
		boolean delete = false;
		String folder = "/home/haitao/ET/03_Workspace/01_TDM-LAST/transform";
		// folder = folder + "/org.talend.transform.parser.xsd";

		UnusedMessagesCleaner cleaner = new UnusedMessagesCleaner();

//		cleaner.riseUnsedMessagesInProp(folder, delete);
		cleaner.riseUnsedMessagesInJava(folder, delete);
	}

}
