package com.acmerobotics.library.dashboard.util;

import android.content.Context;
import android.util.Log;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dalvik.system.DexFile;

/**
 * Created by ryanbrott on 8/20/17.
 */

public class ClasspathScanner {
    public static final String TAG = "ClasspathScanner";

    private DexFile dexFile;
    private ClassFilter filter;

    public ClasspathScanner(ClassFilter filter) {
        Context context = AppUtil.getInstance().getApplication();
        try {
            this.dexFile = new DexFile(context.getPackageCodePath());
        } catch (IOException e) {
            Log.w(TAG, e);
        }
        this.filter = filter;
    }

    public void scanClasspath() {
        List<String> classNames = new ArrayList<String>(Collections.list(dexFile.entries()));

        ClassLoader classLoader = ClasspathScanner.class.getClassLoader();

        for (String className : classNames) {
            if (filter.shouldProcessClass(className)) {
                try {
                    Class clazz = Class.forName(className, false, classLoader);

                    filter.processClass(clazz);
                } catch (ClassNotFoundException e) {
                    Log.w(TAG, e);
                }
            }
        }
    }
}