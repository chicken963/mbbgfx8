package ru.orthodox.mbbg.utils.common;

public class TimeRepresentationConverter {
    public static String toStringFormat(double length) {
        int minutes = (int) Math.floor(length / 60);
        int seconds = (int) (Math.floor(length % 60));
        return formatTimeSegment(minutes) + ":" + formatTimeSegment(seconds);
    }

    public static double toDoubleFormat(String string) {
        if (string.contains(":")) {
            String[] timeSegments = string.split(":");
            double minutes = Double.parseDouble(timeSegments[0]);
            double seconds = Double.parseDouble(timeSegments[1]);
            return minutes * 60 + seconds;
        }
        return 0;
    }

    public static String getSongProgressAsString(double current, double maximum) {
        int currentMinutes = (int) Math.floor(current / 60);
        int currentSeconds = (int) (Math.floor(current % 60));
        int minutesInCurrentTrack = (int) Math.floor(maximum / 60);
        int secondsInCurrentTrack = (int) Math.floor(maximum % 60);
        StringBuilder sb = new StringBuilder()
                .append(formatTimeSegment(currentMinutes))
                .append(":")
                .append(formatTimeSegment(currentSeconds))
                .append("/")
                .append(formatTimeSegment(minutesInCurrentTrack))
                .append(":")
                .append(formatTimeSegment(secondsInCurrentTrack));
        return sb.toString();
    }

    private static String formatTimeSegment(int value) {
        return String.format("%02d", value);
    }
}
