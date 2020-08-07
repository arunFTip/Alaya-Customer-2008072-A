package com.ftipinfosol.alayachits.Adapters;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

public final class HttpCache {
    private HttpCache() {}

    public static void write(Context context, String key, String object){
        if(context!=null)
        {
            try {
                FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
                OutputStreamWriter oos = new OutputStreamWriter(fos);
                oos.write(object);
                oos.close();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String read(Context context, String key) throws IOException{
        FileInputStream fis = context.openFileInput(key);
        int ch;
        StringBuilder sb = new StringBuilder();
        while((ch = fis.read()) != -1) {
            sb.append((char)ch);
        }
        return String.valueOf(sb);
    }
}

