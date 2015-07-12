package com.carrero.josmary.popularmovies.Activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.carrero.josmary.popularmovies.Adapters.PosterAdapter;
import com.carrero.josmary.popularmovies.Model.Movie;
import com.carrero.josmary.popularmovies.MoviesApi.FetchMoviesTask;
import com.carrero.josmary.popularmovies.R;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    static public ArrayList<Movie> moviesList;
    static public ArrayList<String> images;
    static public PosterAdapter posterAdapter;
    static GridView gridview;
    public static Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A movies fragment containing a simple view.
     */
    public static class MoviesFragment extends Fragment {

        public MoviesFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            setHasOptionsMenu(true);
            initComponents(rootView);
            return rootView;
        }

        public void initComponents(View view) {
            moviesList = new ArrayList<Movie>();
            images = new ArrayList<String>();
            gridview = (GridView) view.findViewById(R.id.gridview);
            posterAdapter = new PosterAdapter(getActivity());
            gridview.setAdapter(posterAdapter);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra("movie_id", moviesList.get(position).getId());
                    intent.putExtra("movie_position", position);
                    startActivity(intent);
                }
            });
        }

        public void updateMovies() {
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortingOrder = sharedPrefs.getString(
                    getString(R.string.pref_sorting_order_key),
                    getString(R.string.pref_sorting_order_default_value));

            String sortingCriteria = sharedPrefs.getString(getString(R.string.pref_sorting_criteria_key), getString(R.string.pref_sorting_criteria_default_value));
            new FetchMoviesTask().execute(sortingOrder, sortingCriteria, null);
            gridview.setAdapter(posterAdapter);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.main_fragment, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            int id = item.getItemId();

            if (id == R.id.action_fragment_settings) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onResume() {
            Toast.makeText(getActivity(), "Getting Movies ...", Toast.LENGTH_SHORT).show();
            initComponents(getView());
            updateMovies();
            super.onResume();
        }
    }
}
