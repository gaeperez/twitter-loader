package es.uvigo.ei.sing.twitterloader.utils;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class Functions {

    public LocalDateTime convertToLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public LocalDateTime convertToLocalDateTime(Date date) {
        return date != null ? date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime() : null;
    }

    public Date convertToDate(LocalDateTime date) {
        return date != null ? Date.from(date.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    public long localDateTimeToUnix(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000;
    }

    public LocalDateTime getStartOfDay(LocalDateTime date) {
        return date.toLocalDate().atStartOfDay();
    }

    public LocalDateTime getEndOfDay(LocalDateTime date) {
        return date.toLocalDate().atTime(LocalTime.MAX);
    }

    public String doHash(String toHash) {
        String toRet = "";

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte hash : encodedHash) {
                String hex = Integer.toHexString(0xff & hash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            toRet = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return toRet;
    }

    public Map<Integer, Integer> groupDatesByValue(String value, Set<LocalDateTime> dates) {
        Map<Integer, Integer> mapValueCount = new HashMap<>();

        switch (value) {
            case "year":
                dates.stream().map(LocalDateTime::getYear).forEach(year -> mapValueCount.merge(year, 1, Integer::sum));
                break;
            case "month":
                dates.stream().map(LocalDateTime::getMonthValue).forEach(month -> mapValueCount.merge(month, 1, Integer::sum));
                break;
            case "week":
                dates.stream().map(LocalDateTime::getDayOfMonth).forEach(week -> mapValueCount.merge(week, 1, Integer::sum));
                break;
            case "day":
                dates.stream().map(LocalDateTime::getDayOfWeek).forEach(day -> mapValueCount.merge(day.getValue(), 1, Integer::sum));
                break;
            case "hour":
                dates.stream().map(LocalDateTime::getHour).forEach(hour -> mapValueCount.merge(hour, 1, Integer::sum));
                break;
            default:
                break;
        }

        return mapValueCount;
    }


    public boolean isNumericalArgument(String argument) {
        String[] args = argument.split(",");
        boolean isNumericalArgument = true;
        for (String arg : args) {
            try {
                Integer.parseInt(arg);
            } catch (NumberFormatException nfe) {
                isNumericalArgument = false;
                break;
            }
        }
        return isNumericalArgument;
    }
}
