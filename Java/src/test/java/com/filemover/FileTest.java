package com.filemover;

import com.filemover.entities.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileTest
{
    RealPathFactory realPathFactory = null;

    @Before
    public void setUp()
    {
        realPathFactory = mock(RealPathFactory.class);
    }

    @After
    public void tearDown()
    {
        realPathFactory = null;
    }

    @Test
    public void testWithNormalFolder() throws Exception
    {
        when(realPathFactory.toRealPath(any(String.class))).thenReturn("/some/folder");

        File dirClass = new File(realPathFactory);

        dirClass.validate("/some/folder");

        assertThat(dirClass.getPath(), is(equalTo(Paths.get("/some/"))));
        assertThat(dirClass.getValidated(), is(true));
    }

    @Test
    public void testWithNormalFolderEndingWithSlash() throws Exception
    {
        when(realPathFactory.toRealPath(any(String.class))).thenReturn("/some/folder");

        File dirClass = new File(realPathFactory);

        dirClass.validate("/some/folder/");

        assertThat(dirClass.getPath(), is(equalTo(Paths.get("/some/"))));
        assertThat(dirClass.getValidated(), is(true));
    }

    @Test
    public void testWithShortFolder() throws Exception
    {
        when(realPathFactory.toRealPath(any(String.class))).thenReturn("/some");

        File dirClass = new File(realPathFactory);

        dirClass.validate("/some");

        assertThat(dirClass.getPath(), is(equalTo(Paths.get("/"))));
        assertThat(dirClass.getValidated(), is(true));
    }

    @Test
    public void testWithRootFolder() throws Exception
    {
        when(realPathFactory.toRealPath(any(String.class))).thenReturn("/");

        File dirClass = new File(realPathFactory);

        dirClass.validate("/");

        assertThat(dirClass.getPath(), is(equalTo(Paths.get("/"))));
        assertThat(dirClass.getValidated(), is(true));
    }

    @Test
    public void testWithIOException() throws Exception
    {
        when(realPathFactory.toRealPath(any(String.class))).thenThrow(new IOException());

        File dirClass = new File(realPathFactory);

        dirClass.validate("/can/be/anything/does/not/matter");

        assertThat(dirClass.getValidated(), is(false));
    }

    @Test()
    public void testWithNullFolder() throws Exception
    {
        File dirClass = new File(realPathFactory);

        try
        {
            dirClass.validate(null);

            fail("IllegalArgumentException should have been thrown");
        }
        catch(IllegalArgumentException e)
        {
            assertThat(dirClass.getValidated(), is(false));
        }
    }
}