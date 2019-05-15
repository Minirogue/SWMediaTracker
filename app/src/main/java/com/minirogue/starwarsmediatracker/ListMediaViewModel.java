package com.minirogue.starwarsmediatracker;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.minirogue.starwarsmediatracker.database.MediaAndNotes;
import com.minirogue.starwarsmediatracker.database.MediaItem;
import com.minirogue.starwarsmediatracker.database.MediaNotes;
import com.minirogue.starwarsmediatracker.database.SWMRepository;

import java.util.ArrayList;
import java.util.List;

public class ListMediaViewModel extends AndroidViewModel {
    private SWMRepository repository;
    private LiveData<List<FilterObject>> filters;
    private LiveData<List<FilterObject>> allFilters;

    public ListMediaViewModel(@NonNull Application application) {
        super(application);
        repository = new SWMRepository(application);
        filters = repository.getFilters();
        allFilters = repository.getAllFilters();
    }

    public LiveData<List<FilterObject>> getAllFilters(){
        return allFilters;
    }

    public void removeFilter(FilterObject filter){
        repository.removeFilter(filter);
    }
    void addFilter(FilterObject filter){
        repository.addFilter(filter);
    }

    boolean isCurrentFilter(FilterObject filter){
        return repository.isCurrentFilter(filter);
    }

    public LiveData<List<FilterObject>> getFilters() {
        return filters;
    }

    public LiveData<List<MediaAndNotes>> getFilteredMediaAndNotes() {
        return repository.getFilteredMediaAndNotes();
    }
    public void update(MediaNotes mediaNotes){
        repository.update(mediaNotes);
    }
    public String convertTypeToString(int typeId){
        return repository.convertTypeToString(typeId);
    }
}
