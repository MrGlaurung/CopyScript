package com.filemover;

import java.io.IOException;
import java.nio.file.Paths;

public class RealPathFactory
{
    public String toRealPath(String inPath) throws IOException
    {
        return Paths.get(inPath).toRealPath().toString();
    }
}
