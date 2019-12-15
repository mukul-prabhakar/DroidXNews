package in.co.codewithmike.DroidXNews;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import in.co.codewithmike.DroidXNews.utils.Tools;

public class NewsDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView appbar_title, appbar_subtitle;
    private LinearLayout titleAppbar;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private String mUrl, mTitle, mSource;
    private ImageView closeDetailNews, shareNews, openBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Tools.setSystemBarColorInt(this, Color.parseColor("#FFFFFF"));

        initToolbar();

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        mTitle = intent.getStringExtra("title");

        appbar_title.setText(mTitle);
        appbar_subtitle.setText(mUrl);

        closeDetailNews.setOnClickListener(this);
        openBrowser.setOnClickListener(this);
        shareNews.setOnClickListener(this);

        initWebView(mUrl);

    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        titleAppbar = findViewById(R.id.title_appbar);
        appbar_title = findViewById(R.id.title_on_appbar);
        appbar_subtitle = findViewById(R.id.subtitle_on_appbar);
        appBarLayout = findViewById(R.id.appbar);
        closeDetailNews = findViewById(R.id.closeDetailNews);
        shareNews = findViewById(R.id.shareNews);
        openBrowser = findViewById(R.id.openBrowser);
    }


    private void initWebView(String url) {
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeDetailNews:
                finish();
                break;
            case R.id.shareNews:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plan");
                    i.putExtra(Intent.EXTRA_SUBJECT, mSource);
                    String body = mTitle + "\n" + mUrl + "\n" + "Share from the DroidXNews App" + "\n";
                    i.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(i, "Share with :"));

                } catch (Exception e) {
                    Toast.makeText(this, "Hmm.. Sorry, \nCannot be share", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.openBrowser:
                Intent ob = new Intent(Intent.ACTION_VIEW);
                ob.setData(Uri.parse(mUrl));
                startActivity(ob);
                break;
            default:
                Toast.makeText(this, "nothing found", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
