package com.minirogue.starwarscanontracker.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

@Dao
public interface DaoCharacter {

// --Commented out by Inspection START (6/6/19 8:33 PM):
// --Commented out by Inspection START (6/6/19 8:33 PM):
////    @Insert(onConflict = OnConflictStrategy.IGNORE)
////    long insert(Character character);
// --Commented out by Inspection STOP (6/6/19 8:33 PM)
// --Commented out by Inspection STOP (6/6/19 8:33 PM)
    @Update
    void update(Character character);
    /*@Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    Character getCharacterById(int id);
    @Query("SELECT * FROM characters")
    List<Character> getAllCharacters();
    @Query("SELECT name FROM characters ORDER BY name")
    List<String> getAllCharacterNames();
// --Commented out by Inspection START (6/6/19 8:33 PM):
//    @Query("SELECT * FROM characters WHERE name = :name LIMIT 1")
//    Character getCharacterByName(String name);*/
//
//
//    //The following is for MediaCharacterJoin
// --Commented out by Inspection STOP (6/6/19 8:33 PM)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MediaCharacterJoin mediaCharacterJoin);
    /*@Query("SELECT * FROM characters INNER JOIN media_character_join ON id=media_character_join.characterId WHERE media_character_join.mediaId=:mediaId")
    List<Character> getCharactersFromMedia(final int mediaId);
    @Query("SELECT * FROM media_items INNER JOIN media_character_join ON id=media_character_join.mediaId WHERE media_character_join.characterId=:characterID")
    List<MediaItem> getMediaFromCharacter(final int characterID);
    @Query("DELETE FROM media_character_join")
    void clearMediaCharacterJoin();
    @Query("SELECT * FROM media_character_join")
    List<MediaCharacterJoin> getAllMCJoin();*/



}