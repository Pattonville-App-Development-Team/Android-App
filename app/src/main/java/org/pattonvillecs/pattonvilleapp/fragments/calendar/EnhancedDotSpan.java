package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

/**
 * Created by skaggsm on 12/29/16.
 *
 * @see com.prolificinteractive.materialcalendarview.spans.DotSpan
 */

public class EnhancedDotSpan implements LineBackgroundSpan {

    private final float radius;
    private final float xOffset;
    private final int color;

    public EnhancedDotSpan(float radius, float xOffset, int color) {
        this.radius = radius;
        this.xOffset = xOffset;
        this.color = color;
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

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence charSequence, int start, int end, int lineNum) {
        int oldColor = paint.getColor();
        if (color != 0) {
            paint.setColor(color);
        }
        canvas.drawCircle((left + right) / 2 + xOffset, bottom + radius, radius, paint);
        paint.setColor(oldColor);
    }
}