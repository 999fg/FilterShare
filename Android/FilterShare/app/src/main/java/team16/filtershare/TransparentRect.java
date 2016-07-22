package team16.filtershare;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by harrykim on 2016. 7. 11..
 */
public class TransparentRect extends View {
    private int preview_height;
    private int preview_width;

    public TransparentRect(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScreenSize(int w, int h){
        preview_height = h;
        preview_width = w;
    }



    private void setDrawable(int resid) {
        this.setBackgroundResource(resid);
    }



    public void clear() {
        setBackgroundDrawable(null);
    }

    public void setUpper(){

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();
        params.height = (preview_height - preview_width)/2;
        params.topMargin=0;
        this.setLayoutParams(params);
        setDrawable(R.drawable.transparent_rect);

    }
    public void setLower(){

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();
        params.height = (preview_height - preview_width)/2;
        params.topMargin = preview_height - (preview_height - preview_width)/2;
        Log.d("rect preview wh", "w: "+preview_width+" h "+preview_height);
        Log.d("height&tMargin", "h: "+params.height+" t: "+params.topMargin);
        this.setLayoutParams(params);
        setDrawable(R.drawable.transparent_rect);

    }

}