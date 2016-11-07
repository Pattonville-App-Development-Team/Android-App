package org.pattonvillecs.pattonvilleapp;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by skaggsm on 11/7/16.
 */

public enum DataSource {
    DISTRICT(0, true, true, false),
    HIGH_SCHOOL(1, true, true, true),
    HEIGHTS_MIDDLE_SCHOOL(2, true, true, true),
    HOLMAN_MIDDLE_SCHOOL(3, true, true, true),
    REMINGTON_TRADITIONAL_SCHOOL(4, true, true, true),
    WILLOW_BROOK_ELEMTARY(5, true, true, true),
    BRIDGEWAY_ELEMENTARY(6, true, true, true),
    DRUMMOND_ELEMENTARY(7, true, true, true),
    ROSE_ACRES_ELEMENTARY(8, true, true, true),
    PARKWOOD_ELEMENTARY(9, true, true, true);

    public static final Set<DataSource> SCHOOLS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            HIGH_SCHOOL,
            HEIGHTS_MIDDLE_SCHOOL,
            HOLMAN_MIDDLE_SCHOOL,
            REMINGTON_TRADITIONAL_SCHOOL,
            WILLOW_BROOK_ELEMTARY,
            BRIDGEWAY_ELEMENTARY,
            DRUMMOND_ELEMENTARY,
            ROSE_ACRES_ELEMENTARY,
            PARKWOOD_ELEMENTARY
    )));
    public static final Set<DataSource> MIDDLE_SCHOOLS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            HEIGHTS_MIDDLE_SCHOOL,
            HOLMAN_MIDDLE_SCHOOL,
            REMINGTON_TRADITIONAL_SCHOOL
    )));
    public static final Set<DataSource> HIGH_SCHOOLS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            HIGH_SCHOOL
    )));
    public static final Set<DataSource> ALL = Collections.unmodifiableSet(EnumSet.allOf(DataSource.class));
    public static final Set<DataSource> ELEMENTARY_SCHOOLS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            REMINGTON_TRADITIONAL_SCHOOL,
            WILLOW_BROOK_ELEMTARY,
            BRIDGEWAY_ELEMENTARY,
            DRUMMOND_ELEMENTARY,
            ROSE_ACRES_ELEMENTARY,
            PARKWOOD_ELEMENTARY
    )));
    public static final Set<DataSource> DISABLEABLE_NEWS_SOURCES = Collections.unmodifiableSet(Stream.of(DataSource.ALL).filter(value -> value.hasNews && value.isDisableable).collect(Collectors.toSet()));
            /*
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                    HIGH_SCHOOL,
                    HEIGHTS_MIDDLE_SCHOOL,
                    HOLMAN_MIDDLE_SCHOOL,
                    REMINGTON_TRADITIONAL_SCHOOL,
                    WILLOW_BROOK_ELEMTARY,
                    BRIDGEWAY_ELEMENTARY,
                    DRUMMOND_ELEMENTARY,
                    ROSE_ACRES_ELEMENTARY,
                    PARKWOOD_ELEMENTARY
            )));
            */

    public final int id;
    public final boolean hasCalendar, hasNews, isDisableable;


    DataSource(int id, boolean hasCalendar, boolean hasNews, boolean isDisableable) {
        this.id = id;
        this.hasCalendar = hasCalendar;
        this.hasNews = hasNews;
        this.isDisableable = isDisableable;
    }
}
