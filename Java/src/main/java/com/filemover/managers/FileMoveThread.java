package com.filemover.managers;

import com.filemover.entities.File;

/**
 * Created by al on 2015-12-22.
 */
public class FileMoveThread implements Runnable
{
    public Thread t;
    private File ourContent;

    public FileMoveThread(File threadContent)
    {
        ourContent = threadContent;
    }

    public void run()
    {
        try
        {
            // I now have one specific Content that is either a file or a Directory.
            // This content will now be checked and if it is a movie file then I will
            // start treating it.
            boolean isDir = false;

            // find out of this is a file or directory.
            if(ourContent.getPath().toFile().isDirectory()) isDir = true;

            // if directory find all files.
            if(isDir)
            {
            }

            // for each file send to checkup function to determin if it's a movie file.

            // if movie file check the directory name (or file name) to see if it's
            // located in the destination folder.

            // Perform move.
        }
        catch (Exception ex) { }
    }

    public void start()
    {
        if (t == null)
        {
            t = new Thread(this);
            t.start();
        }
    }
}
