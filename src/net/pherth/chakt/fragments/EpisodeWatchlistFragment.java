package net.pherth.chakt.fragments;

import java.util.ArrayList;
import java.util.List;

import net.pherth.chakt.PreferencesActivity_;
import net.pherth.chakt.R;
import net.pherth.chakt.SingleEpisodeActivity_;
import net.pherth.chakt.SingleMovieActivity_;
import net.pherth.chakt.TraktInterface;
import net.pherth.chakt.TraktWrapper;
import net.pherth.chakt.adapter.BaseStickylistAdapter;
import net.pherth.chakt.adapter.BaselistAdapter;
import net.pherth.chakt.adapter.EpisodeStickylistAdapter;
import net.pherth.chakt.adapter.EpisodelistAdapter;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.jakewharton.trakt.TraktException;
import com.jakewharton.trakt.entities.TvEntity;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

import de.keyboardsurfer.android.widget.crouton.Style;

@EFragment(R.layout.fragment_basestickylist)
public class EpisodeWatchlistFragment extends TraktFragment implements TraktInterface {

	TraktWrapper tw;
	@ViewById
	ListView list;
	
	@Bean
	EpisodeStickylistAdapter adapter;
	
	@AfterViews
	void loadFragment() {
		adapter.init(getActivity(), "episode");
		
		// Assign adapter to ListView
		list.setAdapter(adapter); 
		list.setItemsCanFocus(false);
		
		list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	TvEntity entity =(TvEntity) (parent.getItemAtPosition(position));
            	Intent recentIntent = new Intent(getActivity().getApplicationContext(), SingleEpisodeActivity_.class);
            	recentIntent.putExtra("episode", entity.episode);
            	recentIntent.putExtra("show", entity.show);
                startActivityForResult(recentIntent, 0);
            }
        });
		
		tw = TraktWrapper.getInstance();
		getProgress();
	}
	
	public static EpisodeWatchlistFragment newInstance() {
		EpisodeWatchlistFragment f = new EpisodeWatchlistFragment_();
		f.setRetainInstance(true);
		return f;
	}
	
	
	@Background
	public void getProgress() {
		List<TvShow> episodes;
		try {
			episodes = (ArrayList<TvShow>) tw.userService().watchlistEpisodes(tw.username).fire();
		} catch (TraktException e) {
			displayCrouton(tw.handleError(e, getActivity()), Style.ALERT);
			return;
		}

		notifyDataset(episodes);
	}
	
	@UiThread
	void notifyDataset(List<TvShow> shows) {
		adapter.clear();
		for(TvShow show : shows) {
			for(TvShowEpisode episode : show.episodes) {
				adapter.add(show, episode);
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	@OptionsItem
	void menu_settings() {
		Intent recentIntent = new Intent(getActivity().getApplicationContext(), PreferencesActivity_.class);
        startActivityForResult(recentIntent, 0);
	}
	
}
