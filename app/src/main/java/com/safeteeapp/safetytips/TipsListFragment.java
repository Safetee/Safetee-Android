package com.safeteeapp.safetytips;


import android.app.ListFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.safeteeapp.safetee.R;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TipsListFragment extends ListFragment implements OnDismissCallback {
    private TipsAdapter mAdapter;
    private MediaPlayer mPlayer;
    private static final SimpleDateFormat mDateAddedFormatter = new SimpleDateFormat("MMMM d, yyyy - hh:mm a", Locale.getDefault());


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new TipsAdapter(getActivity());
        SwipeDismissAdapter adapter = new SwipeDismissAdapter(mAdapter, this);
        setListAdapter(adapter);
        getListView().setBackgroundColor(getResources().getColor(R.color.card_gray));
        adapter.setAbsListView(getListView());

        setEmptyText("No Tips");

        getListView().setDividerHeight(0);
        getListView().setDivider(null);
        getListView().setSelector(new ColorDrawable(android.R.color.transparent));
        getListView().setHeaderDividersEnabled(true);
        getListView().setPadding(getListView().getPaddingLeft(),
                getListView().getPaddingTop() + 10, getListView().getPaddingRight(),
                getListView().getPaddingBottom() + 10);
        getListView().setClipToPadding(false);
    }




    @Override
    public void onListItemClick(android.widget.ListView l, View v, int position, long id) {
        TipItem item = mAdapter.getItem(position);
        gotoTipView(v, item);
    }

    private void gotoTipView(final View v, final TipItem item){
        Intent i = new Intent(v.getContext(), TipView.class);
        i.putExtra("title", item.getName());
        i.putExtra("body", item.getBody());
        i.putExtra("uniid", item.getUniqueid());
        i.putExtra("Tid", String.valueOf(item.getId()));
        i.putExtra("date", getTime(item.getTime()));
        i.putExtra("sender", item.getBy());
        startActivity(i);
    }

    public static String getTime(long milliSeconds) {
        Date date = new Date(milliSeconds);
        return mDateAddedFormatter.format(date);
    }

    @Override
    public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {

    }



}
