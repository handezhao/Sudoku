package sudo.hdz.com.sudoku.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import sudo.hdz.com.sudoku.App;

/**
 * Description:
 * Created by hdz on 18/05/2018.
 */

public class SFHelper {

    private SFHelper() {
    }

    private SharedPreferences.Editor editor;
    private SharedPreferences sp;


    public void initSharedPreference() {
        sp = App.app.getSharedPreferences("sudoku", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public void putSudoku(String key, int[][] value) {
        if (value == null || value.length == 0) {
            putString(key, "");
        } else {
            Gson gson = new Gson();
            String target = gson.toJson(value);
            putString(key, target);
        }
    }

    public int[][] getSudoku(String key) {
        String target = getString(key, "");
        if (TextUtils.isEmpty(target)) return null;

        Gson gson = new Gson();
        return gson.fromJson(target, int[][].class);
    }


    public static SFHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final SFHelper INSTANCE = new SFHelper();
    }

}
