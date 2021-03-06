package com.minirogue.starwarscanontracker.view.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.preference.*
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.core.model.FilterUpdater
import com.minirogue.starwarscanontracker.core.model.repository.SWMRepository
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaType
import com.minirogue.usecase.GetAllMediaTypesUseCase
import com.minirogue.usecase.UpdateMediaDatabaseUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat()/*, SharedPreferences.OnSharedPreferenceChangeListener */ {

    companion object {
        private const val TAG = "SettingsFragment"
    }

    @Inject
    lateinit var repo: SWMRepository

    @Inject
    lateinit var updateMediaDatabaseUseCase: UpdateMediaDatabaseUseCase

    @Inject
    lateinit var filterUpdater: FilterUpdater

    @Inject
    lateinit var getAllMediaTypesUseCase: GetAllMediaTypesUseCase

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)


        SetTypePreferences(requireActivity().applicationContext,
            findPreference("permanent_filters")!!,
            getAllMediaTypesUseCase).execute()
        val checkboxone = findPreference<EditTextPreference>(getString(R.string.checkbox1_default_text))
        checkboxone?.setSummaryProvider { checkboxone.text }
        val checkboxtwo = findPreference<EditTextPreference>(getString(R.string.checkbox2_default_text))
        checkboxtwo?.setSummaryProvider { checkboxtwo.text }
        val checkboxthree = findPreference<EditTextPreference>(getString(R.string.checkbox3_default_text))
        checkboxthree?.setSummaryProvider { checkboxthree.text }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key == "update_from_online") {
            updateMediaDatabaseUseCase(true)
        } else if (preference?.parent?.key == "permanent_filters") {
            GlobalScope.launch(Dispatchers.Default) {
                val filter = repo.getFilter(preference.order, FilterType.FILTERCOLUMN_TYPE)
                filter?.let {
                    filter.active = false
                    repo.update(filter)
                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    class SetTypePreferences(
        ctx: Context,
        category: PreferenceCategory,
        private val getAllMediaTypesUseCase: GetAllMediaTypesUseCase,
    ) : AsyncTask<Void, Void, List<MediaType>>() {
        private val ctxRef = WeakReference(ctx)
        private val catRef = WeakReference(category)

        override fun doInBackground(vararg p0: Void?): List<MediaType> {
            return ctxRef.get()?.let { getAllMediaTypesUseCase() } ?: emptyList()
        }

        override fun onPostExecute(result: List<MediaType>?) {
            val ctx = ctxRef.get()
            if (ctx != null) {
                for (type in result!!) {
                    val newPref = CheckBoxPreference(ctxRef.get())
                    newPref.setDefaultValue(true)
                    newPref.title = type.text
                    newPref.key = type.text
                    newPref.order = type.id
                    catRef.get()?.addPreference(newPref)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        filterUpdater.updateJustCheckboxFilters()
        Log.d(TAG, "onPause called in SettingsFragment")
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
