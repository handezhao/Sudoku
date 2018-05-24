package sudo.hdz.com.sudoku;

import android.app.Application;

import java.io.File;

import sudo.hdz.com.sudoku.exception.ExceptionHandler;
import sudo.hdz.com.sudoku.utils.Constant;
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
        init();
    }

    private void init() {
        SFHelper.getInstance().initSharedPreference();
        createDirectory();
        ExceptionHandler.getInstance().setDirectory(Constant.PATH_EXCEPTION);
        ExceptionHandler.getInstance().start();
    }

    private void createDirectory() {
        File file = new File(Constant.APPLICATION_ROOT);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(Constant.PATH_EXCEPTION);
        if (!file.exists()) {
            file.mkdir();
        }
    }

}
