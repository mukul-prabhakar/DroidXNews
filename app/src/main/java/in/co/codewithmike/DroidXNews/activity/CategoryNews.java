package in.co.codewithmike.DroidXNews.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import in.co.codewithmike.DroidXNews.NewsDetailActivity;
import in.co.codewithmike.DroidXNews.R;
import in.co.codewithmike.DroidXNews.adapter.CategoryListAdapter;
import in.co.codewithmike.DroidXNews.api.ApiClient;
import in.co.codewithmike.DroidXNews.api.ApiInterface;
import in.co.codewithmike.DroidXNews.model.Article;
import in.co.codewithmike.DroidXNews.model.News;
import in.co.codewithmike.DroidXNews.utils.CustomAppBarOffset;
import in.co.codewithmike.DroidXNews.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryNews extends AppCompatActivity {

    public static final String API_KEY = "0941f2672e0e4d5e9fd6dc7ef68c4dd2";

    private Toolbar mToolbar;
    private AppBarLayout appBarLayout;
    private ImageView categoryImage;
    private RecyclerView mRecyclerView;
    private CategoryListAdapter categoryListAdapter;
    private TextView defaultToolbarText, defaultPrimaryText;
    private List<Article> articles = new ArrayList<>();
    private String selCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_news);
        Tools.setSystemBarColorInt(this, Color.parseColor("#FFFFFF"));

        mappingOfToolbar();
        initComponents();

        Intent intent = getIntent();
        selCat = intent.getStringExtra("selCategoryNews");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date date = null;//You will get date object relative to server/client timezone wherever it is parsed
        try {
            date = dateFormat.parse("2017-04-26T20:55:00.000Z");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat formatter = new SimpleDateFormat("dd MMM"); //If you need time just put specific format for time like 'HH:mm:ss'
        String dateStr = formatter.format(date);
        Log.d("gggggg", "onCreate: " + dateStr);

        assert selCat != null;
        switch (selCat) {
            case "health":
                categoryImage.setImageResource(R.drawable.ic_category_health);
                categoryImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.purple_800)));
                categoryImage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.purple_100)));

                appBarLayout.addOnOffsetChangedListener(new CustomAppBarOffset(this, defaultToolbarText,
                        defaultPrimaryText, mToolbar, "Health News", categoryImage));
                break;
            case "science":
                categoryImage.setImageResource(R.drawable.ic_category_science);
                categoryImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_800)));
                categoryImage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_100)));

                appBarLayout.addOnOffsetChangedListener(new CustomAppBarOffset(this, defaultToolbarText,
                        defaultPrimaryText, mToolbar, "Science News", categoryImage));
                break;
            case "sports":
                categoryImage.setImageResource(R.drawable.ic_category_sports);
                categoryImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.orange_800)));
                categoryImage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.orange_100)));

                appBarLayout.addOnOffsetChangedListener(new CustomAppBarOffset(this, defaultToolbarText,
                        defaultPrimaryText, mToolbar, "Sports News", categoryImage));
                break;
            case "business":
                categoryImage.setImageResource(R.drawable.ic_category_business);
                categoryImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.lime_800)));
                categoryImage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.lime_100)));

                appBarLayout.addOnOffsetChangedListener(new CustomAppBarOffset(this, defaultToolbarText,
                        defaultPrimaryText, mToolbar, "Business News", categoryImage));
                break;
            case "entertainment":
                categoryImage.setImageResource(R.drawable.ic_entertainment);
                categoryImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.teal_800)));
                categoryImage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.teal_100)));

                appBarLayout.addOnOffsetChangedListener(new CustomAppBarOffset(this, defaultToolbarText,
                        defaultPrimaryText, mToolbar, "Entertainment News", categoryImage));
                break;
        }

        getWindow().setSharedElementEnterTransition(TransitionInflater
                .from(this)
                .inflateTransition(R.transition.shared_transition_dir));
        /*categoryImage.setTransitionName("slide");*/

        LoadCategoryJson();
    }

    private void initComponents() {
        categoryImage = findViewById(R.id.categoryImage);
        appBarLayout = findViewById(R.id.appbar4);
        defaultToolbarText = findViewById(R.id.defaultToolbarText);
        defaultPrimaryText = findViewById(R.id.defaultPrimaryText);
        mRecyclerView = findViewById(R.id.rv13);
    }

    private void mappingOfToolbar() {
        mToolbar = findViewById(R.id.authentication_main);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void LoadCategoryJson() {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        String country = "in";

        Call<News> call;
        call = apiInterface.getCategoryNews(country, selCat, API_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticles() != null) {

                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    articles = response.body().getArticles();
                    Log.d("CategoryNews.java", "onResponse: " + articles);
                    categoryListAdapter = new CategoryListAdapter(articles, CategoryNews.this);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(CategoryNews.this));
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setAdapter(categoryListAdapter);
                    categoryListAdapter.notifyDataSetChanged();

                    initListener();
                    // swipeRefreshLayout.setRefreshing(false);

                } else {

                    // swipeRefreshLayout.setRefreshing(false);

                    String errorCode;
                    switch (response.code()) {
                        case 404:
                            errorCode = "404 not found";
                            break;
                        case 500:
                            errorCode = "500 server broken";
                            break;
                        default:
                            errorCode = "unknown error";
                            break;
                    }

                    showErrorMessage(
                            R.drawable.no_result,
                            "No Result",
                            "Please Try Again!\n" +
                                    errorCode);

                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

                //swipeRefreshLayout.setRefreshing(false);
                showErrorMessage(
                        R.drawable.oops,
                        "Oops..",
                        "Network failure, Please Try Again\n" +
                                t.toString());
            }
        });
    }

    private void showErrorMessage(int imageView, String title, String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

/*        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
        }

        errorImage.setImageResource(imageView);
        errorTitle.setText(title);
        errorMessage.setText(message);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadingSwipeRefresh("");
            }
        });*/

    }

    private void initListener() {
        categoryListAdapter.setOnItemClickListener(new CategoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(CategoryNews.this, NewsDetailActivity.class);

                Article article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());

                startActivity(intent);

            }
        });

    }

}
