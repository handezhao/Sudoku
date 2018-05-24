package sudo.hdz.com.sudoku.exception;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
/**
 * Description:
 * Created by hdz on 24/05/2018.
 */

public class ExceptionHandler implements UncaughtExceptionHandler {

    private static final String TAG = "ExceptionHandler";

    private static final String LOG_PREFIX = "";

    private static final String LOG_SUFFIX = ".txt";

    private String directory;

    private static ExceptionHandler instance;

    private ExceptionHandler() {
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        String message = extractStackTrace(e);
        save(directory, message);
        System.exit(1);
    }

    private String extractStackTrace(Throwable throwable) {
        StringWriter sw = null;
        PrintWriter pw = null;

        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);

            throwable.printStackTrace(pw);
            return sw.toString();
        } catch (Exception e) {
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (Exception e) {
                }
            }

            if (pw != null) {
                try {
                    pw.close();
                } catch (Exception e) {
                }
            }
        }

        return null;
    }

    private void save(String directory, String result) {
        FileOutputStream fos = null;

        try {
            createDirectory(directory);

            File file = new File(directory, LOG_PREFIX + System.currentTimeMillis() + LOG_SUFFIX);
            fos = new FileOutputStream(file);
            fos.write(result.getBytes());
            fos.flush();
        } catch (Exception e) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void createDirectory(String directory) {
        try {
            File file = new File(directory);
            if (file.exists()) {
                if (file.isFile()) {
                    boolean b = file.delete();
                    Log.d(TAG, "file was deleted ? " + b);
                }
            } else {
                boolean success = file.mkdirs();
                Log.d(TAG, "directory was created ? " + success);
            }
        } catch (Exception e) {
        }
    }

    public void start() {
        Thread.setDefaultUncaughtExceptionHandler(instance);
    }

    public static ExceptionHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final ExceptionHandler INSTANCE = new ExceptionHandler();
    }
}
