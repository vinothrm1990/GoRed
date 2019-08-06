package shadowws.in.gored.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import shadowws.in.gored.R;

public class CountDrawable extends Drawable {

    private Paint mBadgePaint;
    private Paint mTextPaint;
    private Rect mTxtRect = new Rect();
    private String mCount = "";
    private boolean mWillDraw;

    public CountDrawable(Context context) {

        float mTextSize = context.getResources().getDimension(R.dimen.badge_count_textsize);
        mBadgePaint = new Paint();
        mBadgePaint.setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.transparent_white));
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.RED);
        mTextPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "big_john_bold.otf"));
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {

        if (!mWillDraw) {
            return;
        }
        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;

        // Position the badge in the top-right quadrant of the icon.

        /*Using Math.max rather than Math.min */

        float radius = ((Math.max(width, height) / 2)) / 2;
        float centerX = (width - radius - 1) +5;
        float centerY = radius -5;
        if(mCount.length() <= 3){
            // Draw badge circle.
            canvas.drawCircle(centerX, centerY, (int)(radius+5.5), mBadgePaint);
        }
        else{
            canvas.drawCircle(centerX, centerY, (int)(radius+6.5), mBadgePaint);
        }
        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
        float textHeight = mTxtRect.bottom - mTxtRect.top;
        float textY = centerY + (textHeight / 2f);
        if(mCount.length() > 3)
            canvas.drawText("10+", centerX, textY, mTextPaint);
        else
            canvas.drawText(mCount, centerX, textY, mTextPaint);
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    public void setCount(String count) {
        mCount = count;

        // Only draw a badge if there are notifications.
        mWillDraw = !count.equalsIgnoreCase("0");
        invalidateSelf();
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}