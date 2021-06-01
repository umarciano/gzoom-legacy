import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.location.*;
import org.ofbiz.service.*;

def result = ServiceUtil.returnSuccess();

class MyStringUtil {

    static parseLong(str, defValue) {
        if (UtilValidate.isEmpty(str))
            return defValue;
        try {
            return Long.parseLong(str);
        } catch (ex) {
            return defValue;
        }
    }

    static parseDate(str, format, defValue) {
        if (UtilValidate.isEmpty(str))
            return defValue;
        try {
            return format.parse(str);
        } catch (ex) {
            return defValue;
        }
    }

    static parseSqlTimestamp(str, format, defValue) {
        Date date = parseDate(str, format, defValue);
        if (date == null)
            return defValue;
        return new java.sql.Timestamp(date.getTime());
    }
}

class OfbizLogParser {

    static final TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

    static final TIMESTAMP_PATTERN = ".*";
    static final THREAD_PATTERN = ".*";
    static final FILENAME_PATTERN = "[\\S]*";
    static final LINENUM_PATTERN = "\\d*";
    static final LOGLEVEL_PATTERN = "[\\S]*";
    static final MESSAGE_PATTERN = ".*";
    //
    static final TIMESTAMP_CAPTURE = "(" + TIMESTAMP_PATTERN + ")";
    static final THREAD_CAPTURE = "(" + THREAD_PATTERN + ")";
    static final FILENAME_CAPTURE = "(" + FILENAME_PATTERN + ")";
    static final LINENUM_CAPTURE = "(" + LINENUM_PATTERN + ")";
    static final LOGLEVEL_CAPTURE = "(" + LOGLEVEL_PATTERN + ")";
    static final MESSAGE_CAPTURE = "(" + MESSAGE_PATTERN + ")";

    List listeners = [];
    Pattern linePattern = Pattern.compile(TIMESTAMP_CAPTURE + " \\(" + THREAD_CAPTURE + "\\) \\[\\s*" + FILENAME_CAPTURE + ":" + LINENUM_CAPTURE + "\\s*:" + LOGLEVEL_CAPTURE + "\\s*\\]" + MESSAGE_CAPTURE);

    void addListener(listener) {
        listeners.add(listener);
    }

    void parse(logFileLocation) {
        def url = FlexibleLocation.resolveLocation(logFileLocation);
        def is = url.openStream();
        try {
            parseStream(is);
        } finally {
            is.close();
        }
    }

    void parseStream(is) {
        def isr = new InputStreamReader(is);
        def br = new BufferedReader(isr);

        listeners.each { listener ->
            listener.init();
        }

        def eventId = 0L;
        while (true) {
            def line = br.readLine();
            if (line == null)
                break;

            def event = [:];
            event.eventId = ++eventId;
            setEventInfo(line, event);

            listeners.each { listener ->
                listener.receive(event);
            }
        }

        listeners.each { listener ->
            listener.finish();
        }
    }

    void setEventInfo(line, eventMap) {
        def matcher = linePattern.matcher(line);
        if (matcher.lookingAt()) {
            eventMap.timestamp = MyStringUtil.parseSqlTimestamp(matcher.group(1), TIMESTAMP_FORMAT, null);
            eventMap.thread = matcher.group(2);
            eventMap.file = matcher.group(3);
            eventMap.line = MyStringUtil.parseLong(matcher.group(4), null);
            eventMap.level = matcher.group(5);
            eventMap.message = matcher.group(6);
        }
    }
}

class PrintCsvListener {

    PrintWriter logFile
    List columns = [ "eventId", "timestamp", "thread", "file", "line", "level", "message" ]
	char separator = ',';
    char quotation = '\"';

    void init() {
		logFile = new PrintWriter(new FileOutputStream("runtime/logs/ofbizLogAnalysis.csv"));
    	def buffer = new StringBuffer();
		for (def i = 0; i < columns.size(); ++i) {
	    	csvString(buffer, columns[i], i == 0);
		}
    	logFile.println(buffer);
    }

    void finish() {
        logFile.close();
    }

    void receive(event) {
    	def buffer = new StringBuffer();
    	def value;
		for (def i = 0; i < columns.size(); ++i) {
		    value = event.get(columns[i]);
		    if (value instanceof Number) {
	    		csvNumber(buffer, value, i == 0);
		    } else {
	    		csvString(buffer, value, i == 0);
		    }
		}
    	logFile.println(buffer);
    }

    void csvString(buffer, value, first=false) {
        if (!first)
            buffer.append(separator);
        buffer.append(quotation);
        if (UtilValidate.isNotEmpty(value))
            buffer.append(value.toString().replaceAll("\"", "\"\""));
    	buffer.append(quotation);
    }

    void csvNumber(buffer, value, first=false) {
        if (!first)
            buffer.append(separator);
    	if (UtilValidate.isNotEmpty(value))
    		buffer.append(value);
    }
}

class OfbizLogDetailListener {

