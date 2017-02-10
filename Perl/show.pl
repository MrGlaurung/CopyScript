#!/usr/bin/perl -w
##############################################################################
##############################################################################
##############################################################################
#####                                                                    #####
#####                                                                    #####
#####    Perl script to move TV-series files from download directory     #####
#####     to correct TV-Series folder and then into the correct          #####
#####                   season of that series.                           #####
#####                                                                    #####
#####            Coded by Q (code /at/ liden /dot/ cx)                   #####
#####                                                                    #####
#####                  Released under GNU license                        #####
#####                                                                    #####
#####                                                                    #####
##############################################################################
##############################################################################
##############################################################################

use Cwd;
use strict;
use File::Copy;
use File::Path;

# Initiate $ARGV[0] and [1] to make sure they are something.
$ARGV[0] //= "";
$ARGV[1] //= "";

# Set DEBUG level. 0 = normal operation, 1 = debug messages and no move
# or delete while 2 will vomit out messages (and still not move/delete).
# Careful with the "2" setting as the wall of text will probably hit
# you with a bonecrushing sound.
my $DEBUG = 0;

# Setup current working directory.
my $curDir = getcwd;

# Set up the .show.alias variable
my %dotAlias;

# First get our source and destination directories.
# The destination directory is the second argument unless we don't have a
# second argument, in that case it's 'cwd'/TV-Series/
my $destDir = $curDir . "/TV-Series/";
if($ARGV[1] ne "")
{
	$destDir = $ARGV[1];
	# Make sure there is a / sign in the end 
	if(substr($destDir, -1) ne "/") { $destDir .= "/" }
}

# The source directory is the first argument unless it's empty and then
# the source directory will be ../Download/transmission/completed/
my $sourceDir = "../Download/transmission/completed/";
if($ARGV[0] ne "")
{
	$sourceDir = $ARGV[0];
	# Make sure there is a / sign in the end 
	if(substr($sourceDir, -1) ne "/") { $sourceDir .= "/" }
}

if($DEBUG >= 1)
{
	print "Starting, source directory: " . $sourceDir . "\n";
	print "And destination directory: " . $destDir . "\n";
}

# Now to check if the user made any errors...
if( !-d $sourceDir || !-d $destDir || !-d $curDir 
	|| !-w $sourceDir || !-w $destDir )
{
	Error("Destination/Source directories isn't found or isn't writable.");
}
	
TraverseDirectory($sourceDir, 1);
if($DEBUG >= 2)
{
	print "\$ARGV[0]   : '" . $ARGV[0]   . "'\n";
	print "\$ARGV[1]   : '" . $ARGV[1]   . "'\n";
	print "\$curDir    : '" . $curDir    . "'\n";
	print "\$destDir   : '" . $destDir   . "'\n";
	print "\$sourceDir : '" . $sourceDir . "'\n";
}
print "\nAll done. Cleaning up after DFM.\n";
exit 1;

