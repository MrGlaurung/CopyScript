package com.filemover.properties;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by al on 2015-12-27.
 */
public class FileExtentions
{
    public static final Set<String> VIDEO_EXTENSIONS =
            new HashSet<>(
                    Arrays.asList(
                            "mkv",
                            "flv",
                            "vob",
                            "ogv",
                            "ogg",
                            "drc",
                            "gif",
                            "gifv",
                            "mng",
                            "avi",
                            "mov",
                            "qt",
                            "wmv",
                            "yuv",
                            "rm",
                            "mvb",
                            "asf",
                            "mp4",
                            "m4p",
                            "m4v",
                            "mpg",
                            "mp2",
                            "mpeg",
                            "mpe",
                            "mpv",
                            "m2v",
                            "svi",
                            "3gp",
                            "3g2",
                            "mxl",
                            "roq",
                            "nsv",
                            "flv",
                            "f4v",
                            "f4p",
                            "f4p",
                            "f4a",
                            "f4b"
                    ));

    public static final Set<String> SUBTITLE_EXTENSIONS =
            new HashSet<>(
                    Arrays.asList(
                            "srt"
                    ));
}
