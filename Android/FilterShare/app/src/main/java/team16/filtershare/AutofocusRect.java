package team16.filtershare;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by harrykim on 2016. 7. 11..
 */
public class AutofocusRect extends View {
    private float center_x;
    private float center_y;

    private float parent_width;
    private float parent_height;

    public AutofocusRect(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLocation(float x, float y){
        center_x = x;
        center_y = y;

    }

    public void setParentInfo(float width, float height){
        parent_width=width;
        parent_height=height;
    }

    private void setDrawable(int resid) {
        this.setBackgroundResource(resid);
    }

    public void showStart(){
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();
        params.leftMargin = (int) (center_x-params.width/2);
        params.topMargin = (int) (center_y-params.height/2);
        this.setLayoutParams(params);
        setDrawable(R.drawable.back);
    }

    public void clear() {
        setBackgroundDrawable(null);
    }

}