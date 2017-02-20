package cn.qiditu.guet.android.webget;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class UrlInputActivity extends AppCompatActivity {

    @BindView(R.id.url_input)
    EditText etUrlInput;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_input);
        ButterKnife.bind(this);

        Intent intent = this.getIntent();
        final int defaultHeight = this.getResources()
                                        .getDimensionPixelOffset(
                                                R.dimen.default_actionbar_size);
        etUrlInput.setHeight(intent.getIntExtra(MainActivity.intentToolBarHeight,
                                                defaultHeight));
        etUrlInput.setText(intent.getStringExtra(MainActivity.intentURL));
    }

    public static final int resultCodeResultNotWithURL = 1;
    public static final int resultCodeResultWithURL = 2;
    public static final String intentURL = "CurrentURL";

    @Override
    @OnClick({R.id.btn_return_input, R.id.layout_bottom})
    public void onBackPressed() {
        this.setResult(resultCodeResultNotWithURL);
        super.onBackPressed();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.copy_url)
    void copyURL() {
        ClipboardManager cm =(ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("URL", etUrlInput.getText().toString()));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.paste_goto)
    void pasteAndGoTo() {
        ClipboardManager cm =(ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);
        String url = "";
        if(cm.hasPrimaryClip()) {
            ClipData clipData = cm.getPrimaryClip();
            url = clipData.getItemAt(0).coerceToText(this).toString();
        }
        etUrlInput.setText(url);
        returnWithURL();
    }

    @SuppressWarnings("unused")
    @OnEditorAction(R.id.url_input)
    boolean onUrlInputAction() {
        returnWithURL();
        return true;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_enter)
    void returnWithURL() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(intentURL, etUrlInput.getText().toString());
        this.setResult(resultCodeResultWithURL, intent);
        super.onBackPressed();
    }

}
