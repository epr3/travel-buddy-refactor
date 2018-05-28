package ase.com.travel_buddy.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import ase.com.travel_buddy.Models.Moment;

@Database(entities = {Moment.class}, version = 1)
public abstract class TravelBuddyDatabase extends RoomDatabase{
    private static TravelBuddyDatabase db;

    public abstract MomentDAO getMomentDAO();

    public static TravelBuddyDatabase getInstance(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context, TravelBuddyDatabase.class, "travel_buddy.db")
                    .allowMainThreadQueries()
                    .build();
        }

        return db;
    }
}
