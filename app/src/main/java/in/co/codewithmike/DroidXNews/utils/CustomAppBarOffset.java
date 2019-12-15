package in.co.codewithmike.DroidXNews.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

public class CustomAppBarOffset implements AppBarLayout.OnOffsetChangedListener {
    private TextView textView, textView1;
    private Toolbar toolbar;
    private String section;
    private ImageView imageView;
    private Activity activity;
    private int orientation;
    private TextView beta;

    public CustomAppBarOffset(Activity activity, TextView textView, TextView textView1,
                              Toolbar toolbar, String section, ImageView imageView, TextView beta) {
        this.textView = textView;
        this.textView1 = textView1;
        this.toolbar = toolbar;
        this.section = section;
        this.imageView = imageView;
        this.beta = beta;
        this.activity = activity;
    }

    public CustomAppBarOffset(Activity activity, TextView textView, TextView textView1,
                              Toolbar toolbar, String section, ImageView imageView) {
        this.textView = textView;
        this.textView1 = textView1;
        this.toolbar = toolbar;
        this.section = section;
        this.imageView = imageView;
        this.activity = activity;
        beta = null;
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        orientation = activity.getResources().getConfiguration().orientation;
        if (i < 0) {
            toolbar.setElevation(5.0f);
        } else {
            toolbar.setElevation(0.0f);
        }
        textView1.setText(section);

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            float fraction = ((float) Math.abs(i) / appBarLayout.getTotalScrollRange());
            int range = imageView.getBottom();
            fraction = Math.max(fraction, 0f);
            // float delay = 0.15f;
            //float speed = 1f;
            imageView.setTranslationY(fraction * range * -2);
            if (beta != null) {
                beta.setTranslationY(fraction * range * -2);
            }
        }

        int lengths = Math.abs(i) - appBarLayout.getTotalScrollRange();
        if (lengths >= -100 && lengths <= 0) {
            textView.setText(section);
        } else {
            textView.setText(null);
        }
    }
}
