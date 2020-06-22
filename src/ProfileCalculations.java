import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
     *
     * @param operand1      - JSON profile to be used as the first operand.
     * @param operand2      - JSON profile to be used as the second operand.
     * @param verboseOutput - more process details are shown to the user if true.
     * @param targetFile    - file location to store the produced profile.
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
     *
     * @param operand1      - JSON profile to be used as the first operand.
     * @param operand2      - JSON profile to be used as the second operand.
     * @param verboseOutput - more process details are shown to the user if true.
     */
    public ProfileCalculations(File operand1, File operand2, boolean verboseOutput)
    {
        operands = new ArrayList<File>();
        operands.add(operand1);
        operands.add(operand2);

        this.verboseOutput = verboseOutput;
        targetFile = new File("/calculated.json");
    }

    /**
     * Constructor for a calculation with a directory for operands and a provided target file.
     *
     * @param directory     - directory containing profile JSONs to be operated on.
     * @param verboseOutput - more process details are shown to the user if true.
     * @param targetFile    - file location to store the produced profile.
     */
    public ProfileCalculations(File directory, boolean verboseOutput, File targetFile) throws NullPointerException
    {
        operands = new ArrayList<File>();

        for (File child : Objects.requireNonNull(directory.listFiles())) {//Adds all immediate children of the directory provided to the operands list.
            if (child.isFile() && child.getPath().substring(targetFile.getPath().lastIndexOf(".") + 1).equals("json"))
                operands.add(child);
        }

        if (operands.isEmpty())
            throw new NullPointerException("No profiles in the provided directory.");

        this.targetFile = targetFile;
        this.verboseOutput = verboseOutput;
    }

    /**
     * Constructor for a calculation with a directory for operands and no provided target file.
     *
     * @param directory     - directory containing
     * @param verboseOutput - more process details are shown to the user if true.
     */
    public ProfileCalculations(File directory, boolean verboseOutput) throws NullPointerException
    {
        operands = new ArrayList<File>();

        for (File child : Objects.requireNonNull(directory.listFiles())) {//Adds all immediate children of the directory provided to the operands list.
            if (child.isFile() && child.getPath().substring(targetFile.getPath().lastIndexOf(".") + 1).equals("json"))
                operands.add(child);
        }

        if (operands.isEmpty())
            throw new NullPointerException("No profiles in the provided directory.");

        this.verboseOutput = verboseOutput;
        targetFile = new File("/calculated.json");
    }

    /**
     * Obtains the target file being used.
     * @return File object for the created profile's destination.
     */
    public File getTargetFile()
    {
        return targetFile;
    }

    /**
     * Creates a profile representing the union of all profiles in the operands list, writing the result to the target file.
     * @throws IOException - in the event of a standard I/O error.
     * @throws ParseException - in the event that a provided profile JSON cannot be parsed.
     */
    @SuppressWarnings("unchecked")
    public void computeUnion() throws IOException, ParseException
    {
        if(verboseOutput)
        {
            System.out.println("Calculating union of following profiles:");
            for(File op : operands)
                System.out.println(op.getPath());
        }
        JSONArray currentProfile = new JSONArray();

        for (File operand : operands)
        {
            JSONArray enabled = readProfile(operand);
            currentProfile = union(currentProfile, enabled);
            if (verboseOutput)
                System.out.println("Process completed for profile: " + operand.getPath());
        }

        writeFinalProfile(currentProfile);
    }

    /**
     * Creates a profile representing the intersection of all profiles in the operands list, writing the result to the target file.
     * @throws IOException - in the event of a standard I/O error.
     * @throws ParseException - in the event that a provided profile JSON cannot be parsed.
     */
    @SuppressWarnings("unchecked")
    public void computeIntersection() throws IOException, ParseException
    {
        JSONArray currentProfile = readProfile(operands.get(0));

        if(verboseOutput)
        {
            System.out.println("Calculating intersection of following profiles:");
            for(File op : operands)
                System.out.println(op.getPath());
        }

        for(File operand : operands)
        {
            JSONArray enabled = readProfile(operand);
            currentProfile = intersection(currentProfile, enabled);
            if (verboseOutput)
                System.out.println("Process completed for profile: " + operand.getPath());
        }

        writeFinalProfile(currentProfile);
    }

    /**
     * Creates a profile representing the difference of two profiles in the operands list, writing the result to the target file.
     * @throws IOException - in the event of a standard I/O error.
     * @throws ParseException - in the event that a provided profile JSON cannot be parsed.
     * @throws ArgumentException - in the event that the wrong number of operands are provided for this operation.
     */
    @SuppressWarnings("unchecked")
    public void computeDifference() throws IOException, ParseException, ArgumentException
    {
        if(operands.size() != 2)
            throw new ArgumentException("Difference operation requires 2 operands.");

        if(verboseOutput)
        {
            System.out.println("Identified correct number of operands.");
            System.out.println("Computing difference operation " + operands.get(0).getPath() + " - " + operands.get(1).getPath());
        }

        JSONArray profile1 = readProfile(operands.get(0));
        JSONArray profile2 = readProfile(operands.get(1));
        writeFinalProfile(difference(profile1, profile2));
    }

    /**
     * Completes a simple union of two sets of enabled modules.
     * @param profile1 - JSONArray containing the list of enabled modules from the first profile.
     * @param profile2 - JSONArray containing the list of enabled modules from the second profile.
     * @return a JSONArray containing the list of enabled modules in at least one of the provided profiles.
     */
    @SuppressWarnings("unchecked")
    private JSONArray union(JSONArray profile1, JSONArray profile2)
    {
        JSONArray result = new JSONArray();
        result.addAll(profile1);
        result.addAll(profile2);
        return result;
    }

    /**
     * Completes a simple intersection of two sets of enabled modules.
     * @param profile1 - JSONArray containing the list of enabled modules from the first profile.
     * @param profile2 - JSONArray containing the list of enabled modules from the second profile.
     * @return a JSONArray containing the list of enabled modules in both of the provided profiles.
     */
    @SuppressWarnings("unchecked")
    private JSONArray intersection(JSONArray profile1, JSONArray profile2)
    {
        JSONArray result = new JSONArray();
        result.addAll(profile1);
        profile1.removeAll(profile2);
        result.removeAll(profile1);
        return result;
    }


    /**
     * Completes a simple set difference of two sets of enabled modules.
     * @param profile1 - JSONArray containing the list of enabled modules from profile 1 (being removed FROM).
     * @param profile2 - JSONArray containing the list of enabled modules from profile 2 (being REMOVED).
     * @return a JSONArray containing the list of enabled modules in profile1 BUT NOT profile2.
     */
    @SuppressWarnings("unchecked")
    private JSONArray difference(JSONArray profile1, JSONArray profile2)
    {
        JSONArray result = new JSONArray();
        result.addAll(profile1);
        result.removeAll(profile2);
        return result;
    }

    /**
     * Writes a created profile to the target file.
     * @param enabled - JSONArray containing modules which should be in the final profiles Enabled list.
     * @throws IOException - in the event of a standard file writing error.
     */
    @SuppressWarnings("unchecked")
    private void writeFinalProfile(JSONArray enabled) throws IOException
    {
        JSONObject profileObj = new JSONObject();

        profileObj.put("EnabledList", enabled);
        profileObj.put("Operation", "0");

        Files.write(Paths.get(targetFile.getPath()), profileObj.toJSONString().getBytes());
    }

    /**
     * Reads a provided profile JSON, extracting its enabled list.
     * @param profile - a JSON profile operand.
     * @return a JSONArray containing elements in the profile's Enabled list.
     * @throws IOException - in the event of a standard file-reading error.
     * @throws ParseException - in the event that the JSON file cannot be parsed.
     */
    private JSONArray readProfile(File profile) throws IOException, ParseException
    {
        JSONParser parser = new JSONParser();
        FileReader reader = new FileReader(profile);

        JSONObject profileObject = (JSONObject)parser.parse(reader);

        return (JSONArray)profileObject.get("EnabledList");
    }
}