	Pattern requestBegunPattern = Pattern.compile("\\s\\[\\[\\[(.*)\\] Request Begun");
	Pattern requestDonePattern = Pattern.compile("\\s\\[\\[\\[(.*)\\] Request Done");
	Pattern firstVisitPattern = Pattern.compile("\\sThis is the first request in this visit. sessionId=(.*)");
	Pattern renderViewPattern = Pattern.compile("\\sRendering View \\[(.*)\\], sessionId=(.*)");
	Pattern syncServicePattern = Pattern.compile("\\sSync service \\[(.*)\\] finished in \\[(\\d*)\\]");
	Pattern runSyncServicePattern = Pattern.compile("\\sRun sync service \\[(.*)\\] finished in \\[(\\d*)\\]");
    Pattern runQueryPattern = Pattern.compile("\\sRun query in (\\d+) milliseconds: (.*)");
	Pattern runEventPattern = Pattern.compile("\\sRun event in (\\d+) milliseconds: (.*)");
	Pattern runActionPattern = Pattern.compile("\\sRun action \\[(.*)\\] in (\\d+) milliseconds: (.*)");
    // TODO
	
    void init() {
    }

    void finish() {
 	}

    void receive(event) {
		if (UtilValidate.isNotEmpty(event.message)) {
			def matcher;
			matcher = requestBegunPattern.matcher(event.message);
			if (matcher.lookingAt()) {
				event.requestBegun = matcher.group(1);
				return;
			}
    		matcher = requestDonePattern.matcher(event.message);
    		if (matcher.lookingAt()) {
    			event.requestDone = matcher.group(1);
    			return;
    		}
    	    matcher = firstVisitPattern.matcher(event.message);
    		if (matcher.lookingAt()) {
    			event.session = matcher.group(1);
    			return;
    		}
    	    matcher = renderViewPattern.matcher(event.message);
    		if (matcher.lookingAt()) {
    			event.renderView = matcher.group(1);
    			event.session = matcher.group(2);
    			return;
    		}
    	    matcher = syncServicePattern.matcher(event.message);
    		if (matcher.lookingAt()) {
    			event.service = matcher.group(1);
    			event.millis = MyStringUtil.parseLong(matcher.group(2), null);
    			return;
    		}
    	    matcher = runSyncServicePattern.matcher(event.message);
    		if (matcher.lookingAt()) {
    			event.millis = MyStringUtil.parseLong(matcher.group(1), null);
    			event.query = matcher.group(2);
    			return;
    		}
    		matcher = runQueryPattern.matcher(event.message);
            if (matcher.lookingAt()) {
                event.millis = MyStringUtil.parseLong(matcher.group(1), null);
                event.query = matcher.group(1);
                return;
            }
            matcher = runEventPattern.matcher(event.message);
            if (matcher.lookingAt()) {
                event.millis = MyStringUtil.parseLong(matcher.group(1), null);
                event.query = matcher.group(1);
                return;
            }
            matcher = runActionPattern.matcher(event.message);
            if (matcher.lookingAt()) {
                event.millis = MyStringUtil.parseLong(matcher.group(1), null);
                event.query = matcher.group(2);
                return;
            }
		}
	}
}

class OfbizLogDigestListener {

	Map threadMap = [:]
	PrintCsvListener csvListener

	void init() {
		csvListener = new PrintCsvListener();
		csvListener.columns = [ "eventId", "start", "finish", "diff", "session", "thread", "request" ];
		csvListener.init();
	}

	void finish() {
		csvListener.finish();
	}

	void receive(event) {
 		def request;
		if (UtilValidate.isNotEmpty(event.requestBegun)) {
		    request = newRequest(event);
	 		request.start = event.timestamp;
	 		request.request = event.requestBegun;
		} else if (UtilValidate.isNotEmpty(event.requestDone)) {
			request = needRequest(event);
			request.finish = event.timestamp;
			def pippo = threadMap.get(request.thread);
			request.diff = UtilDateTime.getInterval(pippo.start, request.finish);
			threadMap.remove(request.thread);
			csvListener.receive(request);
		} else if (UtilValidate.isNotEmpty(event.session)) {
			request = needRequest(event);
			request.session = event.session;
		}
 	}

	Map newRequest(event) {
    	def request = [:];
		request.eventId = event.eventId;
		request.thread = event.thread;
		request.start = event.timestamp;
		threadMap.put(request.thread, request);
		return request;
 	}

	Map needRequest(event) {
		def request = threadMap.get(event.thread);
		if (request == null)
		    request = newRequest(event);
		return request;
 	}
}

if (UtilValidate.isEmpty(context.logFileLocation))
    context.logFileLocation = "runtime/logs/ofbiz.log";
def ofbizLogParser = new OfbizLogParser();
ofbizLogParser.addListener(new OfbizLogDetailListener());
ofbizLogParser.addListener(new OfbizLogDigestListener());
ofbizLogParser.parse(context.logFileLocation);

return result;
