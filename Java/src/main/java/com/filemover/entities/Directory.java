package com.filemover.entities;

import com.filemover.RealPathFactory;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by al on 2016-01-05.
 */
public class Directory extends Content
{
    private Set<Content> content;
    private Boolean isSource;

    // The constructor of the directory is creating a Set which will contain all of the content in the directory
    // but we'll make sure that the content is only video files, subtitle files or other directories.
    // We'll also have to make sure that the directory is a TV-series so it's relevant. The check if it's
    // a TV-series cannot be executed if this is the source directory.
    public Directory(RealPathFactory realPathFactory, String incomingPath, Boolean isSource)
    {
        super(realPathFactory, incomingPath);
        this.isSource = isSource;

        this.content = new HashSet<>();

        // Now we start this show.

        // Prio 1 - if this is source then start checking subdirectories. Actually, wait with this to later.
        // Prio 2 - check if this is a TV-series directory.
        if(!this.isSource) this.isThisATVSeries();
        // Prio 3 - find all content of this directory and add the content to the "content" HashSet.
        // Prio 4 - find files in this directory and go through them. Find subdirectories if there
        //          are any and go through each subdirectory to find everything that needs to be
        //          transfered.
        // Prio 5 - Move all files that is found in this directory and the subdirectories and
        //          shut down this leaf.
    }

    public Directory(RealPathFactory realPathFactory, String incomingPath)
    {
        this(realPathFactory, incomingPath, false);
    }

    private Boolean tvSeriesCheck(java.io.File fileToCheck)
    {
        // Now we know that what we have is a video file. Now check if the shows name
        // is located in TV-Series (destination directory).
        if(!findTVSeries()) return false;

        // Now I know everything about the file and the destination folder, now
        // we reset a lot of parameters in this object to reflect that this is
        // the file that we actually want to move.
        this.path = Paths.get(fileToCheck.getPath());

        setUpParts(this.path.toString());

        return true;
    }

    private Boolean findTVSeries()
    {
        // Here we need to find out the actual show name.
        // That is most likely the directories name where the file is located.
        return true;
    }
}
