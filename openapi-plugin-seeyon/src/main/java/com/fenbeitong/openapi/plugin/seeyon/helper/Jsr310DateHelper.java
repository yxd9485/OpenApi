package com.fenbeitong.openapi.plugin.seeyon.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Jsr310DateHelper
 *
 * <p>日期工具类
 *
 * @author ivan
 * @version 1.0 Created by ivan on 12/15/18 - 7:17 PM.
 */
@Data
@AllArgsConstructor
@Builder
public class Jsr310DateHelper {

  private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
  private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
  private static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
  private static final String DATE_TIME_25_LENGTH = "yyyy-MM-dd+HH:mm:ss.SSSSS";
  private static final String DATE_TIME_8_LENGTH = "yyyyMMdd";

  @Builder.Default private String datePattern = DEFAULT_DATE_PATTERN;
  @Builder.Default private String dateTimePattern = DEFAULT_DATE_TIME_PATTERN;
  @Builder.Default private String timePattern = DEFAULT_TIME_PATTERN;
  @Builder.Default private ZoneId zoneId = ZoneId.systemDefault();

  public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
    Instant instant = Instant.ofEpochMilli(timestamp);
    ZoneId zone = ZoneId.systemDefault();
    return LocalDateTime.ofInstant(instant, zone);
  }

  public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
    ZoneId zone = ZoneId.systemDefault();
    Instant instant = localDateTime.atZone(zone).toInstant();
    return instant.toEpochMilli();
  }

  public static LocalDateTime parseStringToDateTime(String time) {
    DateTimeFormatter df = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN);
    return LocalDateTime.parse(time, df);
  }

  public static LocalDateTime parseStringToDateTime(String time, String format) {
    DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
    return LocalDateTime.parse(time, df);
  }

  public static boolean afterComparedDays(LocalDate given, LocalDate compare, long gap) {
    LocalDate localDate = compare.minusDays(gap);
    return given.isAfter(localDate);
  }

  public static boolean equalComparedDayGaps(LocalDate given, LocalDate compare, long gap) {
    LocalDate localDate = compare.minusDays(gap);
    return given.isEqual(localDate);
  }

  public static LocalDateTime getStartTime() {
    return LocalDateTime.now().with(LocalTime.MIN);
  }

  public static LocalDateTime getEndTime() {
    return LocalDateTime.now().with(LocalTime.MAX);
  }

  public static String getDateTime25Length() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_25_LENGTH));
  }

  public static String getDateTime8Length() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_8_LENGTH));
  }
}
