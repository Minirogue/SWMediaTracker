package com.minirogue.starwarscanontracker.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.minirogue.starwarscanontracker.model.room.MediaDatabase;
import com.minirogue.starwarscanontracker.model.room.dao.DaoSeries;
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject;
import com.minirogue.starwarscanontracker.R;
import com.minirogue.starwarscanontracker.model.room.dao.DaoFilter;
import com.minirogue.starwarscanontracker.model.room.dao.DaoType;
import com.minirogue.starwarscanontracker.model.room.entity.FilterType;
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem;
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes;
import com.minirogue.starwarscanontracker.model.room.entity.MediaType;
import com.minirogue.starwarscanontracker.model.room.entity.Series;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static com.minirogue.starwarscanontracker.model.room.entity.FilterType.FILTERCOLUMN_HASREADWATCHED;
import static com.minirogue.starwarscanontracker.model.room.entity.FilterType.FILTERCOLUMN_OWNED;
import static com.minirogue.starwarscanontracker.model.room.entity.FilterType.FILTERCOLUMN_SERIES;
import static com.minirogue.starwarscanontracker.model.room.entity.FilterType.FILTERCOLUMN_TYPE;
import static com.minirogue.starwarscanontracker.model.room.entity.FilterType.FILTERCOLUMN_WANTTOREADWATCH;

//TODO convert to kotlin and use coroutines
public class CSVImporter extends AsyncTask<Integer, String, Void> {

    //private static final String TAG = "CSVImport";

    public static final int SOURCE_ONLINE = 1;
    private WeakReference<Application> appRef;
    private boolean wifiOnly;
    private ConnectivityManager connMgr;
    private HashMap<String, Integer> convertType = new HashMap<>();
    private HashMap<String, Integer> convertSeries = new HashMap<>();
    private long newVersionId;
    private boolean forced;


    public CSVImporter(Application application, boolean forced) {
        appRef = new WeakReference<>(application);
        connMgr = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiOnly = PreferenceManager.getDefaultSharedPreferences(appRef.get()).getBoolean(appRef.get().getString(R.string.setting_unmetered_sync_only), true);
        this.forced = forced;
    }

