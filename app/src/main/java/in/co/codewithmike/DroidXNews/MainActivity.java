package in.co.codewithmike.DroidXNews;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.co.codewithmike.DroidXNews.activity.CategoryNews;
import in.co.codewithmike.DroidXNews.adapter.Adapter;
import in.co.codewithmike.DroidXNews.api.ApiClient;
import in.co.codewithmike.DroidXNews.api.ApiInterface;
import in.co.codewithmike.DroidXNews.model.Article;
import in.co.codewithmike.DroidXNews.model.News;
import in.co.codewithmike.DroidXNews.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    public static final String API_KEY = "0941f2672e0e4d5e9fd6dc7ef68c4dd2";

    private Toolbar mToolbar;
    private AppBarLayout appBarLayout;
    private TextView textHead1, textHead2;
    private NestedScrollView nestedScrollView;
    private FloatingActionButton tabHealth, tabScience, tabSports, tabBusiness, tabEntertainment;
    private String selectedNewsCategory = "";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private Adapter adapter;
    private TextView topHeadline;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout errorLayout;
    private ImageView errorImage;
    private TextView errorTitle, errorMessage;
    private Button btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tools.setSystemBarColorInt(this, Color.parseColor("#FFFFFF"));

        initComponents();
        mappingOfToolbar();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        topHeadline = findViewById(R.id.topheadelines);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        onLoadingSwipeRefresh("");

        errorLayout = findViewById(R.id.errorLayout);
        errorImage = findViewById(R.id.errorImage);
        errorTitle = findViewById(R.id.errorTitle);
        errorMessage = findViewById(R.id.errorMessage);
        btnRetry = findViewById(R.id.btnRetry);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    appBarLayout.setElevation(20.0f);

                } else if (scrollX == scrollY) {
                    appBarLayout.setElevation(0.0f);

                }
            }
        });

        tabHealth.setOnClickListener(this);
        tabScience.setOnClickListener(this);
        tabSports.setOnClickListener(this);
        tabBusiness.setOnClickListener(this);
        tabEntertainment.setOnClickListener(this);

    }

    private void initComponents() {
        textHead1 = findViewById(R.id.textHead1);
        textHead2 = findViewById(R.id.textHead2);
        nestedScrollView = findViewById(R.id.nsv1);
        tabHealth = findViewById(R.id.tabHealth);
        tabScience = findViewById(R.id.tabScience);
        tabSports = findViewById(R.id.tabSports);
        tabBusiness = findViewById(R.id.tabBusiness);
        tabEntertainment = findViewById(R.id.tabEntertainment);
    }

    private void mappingOfToolbar() {
        mToolbar = findViewById(R.id.headToolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        appBarLayout = findViewById(R.id.appbar1);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_center);
        animation.setInterpolator(new AnticipateOvershootInterpolator());
        appBarLayout.setElevation(0.0f);
        animation.setDuration(1000);
        textHead1.startAnimation(animation);
        textHead2.startAnimation(animation);
    }

    public void LoadJson(final String keyword) {

        errorLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        String country = "in";
        String language = "en";

        Call<News> call;

        if (keyword.length() > 0) {
            call = apiInterface.getNewsSearch(keyword, language, "publishedAt", API_KEY);
        } else {
            call = apiInterface.getNews(country, API_KEY);
        }

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticles() != null) {

                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    articles = response.body().getArticles();
                    adapter = new Adapter(articles, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    initListener();

                    topHeadline.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);


                } else {

                    topHeadline.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);

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
                topHeadline.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                showErrorMessage(
                        R.drawable.oops,
                        "Oops..",
                        "Network failure, Please Try Again\n" +
                                t.toString());
            }
        });

    }


    private void initListener() {

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageView imageView = view.findViewById(R.id.img);
                Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);

                Article article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());

                startActivity(intent);
             /*   Pair<View, String> pair = Pair.create((View) imageView, ViewCompat.getTransitionName(imageView));
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this,
                        pair
                );


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(intent, optionsCompat.toBundle());
                } else {
                    startActivity(intent);
                }*/

            }
        });

    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    onLoadingSwipeRefresh(query);
                } else {
                    Toast.makeText(MainActivity.this, "Type more than two letters!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchMenuItem.getIcon().setVisible(false, false);

        return true;
    }*/

    @Override
    public void onRefresh() {
        LoadJson("");
    }

    private void onLoadingSwipeRefresh(final String keyword) {

        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        LoadJson(keyword);
                    }
                }
        );

    }

    private void showErrorMessage(int imageView, String title, String message) {

        if (errorLayout.getVisibility() == View.GONE) {
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
        });

    }

    @Override
    public void onClick(View v) {
        Intent catIntent;

        switch (v.getId()) {
            case R.id.tabHealth:
                selectedNewsCategory = "health";
                catIntent = new Intent(MainActivity.this, CategoryNews.class);
                catIntent.putExtra("selCategoryNews", selectedNewsCategory);
                startActivity(catIntent);
                break;
            case R.id.tabScience:
                selectedNewsCategory = "science";
                catIntent = new Intent(MainActivity.this, CategoryNews.class);
                catIntent.putExtra("selCategoryNews", selectedNewsCategory);
                startActivity(catIntent);
                break;
            case R.id.tabSports:
                selectedNewsCategory = "sports";
                catIntent = new Intent(MainActivity.this, CategoryNews.class);
                catIntent.putExtra("selCategoryNews", selectedNewsCategory);
                startActivity(catIntent);
                break;
            case R.id.tabBusiness:
                selectedNewsCategory = "business";
                catIntent = new Intent(MainActivity.this, CategoryNews.class);
                catIntent.putExtra("selCategoryNews", selectedNewsCategory);
                startActivity(catIntent);
                break;
            case R.id.tabEntertainment:
                selectedNewsCategory = "entertainment";
                catIntent = new Intent(MainActivity.this, CategoryNews.class);
                catIntent.putExtra("selCategoryNews", selectedNewsCategory);
                startActivity(catIntent);
                break;
        }
    }
}
