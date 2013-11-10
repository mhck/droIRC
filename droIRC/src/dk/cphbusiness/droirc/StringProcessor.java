package dk.cphbusiness.droirc;

public class StringProcessor {
	public static String processLine(String line, String server, String nickname) {
		int indexNickStart = 0, indexNickEnd = 0, indexMsgStart = 0;
		boolean nickFound = false;
		if (line.indexOf(":" + server) >= 0) { // if server msg
			int serverMsgLength = server.length() + nickname.length() + 8;
			return line.substring(serverMsgLength);
		}
		if (line.indexOf("~" + nickname) >= 0) // notice from server e.g. :drobot!~drobot@188-179-73-182-static.dk.customer.tdc.net JOIN #droirc
			return line;
		if (line.indexOf(nickname + " MODE" + " " + nickname) >= 0) // telling of mode from server when joining
			return line;
		for (int i = 0; i < line.length(); i++) {
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
		return "<" + line.substring(indexNickStart, indexNickEnd) + "> " + line.substring(indexMsgStart);
	}
}
