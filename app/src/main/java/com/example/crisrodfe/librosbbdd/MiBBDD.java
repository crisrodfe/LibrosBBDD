package com.example.crisrodfe.librosbbdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by CrisRodFe.
 */
public class MiBBDD extends SQLiteOpenHelper
{
    //Variables de la bd,tabla y columnas.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "libros.db";
    private static final String TABLE_LIBROS = "libros";

    public static final String COLUMN_TITULO = "titulo";
    public static final String COLUMN_AUTOR = "autor";
    public static final String COLUMN_EDITORIAL = "editorial";


    public MiBBDD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Creación de la tabla.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +TABLE_LIBROS + "("
                + COLUMN_TITULO+ " TEXT,"
                + COLUMN_AUTOR+ " TEXT, "
                + COLUMN_EDITORIAL + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBROS);
        onCreate(db);
    }

    //Añadirá un nuevo registro a la tabla de libros.
    public void addLibro(String titulo, String autor, String editorial)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_AUTOR, autor);
        values.put(COLUMN_EDITORIAL, editorial);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_LIBROS, null, values);
    }

    /**
     * Devuelve una cadena con formato con todos los registros de la tabla de libros,ordenados según el campo indicado por parámetro.
     * @return String
     */
    public ArrayList<String[]> getLibros(String orden)
    {
        String query = "Select titulo,autor,editorial FROM " + TABLE_LIBROS +" ORDER BY "+orden+" ASC";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String[]> libros = new ArrayList<>();

        if(cursor.getCount() > 0)
        {
            do {
                String[] libro = new String[3];
                libro[0] = cursor.getString(0);
                libro[1] = cursor.getString(1);
                libro[2] = cursor.getString(2);
                libros.add(libro);
            } while (cursor.moveToNext());
        }
        return libros;
    }

    /**
     * Devuelve todos los títulos de los libros ordenados según el campo ordenado por parámetro.
     * @param orden
     * @return
     */
    public String[] getTitulos(String orden)
    {

        String query = "Select "+COLUMN_TITULO+" FROM " + TABLE_LIBROS +" ORDER BY "+orden+" ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        String [] titulos = new String[cursor.getCount()];
        int i = 0;
        if(cursor.getCount() > 0)
        {
            do
            {
                titulos[i] = cursor.getString(0);
                i++;
            } while (cursor.moveToNext());
        }

        return titulos;
    }

    /**
     * Devuelve el libro con el título indicado por parámetro.
     * @param titulo
     * @return
     */
    public String[] getLibro(String titulo)
    {
        String query = "Select * FROM " + TABLE_LIBROS +" WHERE "+COLUMN_TITULO+" = '"+titulo+"'; ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        String[] libro = new String[3];
        libro[0] = cursor.getString(0);
        libro[1] = cursor.getString(1);
        libro[2] = cursor.getString(2);

        return libro;
    }

    /*
     *Modifica un registro de la base de datos.
     */
    public void modificarLibros(String tituloAntes,String titulo, String autor, String editorial)
    {
        ContentValues valores = new ContentValues();
        valores.put(COLUMN_TITULO,titulo);
        valores.put(COLUMN_AUTOR,autor);
        valores.put(COLUMN_EDITORIAL,editorial);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_LIBROS, valores, "titulo='"+tituloAntes+"' ", null);

    }

    /**
     * Comprueba si un registro existe o no en la base de datos.
     * @param titulo
     * @return
     */
    public boolean existeLibro(String titulo)
    {
        String query = "Select "+COLUMN_TITULO+" FROM " + TABLE_LIBROS +" WHERE "+COLUMN_TITULO+" = '"+titulo+"'; ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        return cursor.getCount() > 0;
    }

    /**
     * Elimina el registro con el campo titulo indicado por parámetro.
     * @param titulo
     */
    public void deleteLibro(String titulo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LIBROS,"titulo=?",new String[]{titulo});
    }
}