# Directory function. Called recursively to find all subdirectories.
sub TraverseDirectory
{
	# Find all in-arguments.
	my $n = scalar(@_);
	if( $n != 2 ) { Error("TraverseDirectory called inacurately!"); }
	my $lSource = $_[0]; # Which source directory are we in?
	my $topDir = $_[1]; # (bool) Is this the top source directory?

	# Fetch everything in this directory.
    opendir my $fullDirectory, $lSource or Error($!);
    my @lItemsInDirectory = readdir($fullDirectory);
    closedir $fullDirectory;

    foreach my $lOneItem ( @lItemsInDirectory )
    {
    	if( $lOneItem eq "." || $lOneItem eq ".." || substr($lOneItem, 1) eq "." ) { next; }
    	if($DEBUG >= 2) { print "===============================================================================\n"; }
 
    	my $lFullPath = $lSource . $lOneItem;
    	if($DEBUG >= 2)
    	{
    		print "lSource    : " . $lSource . "\n";
    		print "Now working: " . $lFullPath . "\n";
    	}

    	# First check if this is a directory...
    	if(-d $lFullPath )
    	{
    		if($DEBUG >= 1) { print "Dir found  : " . $lOneItem . "\n"; }

    		# So, this is a directory and we need to traverse it.
    		# This time, mark it as NOT the root (top) directory.
    		TraverseDirectory( $lFullPath . "/", 0 );
    	}
    	else
    	{
	    	my $fileEnding = (split(/\./, $lOneItem))[-1];
	    	if(!$fileEnding) { $fileEnding = ""; }
	     	if($DEBUG >= 2) { print "File ending: '" . $fileEnding . "'\n"; }
			if( ( $fileEnding eq "FLV" || $fileEnding eq "PAR2" || $fileEnding eq "asf"
				|| $fileEnding eq "avi" || $fileEnding eq "bdjo" || $fileEnding eq "bdmv"
				|| $fileEnding eq "clpi" || $fileEnding eq "crt" || $fileEnding eq "divx"
				|| $fileEnding eq "flv" || $fileEnding eq "idx" || $fileEnding eq "img"
				|| $fileEnding eq "iso" || $fileEnding eq "m2ts" || $fileEnding eq "m4v"
				|| $fileEnding eq "mkv" || $fileEnding eq "mov" || $fileEnding eq "mp3"
				|| $fileEnding eq "mp4" || $fileEnding eq "mpg" || $fileEnding eq "otf"
				|| $fileEnding eq "par2" || $fileEnding eq "pcm" || $fileEnding eq "sfv"
				|| $fileEnding eq "smi" || $fileEnding eq "swf" || $fileEnding eq "tbn" )
				&& $lOneItem !~ /.*sample.*/i )
			{
				if( $DEBUG >= 1 ) { print "File 2 move: " . $lOneItem . "\n"; }

				my $allOK = PerformFileTransfer($lFullPath, $lOneItem, $topDir);

				if($allOK)
				{
					# If this is NOT the original top/source directory then we should
					# delete the directory, otherwise we should delete the file.
                	my $catastrophicFailure = "Unable to delete.\n";
                	$catastrophicFailure .= "Topdir: " . $topDir . "\n";
                	$catastrophicFailure .= "lSource: " . $lSource . "\n";
                	$catastrophicFailure .= "lFullPath: " . $lFullPath . "\n";
                	$catastrophicFailure .= "lOneItem: " . $lOneItem . "\n";

					if(!$topDir)
					{
						# Delete the whole tree.
						if($DEBUG >= 2) { print "Now del dir: " . $lSource . "\n"; }
	                    if(!$DEBUG)
	                    {
	                    	rmtree $lSource or Error($catastrophicFailure . $!);
	                    }
					}
					else
					{
						# Delete the file.
						if($DEBUG >= 2) { print "Now delfile: " . $lFullPath . "\n"; }
						if(!$DEBUG) { MyUnlink($lFullPath) or Error($catastrophicFailure . %!); }
					}
				}
				else
				{
					if($DEBUG >= 1)
					{
						print "Didn't find the file:\n";
						print "\$lFullPath : ". $lFullPath . "\n";
						print "\$lOneItem  : ". $lOneItem . "\n";
						print "\$ltopDir   : ". $topDir . "\n";
					}
				}
			}
		}
    }

	# Everything went OK.
	return 1;
}

