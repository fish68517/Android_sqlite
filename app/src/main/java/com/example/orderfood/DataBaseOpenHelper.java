package com.example.orderfood;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.orderfood.model.User;
import com.example.orderfood.model.Group;
import com.example.orderfood.model.Conversation;

import java.util.ArrayList;
import java.util.List;

public class DataBaseOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "chatapp.db";
    public static final int DATABASE_VERSION = 1;

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NICKNAME = "nickname";

    // Contacts table
    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_CONTACT_ID = "contact_id";
    public static final String COLUMN_FK_USER_ID = "user_id";
    public static final String COLUMN_FK_CONTACT_USER_ID = "contact_user_id";
    public static final String COLUMN_REMARK_NAME = "remark_name";

    // Messages table
    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "message_id";
    public static final String COLUMN_SENDER_ID = "sender_id";
    public static final String COLUMN_RECEIVER_ID = "receiver_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_IS_RETRACTED = "is_retracted";

    // Deleted Messages table (for "delete for me" functionality)
    public static final String TABLE_DELETED_MESSAGES = "deleted_messages";
    public static final String COLUMN_DELETED_MSG_ID = "message_id";
    public static final String COLUMN_DELETED_USER_ID = "user_id";

    // Groups table
    public static final String TABLE_GROUPS = "groups";
    public static final String COLUMN_GROUP_ID = "group_id";
    public static final String COLUMN_GROUP_NAME = "group_name";
    public static final String COLUMN_CREATOR_ID = "creator_id";
    public static final String COLUMN_GROUP_CREATED_AT = "created_at";

    // Group members table
    public static final String TABLE_GROUP_MEMBERS = "group_members";
    public static final String COLUMN_GM_GROUP_ID = "group_id";
    public static final String COLUMN_GM_USER_ID = "user_id";


    public DataBaseOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT NOT NULL UNIQUE,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_NICKNAME + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + COLUMN_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FK_USER_ID + " INTEGER,"
                + COLUMN_FK_CONTACT_USER_ID + " INTEGER,"
                + COLUMN_REMARK_NAME + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_FK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_FK_CONTACT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        // Insert dummy data
        addDummyData(db);

        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SENDER_ID + " INTEGER,"
                + COLUMN_RECEIVER_ID + " INTEGER,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_IS_RETRACTED + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + COLUMN_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);

        String CREATE_DELETED_MESSAGES_TABLE = "CREATE TABLE " + TABLE_DELETED_MESSAGES + "("
                + COLUMN_DELETED_MSG_ID + " INTEGER,"
                + COLUMN_DELETED_USER_ID + " INTEGER,"
                + "PRIMARY KEY (" + COLUMN_DELETED_MSG_ID + ", " + COLUMN_DELETED_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_DELETED_MSG_ID + ") REFERENCES " + TABLE_MESSAGES + "(" + COLUMN_MESSAGE_ID + "),"
                + "FOREIGN KEY(" + COLUMN_DELETED_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_DELETED_MESSAGES_TABLE);

        String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + "("
                + COLUMN_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_GROUP_NAME + " TEXT NOT NULL,"
                + COLUMN_CREATOR_ID + " INTEGER,"
                + COLUMN_GROUP_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_CREATOR_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_GROUPS_TABLE);

        String CREATE_GROUP_MEMBERS_TABLE = "CREATE TABLE " + TABLE_GROUP_MEMBERS + "("
                + COLUMN_GM_GROUP_ID + " INTEGER,"
                + COLUMN_GM_USER_ID + " INTEGER,"
                + "PRIMARY KEY (" + COLUMN_GM_GROUP_ID + ", " + COLUMN_GM_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_GM_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + "),"
                + "FOREIGN KEY(" + COLUMN_GM_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_GROUP_MEMBERS_TABLE);

        // Modify messages table - This should be done via migration in a real app
        // For this project, we just add it to the initial create and drop/recreate on upgrade.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        String CREATE_MESSAGES_TABLE_V2 = "CREATE TABLE " + TABLE_MESSAGES + "("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SENDER_ID + " INTEGER,"
                + COLUMN_RECEIVER_ID + " INTEGER," // Can be null for group messages
                + COLUMN_GROUP_ID + " INTEGER," // Can be null for direct messages
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_IS_RETRACTED + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + COLUMN_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + "),"
                + "CHECK ((" + COLUMN_RECEIVER_ID + " IS NOT NULL AND " + COLUMN_GROUP_ID + " IS NULL) OR "
                + "(" + COLUMN_RECEIVER_ID + " IS NULL AND " + COLUMN_GROUP_ID + " IS NOT NULL))"
                + ")";
        db.execSQL(CREATE_MESSAGES_TABLE_V2);
    }

    private void addDummyData(SQLiteDatabase db) {
        // Add users
        addUser(db, "我的测试账号", "123", "我的测试账号"); // id 1
        addUser(db, "张三", "123", "张三"); // id 2
        addUser(db, "李四", "123", "李四");     // id 3
        addUser(db, "王五", "123", "王五");   // id 4

        // Add contacts for user 'me' (id=1)
        addContact(db, 1, 2);
        addContact(db, 1, 3);
        addContact(db, 1, 4);
    }

    private long addUser(SQLiteDatabase db, String username, String password, String nickname) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_NICKNAME, nickname);
        return db.insert(TABLE_USERS, null, values);
    }

    private void addContact(SQLiteDatabase db, int userId, int contactUserId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FK_USER_ID, userId);
        values.put(COLUMN_FK_CONTACT_USER_ID, contactUserId);
        db.insert(TABLE_CONTACTS, null, values);
    }

    public void addContact(int userId, int contactUserId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FK_USER_ID, userId);
        values.put(COLUMN_FK_CONTACT_USER_ID, contactUserId);
        db.insert(TABLE_CONTACTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_MEMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELETED_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Method to get all contacts for a user
    public List<User> getContacts(int userId) {
        List<User> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u." + COLUMN_USER_ID + ", u." + COLUMN_NICKNAME + " FROM " + TABLE_USERS + " u INNER JOIN "
                + TABLE_CONTACTS + " c ON u." + COLUMN_USER_ID + " = c." + COLUMN_FK_CONTACT_USER_ID
                + " WHERE c." + COLUMN_FK_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                // It's better to create a proper User model class
                // For now, setting nickname to username field for simplicity
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)));
                contactList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // It's better to manage db connection more carefully.
        // db.close();
        return contactList;
    }

    public List<User> searchUsers(String usernameQuery, int currentUserId) {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " LIKE ? AND " + COLUMN_USER_ID + " != ?";

        Cursor cursor = db.rawQuery(query, new String[]{"%" + usernameQuery + "%", String.valueOf(currentUserId)});

        if (cursor.moveToFirst()) {
            do {
                if (!isContact(currentUserId, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)))) {
                    User user = new User();
                    user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                    user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                    user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)));
                    userList.add(user);
                }
        /*        User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)));
                userList.add(user);*/
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    public boolean isContact(int userId, int potentialContactId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_FK_USER_ID + " = ? AND " + COLUMN_FK_CONTACT_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(potentialContactId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Add a message to the database
    public void addDirectMessage(int senderId, int receiverId, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER_ID, senderId);
        values.put(COLUMN_RECEIVER_ID, receiverId);
        values.put(COLUMN_CONTENT, content);
        // Timestamp is added by default by the table
        db.insert(TABLE_MESSAGES, null, values);
    }

    public void addGroupMessage(int senderId, int groupId, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER_ID, senderId);
        values.put(COLUMN_GROUP_ID, groupId);
        values.put(COLUMN_CONTENT, content);
        db.insert(TABLE_MESSAGES, null, values);
    }

    // Get all messages between two users
    public Cursor getMessages(int userId1, int userId2) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m.* FROM " + TABLE_MESSAGES + " m"
                + " LEFT JOIN " + TABLE_DELETED_MESSAGES + " dm ON m." + COLUMN_MESSAGE_ID + " = dm." + COLUMN_DELETED_MSG_ID
                + " AND dm." + COLUMN_DELETED_USER_ID + " = ?"
                + " WHERE ((m." + COLUMN_SENDER_ID + " = ? AND m." + COLUMN_RECEIVER_ID + " = ?) OR (m." + COLUMN_SENDER_ID + " = ? AND m." + COLUMN_RECEIVER_ID + " = ?))"
                + " AND dm." + COLUMN_DELETED_MSG_ID + " IS NULL"
                + " ORDER BY m." + COLUMN_TIMESTAMP + " ASC";

        return db.rawQuery(query, new String[]{String.valueOf(userId1), String.valueOf(userId1), String.valueOf(userId2), String.valueOf(userId2), String.valueOf(userId1)});
    }

    public Cursor getGroupMessages(int groupId, int currentUserId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m.* FROM " + TABLE_MESSAGES + " m"
                + " LEFT JOIN " + TABLE_DELETED_MESSAGES + " dm ON m." + COLUMN_MESSAGE_ID + " = dm." + COLUMN_DELETED_MSG_ID
                + " AND dm." + COLUMN_DELETED_USER_ID + " = ?"
                + " WHERE m." + COLUMN_GROUP_ID + " = ?"
                + " AND dm." + COLUMN_DELETED_MSG_ID + " IS NULL"
                + " ORDER BY m." + COLUMN_TIMESTAMP + " ASC";

        return db.rawQuery(query, new String[]{String.valueOf(currentUserId), String.valueOf(groupId)});
    }

    // Retract a message for all users
    public void retractMessage(long messageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_RETRACTED, 1);
        db.update(TABLE_MESSAGES, values, COLUMN_MESSAGE_ID + " = ?", new String[]{String.valueOf(messageId)});
    }

    // Delete a message for the current user only
    public void deleteMessageForMe(long messageId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DELETED_MSG_ID, messageId);
        values.put(COLUMN_DELETED_USER_ID, userId);
        db.insert(TABLE_DELETED_MESSAGES, null, values);
    }

    public long createGroup(String groupName, int creatorId, List<Integer> memberIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues groupValues = new ContentValues();
        groupValues.put(COLUMN_GROUP_NAME, groupName);
        groupValues.put(COLUMN_CREATOR_ID, creatorId);
        long groupId = db.insert(TABLE_GROUPS, null, groupValues);

        if (groupId != -1) {
            // Add creator to the group
            addGroupMember(db, groupId, creatorId);
            // Add other members
            for (int memberId : memberIds) {
                addGroupMember(db, groupId, memberId);
            }
        }
        return groupId;
    }

    private void addGroupMember(SQLiteDatabase db, long groupId, int userId) {
        ContentValues memberValues = new ContentValues();
        memberValues.put(COLUMN_GM_GROUP_ID, groupId);
        memberValues.put(COLUMN_GM_USER_ID, userId);
        db.insert(TABLE_GROUP_MEMBERS, null, memberValues);
    }

    public List<Group> getGroupsForUser(int userId) {
        List<Group> groupList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT g.* FROM " + TABLE_GROUPS + " g INNER JOIN "
                + TABLE_GROUP_MEMBERS + " gm ON g." + COLUMN_GROUP_ID + " = gm." + COLUMN_GM_GROUP_ID
                + " WHERE gm." + COLUMN_GM_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Group group = new Group();
                group.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID)));
                group.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_NAME)));
                group.setCreatorId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CREATOR_ID)));
                group.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_CREATED_AT)));
                groupList.add(group);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return groupList;
    }

    public List<User> getGroupMembers(long groupId) {
        List<User> memberList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.* FROM " + TABLE_USERS + " u INNER JOIN "
                + TABLE_GROUP_MEMBERS + " gm ON u." + COLUMN_USER_ID + " = gm." + COLUMN_GM_USER_ID
                + " WHERE gm." + COLUMN_GM_GROUP_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)));
                memberList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return memberList;
    }

    public Group getGroupDetails(long groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GROUPS, null, COLUMN_GROUP_ID + " = ?",
                new String[]{String.valueOf(groupId)}, null, null, null);
        Group group = null;
        if (cursor.moveToFirst()) {
            group = new Group();
            group.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID)));
            group.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_NAME)));
            group.setCreatorId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CREATOR_ID)));
            group.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_CREATED_AT)));
        }
        cursor.close();
        return group;
    }

    public User getUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)));
        }
        cursor.close();
        return user;
    }

    public void deleteContact(int userId, int contactId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Start a transaction to ensure both operations succeed or fail together
        db.beginTransaction();
        try {
            // 1. Delete the contact relationship
            db.delete(TABLE_CONTACTS, COLUMN_FK_USER_ID + " = ? AND " + COLUMN_FK_CONTACT_USER_ID + " = ?", new String[]{String.valueOf(userId), String.valueOf(contactId)});
            db.delete(TABLE_CONTACTS, COLUMN_FK_USER_ID + " = ? AND " + COLUMN_FK_CONTACT_USER_ID + " = ?", new String[]{String.valueOf(contactId), String.valueOf(userId)});


            // 2. Delete all direct messages between the two users
            String whereClause = "((" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?) OR (" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?)) AND " + COLUMN_GROUP_ID + " IS NULL";
            db.delete(TABLE_MESSAGES, whereClause, new String[]{String.valueOf(userId), String.valueOf(contactId), String.valueOf(contactId), String.valueOf(userId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateContactRemark(int userId, int contactId, String remark) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REMARK_NAME, remark);
        db.update(TABLE_CONTACTS, values, COLUMN_FK_USER_ID + " = ? AND " + COLUMN_FK_CONTACT_USER_ID + " = ?", new String[]{String.valueOf(userId), String.valueOf(contactId)});
    }

    public void updateGroupName(long groupId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, newName);
        db.update(TABLE_GROUPS, values, COLUMN_GROUP_ID + " = ?", new String[]{String.valueOf(groupId)});
    }

    public void removeGroupMember(long groupId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUP_MEMBERS, COLUMN_GM_GROUP_ID + " = ? AND " + COLUMN_GM_USER_ID + " = ?", new String[]{String.valueOf(groupId), String.valueOf(userId)});
    }

    public void deleteGroup(long groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // First, delete all members of the group
        db.delete(TABLE_GROUP_MEMBERS, COLUMN_GM_GROUP_ID + " = ?", new String[]{String.valueOf(groupId)});
        // Optional: Delete all messages for the group
        // db.delete(TABLE_MESSAGES, COLUMN_GROUP_ID + " = ?", new String[]{String.valueOf(groupId)});
        // Then, delete the group itself
        db.delete(TABLE_GROUPS, COLUMN_GROUP_ID + " = ?", new String[]{String.valueOf(groupId)});
    }

    public void addGroupMembers(long groupId, List<Integer> memberIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int memberId : memberIds) {
            addGroupMember(db, groupId, memberId);
        }
    }

    public List<User> getContactsNotInGroup(int userId, long groupId) {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.* FROM " + TABLE_USERS + " u INNER JOIN " + TABLE_CONTACTS + " c ON u." + COLUMN_USER_ID + " = c." + COLUMN_FK_CONTACT_USER_ID
                + " WHERE c." + COLUMN_FK_USER_ID + " = ? AND u." + COLUMN_USER_ID + " NOT IN (SELECT " + COLUMN_GM_USER_ID + " FROM "
                + TABLE_GROUP_MEMBERS + " WHERE " + COLUMN_GM_GROUP_ID + " = ?)";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    public List<Conversation> getConversations(int userId) {
        List<Conversation> conversations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. Get Direct Chats
        String directPartnersQuery = "SELECT DISTINCT " + COLUMN_SENDER_ID + " FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_RECEIVER_ID + " = " + userId + " AND " + COLUMN_GROUP_ID + " IS NULL UNION " +
                "SELECT DISTINCT " + COLUMN_RECEIVER_ID + " FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_SENDER_ID + " = " + userId + " AND " + COLUMN_GROUP_ID + " IS NULL";

        Cursor partnersCursor = db.rawQuery(directPartnersQuery, null);
        List<Integer> partnerIds = new ArrayList<>();
        if (partnersCursor.moveToFirst()) {
            do {
                partnerIds.add(partnersCursor.getInt(0));
            } while (partnersCursor.moveToNext());
        }
        partnersCursor.close();

        for (int partnerId : partnerIds) {
            User partner = getUser(partnerId);
            if (partner == null) continue;

            Cursor lastMsgCursor = db.rawQuery("SELECT " + COLUMN_CONTENT + ", " + COLUMN_TIMESTAMP + " FROM " + TABLE_MESSAGES + " WHERE ((" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?) OR (" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?)) AND " + COLUMN_IS_RETRACTED + " = 0 ORDER BY " + COLUMN_TIMESTAMP + " DESC LIMIT 1", new String[]{String.valueOf(userId), String.valueOf(partnerId), String.valueOf(partnerId), String.valueOf(userId)});

            if (lastMsgCursor.moveToFirst()) {
                Conversation conv = new Conversation();
                conv.setId(partnerId);
                conv.setName(partner.getNickname());
                conv.setLastMessage(lastMsgCursor.getString(0));
                conv.setTimestamp(lastMsgCursor.getString(1));
                conv.setGroup(false);
                conversations.add(conv);
            }
            lastMsgCursor.close();
        }

        // 2. Get Group Chats
        List<Group> groups = getGroupsForUser(userId);
        for (Group group : groups) {
            Cursor lastMsgCursor = db.rawQuery("SELECT " + COLUMN_CONTENT + ", " + COLUMN_TIMESTAMP + " FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_GROUP_ID + " = ? AND " + COLUMN_IS_RETRACTED + " = 0 ORDER BY " + COLUMN_TIMESTAMP + " DESC LIMIT 1", new String[]{String.valueOf(group.getId())});
            if (lastMsgCursor.moveToFirst()) {
                Conversation conv = new Conversation();
                conv.setId(group.getId());
                conv.setName(group.getName());
                conv.setLastMessage(lastMsgCursor.getString(0));
                conv.setTimestamp(lastMsgCursor.getString(1));
                conv.setGroup(true);
                conversations.add(conv);
            }
            lastMsgCursor.close();
        }

        // 3. Sort by timestamp
        conversations.sort((c1, c2) -> c2.getTimestamp().compareTo(c1.getTimestamp()));

        return conversations;
    }

    public User loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_NICKNAME},
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)));
        }
        cursor.close();
        return user;
    }

    public boolean userExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + " = ?", new String[]{username},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public long registerUser(String username, String password) {
        if (userExists(username)) {
            return -1; // 代表用户已存在
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_NICKNAME, username); // 默认使用用户名作为昵称
        return db.insert(TABLE_USERS, null, values);
    }

    public int updateUserNickname(int userId, String newNickname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NICKNAME, newNickname);
        return db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public Cursor searchAllMessages(int userId, String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT "
                + "m." + COLUMN_MESSAGE_ID + ", "
                + "m." + COLUMN_CONTENT + ", "
                + "m." + COLUMN_TIMESTAMP + ", "
                + "m." + COLUMN_SENDER_ID + ", "
                + "us." + COLUMN_NICKNAME + " AS sender_nickname, "
                + "COALESCE(ug." + COLUMN_NICKNAME + ", g." + COLUMN_GROUP_NAME + ") AS conversation_name, "
                + "m." + COLUMN_RECEIVER_ID + ", " // For direct messages
                + "m." + COLUMN_GROUP_ID + " "     // For group messages
                + "FROM " + TABLE_MESSAGES + " m "
                + "INNER JOIN " + TABLE_USERS + " us ON m." + COLUMN_SENDER_ID + " = us." + COLUMN_USER_ID + " "
                // Join for direct message conversation name
                + "LEFT JOIN " + TABLE_USERS + " ug ON (m." + COLUMN_RECEIVER_ID + " = ug." + COLUMN_USER_ID + " AND m." + COLUMN_SENDER_ID + " = " + userId + ") OR (m." + COLUMN_SENDER_ID + " = ug." + COLUMN_USER_ID + " AND m." + COLUMN_RECEIVER_ID + " = " + userId + " AND m." + COLUMN_SENDER_ID + " != " + userId + ") "
                // Join for group message conversation name
                + "LEFT JOIN " + TABLE_GROUPS + " g ON m." + COLUMN_GROUP_ID + " = g." + COLUMN_GROUP_ID + " "
                // Ensure user is part of the conversation
                + "WHERE (m." + COLUMN_RECEIVER_ID + " = " + userId + " OR m." + COLUMN_SENDER_ID + " = " + userId + " OR m." + COLUMN_GROUP_ID + " IN (SELECT " + COLUMN_GM_GROUP_ID + " FROM " + TABLE_GROUP_MEMBERS + " WHERE " + COLUMN_GM_USER_ID + " = " + userId + ")) "
                + "AND m." + COLUMN_CONTENT + " LIKE ? "
                + "AND m." + COLUMN_IS_RETRACTED + " = 0 "
                // Exclude messages deleted by the current user
                + "AND NOT EXISTS (SELECT 1 FROM " + TABLE_DELETED_MESSAGES + " dm WHERE dm." + COLUMN_DELETED_MSG_ID + " = m." + COLUMN_MESSAGE_ID + " AND dm." + COLUMN_DELETED_USER_ID + " = " + userId + ") "
                + "ORDER BY m." + COLUMN_TIMESTAMP + " DESC";

        return db.rawQuery(searchQuery, new String[]{"%" + query + "%"});
    }
} 