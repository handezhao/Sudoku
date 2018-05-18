package sudo.hdz.com.sudoku;

import android.app.Application;

import sudo.hdz.com.sudoku.utils.SFHelper;

/**
 * Description:
 * Created by hdz on 18/05/2018.
 */

public class App extends Application {

    public static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        SFHelper.getInstance().initSharedPreference();
    }

}
