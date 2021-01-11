package com.carista.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.carista.data.realtimedb.models.PostModel;

import java.util.List;

@Dao
public interface PostDao {
    @Query("SELECT * FROM PostModel ORDER BY id DESC")
    List<PostModel> getAll();

    @Insert
    void insertAll(PostModel... posts);

    @Delete
    void delete(PostModel postModel);

    @Query("DELETE FROM PostModel")
    void deleteAll();
}
