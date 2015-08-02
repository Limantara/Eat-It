package me.limantara.eatit.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import me.limantara.eatit.model.Venue;

/**
 * Created by edwinlimantara on 8/1/15.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private static SQLiteHelper mInstance;
    private static final String DB_NAME = "eat_it_or_leave_it";
    private static final int DB_VERSION = 1;

    /**
     * Get an instance of SQLiteOpenHelper
     *
     * @param context
     * @return
     */
    public static synchronized SQLiteHelper getInstance(Context context) {
        if(mInstance == null)
            mInstance = new SQLiteHelper(context.getApplicationContext());

        return mInstance;
    }

    /**
     * Create a new instance of SQLiteOpenHelper. Maximum one instance.
     *
     * @param context
     */
    private SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS foods");
        this.onCreate(db);
    }

    /**
     * Get all the foods corresponding to a particular venue.
     *
     * @param venue
     * @return
     */
    public List<Venue.Item> getAllFoodsFromVenue(Venue venue) {
        List<Venue.Item> foods = new ArrayList<>();
        String query = "SELECT * FROM foods " +
                "WHERE venue_locu_id = '" + venue.locu_id + "' " +
                "ORDER BY created_at DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                Venue.Item food = new Venue.Item();
                food.name = cursor.getString(1);
                food.price = cursor.getString(2);
                food.description = cursor.getString(3);
                food.images = new ArrayList<>();
                food.images.add(cursor.getString(4));
                food.created_at = Long.parseLong(cursor.getString(5));

                foods.add(food);
            }
            while(cursor.moveToNext());
        }

        return foods;
    }

    public Venue findVenueFromFood(Venue.Item food) {
        String query = "SELECT * FROM VENUES WHERE locu_id = '" + food.venue_locu_id + "';";
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        return new Venue(cursor);
    }

    /**
     * Set the eat_it and expired columns of a particular food record to true.
     * If expired column was true, cancel updating.
     *
     * @param food
     */
    public void eatIt(Venue venue, Venue.Item food) {
        List<Venue.Item> venueFoods = getAllFoodsFromVenue(venue);

        for(Venue.Item venueFood : venueFoods) {
            if(food.name.equals(venueFood.name)) {
                SQLiteDatabase db = getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("eat_it", 1);
                values.put("expired", 1);
                String[] args = {food.name, "0"};

                db.update("foods", values, "name=? AND expired=?", args);
                db.close();
            }
        }
    }

    /**
     * Create a new venue if it's not in the database yet.
     * Skip it otherwise.
     *
     * @param venue
     */
    public void createVenue(Venue venue) {
        if(hasVenue(venue))
            return;

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("locu_id", venue.locu_id);
        values.put("name", venue.name);
        values.put("address", venue.getDisplayAddress());

        db.insert("venues", null, values);
        db.close();
    }

    /**
     * Check if a venue exists in the database.
     *
     * @param venue
     * @return
     */
    public boolean hasVenue(Venue venue) {
        String query = "SELECT COUNT(*) FROM venues WHERE locu_id = '" + venue.locu_id + "';";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        int count = cursor.getInt(0);
        db.close();

        return count != 0;
    }

    /**
     * Create food and its corresponding venue if it's not in the database yet.
     * Do nothing if the selected venue already has the food.
     *
     * @param food
     * @param venue
     * @param selectedPicture
     */
    public void createFood(Venue.Item food, Venue venue, String selectedPicture) {
        if( ! hasVenue(venue))
            createVenue(venue);

        if(hasMenu(venue, food))
            return;

        SQLiteDatabase db = this.getWritableDatabase();
        long created_at = TimeHelper.now().getTimeInMillis();

        ContentValues values = new ContentValues();
        values.put("name", food.name);
        values.put("price", food.price);
        values.put("description", food.description);
        values.put("image_url", selectedPicture);
        values.put("venue_locu_id", venue.locu_id);
        values.put("created_at", created_at);

        db.insert("foods", null, values);
        db.close();
    }

    /**
     * Check if a venue has a particular food.
     *
     * @param venue
     * @param food
     * @return
     */
    public boolean hasMenu(Venue venue, Venue.Item food) {
        List<Venue.Item> venueFoods = getAllFoodsFromVenue(venue);

        for(Venue.Item venueFood : venueFoods) {
            if(food.name.equals(venueFood.name))
                return true;
        }

        return false;
    }

    /**
     * Get all foods record in the database.
     *
     * @return
     */
    public List<Venue.Item> getAllFoods() {
        List<Venue.Item> foods = new ArrayList<>();
        String query = "SELECT * FROM foods";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                Venue.Item food = new Venue.Item(cursor);
                foods.add(food);
            }
            while(cursor.moveToNext());
        }

        return foods;
    }

    /**
     * Get the latest food added to the database.
     *
     * @return
     */
    public Venue.Item getLatestFood() {
        String query = "SELECT * FROM foods LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            Venue.Item food = new Venue.Item(cursor);
            return food;
        }

        return null;
    }

    /**
     * Drop all tables in the database and re-create them.
     */
    public void refresh() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS foods");
        db.execSQL("DROP TABLE IF EXISTS venues");
        createTables(db);
    }

    /**
     * Helper method to create tables in the database.
     *
     * @param db
     */
    private void createTables(SQLiteDatabase db) {
        String CREATE_FOOD_TABLE = "CREATE TABLE IF NOT EXISTS foods ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "price TEXT, " +
                "description TEXT, " +
                "image_url TEXT, " +
                "created_at long, " +
                "venue_locu_id TEXT, " +
                "eat_it INTEGER DEFAULT 0, " +
                "expired INTEGER DEFAULT 0 );";

        String CREATE_VENUE_TABLE = "CREATE TABLE IF NOT EXISTS venues ( " +
                "locu_id TEXT, " +
                "name TEXT, " +
                "address TEXT );";

        db.execSQL(CREATE_FOOD_TABLE);
        db.execSQL(CREATE_VENUE_TABLE);
    }

}