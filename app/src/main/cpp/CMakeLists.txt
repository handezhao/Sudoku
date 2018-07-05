cmake_minimum_required(VERSION 3.4.1)

add_library( sudoku
             SHARED
             sudoku-lib.cpp )

find_library( log-lib
              log )

target_link_libraries( sudoku
                       ${log-lib} )
