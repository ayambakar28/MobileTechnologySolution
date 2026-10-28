package com.example.myapplication.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.model.User

class DBHelper(context: Context) :   SQLiteOpenHelper (context, "users.db", null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
                CREATE TABLE users(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL
                )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {

    }
    fun register(email: String, password: String) : Long{
        val values = ContentValues().apply {
            put("email", email)
            put("password", password)
        }
        return writableDatabase.insertWithOnConflict(
            "users",
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }
    fun login(email: String, password: String): User?{
        val cursor = readableDatabase.rawQuery(
            "SELECT id, email, password FROM users WHERE email = ? AND password = ?", arrayOf(email, password)
        )
        cursor.use{
            if (it.moveToFirst()){
                return User(
                    it.getInt(0),
                    it.getString(1),
                    it.getString(2)
                )
            }else{
                return null
            }
        }
    }

    fun getUserByID(id: Int): User?{
        val cursor = readableDatabase.rawQuery(
            "SELECT id, email, password FROM users WHERE id = ?", arrayOf(id.toString())
        )
        cursor.use {
            if(it.moveToFirst()){
                return User(
                    it.getInt(0),
                    it.getString(1),
                    it.getString(2)
                )
            }else{
                return null
            }
        }
    }

    fun updateUser(id:Int, email:String, password:String): Int{
        val values = ContentValues().apply {
            put("email", email)
            put("password", password)
        }

        return writableDatabase.update(
            "users",
            values,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    fun delete(id: Int): Int{
        return writableDatabase.delete(
            "users",
            "id = ?",
            arrayOf(id.toString())
        )
    }
}


