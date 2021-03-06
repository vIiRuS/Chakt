package net.pherth.chakt.fragments;

import net.pherth.chakt.R;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


@EFragment(R.layout.fragment_single_show)
public class SingleBaseFragment extends TraktFragment {
	
	@UiThread
	void updateMenuIcon(MenuItem item, Integer id) {
		item.setIcon(id);
	}
}
