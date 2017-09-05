package in.tomtontech.markaz.Personal;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 *
 * Created by FATHIMA on 8/25/2017.
 */
public class SquareImageView extends android.support.v7.widget.AppCompatImageView {
    public SquareImageView(Context context)
    {
        super(context);
    }
    public SquareImageView(Context context, AttributeSet attributeSet)
    {
        super(context,attributeSet);
    }
    public SquareImageView(Context context,AttributeSet attributeSet,int defStyle)
    {
        super(context,attributeSet,defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(),getMeasuredWidth());
    }
}
