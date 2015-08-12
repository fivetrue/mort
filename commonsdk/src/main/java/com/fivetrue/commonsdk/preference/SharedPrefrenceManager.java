package com.fivetrue.commonsdk.preference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefrenceManager {

	private SharedPreferences pref;
	private Editor editor;
	private Context context;

	public SharedPrefrenceManager(Context context, String name) {
		this.context = context;
		pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	public Map<String, ?> loadAll() {
		return pref.getAll();
	}

	public boolean clearPref() {
		editor = pref.edit();
		editor.clear();
		return editor.commit();
	}

	public boolean remove(String key){
		editor = pref.edit();
		editor.remove(key);
		return editor.commit();
	}

	/**
	 * String 값을 저장한다.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean savePref(String key, String value) {
		editor = pref.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	/**
	 * int 값을 저장한다.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean savePref(String key, int value) {
		editor = pref.edit();
		editor.putInt(key, value);
		return editor.commit();
	}

	/**
	 * long 값을 저장한다.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean savePref(String key, long value) {
		editor = pref.edit();
		editor.putLong(key, value);
		return editor.commit();
	}

	/**
	 * boolean 값을 저장한다.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean savePref(String key, boolean value) {
		editor = pref.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}

	/**
	 * 여러개의 String 값을 저장한다.
	 * 
	 * @param hashMap
	 * @return
	 */
	public boolean savePref(HashMap<String, String> hashMap) {
		editor = pref.edit();
		Set<String> keySet = hashMap.keySet();
		Object[] keyArray = keySet.toArray();

		for (int i = 0; i < hashMap.size(); i++) {
			editor.putString(keyArray[i].toString(), hashMap.get(keyArray[i]));
		}
		return editor.commit();
	}

	/**
	 * String 값을 가져온다.
	 * 
	 * @param key
	 * @return
	 */
	public String loadStringPref(String key) {
		return pref.getString(key, "");
	}

	public String loadStringPref(String key, String defaultValue) {
		return pref.getString(key, defaultValue);
	}

	/**
	 * int 값을 가져온다.
	 * 
	 * @param key
	 * @return
	 */
	public int loadIntPref(String key) {
		return pref.getInt(key, 0);
	}

	public int loadIntPref(String key, int defaultValue) {
		return pref.getInt(key, defaultValue);
	}

	/**
	 * userPermission 전용
	 * 기본값이 -1(non login)
	 * 
	 * @param key
	 * @return
	 */
	public int loadUserPermissionPref(String key) {
		return pref.getInt(key, -1);
	}

	/**
	 * long 값을 가져온다.
	 * 
	 * @param key
	 * @return
	 */
	public long loadLongPref(String key) {
		return pref.getLong(key, (long) 0);
	}

	/**
	 * boolean 값을 가져온다.
	 * 
	 * @param key
	 * @return
	 */
	public boolean loadBoolPref(String key) {
		return pref.getBoolean(key, false);
	}

	public boolean loadBoolPref(String key, boolean defaultValue) {
		return pref.getBoolean(key, defaultValue);
	}

	/**
	 * boolean 값을 가져온다.
	 * 
	 * @param key
	 * @return
	 */
	public boolean loadUserOffLineMode(String key) {
		return pref.getBoolean(key, false);
	}

	public void saveUserOffLineMode(String key, boolean value) {
		editor = pref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void saveUserLinkVId(String key, String value){
		editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String loadUserLinkVId(String key){
		return pref.getString(key, "");
	}

	/**
	 * 여러개의 String 값을 가져온다.
	 * 
	 * @param keys
	 *            : ArrayList<키값>
	 * @return : HashMap<키값, 정보>
	 */
	public HashMap<String, String> loadStringPref(ArrayList<String> keys) {

		HashMap<String, String> returnHashMap = new HashMap<String, String>();

		for (int i = 0; i < keys.size(); i++) {
			returnHashMap.put(keys.get(i), pref.getString(keys.get(i), ""));
		}

		return returnHashMap;
	}

	/**
	 * 해당 key가 pref에 포함되어 있는지 체크
	 * 
	 * @param key
	 * @return treu : 해당 key가 존재, false : 해당 key가 존재하지 않음
	 */
	public boolean contains(String key) {
		return pref.contains(key);
	}

	public Context getContext(){
		return this.context;
	}
}
