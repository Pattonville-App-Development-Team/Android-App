/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pattonvillecs.pattonvilleapp;

import android.support.annotation.DrawableRes;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import org.apache.commons.collections4.comparators.ComparatorChain;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;

import static android.graphics.Color.parseColor;
import static com.annimon.stream.Optional.empty;
import static com.annimon.stream.Optional.of;

@SuppressWarnings("ImmutableEnumChecker") // Optional<E> types are immutable
public enum DataSource {
    DISTRICT("Learning Center", "District",
            false, false, false, false,
            "http://psdr3.org/",
            "11097 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8500",
            empty(),
            empty(),
            of("Learning%20Center.ics"),
            of("http://fccms.psdr3.org/District/news/?plugin=xml&leaves"),
            "PSD",
            parseColor("#007a33"),
            empty(),
            empty(),
            "District",
            of(R.drawable.d_mascot)),
    HIGH_SCHOOL("Pattonville High School", "High School",
            true, true, false, false,
            "http://phs.psdr3.org/",
            "2497 Creve Coeur Mill Road, Maryland Heights, MO 63043",
            "(314) 213-8500",
            of("(314) 213-8351"),
            of("(314) 213-8696"),
            of("High%20School.ics"),
            of("http://fccms.psdr3.org/HighSchool/news/?plugin=xml&leaves"),
            "HS",
            parseColor("#02d4c4"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94969"),
            of("http://psdr3.nutrislice.com/menu/pattonville-high"),
            "Pattonville-High-School",
            of(R.drawable.hs_mascot)),
    HEIGHTS_MIDDLE_SCHOOL("Heights Middle School", "Heights",
            true, false, true, false,
            "http://ht.psdr3.org/",
            "195 Fee Fee Road, Maryland Heights, MO 63043 ",
            "(314) 213-8500",
            of("(314) 213-8333"),
            of("(314) 213-8633"),
            of("Heights.ics"),
            of("http://fccms.psdr3.org/Heights/news/?plugin=xml&leaves"),
            "HT",
            parseColor("#ddd739"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94968"),
            of("http://psdr3.nutrislice.com/menu/pattonville-heights"),
            "Heights-Middle-School",
            empty()),
    HOLMAN_MIDDLE_SCHOOL("Holman Middle School", "Holman",
            true, false, true, false,
            "http://ho.psdr3.org/",
            "11055 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8032",
            of("(314) 213-8332"),
            of("(314) 213-8632"),
            of("Holman.ics"),
            of("http://fccms.psdr3.org/Holman/news/?plugin=xml&leaves"),
            "HO",
            parseColor("#ff8d00"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94975"),
            of("http://psdr3.nutrislice.com/menu/holman"),
            "Holman-Middle-School",
            empty()),
    REMINGTON_TRADITIONAL_SCHOOL("Remington Traditional School", "Remington",
            true, false, true, true,
            "http://remington.psdr3.org/",
            "102 Fee Fee Rd, Maryland Heights, MO 63043",
            "(314) 213-8016",
            of("(314) 213-8116"),
            of("(314) 213-8616"),
            of("Remington.ics"),
            of("http://fccms.psdr3.org/Remington/news/?plugin=xml&leaves"),
            "RT",
            parseColor("#e50b00"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94971"),
            of("http://psdr3.nutrislice.com/menu/remington-traditional"),
            "Remington-Traditional",
            of(R.drawable.rt_mascot)),
    BRIDGEWAY_ELEMENTARY("Bridgeway Elementary School", "Bridgeway",
            true, false, false, true,
            "http://bridgeway.psdr3.org/",
            "11635 Oakbury Court, Bridgeton, MO 63044",
            "(314) 213-8012",
            of("(314) 213-8112"),
            of("(314) 213-8612"),
            of("Bridgeway.ics"),
            of("http://fccms.psdr3.org/Bridgeway/news/?plugin=xml&leaves"),
            "BW",
            parseColor("#724338"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94979"),
            of("http://psdr3.nutrislice.com/menu/bridgeway"),
            "Bridgeway-Elementary",
            of(R.drawable.bw_mascot)),
    DRUMMOND_ELEMENTARY("Drummond Elementary School", "Drummond",
            true, false, false, true,
            "http://drummond.psdr3.org/",
            "3721 St. Bridget Lane, St Ann, MO 63074",
            "(314) 213-8419",
            of("(314) 213-8519"),
            of("(314) 213-8619"),
            of("Drummond.ics"),
            of("http://fccms.psdr3.org/Drummond/news/?plugin=xml&leaves"),
            "DR",
            parseColor("#73c300"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94976"),
            of("http://psdr3.nutrislice.com/menu/drummond"),
            "Drummond-Elementary",
            of(R.drawable.dr_mascot)),
    ROSE_ACRES_ELEMENTARY("Rose Acres Elementary School", "Rose Acres",
            true, false, false, true,
            "http://roseacres.psdr3.org/",
            "2905 Rose Acres Lane, Maryland Heights, MO 63043",
            "(314) 213-8017",
            of("(314) 213-8117"),
            of("(314) 213-8617"),
            of("Rose%20Acres.ics"),
            of("http://fccms.psdr3.org/RoseAcres/news/?plugin=xml&leaves"),
            "RA",
            parseColor("#f6258e"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94970"),
            of("http://psdr3.nutrislice.com/menu/rose-acres"),
            "Rose-Acres-Elementary",
            of(R.drawable.ra_mascot)),
    PARKWOOD_ELEMENTARY("Parkwood Elementary School", "Parkwood",
            true, false, false, true,
            "http://parkwood.psdr3.org/",
            "3199 Parkwood Lane, Maryland Heights, MO 63043",
            "(314) 213-8015",
            of("(314) 213-8115"),
            of("(314) 213-8615"),
            of("Parkwood.ics"),
            of("http://fccms.psdr3.org/Parkwood/news/?plugin=xml&leaves"),
            "PW",
            parseColor("#a300ff"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94967"),
            of("http://psdr3.nutrislice.com/menu/parkwood"),
            "Parkwood-Elementary",
            of(R.drawable.pw_mascot)),
    WILLOW_BROOK_ELEMENTARY("Willow Brook Elementary School", "Willow Brook",
            true, false, false, true,
            "http://willowbrook.psdr3.org/",
            "11022 Schuetz Road, Creve Coeur, MO 63146",
            "(314) 213-8018",
            of("(314) 213-8118"),
            of("(314) 213-8618"),
            of("Willow%20Brook.ics"),
            of("http://fccms.psdr3.org/WillowBrook/news/?plugin=xml&leaves"),
            "WB",
            parseColor("#000178"),
            of("https://www.peachjar.com/index.php?a=28&b=138&region=94953"),
            of("http://psdr3.nutrislice.com/menu/willow-brook"),
            "Willow-Brook-Elementary",
            of(R.drawable.wb_mascot)),
    EARLY_CHILDHOOD("Early Childhood", "Early Childhood",
            true, false, false, false,
            "http://ec.psdr3.org/",
            "11097 St. Charles Rock Road, St. Ann, MO 63074",
            "(314) 213-8105",
            empty(),
            of("(314) 213-8696"),
            of("Early%20Childhood.ics"),
            empty(),
            "EC",
            parseColor("#000000"),
            empty(),
            empty(),
            "Early-Childhood",
            empty());

    public static final Comparator<DataSource> DEFAULT_ORDERING = new ComparatorChain<>(Arrays.asList(
            (Comparator<DataSource>) (o1, o2) -> {
                Integer o1Value = o1.getSortingScore();
                Integer o2Value = o2.getSortingScore();
                return o1Value.compareTo(o2Value);
            },
            (o1, o2) -> o1.name.compareTo(o2.name)));

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

    public static final Set<DataSource> CALENDARS = Collections.unmodifiableSet(Stream.of(DataSource.ALL)
            .filter(d -> d.calendarURL.isPresent())
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(DataSource.class))));

    public final String name, shortName, websiteURL, address, mainNumber, initialsName, topicName;
    public final int calendarColor;
    @DrawableRes
    public final int mascotDrawableRes;
    public final boolean isDisableable, isHighSchool, isMiddleSchool, isElementarySchool;
    public final Optional<String> attendanceNumber, faxNumber, peachjarLink, nutrisliceLink, newsURL, calendarURL;

    DataSource(String name,
               String shortName,
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
               String topicName,
               Optional<Integer> mascotDrawableRes) {
        this.name = name;
        this.shortName = shortName;
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
        this.mascotDrawableRes = mascotDrawableRes.orElse(R.drawable.d_mascot);
    }

    public int getSortingScore() {
        if (!this.isDisableable)
            return 0;
        else if (this.isHighSchool)
            return 1;
        else if (this.isMiddleSchool)
            return 2;
        else if (this.isElementarySchool)
            return 3;
        else
            return 4;
    }

    @Override
    public String toString() {
        return this.name;
    }
}