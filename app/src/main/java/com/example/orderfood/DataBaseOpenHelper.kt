package com.example.orderfood

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.orderfood.model.Conversation
import com.example.orderfood.model.Group
import com.example.orderfood.model.User

class DataBaseOpenHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "chatapp.db"
        const val DATABASE_VERSION = 1

        // Users table
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_NICKNAME = "nickname"

        // Contacts table
        const val TABLE_CONTACTS = "contacts"
        const val COLUMN_CONTACT_ID = "contact_id"
        const val COLUMN_FK_USER_ID = "user_id"
        const val COLUMN_FK_CONTACT_USER_ID = "contact_user_id"
        const val COLUMN_REMARK_NAME = "remark_name"

        // Messages table
        const val TABLE_MESSAGES = "messages"
        const val COLUMN_MESSAGE_ID = "message_id"
        const val COLUMN_SENDER_ID = "sender_id"
        const val COLUMN_RECEIVER_ID = "receiver_id"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_IS_RETRACTED = "is_retracted"

        // Deleted Messages table (for "delete for me" functionality)
        const val TABLE_DELETED_MESSAGES = "deleted_messages"
        const val COLUMN_DELETED_MSG_ID = "message_id"
        const val COLUMN_DELETED_USER_ID = "user_id"

        // Groups table
        const val TABLE_GROUPS = "groups"
        const val COLUMN_GROUP_ID = "group_id"
        const val COLUMN_GROUP_NAME = "group_name"
        const val COLUMN_CREATOR_ID = "creator_id"
        const val COLUMN_GROUP_CREATED_AT = "created_at"

        // Group members table
        const val TABLE_GROUP_MEMBERS = "group_members"
        const val COLUMN_GM_GROUP_ID = "group_id"
        const val COLUMN_GM_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_USERS_TABLE = "CREATE TABLE $TABLE_USERS(" +
                "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT NOT NULL UNIQUE," +
                "$COLUMN_PASSWORD TEXT NOT NULL," +
                "$COLUMN_NICKNAME TEXT)"
        db.execSQL(CREATE_USERS_TABLE)

        val CREATE_CONTACTS_TABLE = "CREATE TABLE $TABLE_CONTACTS(" +
                "$COLUMN_CONTACT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_FK_USER_ID INTEGER," +
                "$COLUMN_FK_CONTACT_USER_ID INTEGER," +
                "$COLUMN_REMARK_NAME TEXT," +
                "FOREIGN KEY($COLUMN_FK_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)," +
                "FOREIGN KEY($COLUMN_FK_CONTACT_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID))"
        db.execSQL(CREATE_CONTACTS_TABLE)

        addDummyData(db)

        val CREATE_DELETED_MESSAGES_TABLE = "CREATE TABLE $TABLE_DELETED_MESSAGES(" +
                "$COLUMN_DELETED_MSG_ID INTEGER," +
                "$COLUMN_DELETED_USER_ID INTEGER," +
                "PRIMARY KEY ($COLUMN_DELETED_MSG_ID, $COLUMN_DELETED_USER_ID)," +
                "FOREIGN KEY($COLUMN_DELETED_MSG_ID) REFERENCES $TABLE_MESSAGES($COLUMN_MESSAGE_ID)," +
                "FOREIGN KEY($COLUMN_DELETED_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID))"
        db.execSQL(CREATE_DELETED_MESSAGES_TABLE)

        val CREATE_GROUPS_TABLE = "CREATE TABLE $TABLE_GROUPS(" +
                "$COLUMN_GROUP_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_GROUP_NAME TEXT NOT NULL," +
                "$COLUMN_CREATOR_ID INTEGER," +
                "$COLUMN_GROUP_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY($COLUMN_CREATOR_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID))"
        db.execSQL(CREATE_GROUPS_TABLE)

        val CREATE_GROUP_MEMBERS_TABLE = "CREATE TABLE $TABLE_GROUP_MEMBERS(" +
                "$COLUMN_GM_GROUP_ID INTEGER," +
                "$COLUMN_GM_USER_ID INTEGER," +
                "PRIMARY KEY ($COLUMN_GM_GROUP_ID, $COLUMN_GM_USER_ID)," +
                "FOREIGN KEY($COLUMN_GM_GROUP_ID) REFERENCES $TABLE_GROUPS($COLUMN_GROUP_ID)," +
                "FOREIGN KEY($COLUMN_GM_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID))"
        db.execSQL(CREATE_GROUP_MEMBERS_TABLE)

        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        val CREATE_MESSAGES_TABLE_V2 = "CREATE TABLE $TABLE_MESSAGES(" +
                "$COLUMN_MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_SENDER_ID INTEGER," +
                "$COLUMN_RECEIVER_ID INTEGER," +
                "$COLUMN_GROUP_ID INTEGER," +
                "$COLUMN_CONTENT TEXT," +
                "$COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "$COLUMN_IS_RETRACTED INTEGER DEFAULT 0," +
                "FOREIGN KEY($COLUMN_SENDER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)," +
                "FOREIGN KEY($COLUMN_RECEIVER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)," +
                "FOREIGN KEY($COLUMN_GROUP_ID) REFERENCES $TABLE_GROUPS($COLUMN_GROUP_ID)," +
                "CHECK (($COLUMN_RECEIVER_ID IS NOT NULL AND $COLUMN_GROUP_ID IS NULL) OR " +
                "($COLUMN_RECEIVER_ID IS NULL AND $COLUMN_GROUP_ID IS NOT NULL)))"
        db.execSQL(CREATE_MESSAGES_TABLE_V2)
    }

    private fun addDummyData(db: SQLiteDatabase) {
        addUser(db, "Test", "Test", "Test")
        addUser(db, "小明", "123", "小明")
        addUser(db, "小红", "123", "小红")
        addUser(db, "小王", "123", "小王")

        addContact(db, 1, 2)
        addContact(db, 1, 3)
        addContact(db, 1, 4)
    }

    private fun addUser(db: SQLiteDatabase, username: String, password: String, nickname: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_NICKNAME, nickname)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    private fun addContact(db: SQLiteDatabase, userId: Int, contactUserId: Int) {
        val values = ContentValues().apply {
            put(COLUMN_FK_USER_ID, userId)
            put(COLUMN_FK_CONTACT_USER_ID, contactUserId)
        }
        db.insert(TABLE_CONTACTS, null, values)
    }

    fun addContact(userId: Int, contactUserId: Int) {
        val db = this.writableDatabase
        addContact(db, userId, contactUserId)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GROUP_MEMBERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DELETED_MESSAGES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GROUPS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun getContacts(userId: Int): List<User> {
        val contactList = mutableListOf<User>()
        val db = this.readableDatabase
        val query = "SELECT u.$COLUMN_USER_ID, u.$COLUMN_NICKNAME FROM $TABLE_USERS u INNER JOIN " +
                "$TABLE_CONTACTS c ON u.$COLUMN_USER_ID = c.$COLUMN_FK_CONTACT_USER_ID " +
                "WHERE c.$COLUMN_FK_USER_ID = ?"

        db.rawQuery(query, arrayOf(userId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val user = User(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
                    )
                    contactList.add(user)
                } while (cursor.moveToNext())
            }
        }
        return contactList
    }

    fun searchUsers(usernameQuery: String, currentUserId: Int): List<User> {
        val userList = mutableListOf<User>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME LIKE ? AND $COLUMN_USER_ID != ?"

        db.rawQuery(query, arrayOf("%$usernameQuery%", currentUserId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
                    if (!isContact(currentUserId, userId)) {
                        val user = User(
                            id = userId,
                            username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                            nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
                        )
                        userList.add(user)
                    }
                } while (cursor.moveToNext())
            }
        }
        return userList
    }

    private fun isContact(userId: Int, potentialContactId: Int): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_CONTACTS WHERE $COLUMN_FK_USER_ID = ? AND $COLUMN_FK_CONTACT_USER_ID = ?"
        db.rawQuery(query, arrayOf(userId.toString(), potentialContactId.toString())).use { cursor ->
            return cursor.count > 0
        }
    }
    fun addDirectMessage(senderId: Int, receiverId: Int, content: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SENDER_ID, senderId)
            put(COLUMN_RECEIVER_ID, receiverId)
            put(COLUMN_CONTENT, content)
        }
        db.insert(TABLE_MESSAGES, null, values)
    }

    fun addGroupMessage(senderId: Int, groupId: Int, content: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SENDER_ID, senderId)
            put(COLUMN_GROUP_ID, groupId)
            put(COLUMN_CONTENT, content)
        }
        db.insert(TABLE_MESSAGES, null, values)
    }

    fun getMessages(userId1: Int, userId2: Int): Cursor {
        val db = this.readableDatabase
        val query = "SELECT m.* FROM $TABLE_MESSAGES m " +
                "LEFT JOIN $TABLE_DELETED_MESSAGES dm ON m.$COLUMN_MESSAGE_ID = dm.$COLUMN_DELETED_MSG_ID AND dm.$COLUMN_DELETED_USER_ID = ? " +
                "WHERE ((m.$COLUMN_SENDER_ID = ? AND m.$COLUMN_RECEIVER_ID = ?) OR (m.$COLUMN_SENDER_ID = ? AND m.$COLUMN_RECEIVER_ID = ?)) " +
                "AND dm.$COLUMN_DELETED_MSG_ID IS NULL " +
                "ORDER BY m.$COLUMN_TIMESTAMP ASC"
        return db.rawQuery(query, arrayOf(userId1.toString(), userId1.toString(), userId2.toString(), userId2.toString(), userId1.toString()))
    }

    fun getGroupMessages(groupId: Int, currentUserId: Int): Cursor {
        val db = this.readableDatabase
        val query = "SELECT m.* FROM $TABLE_MESSAGES m " +
                "LEFT JOIN $TABLE_DELETED_MESSAGES dm ON m.$COLUMN_MESSAGE_ID = dm.$COLUMN_DELETED_MSG_ID AND dm.$COLUMN_DELETED_USER_ID = ? " +
                "WHERE m.$COLUMN_GROUP_ID = ? " +
                "AND dm.$COLUMN_DELETED_MSG_ID IS NULL " +
                "ORDER BY m.$COLUMN_TIMESTAMP ASC"
        return db.rawQuery(query, arrayOf(currentUserId.toString(), groupId.toString()))
    }

    fun retractMessage(messageId: Long) {
        val db = this.writableDatabase
        val values = ContentValues().apply { put(COLUMN_IS_RETRACTED, 1) }
        db.update(TABLE_MESSAGES, values, "$COLUMN_MESSAGE_ID = ?", arrayOf(messageId.toString()))
    }

    fun deleteMessageForMe(messageId: Long, userId: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DELETED_MSG_ID, messageId)
            put(COLUMN_DELETED_USER_ID, userId)
        }
        db.insert(TABLE_DELETED_MESSAGES, null, values)
    }

    fun createGroup(groupName: String, creatorId: Int, memberIds: List<Int>): Long {
        val db = this.writableDatabase
        val groupValues = ContentValues().apply {
            put(COLUMN_GROUP_NAME, groupName)
            put(COLUMN_CREATOR_ID, creatorId)
        }
        val groupId = db.insert(TABLE_GROUPS, null, groupValues)

        if (groupId != -1L) {
            addGroupMember(db, groupId, creatorId)
            for (memberId in memberIds) {
                addGroupMember(db, groupId, memberId)
            }
        }
        return groupId
    }

    private fun addGroupMember(db: SQLiteDatabase, groupId: Long, userId: Int) {
        val memberValues = ContentValues().apply {
            put(COLUMN_GM_GROUP_ID, groupId)
            put(COLUMN_GM_USER_ID, userId)
        }
        db.insert(TABLE_GROUP_MEMBERS, null, memberValues)
    }
    fun getGroupsForUser(userId: Int): List<Group> {
        val groupList = mutableListOf<Group>()
        val db = this.readableDatabase
        val query = "SELECT g.* FROM $TABLE_GROUPS g INNER JOIN " +
                "$TABLE_GROUP_MEMBERS gm ON g.$COLUMN_GROUP_ID = gm.$COLUMN_GM_GROUP_ID " +
                "WHERE gm.$COLUMN_GM_USER_ID = ?"

        db.rawQuery(query, arrayOf(userId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val group = Group(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_NAME)),
                        creatorId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CREATOR_ID)),
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_CREATED_AT))
                    )
                    groupList.add(group)
                } while (cursor.moveToNext())
            }
        }
        return groupList
    }
    fun getGroupMembers(groupId: Long): List<User> {
        val memberList = mutableListOf<User>()
        val db = this.readableDatabase
        val query = "SELECT u.* FROM $TABLE_USERS u INNER JOIN " +
                "$TABLE_GROUP_MEMBERS gm ON u.$COLUMN_USER_ID = gm.$COLUMN_GM_USER_ID " +
                "WHERE gm.$COLUMN_GM_GROUP_ID = ?"

        db.rawQuery(query, arrayOf(groupId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val user = User(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                        nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
                    )
                    memberList.add(user)
                } while (cursor.moveToNext())
            }
        }
        return memberList
    }

    fun getGroupDetails(groupId: Long): Group? {
        val db = this.readableDatabase
        var group: Group? = null
        db.query(TABLE_GROUPS, null, "$COLUMN_GROUP_ID = ?", arrayOf(groupId.toString()), null, null, null).use { cursor ->
            if (cursor.moveToFirst()) {
                group = Group(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_NAME)),
                    creatorId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CREATOR_ID)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_CREATED_AT))
                )
            }
        }
        return group
    }

    fun getUser(userId: Int): User? {
        val db = this.readableDatabase
        var user: User? = null
        db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_ID = ?", arrayOf(userId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                user = User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
                )
            }
        }
        return user
    }

    fun deleteContact(userId: Int, contactId: Int) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_CONTACTS, "$COLUMN_FK_USER_ID = ? AND $COLUMN_FK_CONTACT_USER_ID = ?", arrayOf(userId.toString(), contactId.toString()))
            db.delete(TABLE_CONTACTS, "$COLUMN_FK_USER_ID = ? AND $COLUMN_FK_CONTACT_USER_ID = ?", arrayOf(contactId.toString(), userId.toString()))

            val whereClause = "(($COLUMN_SENDER_ID = ? AND $COLUMN_RECEIVER_ID = ?) OR ($COLUMN_SENDER_ID = ? AND $COLUMN_RECEIVER_ID = ?)) AND $COLUMN_GROUP_ID IS NULL"
            db.delete(TABLE_MESSAGES, whereClause, arrayOf(userId.toString(), contactId.toString(), contactId.toString(), userId.toString()))

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun updateContactRemark(userId: Int, contactId: Int, remark: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply { put(COLUMN_REMARK_NAME, remark) }
        db.update(TABLE_CONTACTS, values, "$COLUMN_FK_USER_ID = ? AND $COLUMN_FK_CONTACT_USER_ID = ?", arrayOf(userId.toString(), contactId.toString()))
    }

    fun updateGroupName(groupId: Long, newName: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply { put(COLUMN_GROUP_NAME, newName) }
        db.update(TABLE_GROUPS, values, "$COLUMN_GROUP_ID = ?", arrayOf(groupId.toString()))
    }
    fun removeGroupMember(groupId: Long, userId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_GROUP_MEMBERS, "$COLUMN_GM_GROUP_ID = ? AND $COLUMN_GM_USER_ID = ?", arrayOf(groupId.toString(), userId.toString()))
    }

    fun deleteGroup(groupId: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_GROUP_MEMBERS, "$COLUMN_GM_GROUP_ID = ?", arrayOf(groupId.toString()))
        db.delete(TABLE_GROUPS, "$COLUMN_GROUP_ID = ?", arrayOf(groupId.toString()))
    }
    fun addGroupMembers(groupId: Long, memberIds: List<Int>) {
        val db = this.writableDatabase
        for (memberId in memberIds) {
            addGroupMember(db, groupId, memberId)
        }
    }
    fun getContactsNotInGroup(userId: Int, groupId: Long): List<User> {
        val userList = mutableListOf<User>()
        val db = this.readableDatabase
        val query = "SELECT u.* FROM $TABLE_USERS u INNER JOIN $TABLE_CONTACTS c ON u.$COLUMN_USER_ID = c.$COLUMN_FK_CONTACT_USER_ID " +
                "WHERE c.$COLUMN_FK_USER_ID = ? AND u.$COLUMN_USER_ID NOT IN (SELECT $COLUMN_GM_USER_ID FROM " +
                "$TABLE_GROUP_MEMBERS WHERE $COLUMN_GM_GROUP_ID = ?)"

        db.rawQuery(query, arrayOf(userId.toString(), groupId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val user = User(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                        nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
                    )
                    userList.add(user)
                } while (cursor.moveToNext())
            }
        }
        return userList
    }

    fun getConversations(userId: Int): List<Conversation> {
        val conversations = mutableListOf<Conversation>()
        val db = this.readableDatabase

        // 1. Get Direct Chats
        val directPartnersQuery = "SELECT DISTINCT $COLUMN_SENDER_ID FROM $TABLE_MESSAGES WHERE $COLUMN_RECEIVER_ID = $userId AND $COLUMN_GROUP_ID IS NULL UNION " +
                "SELECT DISTINCT $COLUMN_RECEIVER_ID FROM $TABLE_MESSAGES WHERE $COLUMN_SENDER_ID = $userId AND $COLUMN_GROUP_ID IS NULL"

        val partnerIds = mutableListOf<Int>()
        db.rawQuery(directPartnersQuery, null).use { partnersCursor ->
            if (partnersCursor.moveToFirst()) {
                do {
                    partnerIds.add(partnersCursor.getInt(0))
                } while (partnersCursor.moveToNext())
            }
        }

        for (partnerId in partnerIds) {
            val partner = getUser(partnerId) ?: continue
            db.rawQuery(
                "SELECT $COLUMN_CONTENT, $COLUMN_TIMESTAMP FROM $TABLE_MESSAGES WHERE (($COLUMN_SENDER_ID = ? AND $COLUMN_RECEIVER_ID = ?) OR ($COLUMN_SENDER_ID = ? AND $COLUMN_RECEIVER_ID = ?)) AND $COLUMN_IS_RETRACTED = 0 ORDER BY $COLUMN_TIMESTAMP DESC LIMIT 1",
                arrayOf(userId.toString(), partnerId.toString(), partnerId.toString(), userId.toString())
            ).use { lastMsgCursor ->
                if (lastMsgCursor.moveToFirst()) {
                    val conv = Conversation(
                        id = partnerId.toLong(),
                        name = partner.nickname,
                        lastMessage = lastMsgCursor.getString(0),
                        timestamp = lastMsgCursor.getString(1),
                        isGroup = false
                    )
                    conversations.add(conv)
                }
            }
        }

        // 2. Get Group Chats
        val groups = getGroupsForUser(userId)
        for (group in groups) {
            db.rawQuery("SELECT $COLUMN_CONTENT, $COLUMN_TIMESTAMP FROM $TABLE_MESSAGES WHERE $COLUMN_GROUP_ID = ? AND $COLUMN_IS_RETRACTED = 0 ORDER BY $COLUMN_TIMESTAMP DESC LIMIT 1", arrayOf(group.id.toString())).use { lastMsgCursor ->
                if (lastMsgCursor.moveToFirst()) {
                    val conv = Conversation(
                        id = group.id,
                        name = group.name,
                        lastMessage = lastMsgCursor.getString(0),
                        timestamp = lastMsgCursor.getString(1),
                        isGroup = true
                    )
                    conversations.add(conv)
                }
            }
        }

        // 3. Sort by timestamp
        conversations.sortByDescending { it.timestamp }

        return conversations
    }

    fun loginUser(username: String, password: String): User? {
        val db = this.readableDatabase
        var user: User? = null
        db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_NICKNAME),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                user = User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
                )
            }
        }
        return user
    }

    fun userExists(username: String): Boolean {
        val db = this.readableDatabase
        db.query(TABLE_USERS, arrayOf(COLUMN_USER_ID), "$COLUMN_USERNAME = ?", arrayOf(username), null, null, null).use { cursor ->
            return cursor.count > 0
        }
    }

    fun registerUser(username: String, password: String): Long {
        if (userExists(username)) {
            return -1L // User already exists
        }
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_NICKNAME, username) // Default nickname is username
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun updateUserNickname(userId: Int, newNickname: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply { put(COLUMN_NICKNAME, newNickname) }
        return db.update(TABLE_USERS, values, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }
    fun searchAllMessages(userId: Int, query: String?): Cursor {
        val db = this.readableDatabase
        val searchQuery = "SELECT " +
                "m.$COLUMN_MESSAGE_ID, " +
                "m.$COLUMN_CONTENT, " +
                "m.$COLUMN_TIMESTAMP, " +
                "m.$COLUMN_SENDER_ID, " +
                "us.$COLUMN_NICKNAME AS sender_nickname, " +
                "COALESCE(ug.$COLUMN_NICKNAME, g.$COLUMN_GROUP_NAME) AS conversation_name, " +
                "m.$COLUMN_RECEIVER_ID, " +
                "m.$COLUMN_GROUP_ID " +
                "FROM $TABLE_MESSAGES m " +
                "INNER JOIN $TABLE_USERS us ON m.$COLUMN_SENDER_ID = us.$COLUMN_USER_ID " +
                "LEFT JOIN $TABLE_USERS ug ON (m.$COLUMN_RECEIVER_ID = ug.$COLUMN_USER_ID AND m.$COLUMN_SENDER_ID = $userId) OR (m.$COLUMN_SENDER_ID = ug.$COLUMN_USER_ID AND m.$COLUMN_RECEIVER_ID = $userId AND m.$COLUMN_SENDER_ID != $userId) " +
                "LEFT JOIN $TABLE_GROUPS g ON m.$COLUMN_GROUP_ID = g.$COLUMN_GROUP_ID " +
                "WHERE (m.$COLUMN_RECEIVER_ID = $userId OR m.$COLUMN_SENDER_ID = $userId OR m.$COLUMN_GROUP_ID IN (SELECT $COLUMN_GM_GROUP_ID FROM $TABLE_GROUP_MEMBERS WHERE $COLUMN_GM_USER_ID = $userId)) " +
                "AND m.$COLUMN_CONTENT LIKE ? " +
                "AND m.$COLUMN_IS_RETRACTED = 0 " +
                "AND NOT EXISTS (SELECT 1 FROM $TABLE_DELETED_MESSAGES dm WHERE dm.$COLUMN_DELETED_MSG_ID = m.$COLUMN_MESSAGE_ID AND dm.$COLUMN_DELETED_USER_ID = $userId) " +
                "ORDER BY m.$COLUMN_TIMESTAMP DESC"

        return db.rawQuery(searchQuery, arrayOf("%$query%"))
    }

    fun updateUsername(userId: Int, userName: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, userName)
        }
        db.update(TABLE_USERS, values, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }
} 