package com.marverenic.music.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.marverenic.music.R;
import com.marverenic.music.instances.LibraryScanner;
import com.marverenic.music.utils.Debug;
import com.marverenic.music.utils.Navigate;
import com.marverenic.music.utils.Themes;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Themes.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_layout);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getSupportActionBar().setElevation(getResources().getDimension(R.dimen.header_elevation));
        }

        getFragmentManager().beginTransaction().replace(R.id.prefFrame, new PrefFragment()).commit();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onResume() {
        super.onResume();
        Themes.setApplicationIcon(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getBoolean("prefAddShortcut", true)) {
            Themes.updateLauncherIcon(this);
            prefs.edit().putBoolean("prefAddShortcut", false).apply();
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigate.home(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Navigate.home(this);
        finish();
    }

    public static class PrefFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);

            final Context context = getActivity();

            Preference bugReportPref = findPreference("prefSendBugReport");
            bugReportPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"marverenic@gmail.com"});
                    try {
                        intent.putExtra(Intent.EXTRA_SUBJECT, "[Jockey bug report] " + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Couldn't send the log file", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    intent.putExtra(Intent.EXTRA_TEXT, "[place any additional details here]");
                    File file = new File(context.getExternalFilesDir(null), Debug.FILENAME);
                    if (!file.exists() || !file.canRead()) {
                        Toast.makeText(context, "Couldn't send the log file", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Uri uri = Uri.parse("file://" + file);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(intent, "Email bug report..."));
                    return true;
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LibraryScanner.saveLibrary(this);
    }
}
