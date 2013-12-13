package dk.cphbusiness.droirc.util;
/**
 * @author Mads Heckmann
 * @version 1.0
 * 
 * EXAMPLE OF SERVER MSGS:
 * 	:asimov.freenode.net 001 drobot :Welcome to the freenode IRC Network drobot
 *  :drobot!~drobot@someip.tdc.net JOIN #droirc
 *  :desktopclient!~ident@192.168.0.1 PRIVMSG #android :hi all
 * 
 * 
 * StringProcessor is used for processing server output.
 */

public class StringProcessor {
	private static final int CHARS_BEFORE_MSG = 15;
	private static final int CHARS_AFTER_NICKNAME = 2;
	public static String processLine(String line, String server, String nickname) {
		System.err.println(nickname);
		System.out.println(line);
				
		int indexNickStart = 0, indexNickEnd = 0, indexMsgStart = 0;
		boolean nickFound = false;
		
		String regexServerJoinMsg = ":{1}" + server + " [0-9]{3} " + nickname; // :asimov.freenode.net 001 drobot :Welcome to the freenode IRC Network drobot
		if (line.contains(regexServerJoinMsg)) { 
			int startindex = line.indexOf(nickname) + nickname.length() + CHARS_AFTER_NICKNAME;
			return line.substring(startindex);
		}
		if (line.indexOf(":" + server) >= 0) { // if server notice msg e.g. :asimov.freenode.net NOTICE * :*** Looking up your hostname
			int serverMsgLength = server.length() + CHARS_BEFORE_MSG;
			return line.substring(serverMsgLength);
		}
		if (line.indexOf(nickname + " MODE" + " " + nickname) >= 0) // telling of mode from server when joining
			return line;
		
		for (int i = 0; i < line.length() - 1; i++) { 
			if (!nickFound && line.substring(i, i + 1).equals(":")) {
				indexNickStart = i + 1;
				continue;
			}	
			if (!nickFound && line.substring(i, i + 1).equals("!")) { 
				indexNickEnd = i;
				nickFound = true;
			}
			if (line.substring(i, i + 1).equals(":")) {
				indexMsgStart = i + 1;
				break;
			}
		}		
		if (nickFound)
			return "<" + line.substring(indexNickStart, indexNickEnd) + "> " + line.substring(indexMsgStart);
		return line;
	}
}