sub PerformFileTransfer
{
	# Find all in-arguments.
	my $n = scalar(@_);
	if( $n != 3 ) { Error("PerformFileTransfer called inacurately!"); }
	my $lFullPath = $_[0]; # Which source directory are we in?
	my $lFileName = $_[1]; # (bool) Is this the top source directory?
	my $lTopDir = $_[2]; # Are we in the top dir?
	my $lFoundSeries = 0; # (bool)
	my $lSeriesPath = ""; # The found series.

	if($DEBUG >= 2)
	{
		print "Moving full: " . $lFullPath . "\n";
		print "Now moving : " . $lFileName . "\n";
	}

	# Check if this is the top source directory and if it is then skip
	# trying to get a Series & Season from it.
	if(!$lTopDir)
	{
		# First, let's break out the directory that this file is in.
		my $lDirPart = (split(/\//, $lFullPath))[-2];
		if($DEBUG >= 2) { print "\$lDirPart  : " . $lDirPart . "\n"; }

		$lSeriesPath = FindSeries($lDirPart);
		if($lSeriesPath) { $lFoundSeries = 1; }
	}

	if(!$lFoundSeries)
	{
		# Didn't find the series from the path so let's try to
		# filename.
		$lSeriesPath = FindSeries($lFileName);
		if($lSeriesPath) { $lFoundSeries = 1; }
	}

	if($DEBUG >= 2) { print "Series Path: " . $lSeriesPath . "\n"; }

	if($lFoundSeries)
	{
		# Move the file.
		if(!$DEBUG)
		{
			move($lFullPath, $lSeriesPath) || Error("Couldn't move file.");
			print "Moved : " . $lFileName . "\n";
		}
		else { print "Moving     : " . $lFullPath . " (to) " . $lSeriesPath . "\n"; }

		return 1;
	}
	else
	{
		# This is a TV-Series, but we cannot find a destination directory.
		print "TRIED to move: " . $lFileName . ", but failed.\n";
		if($DEBUG >= 1) { print "Moving     : " . $lFullPath . " (to) " . $lSeriesPath . "\n"; }
	}

	return 0;
}

# Find a series.
sub FindSeries
{
	# Find all in-arguments.
	my $n = scalar(@_);
	if( $n != 1 ) { Error("FindSeries called inacurately!"); }
	my $lBreakString = $_[0]; # Which source directory are we in?
	my $lSeason = "";

	if($DEBUG >= 2) { print "Break Str  : " . $lBreakString . "\n"; }

    my $lSeries = (split(/.S\d\dE\d\d/i, $lBreakString))[0];
    #$seriesName =~ s/ /\\ /g; # Dont't need to \ spaces when I use perl commands to move the files.
    $lSeries =~ s/\./ /g;
    $lBreakString =~ s/\[/\\[/g;
    $lBreakString =~ s/\]/\\]/g;
    $lBreakString =~ s/^\s+|\s+$//g;
    if($DEBUG >= 1) { print "lBreakStr  : " . $lBreakString . "\n"; }
    if($lBreakString =~ m/.*S(\d)(\d)E.*/i)
    {
        if($1 eq "0" ) { $lSeason = $2; } else { $lSeason = $1 . $2; }

        if($DEBUG >= 2)
        {
        	print "Dir Series : " . $lSeries . "\n";
        	print "Dir Season : " . $lSeason . "\n";
        }
    }
    my $lFullDestination = $destDir . $lSeries . "/Season " . $lSeason . "/";
    if($DEBUG >= 2) { print "lFullDest  : " . $lFullDestination . "\n"; }

    if(-d $lFullDestination)
    {
    	return $lFullDestination;
    }
    else
    {
    	# Since we haven't found any destination directory, we'll try
    	# the alias file.
    	if($DEBUG >= 2) { print "Checking alias file.\n"; }

		if(scalar(%dotAlias) > 0)
		{
			if($DEBUG >= 2) { print "\%dotAlias is full of info. Moving on.\n"; }
		}
		elsif(-e $curDir . "/.show.alias")
		{
			if($DEBUG >= 2) { print "Found alias file, now processing.\n"; }
			%dotAlias = ReadInAlias($curDir . "/.show.alias");
		}
		else
		{
			if($DEBUG >= 1) { print "No alias file found. Stop trying.\n"; }
		}

		if(scalar(%dotAlias))
		{
			# Translate to the alias if there is one.
			if($dotAlias{$lSeries} ne "")
			{
				# Translating
			    my $lFullDestination = $destDir . $dotAlias{$lSeries} . "/Season " . $lSeason . "/";
			    if($DEBUG >= 2) { print "Translated lFullDest  : " . $lFullDestination . "\n"; }

			    if(-d $lFullDestination)
			    {
			    	return $lFullDestination;
			    }
			    else
			    {
				    if($DEBUG >= 1) { print "Translated path didn't exist.\n"; }
			    }
			}
		}
    }

    return "";
}

sub ReadInAlias
{
	# Find arguments to the function.
	my $n = scalar(@_);
	# Return error if arguments are not supplied correctly.
	if($n != 1) { return 1; }
	my $aliasFile = $_[0];
	my %allAlias;

	open(my $MYAL, $aliasFile);

	while (my $row = <$MYAL>)
	{
		chomp $row;
		(my $dest, my $source) = split(/;/, $row);
		$allAlias{$dest} = $source;
	}

	return %allAlias;
}

# I dislike that unlink don't return true/false so I built my own.
# This returns true or false. :D
sub MyUnlink
{
	# Find arguments to the function.
	my $n = scalar(@_);
	# Return error if arguments are not supplied correctly.
	if($n != 1) { return 1; }
	my $delete_target = $_[0];

	if(unlink $delete_target )
	{
	    # Delete was successful
	    return 0;
	}
	else
	{
	    # Delete failed.
	    return 1;
	}
}

# The user made some mistake. Kill him!!! Or .. perhaps just print an error message.
sub Error
{
    # Possible errorcode as argument.
    my $n = scalar(@_);

    print "show.pl Usage:\n";
    print "show.pl [source] [destination]\n\n";
    print "If [source] is unsupplied then ../Download/transmission/completed/ is used.\n";
    print "If [destination] is unsupplied then TV-Series/ is used.\n";
    if($n > 0)
    {
        # We ignore all incoming arguments over the first one. There's only supposed to be one argument.
        print "Error message: " . $_[0] . "\n";
    }
    exit 1;
}
