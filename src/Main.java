import java.io.File;

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

        CalculatorMode mode = null;
        boolean verbose = false;
        File profileOperandOne = null;
        File profileOperandTwo = null;
        File destinationTarget = null;

        flagloop:
        for(String arg : args) {//Establishes all present switches before processing any user input.
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
    }
}
