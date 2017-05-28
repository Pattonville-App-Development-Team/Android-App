package org.pattonvillecs.pattonvilleapp;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static android.graphics.Color.parseColor;
import static com.annimon.stream.Optional.empty;
import static com.annimon.stream.Optional.of;

public enum DataSource {
    DISTRICT("Learning Center", "District", 0,
            false, false, false, false,
            "http://psdr3.org/",
            "11097 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8500",
            empty(),
            empty(),
            of("http://drummond.psdr3.org/ical/Learning%20Center.ics"),
            of("http://fccms.psdr3.org/District/news/?plugin=xml&leaves"),
            "PSD",
            parseColor("#007a33"),
            empty(),
            empty(),
            "District"),
    HIGH_SCHOOL("Pattonville High School", "High School", 1,
            true, true, false, false,
            "http://phs.psdr3.org/",
            "2497 Creve Coeur Mill Road, Maryland Heights, MO 63043",
            "(314) 213-8500",
            of("(314) 213-8351"),
            of("(314) 213-8696"),
            of("http://drummond.psdr3.org/ical/High%20School.ics"),
            of("http://fccms.psdr3.org/HighSchool/news/?plugin=xml&leaves"),
            "HS",
            parseColor("#02d4c4"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94969"),
            of("http://psdr3.nutrislice.com/menu/pattonville-high"),
            "Pattonville-High-School"),
    HEIGHTS_MIDDLE_SCHOOL("Heights Middle School", "Heights", 2,
            true, false, true, false,
            "http://ht.psdr3.org/",
            "195 Fee Fee Road, Maryland Heights, MO 63043 ",
            "(314) 213-8500",
            of("(314) 213-8333"),
            of("(314) 213-8633"),
            of("http://drummond.psdr3.org/ical/Heights.ics"),
            of("http://fccms.psdr3.org/Heights/news/?plugin=xml&leaves"),
            "HT",
            parseColor("#ddd739"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94968"),
            of("http://psdr3.nutrislice.com/menu/pattonville-heights"),
            "Heights-Middle-School"),
    HOLMAN_MIDDLE_SCHOOL("Holman Middle School", "Holman", 3,
            true, false, true, false,
            "http://ho.psdr3.org/",
            "11055 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8032",
            of("(314) 213-8332"),
            of("(314) 213-8632"),
            of("http://drummond.psdr3.org/ical/Holman.ics"),
            of("http://fccms.psdr3.org/Holman/news/?plugin=xml&leaves"),
            "HO",
            parseColor("#ff8d00"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94975"),
            of("http://psdr3.nutrislice.com/menu/holman"),
            "Holman-Middle-School"),
    REMINGTON_TRADITIONAL_SCHOOL("Remington Traditional School", "Remington", 4,
            true, false, true, true,
            "http://remington.psdr3.org/",
            "102 Fee Fee Rd, Maryland Heights, MO 63043",
            "(314) 213-8016",
            of("(314) 213-8116"),
            of("(314) 213-8616"),
            of("http://drummond.psdr3.org/ical/Remington.ics"),
            of("http://fccms.psdr3.org/Remington/news/?plugin=xml&leaves"),
            "RT",
            parseColor("#e50b00"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94971"),
            of("http://psdr3.nutrislice.com/menu/remington-traditional"),
            "Remington-Traditional"),
    BRIDGEWAY_ELEMENTARY("Bridgeway Elementary School", "Bridgeway", 5,
            true, false, false, true,
            "http://bridgeway.psdr3.org/",
            "11635 Oakbury Court, Bridgeton, MO 63044",
            "(314) 213-8012",
            of("(314) 213-8112"),
            of("(314) 213-8612"),
            of("http://drummond.psdr3.org/ical/Bridgeway.ics"),
            of("http://fccms.psdr3.org/Bridgeway/news/?plugin=xml&leaves"),
            "BW",
            parseColor("#724338"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94979"),
            of("http://psdr3.nutrislice.com/menu/bridgeway"),
            "Bridgeway-Elementary"),
    DRUMMOND_ELEMENTARY("Drummond Elementary School", "Drummond", 6,
            true, false, false, true,
            "http://drummond.psdr3.org/",
            "3721 St. Bridget Lane, St Ann, MO 63074",
            "(314) 213-8419",
            of("(314) 213-8519"),
            of("(314) 213-8619"),
            of("http://drummond.psdr3.org/ical/Drummond.ics"),
            of("http://fccms.psdr3.org/Drummond/news/?plugin=xml&leaves"),
            "DR",
            parseColor("#73c300"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94976"),
            of("http://psdr3.nutrislice.com/menu/drummond"),
            "Drummond-Elementary"),
    ROSE_ACRES_ELEMENTARY("Rose Acres Elementary School", "Rose Acres", 7,
            true, false, false, true,
            "http://roseacres.psdr3.org/",
            "2905 Rose Acres Lane, Maryland Heights, MO 63043",
            "(314) 213-8017",
            of("(314) 213-8117"),
            of("(314) 213-8617"),
            of("http://drummond.psdr3.org/ical/Rose%20Acres.ics"),
            of("http://fccms.psdr3.org/RoseAcres/news/?plugin=xml&leaves"),
            "RA",
            parseColor("#f6258e"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94970"),
            of("http://psdr3.nutrislice.com/menu/rose-acres"),
            "Rose-Acres-Elementary"),
    PARKWOOD_ELEMENTARY("Parkwood Elementary School", "Parkwood", 8,
            true, false, false, true,
            "http://parkwood.psdr3.org/",
            "3199 Parkwood Lane, Maryland Heights, MO 63043",
            "(314) 213-8015",
            of("(314) 213-8115"),
            of("(314) 213-8615"),
            of("http://drummond.psdr3.org/ical/Parkwood.ics"),
            of("http://fccms.psdr3.org/Parkwood/news/?plugin=xml&leaves"),
            "PW",
            parseColor("#a300ff"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94967"),
            of("http://psdr3.nutrislice.com/menu/parkwood"),
            "Parkwood-Elementary"),
    WILLOW_BROOK_ELEMENTARY("Willow Brook Elementary School", "Willow Brook", 9,
            true, false, false, true,
            "http://willowbrook.psdr3.org/",
            "11022 Schuetz Road, Creve Coeur, MO 63146",
            "(314) 213-8018",
            of("(314) 213-8118"),
            of("(314) 213-8618"),
            of("http://drummond.psdr3.org/ical/Willow%20Brook.ics"),
            of("http://fccms.psdr3.org/WillowBrook/news/?plugin=xml&leaves"),
            "WB",
            parseColor("#000178"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94953"),
            of("http://psdr3.nutrislice.com/menu/willow-brook"),
            "Willow-Brook-Elementary"),
    EARLY_CHILDHOOD("Early Childhood", "Early Childhood", 10,
            true, false, false, false,
            "http://ec.psdr3.org/",
            "11097 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8105",
            empty(),
            of("(314) 213-8696"),
            of("http://ec.psdr3.org/ical/Early%20Childhood.ics"),
            empty(),
            "EC",
            parseColor("#000000"),
            empty(),
            empty(),
            "Early-Childhood");

    public static final Set<DataSource> ALL = Collections.unmodifiableSet(EnumSet.allOf(DataSource.class));

    public static final Set<DataSource> MIDDLE_SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(value -> value.isMiddleSchool).collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public static final Set<DataSource> HIGH_SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(value -> value.isHighSchool).collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public static final Set<DataSource> ELEMENTARY_SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(d -> d.isElementarySchool)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public static final Set<DataSource> DISABLEABLE_NEWS_SOURCES = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(value -> value.newsURL.isPresent() && value.isDisableable)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public static final Set<DataSource> SCHOOLS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(d -> d.isHighSchool || d.isMiddleSchool || d.isElementarySchool)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public static final Set<DataSource> PEACHJAR = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(d -> d.peachjarLink.isPresent())
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public static final Set<DataSource> NUTRISLICE = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(d -> d.nutrisliceLink.isPresent())
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public final String name, shortName, websiteURL, address, mainNumber, initialsName, topicName;
    public final int id, calendarColor;
    public final boolean isDisableable, isHighSchool, isMiddleSchool, isElementarySchool;
    public final Optional<String> attendanceNumber, faxNumber, peachjarLink, nutrisliceLink, newsURL, calendarURL;

    DataSource(String name,
               String shortName,
               int id,
               boolean isDisableable,
               boolean isHighSchool,
               boolean isMiddleSchool,
               boolean isElementarySchool,
               String websiteLink,
               String address,
               String mainNumber,
               Optional<String> attendanceNumber,
               Optional<String> faxNumber,
               Optional<String> calendarURL,
               Optional<String> newsURL,
               String initialsName,
               int calendarColor,
               Optional<String> peachjarLink,
               Optional<String> nutrisliceLink,
               String topicName) {
        this.name = name;
        this.shortName = shortName;
        this.id = id;
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
        this.calendarURL = calendarURL;
        this.calendarColor = calendarColor;
        this.newsURL = newsURL;
        this.nutrisliceLink = nutrisliceLink;
        this.topicName = topicName;
    }

    @Override
    public String toString() {
        return this.name;
    }
}