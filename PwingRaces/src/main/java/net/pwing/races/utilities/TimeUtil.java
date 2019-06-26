package net.pwing.races.utilities;

public class TimeUtil {

	public static String getShortenedTime(int seconds) {
	    int minutes = seconds / 60;
	    int hours = minutes / 60;
	    int days = hours / 24;

	    seconds -= minutes * 60;
	    minutes -= hours * 60;
	    hours -= days * 24;

	    StringBuilder sb = new StringBuilder();

	    if (days > 0)
	    	sb.append(days).append("d ");

	    if (hours > 0)
	    	sb.append(hours).append("h ");

	    if (minutes > 0)
	    	sb.append(minutes).append("m ");

	    if (seconds > 0)
	    	sb.append(seconds).append("s");

	    return sb.toString();
	}

	public static String getTime(int seconds) {
	    int minutes = seconds / 60;
	    int hours = minutes / 60;
	    int days = hours / 24;

	    seconds -= minutes * 60;
	    minutes -= hours * 60;
	    hours -= days * 24;

	    StringBuilder sb = new StringBuilder();

	    if (days > 0)
	    	sb.append(days).append((days == 1) ? " day " : " days ");

	    if (hours > 0)
	    	sb.append(hours).append((hours == 1) ? " hour " : " hours ");

	    if (minutes > 0)
	    	sb.append(minutes).append((minutes == 1) ? " minute " : " minutes ");

	    if (seconds > 0)
	    	sb.append(seconds).append((seconds == 1) ? " second" : " seconds");

	    return sb.toString();
	}
}
