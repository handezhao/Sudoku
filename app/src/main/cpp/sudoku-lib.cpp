#include <jni.h>
#include <string>
#include "com_github_handezhao_sudoku_UseNative.h"
#include <android/log.h>

#ifndef  LOG_TAG
#define  LOG_TAG    "sudoku_jni"
#define  LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#endif

JNIEXPORT jobjectArray JNICALL
Java_com_github_handezhao_sudoku_UseNative_createNewTwoDimensionalArray
        (JNIEnv *env, jobject, jint x, jint y) {
    jobjectArray ret;
    int i = 0;
    int j = 0;
    jclass intArrCls = env->FindClass("[I");
    if (NULL == intArrCls)
        return NULL;

    ret = env->NewObjectArray(x, intArrCls, NULL);

    jint tmp[1024];
    for (i = 0; i < x; i++) {
        jintArray intArr = env->NewIntArray(y);

        for (j = 0; j < y; j++) {
            tmp[j] = j;
        }

        env->SetIntArrayRegion(intArr, 0, y, tmp);
        env->SetObjectArrayElement(ret, i, intArr);
        env->DeleteLocalRef(intArr);
    }
    return ret;
}

//by https://blog.csdn.net/nibiewuxuanze/article/details/47679927
enum {
    COL_MASK_NONE = 0x000, // 0 0000 0000
    COL_MASK_1 = 0x001,    // 0 0000 0001
    COL_MASK_2 = 0x002,    // 0 0000 0010
    COL_MASK_3 = 0x004,    // 0 0000 0100
    COL_MASK_4 = 0x008,    // 0 0000 1000
    COL_MASK_5 = 0x010,    // 0 0001 0000
    COL_MASK_6 = 0x020,    // 0 0010 0000
    COL_MASK_7 = 0x040,    // 0 0100 0000
    COL_MASK_8 = 0x080,    // 0 1000 0000
    COL_MASK_9 = 0x100,    // 1 0000 0000
    COL_MASK_123 = 0x007,  // 0 0000 0111
    COL_MASK_456 = 0x038,  // 0 0011 1000
    COL_MASK_789 = 0x1C0,  // 1 1100 0000
    COL_MASK_ALL = 0x1FF   // 1 1111 1111
};

typedef struct {
    int base_col; // start col 0~8:valid, -1:invalid
    int curr_col; // current col 0~8:valid, -1:invalid
    unsigned int col_mask;
} col_index_t;

typedef struct {
    int long_row; // long_row 0~80:valid, -1:init, 81:finish
    col_index_t col_index[81]; // col_index[row]
} sodoku_t;

void init_sodoku(sodoku_t *sodoku) {
    do {
        if (sodoku == NULL) { break; }
        memset(sodoku, 0, sizeof(sodoku_t));
        sodoku->long_row = -1; // init invalid long_row
    } while (false);
}

bool sodoku_not_finish(sodoku_t *sodoku) {
    bool not_finish = false;
    do {
        if (sodoku == NULL) { break; }
        if (sodoku->long_row <= 80) {
            not_finish = true;
        }
    } while (false);
    return not_finish;
}

int calc_rand_col() {
    int col;
    col = rand() % 9; // col 0~8
    return col;
}

void calc_col_mask_row(sodoku_t *sodoku, int long_row) {
    int row;
    unsigned int mask = COL_MASK_NONE;

    // clear cell that already fill by other digit(in the same row)
    for (row = long_row % 9; row < long_row; row += 9) {
        mask |= sodoku->col_index[row].col_mask;
    }

    // clear cell that already fill by same digit(in the same col)
    for (row = long_row - long_row % 9; row < long_row; row++) {
        mask |= sodoku->col_index[row].col_mask;
    }

    // clear cell that already fill by same digit(in the same 3x3box)
    for (row = long_row - long_row % 3; row < long_row; row++) {
        if (sodoku->col_index[row].col_mask & COL_MASK_123)
            mask |= COL_MASK_123;
        else if (sodoku->col_index[row].col_mask & COL_MASK_456)
            mask |= COL_MASK_456;
        else // if(sodoku->col_index[row].col_mask & COL_MASK_789)
            mask |= COL_MASK_789;
    }

    sodoku->col_index[long_row].col_mask = ~mask; // store cols that can place
}

void goto_next_row(sodoku_t *sodoku) {
    do {
        if (sodoku == NULL) {
            break;
        }

        sodoku->long_row++;
        if (sodoku->long_row > 80) {
            break;
        }

        /* init col index */
        sodoku->col_index[sodoku->long_row].base_col = calc_rand_col(); // random col
        sodoku->col_index[sodoku->long_row].curr_col = -1; // invalid col

        /* init col mask */
        calc_col_mask_row(sodoku, sodoku->long_row);

    } while (false);
}

