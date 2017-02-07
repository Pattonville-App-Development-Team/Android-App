package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.style.LineBackgroundSpan;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

/**
 * Created by Mitchell Skaggs on 12/29/16.
 *
 * @see com.prolificinteractive.materialcalendarview.spans.DotSpan
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

    public static EnhancedDotSpan createSingle(float radius, int color1) {
        return new EnhancedDotSpan(radius, 0, color1);
    }

    public static Pair<EnhancedDotSpan, EnhancedDotSpan> createPair(float radius, int color1, int color2) {
        return new ImmutablePair<>(new EnhancedDotSpan(radius, -radius * 1.1f, color1), new EnhancedDotSpan(radius, radius * 1.1f, color2));
    }

    public static Triple<EnhancedDotSpan, EnhancedDotSpan, EnhancedDotSpan> createTriple(float radius, int color1, int color2, int color3) {
        return new ImmutableTriple<>(new EnhancedDotSpan(radius, -2 * radius * 1.1f, color1), new EnhancedDotSpan(radius, 0, color2), new EnhancedDotSpan(radius, 2 * radius * 1.1f, color3));
    }

    public static Triple<EnhancedDotSpan, EnhancedDotSpan, EnhancedDotSpan> createTripleWithPlus(float radius, int color1, int color2, int color3) {
        return new ImmutableTriple<>(new EnhancedDotSpan(radius, -2 * radius * 1.1f, color1), new EnhancedDotSpan(radius, 0, color2), new EnhancedDotSpan(radius, 2 * radius * 1.1f, color3, true));
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
}