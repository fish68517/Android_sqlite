package com.archive.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.archive.app.model.Attraction;
import com.archive.app.model.Booking;
import com.archive.app.model.Itinerary;
import com.archive.app.model.ItineraryItem;
import com.archive.app.model.Post;
import com.archive.app.model.User;

import java.util.ArrayList;
import java.util.List;

public class OpenHelperDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "travel_app.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "OpenHelperDataBase";

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USERS_ID = "id";
    public static final String COLUMN_USERS_USERNAME = "username";
    public static final String COLUMN_USERS_PASSWORD = "password";

    // Attractions table
    public static final String TABLE_ATTRACTIONS = "attractions";
    public static final String COLUMN_ATTRACTIONS_ID = "id";
    public static final String COLUMN_ATTRACTIONS_NAME = "name";
    public static final String COLUMN_ATTRACTIONS_DESCRIPTION = "description";
    public static final String COLUMN_ATTRACTIONS_IMAGE_URL = "image_url";
    public static final String COLUMN_ATTRACTIONS_LOCATION = "location";
    public static final String COLUMN_ATTRACTIONS_PRICE = "price";

    // Bookings table
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COLUMN_BOOKINGS_ID = "id";
    public static final String COLUMN_BOOKINGS_USER_ID = "user_id";
    public static final String COLUMN_BOOKINGS_ATTRACTION_ID = "attraction_id";
    public static final String COLUMN_BOOKINGS_DATE = "booking_date";
    public static final String COLUMN_BOOKINGS_STATUS = "status";

    // Itineraries table
    public static final String TABLE_ITINERARIES = "itineraries";
    public static final String COLUMN_ITINERARIES_ID = "id";
    public static final String COLUMN_ITINERARIES_USER_ID = "user_id";
    public static final String COLUMN_ITINERARIES_NAME = "name";
    public static final String COLUMN_ITINERARIES_START_DATE = "start_date";
    public static final String COLUMN_ITINERARIES_END_DATE = "end_date";

    // Itinerary Items table
    public static final String TABLE_ITINERARY_ITEMS = "itinerary_items";
    public static final String COLUMN_ITINERARY_ITEMS_ID = "id";
    public static final String COLUMN_ITINERARY_ITEMS_ITINERARY_ID = "itinerary_id";
    public static final String COLUMN_ITINERARY_ITEMS_ATTRACTION_ID = "attraction_id";
    public static final String COLUMN_ITINERARY_ITEMS_VISIT_DATE = "visit_date";
    public static final String COLUMN_ITINERARY_ITEMS_VISIT_TIME = "visit_time";
    public static final String COLUMN_ITINERARY_ITEMS_NOTES = "notes";

    // Posts table
    public static final String TABLE_POSTS = "posts";
    public static final String COLUMN_POSTS_ID = "id";
    public static final String COLUMN_POSTS_USER_ID = "user_id";
    public static final String COLUMN_POSTS_CONTENT = "content";
    public static final String COLUMN_POSTS_IMAGE_URL = "image_url";
    public static final String COLUMN_POSTS_CREATED_AT = "created_at";

    // region CREATE TABLE statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERS_USERNAME + " TEXT NOT NULL UNIQUE,"
            + COLUMN_USERS_PASSWORD + " TEXT NOT NULL"
            + ")";

    private static final String CREATE_TABLE_ATTRACTIONS = "CREATE TABLE " + TABLE_ATTRACTIONS + "("
            + COLUMN_ATTRACTIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ATTRACTIONS_NAME + " TEXT NOT NULL,"
            + COLUMN_ATTRACTIONS_DESCRIPTION + " TEXT,"
            + COLUMN_ATTRACTIONS_IMAGE_URL + " TEXT,"
            + COLUMN_ATTRACTIONS_LOCATION + " TEXT,"
            + COLUMN_ATTRACTIONS_PRICE + " REAL"
            + ")";

    private static final String CREATE_TABLE_BOOKINGS = "CREATE TABLE " + TABLE_BOOKINGS + "("
            + COLUMN_BOOKINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_BOOKINGS_USER_ID + " INTEGER,"
            + COLUMN_BOOKINGS_ATTRACTION_ID + " INTEGER,"
            + COLUMN_BOOKINGS_DATE + " TEXT,"
            + COLUMN_BOOKINGS_STATUS + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_BOOKINGS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USERS_ID + "),"
            + "FOREIGN KEY(" + COLUMN_BOOKINGS_ATTRACTION_ID + ") REFERENCES " + TABLE_ATTRACTIONS + "(" + COLUMN_ATTRACTIONS_ID + ")"
            + ")";

    private static final String CREATE_TABLE_ITINERARIES = "CREATE TABLE " + TABLE_ITINERARIES + "("
            + COLUMN_ITINERARIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ITINERARIES_USER_ID + " INTEGER,"
            + COLUMN_ITINERARIES_NAME + " TEXT NOT NULL,"
            + COLUMN_ITINERARIES_START_DATE + " TEXT,"
            + COLUMN_ITINERARIES_END_DATE + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_ITINERARIES_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USERS_ID + ")"
            + ")";

    private static final String CREATE_TABLE_ITINERARY_ITEMS = "CREATE TABLE " + TABLE_ITINERARY_ITEMS + "("
            + COLUMN_ITINERARY_ITEMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ITINERARY_ITEMS_ITINERARY_ID + " INTEGER,"
            + COLUMN_ITINERARY_ITEMS_ATTRACTION_ID + " INTEGER,"
            + COLUMN_ITINERARY_ITEMS_VISIT_DATE + " TEXT,"
            + COLUMN_ITINERARY_ITEMS_VISIT_TIME + " TEXT,"
            + COLUMN_ITINERARY_ITEMS_NOTES + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_ITINERARY_ITEMS_ITINERARY_ID + ") REFERENCES " + TABLE_ITINERARIES + "(" + COLUMN_ITINERARIES_ID + "),"
            + "FOREIGN KEY(" + COLUMN_ITINERARY_ITEMS_ATTRACTION_ID + ") REFERENCES " + TABLE_ATTRACTIONS + "(" + COLUMN_ATTRACTIONS_ID + ")"
            + ")";

    private static final String CREATE_TABLE_POSTS = "CREATE TABLE " + TABLE_POSTS + "("
            + COLUMN_POSTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_POSTS_USER_ID + " INTEGER,"
            + COLUMN_POSTS_CONTENT + " TEXT,"
            + COLUMN_POSTS_IMAGE_URL + " TEXT,"
            + COLUMN_POSTS_CREATED_AT + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_POSTS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USERS_ID + ")"
            + ")";
    // endregion

    public OpenHelperDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating database tables...");
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_ATTRACTIONS);
        db.execSQL(CREATE_TABLE_BOOKINGS);
        db.execSQL(CREATE_TABLE_ITINERARIES);
        db.execSQL(CREATE_TABLE_ITINERARY_ITEMS);
        db.execSQL(CREATE_TABLE_POSTS);
        Log.i(TAG, "Database tables created.");
        insertMockData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        // Drop tables in reverse order of creation to avoid foreign key constraints issues
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINERARY_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINERARIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTRACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void insertMockData(SQLiteDatabase db) {
        Log.i(TAG, "Inserting mock data...");

        // Users
        ContentValues userValues1 = new ContentValues();
        userValues1.put(COLUMN_USERS_USERNAME, "1");
        userValues1.put(COLUMN_USERS_PASSWORD, "1");
        long userId1 = db.insert(TABLE_USERS, null, userValues1);

        ContentValues userValues2 = new ContentValues();
        userValues2.put(COLUMN_USERS_USERNAME, "2");
        userValues2.put(COLUMN_USERS_PASSWORD, "2");
        long userId2 = db.insert(TABLE_USERS, null, userValues2);

        // Attractions
        ContentValues attractionValues1 = new ContentValues();
        attractionValues1.put(COLUMN_ATTRACTIONS_NAME, "故宫");
        attractionValues1.put(COLUMN_ATTRACTIONS_DESCRIPTION, "北京故宫是中国明清两代的皇家宫殿，旧称紫禁城，位于北京中轴线的中心。");
        attractionValues1.put(COLUMN_ATTRACTIONS_IMAGE_URL, "beijing_gugong.jpg");
        attractionValues1.put(COLUMN_ATTRACTIONS_LOCATION, "北京市东城区景山前街4号");
        attractionValues1.put(COLUMN_ATTRACTIONS_PRICE, 60.0);
        db.insert(TABLE_ATTRACTIONS, null, attractionValues1);

        ContentValues attractionValues2 = new ContentValues();
        attractionValues2.put(COLUMN_ATTRACTIONS_NAME, "外滩");
        attractionValues2.put(COLUMN_ATTRACTIONS_DESCRIPTION, "上海外滩地处黄浦江畔，是上海的标志性景点之一，全长约1.5公里。");
        attractionValues2.put(COLUMN_ATTRACTIONS_IMAGE_URL, "shanghai_waitan.jpg");
        attractionValues2.put(COLUMN_ATTRACTIONS_LOCATION, "上海市黄浦区中山东一路");
        attractionValues2.put(COLUMN_ATTRACTIONS_PRICE, 0.0);
        db.insert(TABLE_ATTRACTIONS, null, attractionValues2);

        ContentValues attractionValues3 = new ContentValues();
        attractionValues3.put(COLUMN_ATTRACTIONS_NAME, "西湖");
        attractionValues3.put(COLUMN_ATTRACTIONS_DESCRIPTION, "杭州西湖以其秀丽的湖光山色和众多的名胜古迹而闻名中外，被誉为人间天堂。");
        attractionValues3.put(COLUMN_ATTRACTIONS_IMAGE_URL, "hangzhou_xihu.jpg");
        attractionValues3.put(COLUMN_ATTRACTIONS_LOCATION, "浙江省杭州市西湖区");
        attractionValues3.put(COLUMN_ATTRACTIONS_PRICE, 0.0);
        db.insert(TABLE_ATTRACTIONS, null, attractionValues3);

        // Posts
        ContentValues postValues1 = new ContentValues();
        postValues1.put(COLUMN_POSTS_USER_ID, userId1);
        postValues1.put(COLUMN_POSTS_CONTENT, "今天去了故宫，太宏伟了！");
        postValues1.put(COLUMN_POSTS_IMAGE_URL, "post_1.jpg");
        postValues1.put(COLUMN_POSTS_CREATED_AT, "2023-10-27 14:30:00");
        db.insert(TABLE_POSTS, null, postValues1);

        ContentValues postValues2 = new ContentValues();
        postValues2.put(COLUMN_POSTS_USER_ID, userId2);
        postValues2.put(COLUMN_POSTS_CONTENT, "夜游外滩，灯火辉煌，美不胜收。");
        postValues2.put(COLUMN_POSTS_IMAGE_URL, "post_2.jpg");
        postValues2.put(COLUMN_POSTS_CREATED_AT, "2023-10-28 20:00:00");
        db.insert(TABLE_POSTS, null, postValues2);

        Log.i(TAG, "Mock data inserted.");
    }

    // region User Methods
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERS_USERNAME, username);
        values.put(COLUMN_USERS_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public User loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, null,
                COLUMN_USERS_USERNAME + "=? AND " + COLUMN_USERS_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USERS_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERS_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERS_PASSWORD)));
        }
        cursor.close();
        db.close();
        return user;
    }
    // endregion

    // region Attraction Methods
    public long addAttraction(Attraction attraction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ATTRACTIONS_NAME, attraction.getName());
        values.put(COLUMN_ATTRACTIONS_DESCRIPTION, attraction.getDescription());
        values.put(COLUMN_ATTRACTIONS_IMAGE_URL, attraction.getImageUrl());
        values.put(COLUMN_ATTRACTIONS_LOCATION, attraction.getLocation());
        values.put(COLUMN_ATTRACTIONS_PRICE, attraction.getPrice());
        long id = db.insert(TABLE_ATTRACTIONS, null, values);
        db.close();
        return id;
    }

    public List<Attraction> getAllAttractions() {
        List<Attraction> attractions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ATTRACTIONS, null);
        if (cursor.moveToFirst()) {
            do {
                attractions.add(cursorToAttraction(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return attractions;
    }

    private Attraction cursorToAttraction(Cursor cursor) {
        Attraction attraction = new Attraction();
        attraction.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ATTRACTIONS_ID)));
        attraction.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTRACTIONS_NAME)));
        attraction.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTRACTIONS_DESCRIPTION)));
        attraction.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTRACTIONS_IMAGE_URL)));
        attraction.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTRACTIONS_LOCATION)));
        attraction.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ATTRACTIONS_PRICE)));
        return attraction;
    }

    public Attraction getAttractionById(long attractionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Attraction attraction = null;
        Cursor cursor = db.query(TABLE_ATTRACTIONS, null, COLUMN_ATTRACTIONS_ID + " = ?",
                new String[]{String.valueOf(attractionId)}, null, null, null);
        if (cursor.moveToFirst()) {
            attraction = cursorToAttraction(cursor);
        }
        cursor.close();
        db.close();
        return attraction;
    }
    // endregion

    // region Post Methods
    public long addPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POSTS_USER_ID, post.getUserId());
        values.put(COLUMN_POSTS_CONTENT, post.getContent());
        values.put(COLUMN_POSTS_IMAGE_URL, post.getImageUrl());
        values.put(COLUMN_POSTS_CREATED_AT, post.getCreatedAt());
        long id = db.insert(TABLE_POSTS, null, values);
        db.close();
        return id;
    }

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_POSTS + " ORDER BY " + COLUMN_POSTS_CREATED_AT + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                posts.add(cursorToPost(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return posts;
    }
    
     public int updatePost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POSTS_CONTENT, post.getContent());
        values.put(COLUMN_POSTS_IMAGE_URL, post.getImageUrl());
        int rows = db.update(TABLE_POSTS, values, COLUMN_POSTS_ID + " = ?",
                new String[]{String.valueOf(post.getId())});
        db.close();
        return rows;
    }

    public void deletePost(long postId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POSTS, COLUMN_POSTS_ID + " = ?",
                new String[]{String.valueOf(postId)});
        db.close();
    }


    private Post cursorToPost(Cursor cursor) {
        Post post = new Post();
        post.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_POSTS_ID)));
        post.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_POSTS_USER_ID)));
        post.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTS_CONTENT)));
        post.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTS_IMAGE_URL)));
        post.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTS_CREATED_AT)));
        return post;
    }
    // endregion
    
    // region Itinerary Methods
    public long addItinerary(Itinerary itinerary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITINERARIES_USER_ID, itinerary.getUserId());
        values.put(COLUMN_ITINERARIES_NAME, itinerary.getName());
        values.put(COLUMN_ITINERARIES_START_DATE, itinerary.getStartDate());
        values.put(COLUMN_ITINERARIES_END_DATE, itinerary.getEndDate());
        long id = db.insert(TABLE_ITINERARIES, null, values);
        db.close();
        return id;
    }

    public List<Itinerary> getItinerariesForUser(long userId) {
        List<Itinerary> itineraries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITINERARIES, null, COLUMN_ITINERARIES_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null, COLUMN_ITINERARIES_ID + " DESC");
        if (cursor.moveToFirst()) {
            do {
                itineraries.add(cursorToItinerary(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itineraries;
    }

    public int updateItinerary(Itinerary itinerary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITINERARIES_NAME, itinerary.getName());
        values.put(COLUMN_ITINERARIES_START_DATE, itinerary.getStartDate());
        values.put(COLUMN_ITINERARIES_END_DATE, itinerary.getEndDate());
        int rows = db.update(TABLE_ITINERARIES, values, COLUMN_ITINERARIES_ID + " = ?",
                new String[]{String.valueOf(itinerary.getId())});
        db.close();
        return rows;
    }

    public void deleteItinerary(long itineraryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Also delete associated itinerary items
        db.delete(TABLE_ITINERARY_ITEMS, COLUMN_ITINERARY_ITEMS_ITINERARY_ID + " = ?",
                new String[]{String.valueOf(itineraryId)});
        db.delete(TABLE_ITINERARIES, COLUMN_ITINERARIES_ID + " = ?",
                new String[]{String.valueOf(itineraryId)});
        db.close();
    }

    private Itinerary cursorToItinerary(Cursor cursor) {
        Itinerary itinerary = new Itinerary();
        itinerary.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITINERARIES_ID)));
        itinerary.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITINERARIES_USER_ID)));
        itinerary.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITINERARIES_NAME)));
        itinerary.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITINERARIES_START_DATE)));
        itinerary.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITINERARIES_END_DATE)));
        return itinerary;
    }

    public Itinerary getItineraryById(long itineraryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Itinerary itinerary = null;
        Cursor cursor = db.query(TABLE_ITINERARIES, null, COLUMN_ITINERARIES_ID + " = ?",
                new String[]{String.valueOf(itineraryId)}, null, null, null);
        if (cursor.moveToFirst()) {
            itinerary = cursorToItinerary(cursor);
        }
        cursor.close();
        db.close();
        return itinerary;
    }
    // endregion

    // region Booking Methods
    public long addBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOKINGS_USER_ID, booking.getUserId());
        values.put(COLUMN_BOOKINGS_ATTRACTION_ID, booking.getAttractionId());
        values.put(COLUMN_BOOKINGS_DATE, booking.getBookingDate());
        values.put(COLUMN_BOOKINGS_STATUS, booking.getStatus());
        long id = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return id;
    }

    public List<Booking> getBookingsByUserId(long userId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // SQL JOIN Query to get attraction name along with booking details
        String query = "SELECT b.*, a." + COLUMN_ATTRACTIONS_NAME + " FROM " + TABLE_BOOKINGS + " b JOIN "
                + TABLE_ATTRACTIONS + " a ON b." + COLUMN_BOOKINGS_ATTRACTION_ID + " = a." + COLUMN_ATTRACTIONS_ID
                + " WHERE b." + COLUMN_BOOKINGS_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Booking booking = cursorToBooking(cursor);
                // Also get the attraction name from the joined table
                booking.setAttractionName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTRACTIONS_NAME)));
                bookings.add(booking);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookings;
    }

    public Booking getBookingById(long bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Booking booking = null;
        String query = "SELECT b.*, a." + COLUMN_ATTRACTIONS_NAME + " FROM " + TABLE_BOOKINGS + " b JOIN "
                + TABLE_ATTRACTIONS + " a ON b." + COLUMN_BOOKINGS_ATTRACTION_ID + " = a." + COLUMN_ATTRACTIONS_ID
                + " WHERE b." + COLUMN_BOOKINGS_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(bookingId)});

        if (cursor.moveToFirst()) {
            booking = cursorToBooking(cursor);
            booking.setAttractionName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTRACTIONS_NAME)));
        }
        cursor.close();
        db.close();
        return booking;
    }


    public int updateBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOKINGS_DATE, booking.getBookingDate());
        values.put(COLUMN_BOOKINGS_STATUS, booking.getStatus());
        int rows = db.update(TABLE_BOOKINGS, values, COLUMN_BOOKINGS_ID + " = ?",
                new String[]{String.valueOf(booking.getId())});
        db.close();
        return rows;
    }

    public void deleteBooking(long bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKINGS, COLUMN_BOOKINGS_ID + " = ?",
                new String[]{String.valueOf(bookingId)});
        db.close();
    }

    private Booking cursorToBooking(Cursor cursor) {
        Booking booking = new Booking();
        booking.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOKINGS_ID)));
        booking.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOKINGS_USER_ID)));
        booking.setAttractionId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOKINGS_ATTRACTION_ID)));
        booking.setBookingDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKINGS_DATE)));
        booking.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKINGS_STATUS)));
        return booking;
    }
    // endregion
} 