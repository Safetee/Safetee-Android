package com.safeteeapp.cof2;


import android.app.ListFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.cocosw.undobar.UndoBarController;
import com.cocosw.undobar.UndoBarStyle;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.safeteeapp.audiorecorder.models.RecordingItem;
import com.safeteeapp.safetee.R;
import com.safeteeapp.safetytips.TipView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CofListFragment extends ListFragment implements OnDismissCallback, UndoBarController.UndoListener {
    private CofAdapter mAdapter;
    private MediaPlayer mPlayer;
    private static final SimpleDateFormat mDateAddedFormatter = new SimpleDateFormat("MMMM d, yyyy - hh:mm a", Locale.getDefault());


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new CofAdapter(getActivity());
        SwipeDismissAdapter adapter = new SwipeDismissAdapter(mAdapter, this);
        setListAdapter(adapter);
        getListView().setBackgroundColor(getResources().getColor(R.color.card_gray));
        adapter.setAbsListView(getListView());

        setEmptyText("");

        getListView().setDividerHeight(0);
        getListView().setDivider(null);
        getListView().setSelector(new ColorDrawable(getResources().getColor(R.color.transparent)));
        getListView().setHeaderDividersEnabled(true);
        getListView().setPadding(getListView().getPaddingLeft(),
                getListView().getPaddingTop() + 10, getListView().getPaddingRight(),
                getListView().getPaddingBottom() + 10);
        getListView().setClipToPadding(false);
    }




    @Override
    public void onListItemClick(android.widget.ListView l, View v, int position, long id) {
        CofItem item = mAdapter.getItem(position);
        gotoTipView(v, item);
    }

    private void gotoTipView(final View v, final CofItem item){
        Intent i = new Intent(v.getContext(), CofView.class);
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
    public void onUndo(Parcelable token) {
    }

    @Override
    public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
        for (int i : reverseSortedPositions) {
            CofItem item = mAdapter.getItem(i);
            mAdapter.remove(item);

            Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);

            /*
            UndoBarController.UndoBar undoBar = new UndoBarController.UndoBar(getActivity());
            undoBar.style(new UndoBarStyle(R.drawable.ic_undobar_undo, R.string.undo)
                    .setAnim(fadeInAnimation, fadeOutAnimation));
            undoBar.listener(this);
            undoBar.message(R.string.recording_deleted);
            undoBar.token(item);
            undoBar.show();
            */

        }


    }
}
