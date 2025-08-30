package com.bookify.bookify_app.config;

// ********************************************************************************************
// * MongoConvertersConfig: Custom converters for MongoDB date/time mapping                   *
// *                                                                                          *
// * WHAT                                                                                     *
// * - Defines converters between Java's ZonedDateTime <-> java.util.Date.                    *
// * - WriteConverter: stores ZonedDateTime as Date (MongoDB format).                         *
// * - ReadConverter: reads Date back into ZonedDateTime (Java API).                          *
// * - Registers converters as a Spring bean via MongoCustomConversions.                      *
// *                                                                                          *
// * WHY                                                                                      *
// * - MongoDB natively stores dates as BSON Date (mapped to java.util.Date).                 *
// * - Java apps often use ZonedDateTime for richer time zone handling.                       *
// * - Without converters, Spring Data MongoDB cannot directly map ZonedDateTime fields.      *
// * - Central config ensures consistent and automatic conversion app-wide.                   *
// ********************************************************************************************

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

@Configuration
public class MongoConvertersConfig {

    // Explicit converter class: ZonedDateTime -> Date
    static class ZonedDateTimeWriteConverter implements Converter<ZonedDateTime, Date> {
        @Override
        public Date convert(ZonedDateTime source) {
            return Date.from(source.toInstant());
        }
    }

    // Explicit converter class: Date -> ZonedDateTime
    static class ZonedDateTimeReadConverter implements Converter<Date, ZonedDateTime> {
        @Override
        public ZonedDateTime convert(Date source) {
            return source.toInstant().atZone(ZoneId.systemDefault());
        }
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new ZonedDateTimeWriteConverter(),
                new ZonedDateTimeReadConverter()
        ));
    }
}
