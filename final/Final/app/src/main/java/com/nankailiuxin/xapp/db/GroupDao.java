package com.nankailiuxin.xapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nankailiuxin.xapp.bean.Group;
import com.nankailiuxin.xapp.util.CommonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class GroupDao {
    private MyOpenHelper helper;
    private NoteDao noteDataDao;

    public GroupDao(Context context) {
        helper = new MyOpenHelper(context);
        noteDataDao = new NoteDao(context);
    }

    public List<Group> queryGroupAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        List<Group> groupList = new ArrayList<Group>();

        Group group ;
        Cursor cursor = null;
        try {
            cursor = db.query("db_group", null, null, null, null, null, "g_create_time asc");
            while (cursor.moveToNext()) {
                int groupId = cursor.getInt(cursor.getColumnIndex("g_id"));
                String groupName = cursor.getString(cursor.getColumnIndex("g_name"));
                int order = cursor.getInt(cursor.getColumnIndex("g_order"));
                String color = cursor.getString(cursor.getColumnIndex("g_color"));
                int encrypt = cursor.getInt(cursor.getColumnIndex("g_encrypt"));
                String createTime = cursor.getString(cursor.getColumnIndex("g_create_time"));
                String updateTime = cursor.getString(cursor.getColumnIndex("g_update_time"));

                group = new Group();
                group.setId(groupId);
                group.setName(groupName);
                group.setOrder(order);
                group.setColor(color);
                group.setIsEncrypt(encrypt);
                group.setCreateTime(createTime);
                group.setUpdateTime(updateTime);
                groupList.add(group);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return groupList;
    }

    public Group queryGroupByName(String groupName) {
        SQLiteDatabase db = helper.getWritableDatabase();

        Group group = null;
        Cursor cursor = null;
        try {
            Log.i(TAG, "###queryGroupByName: "+groupName);
            cursor = db.query("db_group", null, "g_name=?", new String[]{groupName}, null, null, null);
            while (cursor.moveToNext()) {
                int groupId = cursor.getInt(cursor.getColumnIndex("g_id"));
                int order = cursor.getInt(cursor.getColumnIndex("g_order"));
                String color = cursor.getString(cursor.getColumnIndex("g_color"));
                int encrypt = cursor.getInt(cursor.getColumnIndex("g_encrypt"));
                String createTime = cursor.getString(cursor.getColumnIndex("g_create_time"));
                String updateTime = cursor.getString(cursor.getColumnIndex("g_update_time"));

                group = new Group();
                group.setId(groupId);
                group.setName(groupName);
                group.setOrder(order);
                group.setColor(color);
                group.setIsEncrypt(encrypt);
                group.setCreateTime(createTime);
                group.setUpdateTime(updateTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return group;
    }

    public Group queryGroupById(int groupId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        Group group = null;
        Cursor cursor = null;
        try {
            cursor = db.query("db_group", null, "g_id=?", new String[]{groupId + ""}, null, null, null);
            while (cursor.moveToNext()) {
                int order = cursor.getInt(cursor.getColumnIndex("g_order"));
                String color = cursor.getString(cursor.getColumnIndex("g_color"));
                int encrypt = cursor.getInt(cursor.getColumnIndex("g_encrypt"));
                String groupName = cursor.getString(cursor.getColumnIndex("g_name"));
                String createTime = cursor.getString(cursor.getColumnIndex("g_create_time"));
                String updateTime = cursor.getString(cursor.getColumnIndex("g_update_time"));

                group = new Group();
                group.setId(groupId);
                group.setName(groupName);
                group.setOrder(order);
                group.setColor(color);
                group.setIsEncrypt(encrypt);
                group.setCreateTime(createTime);
                group.setUpdateTime(updateTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return group;
    }

    public void insertGroup(String groupName) {
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query("group", null, "g_name=?", new String[]{groupName}, null, null, null);
            if (!cursor.moveToNext()) {
                ContentValues values = new ContentValues();
                values.put("g_name", groupName);
                values.put("g_color", "#FFFFFF");
                values.put("g_encrypt", 0);
                values.put("g_create_time", CommonUtil.date2string(new Date()));
                values.put("g_update_time", CommonUtil.date2string(new Date()));
                db.insert("db_group", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    public void updateGroup(Group group) {
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("g_name", group.getName());
            values.put("g_order", group.getOrder());
            values.put("g_color", group.getColor());
            values.put("g_encrypt", group.getIsEncrypt());
            values.put("update_time", CommonUtil.date2string(new Date()));
            db.update("db_group", values, "g_id=?", new String[]{group.getId() + ""});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public int deleteGroup(int groupId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        int ret = 0;
        try {
            ret = db.delete("db_group", "g_id=?", new String[]{groupId + ""});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return ret;
    }
}
