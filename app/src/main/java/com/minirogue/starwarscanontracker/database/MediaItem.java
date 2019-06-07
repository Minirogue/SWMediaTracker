package com.minirogue.starwarscanontracker.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "media_items")
public class MediaItem {
    //This class represents the database entries


    @PrimaryKey
    public int id;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "author")
    public String author;
    @ColumnInfo(name = "type")
    public int type;
    @ColumnInfo(name = "description")
    public String description;
    @ColumnInfo(name = "image")
    public String imageURL;
    @ColumnInfo(name = "date")
    public String date;
    @ColumnInfo(name = "timeline")
    public double timeline;


    @NotNull
    @Override
    public String toString() {
        return title;
    }
}