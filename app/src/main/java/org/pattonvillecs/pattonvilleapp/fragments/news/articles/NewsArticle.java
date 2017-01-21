package org.pattonvillecs.pattonvilleapp.fragments.news.articles;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class NewsArticle implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NewsArticle createFromParcel(Parcel in) {
            return new NewsArticle(in);
        }

        public NewsArticle[] newArray(int size) {
            return new NewsArticle[size];
        }
    };
    private Date publishDate;
    private String title, content;
    private int sourceColor;

    public NewsArticle() {

        publishDate = new Date(1484357778);
        title = "Some Title";
        content = "<td class=\"container\" style=\"background-color: #fff3f3f3;\"><div align=\"left\" style=\"line-height:115%;vertical-align:115%;text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>FUEL ED</b> is canceled for after school today, January 12.</font></div>\n" +
                "<br>\n" +
                "<div align=\"left\" style=\"line-height:115%;vertical-align:115%;text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>PALS APPLICATIONS ARE NOW AVAILABLE FOR THE 2017-2018 SCHOOL YEAR</b> and are available outside room B102, E201, and in the Guidance Office. &nbsp;Applications are due next Wednesday, January 18. &nbsp;Please see Mrs. Kuhn if you have any questions.</font></div>\n" +
                "<div align=\"left\" style=\"line-height:115%;vertical-align:115%;text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"></font></div>\n" +
                "<br>\n" +
                "<div align=\"left\" style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>CONTACT TIME MEETINGS FOR TODAY, JANUARY 12:</b></font></div>\n" +
                "<ul style=\"list-style-type:disc;list-style-position:outside;padding-left:36px;padding-right:0px;\">\n" +
                "<li style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>BATTLE OF THE BURETTES CLUB</b> will meet in Mrs. Shearrer's room during Contact Time.</font></li>\n" +
                "<li style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>GERMAN CLUB</b> will meet in H108.</font></li>\n" +
                "<li style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>ALL BOYS INTERESTED IN PLAYING VOLLEYBALL THIS SPRING:</b> &nbsp;Come to the <u>main gym</u> for an informational meeting.</font></li>\n" +
                "<li style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>THE KNITTING CLUB</b> will meet in Ms. Mulanax's room, E206. </font></li>\n" +
                "<li style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>FRENCH CLUB</b> will meet today during Contact Time in Ms. Chabot's room, H109.<br>\n" +
                "<br>\n" +
                "</font></li>\n" +
                "</ul>\n" +
                "<div align=\"left\" style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>THERE WILL </b><u><b>NOT</b></u><b> BE A RENAISSANCE MEETING</b> during Contact Time today, January 12. &nbsp;Stay tuned for locker sign and Taste of Pattonville information!</font></div>\n" +
                "<br>\n" +
                "<div align=\"left\" style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>MATH TUTORS ARE NEEDED</b> at Heights Middle School and Willow Brook Elementary after school. &nbsp;Come to the A+ office, E200, if you can help.</font></div>\n" +
                "<br>\n" +
                "<div align=\"left\" style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>COMMUNITY SERVICE OPPORTUNITIES: &nbsp;</b>Come by the CSO, E200, to sign up or for more info.</font></div>\n" +
                "<ul style=\"list-style-type:disc;list-style-position:outside;padding-left:36px;padding-right:0px;\">\n" +
                "<li style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>Volunteers are needed for a lot of upcoming events at the JCC.</b> &nbsp;The 1st event is Friday, January 20. &nbsp;</font></li>\n" +
                "<li style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>Volunteers are needed for the Rose Acres Carnival</b> on Saturday, January 28.</font></li>\n" +
                "<li style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>Volunteers are needed to help set up the Drummond Book Fair</b> on Thursday, February 9 at 1:30. <br>\n" +
                "<br>\n" +
                "</font></li>\n" +
                "</ul>\n" +
                "<div align=\"left\" style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>FOR ADDITIONAL ANNOUNCEMENTS, </b>go to the PHS website, phs.psdr3.org</font></div>\n" +
                "<br>\n" +
                "<div align=\"left\" style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>THE WORD OF THE WEEK IS KINETIC.</b> &nbsp;Here are synonyms for <b>kinetic</b>: &nbsp;<i>Active, animated, dynamic, energetic, and motion.</i></font></div>\n" +
                "<div align=\"left\" style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"> &nbsp;</font></div>\n" +
                "<div align=\"left\" style=\"text-align:left;\"><font face=\"Arial\" size=\"+0\" color=\"#000000\" style=\"font-family:Arial;font-size:10pt;color:#000000;\"><b>Remember the Pirate Code. &nbsp;Be Respectful. &nbsp;Be Responsible.</div>" + "</td>";

    }

    public NewsArticle(Parcel parcel) {

        String[] strings = parcel.createStringArray();
        title = strings[0];
        content = strings[1];
        publishDate = new Date(parcel.readLong());
    }

    public int getSourceColor() {
        return sourceColor;
    }

    public void setSourceColor(int sourceColor) {
        this.sourceColor = sourceColor;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        String[] strings = new String[]{title, content};

        parcel.writeStringArray(strings);
        parcel.writeLong(publishDate.getTime());
    }
}
