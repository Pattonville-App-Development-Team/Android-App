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
    DISTRICT("Pattonville School District", 0, true, true, false, false, false, false,
            "11097 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8500",
            null,
            null),
    HIGH_SCHOOL("Pattonville High School", 1, true, true, true, true, false, false,
            "2497 Creve Coeur Mill Road, Maryland Heights, MO 63043",
            "(314) 213-8500",
            "(314) 213-8351",
            "(314) 213-8696"),
    HEIGHTS_MIDDLE_SCHOOL("Heights Middle School", 2, true, true, true, false, true, false,
            "195 Fee Fee Road, Maryland Heights, MO 63043 ",
            "(314) 213-8500",
            "(314) 213-8333",
            "(314) 213-8633"),
    HOLMAN_MIDDLE_SCHOOL("Holman Middle School", 3, true, true, true, false, true, false,
            "11055 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8032",
            "(314) 213-8332",
            "(314) 213-8632"),
    REMINGTON_TRADITIONAL_SCHOOL("Remington Traditional School", 4, true, true, true, false, true, true,
            "102 Fee Fee Rd, Maryland Heights, MO 63043",
            "(314) 213-8016",
            "(314) 213-8116",
            "(314) 213-8616"),
    BRIDGEWAY_ELEMENTARY("Bridgeway Elementary School", 5, true, true, true, false, false, true,
            "11635 Oakbury Court, Bridgeton, MO 63044",
            "(314) 213-8012",
            "(314) 213-8112",
            "(314) 213-8612"),
    DRUMMOND_ELEMENTARY("Drummond Elementary School", 6, true, true, true, false, false, true,
            "3721 St. Bridget Lane, St Ann, MO 63074",
            "(314) 213-8419",
            "(314) 213-8519",
            "(314) 213-8619"),
    ROSE_ACRES_ELEMENTARY("Rose Acres Elementary School", 7, true, true, true, false, false, true,
            "2905 Rose Acres Lane, Maryland Heights, MO 63043",
            "(314) 213-8017",
            "(314) 213-8117",
            "(314) 213-8617"),
    PARKWOOD_ELEMENTARY("Parkwood Elementary School", 8, true, true, true, false, false, true,
            "3199 Parkwood Lane, Maryland Heights, MO 63043",
            "(314) 213-8015",
            "(314) 213-8115",
            "(314) 213-8615"),
    WILLOW_BROOK_ELEMENTARY("Willow Brook Elementary School", 9, true, true, true, false, false, true,
            "11022 Schuetz Road, Creve Coeur, MO 63146",
            "(314) 213-8018",
            "(314) 213-8118",
            "(314) 213-8618");

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

    public final String name, address, mainNumber, attendanceNumber, faxNumber;
    public final int id;
    public final boolean hasCalendar, hasNews, isDisableable, isHighSchool, isMiddleSchool, isElementarySchool;


    DataSource(String name, int id, boolean hasCalendar, boolean hasNews, boolean isDisableable,
               boolean isHighSchool, boolean isMiddleSchool, boolean isElementarySchool,
               String address, String mainNumber, String attendanceNumber, String faxNumber) {
        this.name = name;
        this.id = id;
        this.hasCalendar = hasCalendar;
        this.hasNews = hasNews;
        this.isDisableable = isDisableable;
        this.isHighSchool = isHighSchool;
        this.isMiddleSchool = isMiddleSchool;
        this.isElementarySchool = isElementarySchool;
        this.address = address;
        this.mainNumber = mainNumber;
        this.attendanceNumber = attendanceNumber;
        this.faxNumber = faxNumber;
    }
}
