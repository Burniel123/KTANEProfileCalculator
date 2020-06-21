import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Holds processes relating to various set operations the calculator is capable of.
 *
 * @author Daniel Burton
 */
public class ProfileCalculations
{
    ArrayList<File> operands = null;
    File targetFile = null;
    boolean verboseOutput = false;

    /**
     * Constructor for a calculation with two files as operands and a provided target file.
     * @param operand1 - JSON profile to be used as the first operand.
     * @param operand2 - JSON profile to be used as the second operand.
     * @param verboseOutput - more process details are shown to the user if true.
     * @param targetFile - file location to store the produced profile.
     */
    public ProfileCalculations(File operand1, File operand2, boolean verboseOutput, File targetFile)
    {
        operands = new ArrayList<File>();
        operands.add(operand1);
        operands.add(operand2);

        this.targetFile = targetFile;
        this.verboseOutput = verboseOutput;
    }

    /**
     * Constructor for a calculation with two files as operands and no provided target file.
     * @param operand1 - JSON profile to be used as the first operand.
     * @param operand2 - JSON profile to be used as the second operand.
     * @param verboseOutput - more process details are shown to the user if true.
     */
    public ProfileCalculations(File operand1, File operand2, boolean verboseOutput)
    {
        operands = new ArrayList<File>();
        operands.add(operand1);
        operands.add(operand2);

        this.verboseOutput = verboseOutput;
    }

    /**
     * Constructor for a calculation with a directory for operands and a provided target file.
     * @param directory - directory containing
     * @param verboseOutput - more process details are shown to the user if true.
     * @param targetFile - file location to store the produced profile.
     */
    public ProfileCalculations(File directory, boolean verboseOutput, File targetFile) throws NullPointerException
    {
        operands = new ArrayList<File>();

        for(File child : Objects.requireNonNull(directory.listFiles()))
        {//Adds all immediate children of the directory provided to the operands list.
            if(child.isFile() && child.getPath().substring(targetFile.getPath().lastIndexOf(".") + 1).equals("json"))
                operands.add(child);
        }

        if(operands.isEmpty())
            throw new NullPointerException("No profiles in the provided directory.");

        this.targetFile = targetFile;
        this.verboseOutput = verboseOutput;
    }

    /**
     * Constructor for a calculation with a directory for operands and no provided target file.
     * @param directory - directory containing
     * @param verboseOutput - more process details are shown to the user if true.
     */
    public ProfileCalculations(File directory, boolean verboseOutput) throws NullPointerException
    {
        operands = new ArrayList<File>();

        for(File child : Objects.requireNonNull(directory.listFiles()))
        {//Adds all immediate children of the directory provided to the operands list.
            if(child.isFile() && child.getPath().substring(targetFile.getPath().lastIndexOf(".") + 1).equals("json"))
                operands.add(child);
        }

        if(operands.isEmpty())
            throw new NullPointerException("No profiles in the provided directory.");

        this.verboseOutput = verboseOutput;
    }


}
