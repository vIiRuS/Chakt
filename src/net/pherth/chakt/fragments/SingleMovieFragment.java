package net.pherth.chakt.fragments;

import net.pherth.chakt.MainActivity_;
import net.pherth.chakt.PreferencesActivity_;
import net.pherth.chakt.R;
import net.pherth.chakt.SingleMovieActivity_;
import net.pherth.chakt.SlideInBitmapDisplayer;
import net.pherth.chakt.TraktWrapper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.jakewharton.trakt.TraktException;
import com.jakewharton.trakt.entities.Movie;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.keyboardsurfer.android.widget.crouton.Style;


@EFragment(R.layout.fragment_single_movie)
@OptionsMenu(R.menu.activity_single)
public class SingleMovieFragment extends SingleBaseFragment {
	
	Movie movie;
	TraktWrapper tw;
	
	@ViewById
	TextView titletext;
	@ViewById
	TextView releasevalue;
	@ViewById
	TextView runtimevalue;
	@ViewById
	TextView playsvalue;
	@ViewById
	TextView descriptiontext;
	@ViewById
	ImageView headerimage;
	@ViewById
	TextView ratingsvalue;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		movie = (Movie) getActivity().getIntent().getExtras().get("movie");
		tw = TraktWrapper.getInstance();
        return null;
    }
	
	@AfterViews
	void loadData() {
		
		titletext.setText(movie.title);
		releasevalue.setText(android.text.format.DateFormat.format("dd.MM.yyyy", movie.released));
		runtimevalue.setText(movie.runtime.toString() + " " + getString(R.string.minutes));
		ratingsvalue.setText(movie.ratings.percentage + "% (" + movie.ratings.votes + " " + getString(R.string.votes) + ")");
		
		descriptiontext.setText(movie.overview);
		
		ImageLoader loader = ImageLoader.getInstance();
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.displayer(new SlideInBitmapDisplayer(500, getActivity()))
		.cacheInMemory()
		.cacheOnDisc()
		.build();
		
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		if(display.getWidth() > 940) {
			loader.displayImage(movie.images.fanart, headerimage, options);
		} else {
			loader.displayImage(movie.images.getFanart940(), headerimage, options);
		}
		loadDetails();
	}
	
	@Background
	void loadDetails() {
		setIndeterminateProgress(true);
		try {
			movie = tw.movieService().summary(movie.imdbId).fire();
		} catch (TraktException e) {
			displayCrouton(tw.handleError(e, getActivity()), Style.ALERT);
			setIndeterminateProgress(false);
			return;
		}
		setIndeterminateProgress(false);
		displayDetails();
	}
	
	@UiThread
	void displayDetails() {
		if(movie.watched == null) {
			spawnWrongAuthDialog();
		} else {
			if(movie.watched) {
				playsvalue.setText(movie.plays.toString() + " " + getString(R.string.times));
			} else {
				playsvalue.setText(R.string.notplayed);
			}
		}
	}

	@OptionsItem
	@Background
	void watchlist(MenuItem item) {
		if (movie.inWatchlist) {
			displayCrouton(R.string.tryWatchlistRemove, Style.INFO);
		} else {
			displayCrouton(R.string.tryWatchlistAdd, Style.INFO);
		}
		try {
			tw.switchWatchlistMovie(movie);
		} catch (TraktException e) {
			displayCrouton(tw.handleError(e, getActivity()), Style.ALERT);
			return;
		}
		if (movie.inWatchlist) {
			displayCrouton(R.string.movieRemove, Style.CONFIRM);
			movie.inWatchlist = false;
			this.updateMenuIcon(item, R.drawable.ic_watchlist);
		} else {
			displayCrouton(R.string.movieAdd, Style.CONFIRM);
			movie.inWatchlist = true;
			this.updateMenuIcon(item, R.drawable.ic_unwatchlist);
		}
	}
	
	@OptionsItem
	@Background
	void checkin(MenuItem item) {
		displayCrouton(R.string.tryCheckin, Style.INFO);
		try {
			tw.checkinMovie(movie);
		} catch (TraktException e) {
			displayCrouton(tw.handleError(e, getActivity()), Style.ALERT);
			return;
		}
		displayCrouton(R.string.movieCheckin, Style.CONFIRM);
		item.setEnabled(false);
	}
	
	@OptionsItem
	void menu_settings() {
		Intent recentIntent = new Intent(getActivity().getApplicationContext(), PreferencesActivity_.class);
        startActivityForResult(recentIntent, 0);
	}
	
	@OptionsItem
	void home() {
		Intent upIntent = new Intent(getActivity(), MainActivity_.class);
        if (NavUtils.shouldUpRecreateTask(getActivity(), upIntent)) {
            // This activity is not part of the application's task, so create a new task
            // with a synthesized back stack.
            TaskStackBuilder.from(getActivity())
                    .addNextIntent(upIntent)
                    .startActivities();
            getActivity().finish();
        } else {
            // This activity is part of the application's task, so simply
            // navigate up to the hierarchical parent activity.
            NavUtils.navigateUpTo(getActivity(), upIntent);
        }
	}
}
