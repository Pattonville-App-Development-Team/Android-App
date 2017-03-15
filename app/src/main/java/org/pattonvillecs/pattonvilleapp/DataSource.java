package org.pattonvillecs.pattonvilleapp;

import android.graphics.Color;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.annimon.stream.function.Supplier;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@SuppressWarnings("RedundantTypeArguments")
public enum DataSource {
    DISTRICT("Pattonville School District", "District", 0, true, false, false, false, false,
            "http://psdr3.org/",
            "11097 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8500",
            Optional.<String>empty(),
            Optional.<String>empty(),
            "http://drummond.psdr3.org/ical/Learning%20Center.ics",
            "http://fccms.psdr3.org/District/news/?plugin=xml&leaves",
            "PSD",
            Color.parseColor("#007a33"),
            Optional.<String>empty(),
            Optional.<String>empty(),
            "/topics/District"),
    HIGH_SCHOOL("Pattonville High School", "High School", 1, true, true, true, false, false,
            "http://phs.psdr3.org/",
            "2497 Creve Coeur Mill Road, Maryland Heights, MO 63043",
            "(314) 213-8500",
            Optional.of("(314) 213-8351"),
            Optional.of("(314) 213-8696"),
            "http://drummond.psdr3.org/ical/High%20School.ics",
            "http://fccms.psdr3.org/HighSchool/news/?plugin=xml&leaves",
            "HS",
            Color.parseColor("#02d4c4"),
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94969"),
            Optional.of("http://psdr3.nutrislice.com/menu/pattonville-high"),
            "/topics/Pattonville-High-School"),
    HEIGHTS_MIDDLE_SCHOOL("Heights Middle School", "Heights", 2, true, true, false, true, false,
            "http://ht.psdr3.org/",
            "195 Fee Fee Road, Maryland Heights, MO 63043 ",
            "(314) 213-8500",
            Optional.of("(314) 213-8333"),
            Optional.of("(314) 213-8633"),
            "http://drummond.psdr3.org/ical/Heights.ics",
            "http://fccms.psdr3.org/Heights/news/?plugin=xml&leaves",
            "HT",
            Color.parseColor("#ddd739"),
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94968"),
            Optional.of("http://psdr3.nutrislice.com/menu/pattonville-heights"),
            "/topics/Heights-Middle-School"),
    HOLMAN_MIDDLE_SCHOOL("Holman Middle School", "Holman", 3, true, true, false, true, false,
            "http://ho.psdr3.org/",
            "11055 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8032",
            Optional.of("(314) 213-8332"),
            Optional.of("(314) 213-8632"),
            "http://drummond.psdr3.org/ical/Holman.ics",
            "http://fccms.psdr3.org/Holman/news/?plugin=xml&leaves",
            "HO",
            Color.parseColor("#ff8d00"),
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94975"),
            Optional.of("http://psdr3.nutrislice.com/menu/holman"),
            "/topics/Holman-Middle-School"),
    REMINGTON_TRADITIONAL_SCHOOL("Remington Traditional School", "Remington", 4, true, true, false, true, true,
            "http://remington.psdr3.org/",
            "102 Fee Fee Rd, Maryland Heights, MO 63043",
            "(314) 213-8016",
            Optional.of("(314) 213-8116"),
            Optional.of("(314) 213-8616"),
            "http://drummond.psdr3.org/ical/Remington.ics",
            "http://fccms.psdr3.org/Remington/news/?plugin=xml&leaves",
            "RT",
            Color.parseColor("#e50b00"),
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94971"),
            Optional.of("http://psdr3.nutrislice.com/menu/remington-traditional"),
            "/topics/Remington-Traditional"),
    BRIDGEWAY_ELEMENTARY("Bridgeway Elementary School", "Bridgeway", 5, true, true, false, false, true,
            "http://bridgeway.psdr3.org/",
            "11635 Oakbury Court, Bridgeton, MO 63044",
            "(314) 213-8012",
            Optional.of("(314) 213-8112"),
            Optional.of("(314) 213-8612"),
            "http://drummond.psdr3.org/ical/Bridgeway.ics",
            "http://fccms.psdr3.org/Bridgeway/news/?plugin=xml&leaves",
            "BW",
            Color.parseColor("#724338"),
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94979"),
            Optional.of("http://psdr3.nutrislice.com/menu/bridgeway"),
            "/topics/Bridgeway-Elementary"),
    DRUMMOND_ELEMENTARY("Drummond Elementary School", "Drummond", 6, true, true, false, false, true,
            "http://drummond.psdr3.org/",
            "3721 St. Bridget Lane, St Ann, MO 63074",
            "(314) 213-8419",
            Optional.of("(314) 213-8519"),
            Optional.of("(314) 213-8619"),
            "http://drummond.psdr3.org/ical/Drummond.ics",
            "http://fccms.psdr3.org/Drummond/news/?plugin=xml&leaves",
            "DR",
            Color.parseColor("#73c300"),
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94976"),
            Optional.of("http://psdr3.nutrislice.com/menu/drummond"),
            "/topics/Drummond-Elementary"),
    ROSE_ACRES_ELEMENTARY("Rose Acres Elementary School", "Rose Acres", 7, true, true, false, false, true,
            "http://roseacres.psdr3.org/",
            "2905 Rose Acres Lane, Maryland Heights, MO 63043",
            "(314) 213-8017",
            Optional.of("(314) 213-8117"),
            Optional.of("(314) 213-8617"),
            "http://drummond.psdr3.org/ical/Rose%20Acres.ics",
            "http://fccms.psdr3.org/RoseAcres/news/?plugin=xml&leaves",
            "RA",
            Color.parseColor("#f6258e"),
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94970"),
            Optional.of("http://psdr3.nutrislice.com/menu/rose-acres"),
            "/topics/Rose-Acres-Elementary"),
    PARKWOOD_ELEMENTARY("Parkwood Elementary School", "Parkwood", 8, true, true, false, false, true,
            "http://parkwood.psdr3.org/",
            "3199 Parkwood Lane, Maryland Heights, MO 63043",
            "(314) 213-8015",
            Optional.of("(314) 213-8115"),
            Optional.of("(314) 213-8615"),
            "http://drummond.psdr3.org/ical/Parkwood.ics",
            "http://fccms.psdr3.org/Parkwood/news/?plugin=xml&leaves",
            "PW",
            Color.parseColor("#a300ff"),
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94967"),
            Optional.of("http://psdr3.nutrislice.com/menu/parkwood"),
            "/topics/Parkwood-Elementary"),
    WILLOW_BROOK_ELEMENTARY("Willow Brook Elementary School", "Willow Brook", 9, true, true, false, false, true,
            "http://willowbrook.psdr3.org/",
            "11022 Schuetz Road, Creve Coeur, MO 63146",
            "(314) 213-8018",
            Optional.of("(314) 213-8118"),
            Optional.of("(314) 213-8618"),
            "http://drummond.psdr3.org/ical/Willow%20Brook.ics",
            "http://fccms.psdr3.org/WillowBrook/news/?plugin=xml&leaves",
            "WB",
            Color.parseColor("#000178"),
            Optional.of("https://www.peachjar.com/index.php?a=28&b=138&region=94953"),
            Optional.of("http://psdr3.nutrislice.com/menu/willow-brook"),
            "/topics/Willow-Brook-Elementary");

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

