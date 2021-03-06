package com.marverenic.music.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.marverenic.music.R;
import com.marverenic.music.adapters.ArtistListAdapter;
import com.marverenic.music.adapters.SearchPagerAdapter;
import com.marverenic.music.instances.Artist;
import com.marverenic.music.utils.Themes;

import java.util.ArrayList;

public class ArtistFragment extends Fragment {

    private ArrayList<Artist> artistLibrary;
    private ArtistListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getParcelableArrayList(SearchPagerAdapter.DATA_KEY) != null){
            artistLibrary = new ArrayList<>();
            for (Parcelable p : getArguments().getParcelableArrayList(SearchPagerAdapter.DATA_KEY)){
                artistLibrary.add((Artist) p);
            }
        }

        if (artistLibrary == null) {
            adapter = new ArtistListAdapter(getActivity());
        } else {
            adapter = new ArtistListAdapter(artistLibrary, getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ListView artistListView = (ListView) view.findViewById(R.id.list);

        int paddingTop = (int) getActivity().getResources().getDimension(R.dimen.list_margin);
        int paddingH =(int) getActivity().getResources().getDimension(R.dimen.global_padding);
        view.setPadding(paddingH, paddingTop, paddingH, 0);

        artistListView.setAdapter(adapter);
        artistListView.setOnItemClickListener(adapter);
        artistListView.setBackgroundColor(Themes.getBackgroundElevated());

        return view;
    }

    public void updateData(ArrayList<Artist> artistLibrary) {
        this.artistLibrary = artistLibrary;
        adapter.updateData(artistLibrary);
    }
}
