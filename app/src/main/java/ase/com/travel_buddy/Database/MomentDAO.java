package ase.com.travel_buddy.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import ase.com.travel_buddy.Models.Moment;

@Dao
public interface MomentDAO {
    @Insert
    void insert(Moment moment);

    @Insert
    Long insertFromContentValues(Moment moment);

    @Insert
    void insertBulk(List<Moment> moments);

    @Query("SELECT COUNT(*) FROM " + Moment.TABLE_NAME)
    int count();

    @Query("SELECT * FROM "+ Moment.TABLE_NAME)
    List<Moment> getMomentList();

    @Query("SELECT * FROM " + Moment.TABLE_NAME)
    Cursor getMomentListCursor();

    @Query("SELECT * FROM " + Moment.TABLE_NAME + " WHERE " + Moment.COLUMN_ID + " = :id")
    Cursor getMomentByIdCursor(Long id);

    @Query("DELETE FROM " + Moment.TABLE_NAME + " WHERE " + Moment.COLUMN_ID + " = :id")
    int deleteById(long id);

    @Query("DELETE FROM " + Moment.TABLE_NAME)
    int delete();
}