    public static final Set<DataSource> NUTRISLICE = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(new Predicate<DataSource>() {
                @Override
                public boolean test(DataSource d) {
                    return d.nutrisliceLink.isPresent();
                }
            })
            .collect(Collectors.toCollection(new Supplier<EnumSet<DataSource>>() {
                @Override
                public EnumSet<DataSource> get() {
                    return EnumSet.noneOf(DataSource.class);
                }
            })));

    public final String name, shortName, websiteURL, address, mainNumber, calendarURL, newsURL, initialsName, topicName;
    public final int id, calendarColor;
    public final boolean hasNews, isDisableable, isHighSchool, isMiddleSchool, isElementarySchool;
    public final Optional<String> attendanceNumber, faxNumber, peachjarLink, nutrisliceLink;

    DataSource(String name, String shortName, int id, boolean hasNews, boolean isDisableable,
               boolean isHighSchool, boolean isMiddleSchool, boolean isElementarySchool,
               String websiteLink, String address, String mainNumber, Optional<String> attendanceNumber,
               Optional<String> faxNumber, String calendarLink, String newsURL, String initialsName, int calendarColor,
               Optional<String> peachjarLink, Optional<String> nutrisliceLink, String topicName) {
        this.name = name;
        this.shortName = shortName;
        this.id = id;
        this.hasNews = hasNews;
        this.isDisableable = isDisableable;
        this.isHighSchool = isHighSchool;
        this.isMiddleSchool = isMiddleSchool;
        this.isElementarySchool = isElementarySchool;
        this.websiteURL = websiteLink;
        this.address = address;
        this.mainNumber = mainNumber;
        this.attendanceNumber = attendanceNumber;
        this.faxNumber = faxNumber;
        this.initialsName = initialsName;
        this.peachjarLink = peachjarLink;
        this.calendarURL = calendarLink;
        this.calendarColor = calendarColor;
        this.newsURL = newsURL;
        this.nutrisliceLink = nutrisliceLink;
        this.topicName = topicName;
    }
}