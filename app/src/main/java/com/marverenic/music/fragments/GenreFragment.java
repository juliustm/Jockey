package com.marverenic.music.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.marverenic.music.R;
import com.marverenic.music.adapters.GenreListAdapter;
import com.marverenic.music.adapters.SearchPagerAdapter;
import com.marverenic.music.instances.Genre;
import com.marverenic.music.utils.Themes;

import java.util.ArrayList;

public class GenreFragment extends Fragment {

    private ArrayList<Genre> genreLibrary;
    private GenreListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getParcelableArrayList(SearchPagerAdapter.DATA_KEY) != null){
            genreLibrary = new ArrayList<>();
            for (Parcelable p : getArguments().getParcelableArrayList(SearchPagerAdapter.DATA_KEY)){
                genreLibrary.add((Genre) p);
            }
        }

        if (genreLibrary == null) {
            adapter = new GenreListAdapter(getActivity());
        } else {
            adapter = new GenreListAdapter(genreLibrary, getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ListView genreListView = (ListView) view.findViewById(R.id.list);

        int paddingTop = (int) getActivity().getResources().getDimension(R.dimen.list_margin);
        int paddingH =(int) getActivity().getResources().getDimension(R.dimen.global_padding);
        view.setPadding(paddingH, paddingTop, paddingH, 0);

        genreListView.setAdapter(adapter);
        genreListView.setOnItemClickListener(adapter);
        genreListView.setBackgroundColor(Themes.getBackgroundElevated());

        return view;
    }

    public void updateData(ArrayList<Genre> genreLibrary) {
        this.genreLibrary = genreLibrary;
        adapter.updateData(genreLibrary);
    }
}
