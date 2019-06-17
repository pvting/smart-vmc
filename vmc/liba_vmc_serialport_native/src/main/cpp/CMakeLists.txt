cmake_minimum_required(VERSION 3.4.1)

add_library(serialport SHARED
            SerialPort.c)

# Include libraries needed for serialport lib
target_link_libraries(serialport log android)