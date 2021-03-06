package com.marverenic.music.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.marverenic.music.PlayerController;
import com.marverenic.music.R;
import com.marverenic.music.adapters.QueueEditAdapter;
import com.marverenic.music.instances.LibraryScanner;
import com.marverenic.music.utils.Themes;
import com.mobeta.android.dslv.DragSortListView;

public class QueueActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentLayout(R.layout.page_editable_list);
        setContentView(R.id.list);
        super.onCreate(savedInstanceState);

        // The adapter will initialize attach itself and all necessary controllers in its constructor
        // There is no need to create a variable for it
        new QueueEditAdapter(this, (DragSortListView) findViewById(R.id.list));
    }

    public void update (){
        updateMiniplayer();
        new QueueEditAdapter(this, (DragSortListView) findViewById(R.id.list));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.queue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                final AppCompatEditText input = new AppCompatEditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Playlist name");

                final Context context = this;

                new AlertDialog.Builder(this)
                        .setTitle("Save queue as playlist")
                        .setView(input)
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LibraryScanner.createPlaylist(context, input.getText().toString(), PlayerController.getQueue());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();

                int padding = (int) getResources().getDimension(R.dimen.alert_padding);
                ((View) input.getParent()).setPadding(
                        padding - input.getPaddingLeft(),
                        padding,
                        padding - input.getPaddingRight(),
                        input.getPaddingBottom());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void themeActivity() {
        super.themeActivity();
        findViewById(R.id.list).setBackgroundColor(Themes.getBackgroundElevated());
    }

    @Override
    public void updateMiniplayer(){
        if (getResources().getConfiguration().smallestScreenWidthDp >= 700){
            // The tablet layout here uses a FrameLayout instead of a Relative layout
            FrameLayout.LayoutParams contentLayoutParams = (FrameLayout.LayoutParams) (findViewById(R.id.list)).getLayoutParams();
            contentLayoutParams.bottomMargin = 0;
            findViewById(R.id.list).setLayoutParams(contentLayoutParams);
        }
        else {
            RelativeLayout.LayoutParams contentLayoutParams = (RelativeLayout.LayoutParams) (findViewById(R.id.list)).getLayoutParams();
            contentLayoutParams.bottomMargin = 0;
            findViewById(R.id.list).setLayoutParams(contentLayoutParams);
        }

        FrameLayout.LayoutParams playerLayoutParams = (FrameLayout.LayoutParams) (findViewById(R.id.miniplayer)).getLayoutParams();
        playerLayoutParams.height = 0;
        findViewById(R.id.miniplayer).setLayoutParams(playerLayoutParams);
        findViewById(R.id.miniplayer).setVisibility(View.GONE);
    }
}
