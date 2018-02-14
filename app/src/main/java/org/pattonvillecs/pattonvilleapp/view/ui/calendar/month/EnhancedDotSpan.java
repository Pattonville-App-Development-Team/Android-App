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

package org.pattonvillecs.pattonvilleapp.view.ui.calendar.month;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.style.LineBackgroundSpan;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kotlin.jvm.functions.Function1;

/**
 * This {@link android.text.style.ParagraphStyle} displays a single colored dot at a given position with a given radius.
 * <p>
 * There are various utility methods to provide multiple numbers of dots in a row.
 *
 * @author Mitchell Skaggs
 * @see com.prolificinteractive.materialcalendarview.spans.DotSpan
 * @since 1.0.0
 */

public class EnhancedDotSpan implements LineBackgroundSpan {
    private final int plusColor = Color.parseColor("lightgray");
    private final float radius;
    private final float xOffset;
    private final int color;
    private final boolean hasPlus;

    public EnhancedDotSpan(float radius, float xOffset, int color) {
        this(radius, xOffset, color, false);
    }

    public EnhancedDotSpan(float radius, float xOffset, int color, boolean hasPlus) {
        this.radius = radius;
        this.xOffset = xOffset;
        this.color = color;
        this.hasPlus = hasPlus;
    }

    public static List<EnhancedDotSpan> createSingle(float radius, int color1) {
        return Collections.singletonList(new EnhancedDotSpan(radius, 0, color1));
    }

    public static List<EnhancedDotSpan> createDouble(float radius, int color1, int color2) {
        return Arrays.asList(new EnhancedDotSpan(radius, -radius * 1.1f, color1),
                new EnhancedDotSpan(radius, radius * 1.1f, color2));
    }

    public static List<EnhancedDotSpan> createTriple(float radius, int color1, int color2, int color3) {
        return Arrays.asList(new EnhancedDotSpan(radius, -2 * radius * 1.1f, color1),
                new EnhancedDotSpan(radius, 0, color2),
                new EnhancedDotSpan(radius, 2 * radius * 1.1f, color3));
    }

    public static List<EnhancedDotSpan> createTripleWithPlus(float radius, int color1, int color2, int color3) {
        return Arrays.asList(new EnhancedDotSpan(radius, -2 * radius * 1.1f, color1),
                new EnhancedDotSpan(radius, 0, color2),
                new EnhancedDotSpan(radius, 2 * radius * 1.1f, color3, true));
    }

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence charSequence, int start, int end, int lineNum) {
        int oldColor = paint.getColor();

        if (color != 0) {
            paint.setColor(color);
        }
        float xCenter = (left + right) / 2f;
        float circleYCenter = bottom + radius;
        canvas.drawCircle(xCenter + xOffset, circleYCenter, radius, paint);

        if (hasPlus) {
            paint.setColor(plusColor);
            float width = radius * 0.15f;
            float height = radius * 0.65f;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                float round = radius * 1.5f;
                canvas.drawRoundRect(xCenter - width + xOffset, circleYCenter + height, xCenter + width + xOffset, circleYCenter - height, round, round, paint);
                canvas.drawRoundRect(xCenter - height + xOffset, circleYCenter + width, xCenter + height + xOffset, circleYCenter - width, round, round, paint);
            } else {
                canvas.drawRect(xCenter - width + xOffset, circleYCenter + height, xCenter + width + xOffset, circleYCenter - height, paint);
                canvas.drawRect(xCenter - height + xOffset, circleYCenter + width, xCenter + height + xOffset, circleYCenter - width, paint);
            }
        }

        paint.setColor(oldColor);
    }

    public enum DotType {
        SINGLE(integer -> integer == 1),
        DOUBLE(integer -> integer == 2),
        TRIPLE(integer -> integer == 3),
        TRIPLE_PLUS(integer -> integer > 3);

        @SuppressWarnings("ImmutableEnumChecker")
        public final Function1<Integer, Boolean> defaultTestFunction;

        DotType(Function1<Integer, Boolean> defaultTestFunction) {
            this.defaultTestFunction = defaultTestFunction;
        }
    }
}