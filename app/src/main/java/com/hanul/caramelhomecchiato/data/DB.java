package com.hanul.caramelhomecchiato.data;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = )
public class DB extends RoomDatabase{
	private static DB instance;

	public static DB getInstance(){

	}

	@NonNull @Override protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config){
		return null;
	}
	@NonNull @Override protected InvalidationTracker createInvalidationTracker(){
		return null;
	}
	@Override public void clearAllTables(){

	}

	private static final class Singleton {

	}
}
