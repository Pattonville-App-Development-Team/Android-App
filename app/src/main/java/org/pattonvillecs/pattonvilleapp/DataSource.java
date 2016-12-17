package org.pattonvillecs.pattonvilleapp;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.annimon.stream.function.Supplier;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum DataSource {
    DISTRICT("Pattonville School District", "District", 0, true, true, false, false, false, false,
            "11097 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8500",
            null,
            null,
            "http://drummond.psdr3.org/ical/District.ics",
            Optional.<String>empty()),
    HIGH_SCHOOL("Pattonville High School", "High School", 1, true, true, true, true, false, false,
            "2497 Creve Coeur Mill Road, Maryland Heights, MO 63043",
            "(314) 213-8500",
            "(314) 213-8351",
            "(314) 213-8696",
            "http://drummond.psdr3.org/ical/High%20School.ics",
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94969")),
    HEIGHTS_MIDDLE_SCHOOL("Heights Middle School", "Heights", 2, true, true, true, false, true, false,
            "195 Fee Fee Road, Maryland Heights, MO 63043 ",
            "(314) 213-8500",
            "(314) 213-8333",
            "(314) 213-8633",
            "http://drummond.psdr3.org/ical/Heights.ics",
            Optional.<String>empty()),
    HOLMAN_MIDDLE_SCHOOL("Holman Middle School", "Holman", 3, true, true, true, false, true, false,
            "11055 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8032",
            "(314) 213-8332",
            "(314) 213-8632",
            "http://drummond.psdr3.org/ical/Holman.ics",
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94975")),
    REMINGTON_TRADITIONAL_SCHOOL("Remington Traditional School", "Remingon", 4, true, true, true, false, true, true,
            "102 Fee Fee Rd, Maryland Heights, MO 63043",
            "(314) 213-8016",
            "(314) 213-8116",
            "(314) 213-8616",
            "http://drummond.psdr3.org/ical/Remington.ics",
            Optional.<String>empty()),
    BRIDGEWAY_ELEMENTARY("Bridgeway Elementary School", "Bridgeway", 5, true, true, true, false, false, true,
            "11635 Oakbury Court, Bridgeton, MO 63044",
            "(314) 213-8012",
            "(314) 213-8112",
            "(314) 213-8612",
            "http://drummond.psdr3.org/ical/Bridgeway.ics",
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94979")),
    DRUMMOND_ELEMENTARY("Drummond Elementary School", "Drumond", 6, true, true, true, false, false, true,
            "3721 St. Bridget Lane, St Ann, MO 63074",
            "(314) 213-8419",
            "(314) 213-8519",
            "(314) 213-8619",
            "http://drummond.psdr3.org/ical/Drummond.ics",
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94976")),
    ROSE_ACRES_ELEMENTARY("Rose Acres Elementary School", "Rose Acres", 7, true, true, true, false, false, true,
            "2905 Rose Acres Lane, Maryland Heights, MO 63043",
            "(314) 213-8017",
            "(314) 213-8117",
            "(314) 213-8617",
            "http://drummond.psdr3.org/ical/Rose%20Acres.ics",
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94970")),
    PARKWOOD_ELEMENTARY("Parkwood Elementary School", "Parkwood", 8, true, true, true, false, false, true,
            "3199 Parkwood Lane, Maryland Heights, MO 63043",
            "(314) 213-8015",
            "(314) 213-8115",
            "(314) 213-8615",
            "http://drummond.psdr3.org/ical/Parkwood.ics",
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94967")),
    WILLOW_BROOK_ELEMENTARY("Willow Brook Elementary School", "Willow Brook", 9, true, true, true, false, false, true,
            "11022 Schuetz Road, Creve Coeur, MO 63146",
            "(314) 213-8018",
            "(314) 213-8118",
            "(314) 213-8618",
            "http://drummond.psdr3.org/ical/Willow%20Brook.ics",
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94953"));

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

    public static final Set<DataSource> PEACHJAR = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(new Predicate<DataSource>() {
                @Override
                public boolean test(DataSource d) {
                    return d.peachjarLink.isPresent();
                }
            })
            .collect(Collectors.toCollection(new Supplier<EnumSet<DataSource>>() {
                @Override
                public EnumSet<DataSource> get() {
                    return EnumSet.noneOf(DataSource.class);
                }
            })));

    public final String name, shortName, address, mainNumber, attendanceNumber, faxNumber, dataLink;
    public final int id;
    public final boolean hasCalendar, hasNews, isDisableable, isHighSchool, isMiddleSchool, isElementarySchool;
    public final Optional<String> peachjarLink;


    DataSource(String name, String shortName, int id, boolean hasCalendar, boolean hasNews, boolean isDisableable,
               boolean isHighSchool, boolean isMiddleSchool, boolean isElementarySchool,
               String address, String mainNumber, String attendanceNumber, String faxNumber, String dataLink, Optional<String> peachjarLink) {
        this.name = name;
        this.shortName = shortName;
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
        this.peachjarLink = peachjarLink;
        this.dataLink = dataLink;
    }
}
