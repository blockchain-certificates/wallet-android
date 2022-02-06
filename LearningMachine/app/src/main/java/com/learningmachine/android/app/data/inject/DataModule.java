package com.learningmachine.android.app.data.inject;

import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.data.store.LMDatabaseHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = DataBindings.class)
public class DataModule {

    @Provides
    @Singleton
    SQLiteDatabase provideDatabase(LMDatabaseHelper helper) {
        return helper.getWritableDatabase();
    }
}
