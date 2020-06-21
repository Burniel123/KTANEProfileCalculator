import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

/**
 * Holds all fundamental program logic relating to the KTANE Profile Calculator, such as startup.
 *
 * @author Daniel Burton
 */
public class Main
{
    /**
     * Defines operations this program is capable of completing on profiles.
     */
    public enum CalculatorMode
    {
        CREATE, UNION, INTERSECTION, DIFFERENCE, UNRECOGNISED;
    }

    private static CalculatorMode mode = null;
    private static boolean verbose = false;
    private static File profileOperandOne = null;
    private static File profileOperandTwo = null;
    private static File destinationTarget = null;

    /**
     * Main method for the KTANE Profile Calculator. Primary focus is on parsing the user's command line input.
     * @param args - list of command line arguments for the program. These may include:
     *             Flags to indicate which operations to use.
     *             References to files which should be used in the calculation.
     *             Desired name for the completed profile.
     */
    public static void main(String[] args)
    {
        if(args.length == 0)
        {//Users must supply appropriate command line arguments to use this program.
            System.err.println("Invalid syntax! You must supply information on the command line.");
            System.err.println("Please see the documentation for examples of how to use this tool.");
            //TODO: Create the documentation when appropriate and link from here.
            System.exit(-1);
        }

        flagloop:
        for(String arg : args)
        {//Establishes all present switches before processing any user input.
            if (!(arg.startsWith("-") && arg.length() > 1))
                break;

            if (arg.equals("-verbose"))
            {//The user must be able to enable a lengthy output with a verbose flag.
                verbose = true;
                continue;
            }

            if(arg.length() > 2)
            {//All non-verbose flags should only contain the - character and a letter.
                mode = CalculatorMode.UNRECOGNISED;
            }

            switch (arg.charAt(1))
            {//Assign the program's mode. Note if a user
                case 'c' : mode = CalculatorMode.CREATE;break flagloop;
                case 'u' : mode = CalculatorMode.UNION;break flagloop;
                case 'i' : mode = CalculatorMode.INTERSECTION;break flagloop;
                case 'd' : mode = CalculatorMode.DIFFERENCE;break flagloop;
                default : mode = CalculatorMode.UNRECOGNISED;break flagloop;
            }
        }

        try
        {
            parseOperands(args);

            if(mode == CalculatorMode.UNRECOGNISED)
            {//Program must terminate if unable to determine which function to perform.
                System.err.println("Invalid syntax! Unable to parse your command line instructions.");
                System.err.println("Please see the documentation for examples of how to use this tool.");
                //TODO: Create the documentation when appropriate and link from here.
                System.exit(-1);
            }
            else if(mode == CalculatorMode.CREATE)
            {
                ProfileCreator pc = new ProfileCreator(profileOperandOne, destinationTarget, false);
                pc.createProfile();
            }
            else
            {
                ProfileCalculations pc = null;
                if(profileOperandTwo != null && destinationTarget != null)
                    pc = new ProfileCalculations(profileOperandOne, profileOperandTwo, verbose, destinationTarget);
                else if(profileOperandTwo != null)
                    pc = new ProfileCalculations(profileOperandOne, profileOperandTwo, verbose);
                else if(destinationTarget != null)
                    pc = new ProfileCalculations(profileOperandOne, verbose, destinationTarget);
                else
                    pc = new ProfileCalculations(profileOperandOne, verbose);

                if(mode == CalculatorMode.UNION)
                    pc.computeUnion();
                else if(mode == CalculatorMode.INTERSECTION)
                    pc.computeIntersection();
                else
                    pc.computeDifference();
            }
        }
        catch(ArgumentException | ListFormatException | IOException | ParseException e)
        {
            System.out.println("Unable to complete operation due to following reason:");
            System.out.println(e.getMessage());
        }

    }

    /**
     * Deduces the meaning of file operands specified for operations and assigns them to the relevant variables.
     * @param args - String array of arguments entered.
     * @throws ArgumentException - in the event that an unsuitable number of arguments have been provided.
     */
    private static void parseOperands(String[] args) throws ArgumentException
    {
        int fileOperandsCount = 0;
        boolean directorySupplied = false;
        boolean unacceptableFilesSupplied = false;

        if(mode == CalculatorMode.CREATE)
        {//Create is a unary operation and thus has a different format for specifying files to use.
            for(String arg : args)
            {//A second iteration of the argument list to establish which file locations are to be used.
                if(arg.startsWith("-"))
                    continue; //Ignore all flags as they will already have been checked.

                if(fileOperandsCount == 0 && (new File(arg).isFile()))
                {//First file supplied must be an existing file in create mode.
                    fileOperandsCount++;
                    profileOperandOne = new File(arg);
                }
                else if(fileOperandsCount == 1)
                {//Second filename doesn't have to be an existing file, as it's the destination for the profile.
                    fileOperandsCount++;
                    destinationTarget = new File(arg);
                }
                else
                {
                    throw new ArgumentException("Create operation must have one operand only.");
                }

            }
        }
        else
        {//All other operations are binary and thus can be treated in similar ways initially.
            for(String arg : args)
            {//A second iteration of the argument list to establish which file locations are to be used.
                if(arg.startsWith("-"))
                    continue; //Ignore all flags as they will already have been checked.

                if(fileOperandsCount == 0 && (new File(arg).isFile()))
                {//First file supplied may or may not be a file.
                    fileOperandsCount++;
                    profileOperandOne = new File(arg);
                }
                else if(fileOperandsCount == 0 && (new File(arg).isDirectory()) && mode != CalculatorMode.DIFFERENCE)
                {//First file supplied may be a directory for all remaining modes except difference.
                    fileOperandsCount++;
                    profileOperandOne = new File(arg);
                    directorySupplied = true;
                }
                else if(fileOperandsCount == 1 && (new File(arg).isFile()) && !directorySupplied)
                {//Any second file must not be a directory.
                    fileOperandsCount++;
                    profileOperandTwo = new File(arg);
                }
                else if(fileOperandsCount == 1 && directorySupplied)
                {//If a directory was supplied, this will be the target for the profile.
                    fileOperandsCount++;
                    destinationTarget = new File(arg);
                }
                else if(fileOperandsCount == 2 && !directorySupplied)
                {//If this is the third file, it must be a target directory for a file-only operation.
                    fileOperandsCount++;
                    destinationTarget = new File(arg);
                }
                else
                {
                    throw new ArgumentException("Invalid number of files provided.");
                }

            }
        }
    }
}
