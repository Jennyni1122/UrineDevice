package com.leo.device.model.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.leo.device.bean.data.Urine;

/**
 * @author Leo
 * @date 2019-05-05
 */
@Database(entities = {Urine.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase database;

    public static AppDatabase getInstance() {
        if (database != null) {
            return database;
        } else {
            throw new NullPointerException("need init before use");
        }
    }

    public static void init(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, AppDatabase.class, "app.db").build();
        }
    }

    public abstract UrineDao urineDao();
}
