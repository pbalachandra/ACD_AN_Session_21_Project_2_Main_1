package com.acadgild.balu.imdb;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by BALU on 5/15/2016.
 */
public class Utils
{
    public static void CopyStream(InputStream inputStream, OutputStream outputStream)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count = inputStream.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                outputStream.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}
