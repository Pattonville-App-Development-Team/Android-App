package org.pattonvillecs.pattonvilleapp;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.annimon.stream.function.Supplier;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by skaggsm on 11/7/16.
 */

public enum DataSource {
    DISTRICT("District", 0, true, true, false, false, false, false),
    HIGH_SCHOOL("Pattonville High School", 1, true, true, true, true, false, false),
    HEIGHTS_MIDDLE_SCHOOL("Heights", 2, true, true, true, false, true, false),
    HOLMAN_MIDDLE_SCHOOL("Holman", 3, true, true, true, false, true, false),
    REMINGTON_TRADITIONAL_SCHOOL("Remington Traditional School", 4, true, true, true, false, true, true),
    WILLOW_BROOK_ELEMENTARY("Willow Brook", 5, true, true, true, false, false, true),
    BRIDGEWAY_ELEMENTARY("Bridgeway", 6, true, true, true, false, false, true),
    DRUMMOND_ELEMENTARY("Drummond", 7, true, true, true, false, false, true),
    ROSE_ACRES_ELEMENTARY("Rose Acres", 8, true, true, true, false, false, true),
    PARKWOOD_ELEMENTARY("Parkwood", 9, true, true, true, false, false, true);

    public static final Set<DataSource> ALL = Collections.unmodifiableSet(EnumSet.allOf(DataSource.class));

    public static final Set<DataSource> MIDDLE_SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(new Predicate<DataSource>() {
                @Override
                public boolean test(DataSource value) {
                    return value.isMiddleSchool;
                }
            }).collect(Collectors.toCollection(new Supplier<EnumSet<DataSource>>() {
                @Override
                public EnumSet<DataSource> get() {
                    return EnumSet.noneOf(DataSource.class);
                }
            })));

    public static final Set<DataSource> HIGH_SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(new Predicate<DataSource>() {
                @Override
                public boolean test(DataSource value) {
                    return value.isHighSchool;
                }
            }).collect(Collectors.toCollection(new Supplier<EnumSet<DataSource>>() {
                @Override
                public EnumSet<DataSource> get() {
                    return EnumSet.noneOf(DataSource.class);
                }
            })));

    public static final Set<DataSource> ELEMENTARY_SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(new Predicate<DataSource>() {
                @Override
                public boolean test(DataSource d) {
                    return d.isElementarySchool;
                }
            })
            .collect(Collectors.toCollection(new Supplier<EnumSet<DataSource>>() {
                @Override
                public EnumSet<DataSource> get() {
                    return EnumSet.noneOf(DataSource.class);
                }
            })));

    public static final Set<DataSource> DISABLEABLE_NEWS_SOURCES = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(new Predicate<DataSource>() {
                @Override
                public boolean test(DataSource value) {
                    return value.hasNews && value.isDisableable;
                }
            })
            .collect(Collectors.toCollection(new Supplier<EnumSet<DataSource>>() {
                @Override
                public EnumSet<DataSource> get() {
                    return EnumSet.noneOf(DataSource.class);
                }
            })));

    public static final Set<DataSource> SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(new Predicate<DataSource>() {
                @Override
                public boolean test(DataSource d) {
                    return d.isHighSchool || d.isMiddleSchool || d.isElementarySchool;
                }
            })
            .collect(Collectors.toCollection(new Supplier<EnumSet<DataSource>>() {
                @Override
                public EnumSet<DataSource> get() {
                    return EnumSet.noneOf(DataSource.class);
                }
            })));

    public final String name;
    public final int id;
    public final boolean hasCalendar, hasNews, isDisableable, isHighSchool, isMiddleSchool, isElementarySchool;


    DataSource(String name, int id, boolean hasCalendar, boolean hasNews, boolean isDisableable, boolean isHighSchool, boolean isMiddleSchool, boolean isElementarySchool) {
        this.name = name;
        this.id = id;
        this.hasCalendar = hasCalendar;
        this.hasNews = hasNews;
        this.isDisableable = isDisableable;
        this.isHighSchool = isHighSchool;
        this.isMiddleSchool = isMiddleSchool;
        this.isElementarySchool = isElementarySchool;
    }
}
