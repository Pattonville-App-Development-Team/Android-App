package org.pattonvillecs.pattonvilleapp;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by skaggsm on 11/7/16.
 */

public enum DataSource {
    DISTRICT(0, true, true, false, false, false, false),
    HIGH_SCHOOL(1, true, true, true, true, false, false),
    HEIGHTS_MIDDLE_SCHOOL(2, true, true, true, false, true, false),
    HOLMAN_MIDDLE_SCHOOL(3, true, true, true, false, true, false),
    REMINGTON_TRADITIONAL_SCHOOL(4, true, true, true, false, true, true),
    WILLOW_BROOK_ELEMENTARY(5, true, true, true, false, false, true),
    BRIDGEWAY_ELEMENTARY(6, true, true, true, false, false, true),
    DRUMMOND_ELEMENTARY(7, true, true, true, false, false, true),
    ROSE_ACRES_ELEMENTARY(8, true, true, true, false, false, true),
    PARKWOOD_ELEMENTARY(9, true, true, true, false, false, true);

    public static final Set<DataSource> ALL = Collections.unmodifiableSet(EnumSet.allOf(DataSource.class));

    public static final Set<DataSource> MIDDLE_SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(d -> d.isMiddleSchool)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public static final Set<DataSource> HIGH_SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(d -> d.isHighSchool)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public static final Set<DataSource> ELEMENTARY_SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(d -> d.isElementarySchool)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public static final Set<DataSource> DISABLEABLE_NEWS_SOURCES = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(value -> value.hasNews && value.isDisableable)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public static final Set<DataSource> SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(d -> d.isHighSchool || d.isMiddleSchool || d.isElementarySchool)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public final int id;
    public final boolean hasCalendar, hasNews, isDisableable, isHighSchool, isMiddleSchool, isElementarySchool;


    DataSource(int id, boolean hasCalendar, boolean hasNews, boolean isDisableable, boolean isHighSchool, boolean isMiddleSchool, boolean isElementarySchool) {
        this.id = id;
        this.hasCalendar = hasCalendar;
        this.hasNews = hasNews;
        this.isDisableable = isDisableable;
        this.isHighSchool = isHighSchool;
        this.isMiddleSchool = isMiddleSchool;
        this.isElementarySchool = isElementarySchool;
    }
}