    private void importCSVToMediaTypeTable(InputStream inputStream) {
        //Log.d(TAG, "starting media_type import");
        Application app = appRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(app);
            MediaType mediaType;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                mediaType = new MediaType();
                for (int i = 0; i < row.length; i++) {
                    switch (header[i]) {
                        case "id":
                            mediaType.setId(Integer.valueOf(row[i]));
                            //Log.d(TAG, "type ID'ed "+row[i]+" mapped to "+Integer.valueOf(row[i]));
                            break;
                        case "media_type":
                            mediaType.setText(row[i]);
                            //Log.d(TAG, "title found "+row[i]);
                            break;
                        default:
                            //System.out.println("Unused header: " + header[i]);
                    }
                }
                long insertSuccessful = db.getDaoType().insert(mediaType);
                if (insertSuccessful == -1) {
                    db.getDaoType().update(mediaType);
                }
                convertType.put(mediaType.getText(), mediaType.getId());
                //Log.d(TAG, "type added "+mediaType.getId()+" "+mediaType.getTitle());
                if (wifiOnly && connMgr.isActiveNetworkMetered()) {
                    cancel(true);
                    break;
                }
            }
            //Log.d(TAG, "queried mediaTypeTable: "+db.getDaoType().getAllNonLive());
        } catch (IOException ex) {
            throw new RuntimeException("Error reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                //Log.e("CSVImporter","Error while closing input stream from CSV file: " + ex);
            }
        }
    }

    private void importCSVToSeriesTable(InputStream inputStream) {
        //Log.d(TAG, "starting media_type import");
        Application app = appRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(app);
            Series series;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                series = new Series();
                for (int i = 0; i < row.length; i++) {
                    switch (header[i]) {
                        case "id":
                            series.setId(Integer.valueOf(row[i]));
                            break;
                        case "name":
                            series.setTitle(row[i]);
                            break;
                        case "image":
                            series.setImageURL(row[i]);
                            break;
                        case "description":
                            series.setDescription(row[i].replace(";", ","));
                            break;
                        default:
                            //System.out.println("Unused header: " + header[i]);
                    }
                }
                long insertSuccessful = db.getDaoSeries().insert(series);
                if (insertSuccessful == -1) {
                    db.getDaoSeries().update(series);
                }
                convertSeries.put(series.getTitle(), series.getId());
                //Log.d(TAG, "type added "+mediaType.getId()+" "+mediaType.getTitle());
                if (wifiOnly && connMgr.isActiveNetworkMetered()) {
                    cancel(true);
                    break;
                }
            }
            //Log.d(TAG, "queried mediaTypeTable: "+db.getDaoType().getAllNonLive());
        } catch (IOException ex) {
            throw new RuntimeException("Error reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                //Log.e("CSVImporter","Error while closing input stream from CSV file: " + ex);
            }
        }
    }


    private void importCSVToMediaDatabase(InputStream inputStream) {
        Application app = appRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(app);
            MediaItem newItem;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                newItem = new MediaItem();
                for (int i = 0; i < row.length; i++) {
                    switch (header[i]) {
                        case "ID":
                            newItem.id = (Integer.valueOf(row[i]));
                            break;
                        case "title":
                            newItem.title = (row[i]);
                            break;
                        case "series":
                            Integer newSeries = convertSeries.get(row[i]);
                            //convertSeries.get(key) returns null if there is no mapping for the key
                            if (newSeries == null) {
                                newItem.series = -1;
                            } else {
                                newItem.series = (newSeries);
                            }
                            break;
                        case "type":
                            Integer newType = convertType.get(row[i]);
                            //convertType.get(key) returns null if there is no mapping for the key
                            if (newType == null) {
                                newItem.type = -1;
                            } else {
                                newItem.type = (newType);
                            }
                            break;
                        case "description":
                            //semicolons are used as placeholders for commas in the room
                            // to not break the CSV format
                            newItem.description = row[i].replace(";", ",");
                            break;
                        case "review":
                            //semicolons are used as placeholders for commas in the room
                            // to not break the CSV format
                            newItem.review = row[i].replace(";", ",");
                            break;
                        case "author":
                            newItem.author = (row[i]);
                            break;
                        case "image":
                            newItem.imageURL = (row[i]);
                            break;
                        case "amazon_link":
                            newItem.amazonLink = (row[i]);
                            break;
                        case "amazon_stream":
                            newItem.amazonStream = (row[i]);
                            break;
                        case "released":
                            if (row[i].equals("")) {
                                //default value for release date
                                newItem.date = "99/99/9999";
                            } else {
                                newItem.date = (row[i]);
                            }
                            break;
                        case "timeline":
                            if (row[i].equals("")) {
                                newItem.timeline = 10000.0;
                            } else {
                                newItem.timeline = Double.valueOf(row[i]);
                            }
                            break;
                        default:
                            //System.out.println("Unused header: " + header[i]);
                    }
                }
                long insertSuccessful = db.getDaoMedia().insert(newItem);
                if (insertSuccessful == -1) {
                    db.getDaoMedia().update(newItem);
                }
                db.getDaoMedia().insert(new MediaNotes(newItem.id));
                if (wifiOnly && connMgr.isActiveNetworkMetered()) {
                    cancel(true);
                    break;
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                //Log.e("CSVImporter","Error while closing input stream from CSV file: " + ex);
            }
        }
    }

    /*private void importCSVToCharacterDatabase(InputStream inputStream){
        Application app = appRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(app);
            DaoCharacter charDao = db.getDaoCharacter();
            charDao.clearMediaCharacterJoin();
            Character newCharacter;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                newCharacter = new Character();
                for (int i = 0; i < row.length; i++) {
                    String[] appearances = new String[]{};
                    switch (header[i]) {
                        case "id":
                            newCharacter.setId(Integer.valueOf(row[i]));
                            break;
                        case "name":
                            newCharacter.setName(row[i]);
                            break;
                        case "major_appearance":
                            Log.d("Appearances", row[i]);
                            appearances = row[i].split(";");
                            break;
                        default:
                            System.out.println("Unused header: " + header[i]);
                    }
                    long didInsertWork = charDao.insert(newCharacter);
                    if (didInsertWork == -1){
                        charDao.update(newCharacter);
                    }
                    for (String strMedia : appearances){
                        charDao.insert(new MediaCharacterJoin(Integer.valueOf(strMedia),newCharacter.getId()));
                    }
                }
            }
            Log.d("CSVimport", db.getDaoCharacter().getAllMCJoin().toString());
        } catch (IOException ex) {
            throw new RuntimeException("Error reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Log.e("CSVImporter","Error while closing input stream from CSV file: " + ex);
            }
        }
    }*/


    @Override
    protected Void doInBackground(Integer... params) {
        if (wifiOnly && connMgr.isActiveNetworkMetered()) {
            updateFilters();
            cancel(true);
            return null;
        }
        InputStream inputStream = null;
        if (params[0] == SOURCE_ONLINE) {
            try {
                //update version number
                URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1842257512&single=true&output=csv");
                inputStream = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                newVersionId = Long.valueOf(reader.readLine().split(",")[0]);
                inputStream.close();
                if (!forced && newVersionId == PreferenceManager.getDefaultSharedPreferences(appRef.get()).getLong(appRef.get().getString(R.string.current_database_version), 0)) {
                    cancel(true);
                } else if (forced) {
                    publishProgress("Updating Database");
                }
                //import the media types
                if (isCancelled()) {
                    return null;
                }
                url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1834840175&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToMediaTypeTable(inputStream);
                inputStream.close();
                //import the series table
                if (isCancelled()) {
                    return null;
                }
                url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=485468234&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToSeriesTable(inputStream);
                inputStream.close();
                //import the main media table
                if (isCancelled()) {
                    return null;
                }
                url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=0&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToMediaDatabase(inputStream);
                inputStream.close();
                //import the character table
                /*url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1862227068&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToCharacterDatabase(inputStream);*/
            } catch (MalformedURLException ex) {
                //Log.e("DatabaseUpdate", ex.toString());
                cancel(true);
                return null;
            } catch (IOException ex) {
                //Log.e("DatabaseUpdate", ex.toString());
                cancel(true);
                return null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Application app = appRef.get();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(app);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(app.getString(R.string.current_database_version), newVersionId);
        editor.apply();
        updateFilters();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(appRef.get(), values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (forced) {
            Toast.makeText(appRef.get(), "Database updated", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        if (forced) {
            Toast.makeText(appRef.get(), "Database update failed", Toast.LENGTH_LONG).show();
        }
    }

    private void updateFilters() {
        DaoFilter daoFilter = MediaDatabase.getMediaDataBase(appRef.get()).getDaoFilter();
        DaoType daoType = MediaDatabase.getMediaDataBase(appRef.get()).getDaoType();
        DaoSeries daoSeries = MediaDatabase.getMediaDataBase(appRef.get()).getDaoSeries();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appRef.get());
        Application app = appRef.get();


        //add checkbox filters
        daoFilter.insert(new FilterType(FILTERCOLUMN_OWNED, true));
        daoFilter.insert(new FilterObject(1, FILTERCOLUMN_OWNED, false, prefs.getString(app.getString(R.string.checkbox1_default_text), app.getString(R.string.checkbox1_default_text))));

        daoFilter.insert(new FilterType(FILTERCOLUMN_WANTTOREADWATCH, true));
        daoFilter.insert(new FilterObject(1, FILTERCOLUMN_WANTTOREADWATCH, false, prefs.getString(app.getString(R.string.checkbox2_default_text), app.getString(R.string.checkbox2_default_text))));

        daoFilter.insert(new FilterType(FILTERCOLUMN_HASREADWATCHED, true));
        daoFilter.insert(new FilterObject(1, FILTERCOLUMN_HASREADWATCHED, false, prefs.getString(app.getString(R.string.checkbox3_default_text), app.getString(R.string.checkbox3_default_text))));

        //MediaType filters
        daoFilter.insert(new FilterType(FILTERCOLUMN_TYPE, true));
        List<MediaType> mediaTypes = daoType.getAllNonLive();
        for (MediaType mediaType : mediaTypes) {
            daoFilter.insert(new FilterObject(mediaType.getId(), FILTERCOLUMN_TYPE, false, mediaType.getText()));
        }

        //Series filters
        daoFilter.insert(new FilterType(FILTERCOLUMN_SERIES, true));
        List<Series> seriesList = daoSeries.getAllNonLive();
        for (Series series : seriesList){
            daoFilter.insert(new FilterObject(series.getId(), FILTERCOLUMN_SERIES, false, series.getTitle()));
        }

    }
}