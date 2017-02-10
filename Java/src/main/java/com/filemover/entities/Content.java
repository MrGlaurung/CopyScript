package com.filemover.entities;

import com.filemover.RealPathFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by al on 2016-01-06.
 */
public class Content
{
    private RealPathFactory realPathFactory;

    protected Path path;
    protected String errorMsg;
    protected Boolean validated;
    protected String name;
    protected String strPath;
    protected Boolean isTVSeries;

    public Content()
    {
        this.validated = false;
        this.isTVSeries = false;
    }

    public Content(RealPathFactory realPathFactory, String incomingPath)
    {
        Assert.notNull(realPathFactory);

        this.realPathFactory = realPathFactory;

        this.validated = false;
        this.isTVSeries = false;

        this.validate(incomingPath);
    }

    // Path is the programmatically easy way to get to this object.
    public Path getPath()
    {
        return this.path;
    }

    // Validated is set to true when this object is validated.
    public Boolean getValidated()
    {
        return this.validated;
    }

    // If something goes wrong the error message will reflect the error that occurred in the execution.
    public String getErrorMsg()
    {
        return this.errorMsg;
    }

    // Both directories and Files can be TV-series. The directory can contain TV-series files and the
    // file could be the actual TV-series itself.
    public Boolean isATVSeries() { return this.isTVSeries; }

    // Name is the individual name of the directory or file that will be handled.
    public String getName() { return this.name; }

    // StrPath is the full string to the directory or the file that this object represent.
    public String getStrPath() { return this.strPath + "/" + this.name; }

    // Validate will make sure the string representation of the incoming path is correct.
    // Make note that this will actually not verify that the object exists in the file system.
    // TODO: Make sure the object exists on the current file system.
    public boolean validate(String inDir)
    {
        Assert.notNull(inDir);

        try
        {
            strPath = realPathFactory.toRealPath(inDir) + "/";
        }
        catch(Exception ex)
        {
            errorMsg = "Error: content not found error (" + inDir + ").";

            return validated = false;
        }

        setUpParts(strPath);

        return validated = true;
    }

    // Split up the information we have to get the parts of the path so we can return it when
    // it's needed.
    protected void setUpParts(String strPath)
    {
        this.strPath = strPath;
        String[] pathParts = strPath.split("/");
        this.name = pathParts[pathParts.length-1];
        this.path = Paths.get(strPath);
    }

    private Boolean isThisATVSeries()
    {
        if(isTVSeries) return isTVSeries;

        return isTVSeries;
    }
}
