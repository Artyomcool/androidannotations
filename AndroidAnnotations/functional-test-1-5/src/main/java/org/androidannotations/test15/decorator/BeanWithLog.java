package org.androidannotations.test15.decorator;

import org.androidannotations.annotations.EBean;
import org.androidannotations.custom.Log;

@EBean
public class BeanWithLog {

    @Log
    void test1(String a1, int a2) {
    }

    @Log
    String test2(String a1, int a2) {
        return "abc";
    }

}