void rollback_row(sodoku_t *sodoku) {
    do {
        if (sodoku == NULL) {
            break;
        }

        sodoku->long_row--;

        /* init col mask */
        calc_col_mask_row(sodoku, sodoku->long_row);

    } while (false);
}

int calc_next_col(int col) {
    return (col + 1) % 9; // col 012345678 => 123456780
}

bool goto_next_col(sodoku_t *sodoku) {
    bool success = false;
    int base_col;
    int curr_col;
    int next_col;
    bool found = false;

    do {
        if (sodoku == NULL) {
            break;
        }

        base_col = sodoku->col_index[sodoku->long_row].base_col;
        curr_col = sodoku->col_index[sodoku->long_row].curr_col;

        found = false;

        if (curr_col == -1) {
            // find first
            next_col = base_col;

            // base_col is valid
            if (sodoku->col_index[sodoku->long_row].col_mask & (1 << base_col)) {
                found = true;

                next_col = base_col;
            } else {
                next_col = calc_next_col(base_col);
            }
        } else {
            next_col = calc_next_col(curr_col);
        }

        if (!found) {
            // find next
            while (next_col != base_col) {
                // next_col is valid
                if (sodoku->col_index[sodoku->long_row].col_mask & (1 << next_col)) {
                    found = true;
                    break;
                }

                // find next
                next_col = calc_next_col(next_col);
            }
        }

        if (!found) {
            break;
        }

        // set next_col
        sodoku->col_index[sodoku->long_row].curr_col = next_col;
        sodoku->col_index[sodoku->long_row].col_mask = (1 << next_col); // store selected col

        success = true;
    } while (false);

    return success;
}


void itoa(int i, char *string) {
    int power = 0, j = 0;

    j = i;
    for (power = 1; j > 10; j /= 10) {
        power *= 10;
    }

    for (; power > 0; power /= 10) {
        *string++ = '0' + i / power;
        i %= power;
    }
    *string = '\0';
    printf("%s\n", string);
}

void print_sodoku(sodoku_t *sodoku) {
    int long_row;
    int row;
    int col;
    int digit;
    int cell[81] = {0};
    do {
        if (sodoku == NULL) {
            break;
        }

        for (long_row = 0; long_row < sodoku->long_row; long_row++) {
            row = long_row % 9;
            col = sodoku->col_index[long_row].curr_col;
            digit = long_row / 9 + 1;

            cell[row * 9 + col] = digit;
        }

        LOGI("Sodoku %d\n", sodoku->long_row);

        for (row = 0; row < 9; row++) {
            if (row % 3 == 0) {
                LOGI("+-----+-----+-----+\n");
            }
            char s[100] = "|";
            for (col = 0; col < 9; col++) {
                char a[10] = "";
                itoa(cell[row * 9 + col], a);
                strcat(s, a);
                if (col % 3 == 2) {
                    strcat(s, "|");
                } else {
                    strcat(s, " ");
                }
            }
            LOGI("%s", s);
        }
        LOGI("+-----+-----+-----+\n");
    } while (false);
}

jobjectArray return_sudoku(JNIEnv *env, sodoku_t *sodoku) {
    int long_row;
    int row;
    int col;
    int digit;
    int cell[81] = {0};
    for (long_row = 0; long_row < sodoku->long_row; long_row++) {
        row = long_row % 9;
        col = sodoku->col_index[long_row].curr_col;
        digit = long_row / 9 + 1;

        cell[row * 9 + col] = digit;
    }

    jobjectArray ret;
    int i = 0;
    int j = 0;
    jclass intArrCls = env->FindClass("[I");
    if (NULL == intArrCls) {
        return NULL;
    }

    ret = env->NewObjectArray(9, intArrCls, NULL);

    jint tmp[9];
    for (i = 0; i < 9; i++) {
        jintArray intArr = env->NewIntArray(9);

        for (j = 0; j < 9; j++) {
            tmp[j] = cell[j * 9 + i];
        }

        env->SetIntArrayRegion(intArr, 0, 9, tmp);
        env->SetObjectArrayElement(ret, i, intArr);
        env->DeleteLocalRef(intArr);
    }
    return ret;
}

JNIEXPORT jobjectArray JNICALL Java_com_github_handezhao_sudoku_UseNative_createNewSoduku
        (JNIEnv *env, jobject) {
    sodoku_t sodoku;

    srand((unsigned int) time((time_t *) NULL));

    init_sodoku(&sodoku);
    goto_next_row(&sodoku);

    while (sodoku_not_finish(&sodoku)) {
        if (goto_next_col(&sodoku)) {
            goto_next_row(&sodoku);
        } else {
            rollback_row(&sodoku);
        }
    }
    print_sodoku(&sodoku);

    return return_sudoku(env, &sodoku);
}