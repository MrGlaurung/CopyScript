# Copy Script by Anders Liden, code /at/ liden.cx.
# this script is fully distributable under GNU licence.
# The idea is to be able to copy files from one
# directory to another when needed.

# Entry point for program, let's fetch arguments.
import sys
import os

# If the user does not supply any arguments, then there is no arguments (DUH!)
activeflags = ""
# Debug level 0 = normal run, 1 = few debug messages, 2 = tons.
debugmsg = 2
majorvers = "0"
minorvers = "0"
ver = "1"
filever = "36 Alpha"
version = majorvers + "." + minorvers + "." + ver + "." + filever


def main():
    print_error_msg(2, "pre run sys.argv", sys.argv)
    value = parse_args(sys.argv)
    print_error_msg(1, "post run value", value)
    activeflags = value[0]

    print_error_msg(2, "setting flags", activeflags)
    if activeflags.contains("d"): debugmsg = 2
    if activeflags.contains("v"): debugmsg = 1
    if activeflags.contains("t"): activeflags = "test"
    print_error_msg(2, "flags set to: debugmsg, " + debugmsg + " test, " + activeflags)

    # Now let's move on...
    check_directory(1, value[1], value[2])


# From directory is by default current directory
# To directory is by default ../
def parse_args(arguments):
    """
    :param arguments:
        arguments is supposed to be the input value from sys.argv
    :return:
        returns an array of strings with the following format:
        [0] flags the program have been started with.
        [1] the FROM directory from where we are supposed to copy files.
        [2] the TO directory to where the copies are going.
    """
    returns = ["", "./", "../"]
    argc = 1
    # Check if there are arguments and if the first letter of the first
    # argument is a - sign. If it is a - then the following text is flags.
    while len(arguments) > argc and arguments[argc][0] == "-":
        # Here is our flags, let's sort them out.
        returns[0] = returns[0] + arguments[argc][1:]
        argc += 1

    returns[0] = sanity_check_flags(returns[0])

    if len(arguments) > argc:
        # Ok, let's assume this is "to" directory to begin with.
        returns[2] = arguments[argc]
        argc += 1

    if len(arguments) > argc:
        # First move the "to" to "from" since it was from.
        returns[1] = returns[2]
        # Then move the new argument to one
        returns[2] = arguments[argc]

    # Make sure we have the current directory listed first in our returns.
    returns[1] = os.getcwd() + "/" + check_last_char(returns[1])
    returns[2] = os.getcwd() + "/" + check_last_char(returns[2])

    return returns


def check_directory(topDir, sourcedir, destdir):
    return


def check_file(file):
    return


def check_last_char(checkMe):
    return checkMe if checkMe.endswith("/") else checkMe + "/"


def sanity_check_flags(flags):
    """
    :param flags:
        flags is all the  flags from command line.
    :return:
        either flags if they are accepted, otherwise empty string.

    Check so that the arguments are safe, sane and correct.
    """
    # Flags:
    # d = debug, run with debug information
    # v = verbose, run with verbose information
    # t = test, run through all, but don't perform anything
    myRet = ""

    if flags == "-help":
        print_help()

    while len(flags) > 0:
        if flags[0] in "dvt" and flags[0] not in myRet:
            myRet += flags[0]
        flags = flags[1:]

    return myRet


def print_help():
    """
    Prints help information. Nothing more, nothing less.
    """

    print("CopyScript [-dvt] [from] [to]")
    print("")
    print("Copies files from the from directory to the to directory. The")
    print("files moved will be movie files and inside the destination")
    print("will be placed in the correct folder according to the filename")
    print("with season as a subfolder.")
    print("")
    print("Example: Criminal Minds.S03E17.mkv would be sorted into the")
    print("destination directory/Criminal Minds/Season 3")
    print("The spaces are important.")
    print("")
    print("Flags:")
    print("d\t\tWill run the program in full debug mode (debug level 2)")
    print("v\t\tWill run the program verbosely (debug level 1)")
    print("t\t\tMake a test run, but don't actually move anything.")
    print("Version: " + version)

    exit()


def print_error_msg(errorlvl, errorhead, errormsg):
    """
    Prints a debug message on the screen if the debug level of the script is set high enough.
    """
    errorinfo = ""

    if errorlvl <= debugmsg:
        if isinstance(errormsg, list):
            for val in errormsg:
                if errorinfo != "":
                    errorinfo += "\n"
                errorinfo += val
        elif isinstance(errormsg, str):
            errorinfo = errormsg
        print("Error, " + errorhead + ":\n" + errorinfo)


main()
