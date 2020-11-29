package com.example.farmerapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FormDbHelper extends SQLiteOpenHelper {

    public FormDbHelper(@Nullable Context context) {
        super(context, "query.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table formquery (name text,phone text,address text,complaint text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists formquery");
        onCreate(db);
    }
}
