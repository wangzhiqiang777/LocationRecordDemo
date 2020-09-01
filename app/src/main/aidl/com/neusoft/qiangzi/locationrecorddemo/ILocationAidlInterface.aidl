// ILocationAidlInterface.aidl
package com.neusoft.qiangzi.locationrecorddemo;

// Declare any non-default types here with import statements

interface ILocationAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
    Location getLocation();
    void setRecordLocationEnabled(boolean enabled);
}
