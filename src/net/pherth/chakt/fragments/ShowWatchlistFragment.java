package net.pherth.chakt.fragments;

import java.util.ArrayList;
import java.util.List;

import net.pherth.chakt.PreferencesActivity_;
import net.pherth.chakt.R;
import net.pherth.chakt.SingleShowActivity_;
import net.pherth.chakt.TraktInterface;
import net.pherth.chakt.TraktWrapper;
import net.pherth.chakt.adapter.BaseStickylistAdapter;
import net.pherth.chakt.adapter.BaselistAdapter;
import net.pherth.chakt.adapter.BaselistAdapter_;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.jakewharton.trakt.TraktException;
import com.jakewharton.trakt.entities.TvShow;

import de.keyboardsurfer.android.widget.crouton.Style;

@EFragment(R.layout.fragment_basestickylist)
public class ShowWatchlistFragment extends TraktFragment implements TraktInterface {

	TraktWrapper tw;
	@ViewById
	ListView list;
	
	@Bean
	BaseStickylistAdapter adapter;
	
	@AfterViews
	void loadFragment() {
		adapter.init(getActivity(), "show");
		
		// Assign adapter to ListView
		list.setAdapter(adapter); 
		list.setItemsCanFocus(false);
		
		list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	TvShow selShow =(TvShow) (parent.getItemAtPosition(position));
            	Intent recentIntent = new Intent(getActivity().getApplicationContext(), SingleShowActivity_.class);
            	recentIntent.putExtra("show", selShow);
                startActivityForResult(recentIntent, 0);
            }
        });
		
		tw = TraktWrapper.getInstance();
		getProgress();
	}
	
	public static ShowWatchlistFragment newInstance() {
		ShowWatchlistFragment f = new ShowWatchlistFragment_();
		f.setRetainInstance(true);
		return f;
	}
	
	
	@Background
	public void getProgress() {
		setIndeterminateProgress(true);
		List<TvShow> shows;
		try {
			shows = (ArrayList<TvShow>) tw.userService().watchlistShows(tw.username).fire();
		} catch (TraktException e) {
			displayCrouton(tw.handleError(e, getActivity()), Style.ALERT);
			setIndeterminateProgress(false);
			return;
		}

		notifyDataset(shows);
		setIndeterminateProgress(false);
	}
	
	@UiThread
	void notifyDataset(List<TvShow> shows) {
		adapter.clear();
		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
			adapter.addAll(shows);
		} else {
			for(TvShow show : shows) {
				adapter.add(show);
			}
		}
		adapter.notifyDataSetChanged();
	}
	
}
