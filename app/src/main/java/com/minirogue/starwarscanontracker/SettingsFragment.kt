package com.minirogue.starwarscanontracker

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.preference.*
import com.minirogue.starwarscanontracker.database.CSVImporter
import com.minirogue.starwarscanontracker.database.MediaDatabase
import com.minirogue.starwarscanontracker.database.MediaType
import java.lang.ref.WeakReference


class SettingsFragment : PreferenceFragmentCompat()/*, SharedPreferences.OnSharedPreferenceChangeListener */{



    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)


        SetTypePreferences(activity!!.applicationContext, findPreference("permanent_filters")!!).execute()
        val checkboxone = findPreference<EditTextPreference>(getString(R.string.watched_read))
        checkboxone?.setSummaryProvider { checkboxone.text }
        val checkboxtwo = findPreference<EditTextPreference>(getString(R.string.want_to_watch_read))
        checkboxtwo?.setSummaryProvider { checkboxtwo.text }
        val checkboxthree = findPreference<EditTextPreference>(getString(R.string.owned))
        checkboxthree?.setSummaryProvider { checkboxthree.text }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key == "update_from_online"){
            CSVImporter(this.activity!!.application).execute(CSVImporter.SOURCE_ONLINE)
        }
        return super.onPreferenceTreeClick(preference)
    }

    class SetTypePreferences(ctx : Context, category: PreferenceCategory) : AsyncTask<Void, Void, List<MediaType>>(){
        private val ctxRef = WeakReference<Context>(ctx)
        private val catRef = WeakReference<PreferenceCategory>(category)

        override fun doInBackground(vararg p0: Void?): List<MediaType> {
            return MediaDatabase.getMediaDataBase(ctxRef.get()).daoType.allNonLive
        }

        override fun onPostExecute(result: List<MediaType>?) {
            for (type in result!!){
                val newPref = SwitchPreference(ctxRef.get())
                newPref.setDefaultValue(true)
                newPref.title = type.text
                newPref.key = type.text
                newPref.order = type.id
                catRef.get()!!.addPreference(newPref)
            }
        }
    }


   /*   override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
    }

   override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }*/


}