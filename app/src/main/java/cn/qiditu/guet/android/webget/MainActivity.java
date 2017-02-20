package cn.qiditu.guet.android.webget;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qiditu.signalslot.slots.Slot1;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.content)
    TextView tvContent;
    @BindView(R.id.card_view)
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.setSupportActionBar(toolbar);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.this.refresh();
            }
        });
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        WebGetTask webGetTask = new WebGetTask();
        webGetTask.taskFinished.connect(getFinished, 1);
        try {
            URL url = new URL(currentURL);
            webGetTask.execute(url);
        }
        catch (MalformedURLException e) {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(coordinatorLayout,
                            this.getString(R.string.url_invalid),
                            Snackbar.LENGTH_LONG)
                    .show();
            Log.e("MalformedURLException", currentURL + "\t" + e.getMessage());
        }
    }

//    private void updateText(@NonNull final Queue<String> queue) {
//        if(!queue.isEmpty()) {
//            tvContent.append(queue.poll());
//            if(!queue.isEmpty()) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        updateText(queue);
//                    }
//                }, 500);
//            }
//        }
//    }

    private Slot1<WebGetTask.Result> getFinished = new Slot1<WebGetTask.Result>() {
        @Override
        public void accept(@Nullable WebGetTask.Result result) {
            if(result == null) {
                return;
            }
            swipeRefreshLayout.setRefreshing(false);
            if(result.isError) {
                Snackbar.make(coordinatorLayout, result.error, Snackbar.LENGTH_LONG)
                        .show();
            }
            else {
                if(cardView.getVisibility() != View.VISIBLE) {
                    cardView.setVisibility(View.VISIBLE);
                }
                tvContent.setText(result.result);
//                updateText(result.result);
            }
        }
    };

    private String currentURL = "";
    private static final int requestCodeStartUrlInputActivity = 1;
    public static final String intentToolBarHeight = "ToolBarHeight";
    public static final String intentURL = "CurrentURL";

    @SuppressWarnings("unused")
    @OnClick(R.id.toolbar)
    void onToolTipClicked() {
        Intent intent = new Intent(this, UrlInputActivity.class);
        intent.putExtra(intentToolBarHeight, toolbar.getHeight());
        intent.putExtra(intentURL, currentURL);
        this.startActivityForResult(intent, requestCodeStartUrlInputActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case requestCodeStartUrlInputActivity : {
                switch (resultCode) {
                    case UrlInputActivity.resultCodeResultNotWithURL: {
                    } break;
                    case UrlInputActivity.resultCodeResultWithURL: {
                        if(data.hasExtra(UrlInputActivity.intentURL)) {
                            currentURL = data.getStringExtra(UrlInputActivity.intentURL);
                            refresh();
                        }
                    } break;
                }
            } break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_item_refresh: {
                refresh();
            } break;
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

}
