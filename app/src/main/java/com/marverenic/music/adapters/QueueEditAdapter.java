package com.marverenic.music.adapters;

import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.marverenic.music.PlayerController;
import com.marverenic.music.R;
import com.marverenic.music.activity.ArtistActivity;
import com.marverenic.music.activity.InstanceActivity;
import com.marverenic.music.activity.QueueActivity;
import com.marverenic.music.instances.Library;
import com.marverenic.music.instances.LibraryScanner;
import com.marverenic.music.instances.Playlist;
import com.marverenic.music.instances.Song;
import com.marverenic.music.utils.Debug;
import com.marverenic.music.utils.Navigate;
import com.marverenic.music.utils.Themes;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;

public class QueueEditAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, DragSortListView.DropListener{

    private ArrayList<Song> data;
    private QueueActivity activity;
    private ListView listView;

    public QueueEditAdapter(QueueActivity activity, DragSortListView listView) {
        super();
        this.data = new ArrayList<>(PlayerController.getQueue());
        this.activity = activity;
        this.listView = listView;

        DragSortController controller = new QueueEditAdapter.dragSortController(listView, this, R.id.handle);
        listView.setOnItemClickListener(this);
        listView.setAdapter(this);
        listView.setDropListener(this);
        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View v = convertView;
        if (convertView == null) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.instance_song_drag, parent, false);
            ((ImageView) v.findViewById(R.id.instanceMore)).setColorFilter(Themes.getDetailText());
        }
        final Song s = data.get(position);

        if (s != null) {
            TextView tt = (TextView) v.findViewById(R.id.textSongTitle);
            TextView tt1 = (TextView) v.findViewById(R.id.textSongDetail);
            if (tt != null) {
                tt.setText(s.songName);
                if (position == PlayerController.getPosition()){
                    if (Themes.isLight(activity)) tt.setTextColor(Themes.getPrimary());
                    else tt.setTextColor(Themes.getAccent());
                }
                else{
                    tt.setTextColor(Themes.getListText());
                }
            }
            if (tt1 != null) {
                tt1.setText(s.artistName + " - " + s.albumName);
                if (position == PlayerController.getPosition()){
                    if (Themes.isLight(activity)) tt1.setTextColor(Themes.getAccent());
                    else tt1.setTextColor(Themes.getPrimary());
                }
                else{
                    tt1.setTextColor(Themes.getDetailText());
                }
            }

            v.findViewById(R.id.instanceMore).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu menu = new PopupMenu(activity, v, Gravity.END);
                    String[] options = activity.getResources().getStringArray(R.array.edit_queue_options);
                    for (int i = 0; i < options.length;  i++) {
                        menu.getMenu().add(Menu.NONE, i, i, options[i]);
                    }
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case 0: //Go to artist
                                    Navigate.to(activity, ArtistActivity.class, ArtistActivity.ARTIST_EXTRA, LibraryScanner.findArtistById(s.artistId));
                                    return true;
                                case 1: //Go to album
                                    Navigate.to(activity, InstanceActivity.class, "entry", LibraryScanner.findAlbumById(s.albumId));
                                    return true;
                                case 2: //Add to playlist...
                                    ArrayList<Playlist> playlists = Library.getPlaylists();
                                    String[] playlistNames = new String[playlists.size()];

                                    for (int i = 0; i < playlists.size(); i++) {
                                        playlistNames[i] = playlists.get(i).toString();
                                    }

                                    new AlertDialog.Builder(activity).setTitle("Add \"" + s.songName + "\" to playlist")
                                            .setItems(playlistNames, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    LibraryScanner.addPlaylistEntry(activity, Library.getPlaylists().get(which), s);
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                    return true;
                                case 3: //Remove from queue
                                    new AlertDialog.Builder(activity).setTitle(s.songName)
                                            .setMessage("Remove this song from the queue?")
                                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    data.remove(position);
                                                    notifyDataSetChanged();

                                                    listView.invalidateViews();

                                                    if (PlayerController.getPosition() == position) {
                                                        // If the current song was removed
                                                        PlayerController.changeQueue(data, position);
                                                    } else if (PlayerController.getPosition() > position) {
                                                        // If a song that was before the current playing song was removed...
                                                        PlayerController.changeQueue(data, PlayerController.getPosition() - 1);
                                                    } else {
                                                        // If a song that was after the current playing song was removed...
                                                        PlayerController.changeQueue(data, PlayerController.getPosition());
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                    return true;
                            }
                            return false;
                        }
                    });
                    menu.show();
                }
            });
        } else {
            Debug.log(Debug.LogLevel.WTF, "QueueEditAdapter", "The requested entry is null", activity);
        }

        return v;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return data.size() == 0;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        } else return 0;
    }

    @Override
    public Object getItem(int position) {
        if (data != null) {
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        PlayerController.changeSong(position);
        Navigate.back(activity);
    }

    @Override
    public void drop(int from, int to) {
        Song song = data.remove(from);
        data.add(to, song);

        notifyDataSetChanged();
        listView.invalidateViews();

        if (PlayerController.getPosition() == from){
            // If the current song was moved in the queue
            PlayerController.changeQueue(data, to);
        }
        else if (PlayerController.getPosition() < from && PlayerController.getPosition() >= to){
            // If a song that was after the current playing song was moved to a position before the current song...
            PlayerController.changeQueue(data, PlayerController.getPosition() + 1);
        }
        else if (PlayerController.getPosition() > from && PlayerController.getPosition() <= to){
            // If a song that was before the current playing song was moved to a position after the current song...
            PlayerController.changeQueue(data, PlayerController.getPosition() - 1);
        }
        else{
            // If the number of songs before and after the currently playing song hasn't changed...
            PlayerController.changeQueue(data, PlayerController.getPosition());
        }
    }

    public static class dragSortController extends DragSortController {

        DragSortListView listView;
        BaseAdapter adapter;

        public dragSortController(DragSortListView listView, BaseAdapter adapter, int handleId) {
            super(listView, handleId, ON_DOWN, FLING_REMOVE);
            this.listView = listView;
            this.adapter = adapter;
            setBackgroundColor(Themes.getBackgroundElevated());
        }

        @Override
        public View onCreateFloatView(int position) {
            return super.onCreateFloatView(position);
            /*View v = listView.getChildAt(position + listView.getHeaderViewsCount() - listView.getFirstVisiblePosition());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) v.setElevation(8f);
            return v;*/
        }

        @Override
        public void onDestroyFloatView(View floatView) {
            //do nothing; block super from crashing
        }

        @Override
        public int startDragPosition(MotionEvent ev) {
            return super.startDragPosition(ev);
        }
    }
}
