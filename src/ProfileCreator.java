import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Handles creation of a profile from a list of modules.
 * The module list may be formatted in one of two ways:
 * - A list of module codes in a .txt file, one module per line.
 * - A collection of module codes in a .txt file, in the style of those found on the Challenge Bomb spreadsheets.
 *
 * @author Daniel Burton
 */
public class ProfileCreator
{
    private File moduleList = null;
    private File targetFile = null;
    private boolean verboseOutput = false;

    /**
     * Creates a new ProfileCreator without a destination file. The created file will be dumped in the user's
     * current directory.
     * @param moduleList - .txt file containing a correctly-formatted list of modules.
     * @param verboseOutput - more process details are shown to the user if true.
     */
    public ProfileCreator(File moduleList, boolean verboseOutput)
    {
        this.moduleList = moduleList;
        this.verboseOutput = verboseOutput;
    }

    /**
     * Creates a new ProfileCreator with a destination file. The created file will be stored in this
     * destination where possible.
     * @param moduleList - .txt file containing a correctly-formatted list of modules.
     * @param targetFile - file location to store the produced profile.
     * @param verboseOutput - more process details are shown to the user if true.
     */
    public ProfileCreator(File moduleList, File targetFile, boolean verboseOutput)
    {
        this.moduleList = moduleList;
        this.targetFile = targetFile;
        this.verboseOutput = verboseOutput;
    }

    @SuppressWarnings("unchecked")
    public void createProfile() throws IOException, ListFormatException
    {
        ArrayList<String> moduleCodesToInclude = readModuleCodes();

        if(targetFile != null && !targetFile.getPath().substring(targetFile.getPath().lastIndexOf(".") + 1).equals("json"))
        {//If the user has specified a non-json file for output, throw an exception.
            throw new IOException("If a destination file for your profile is specified, it must be a JSON file.");
            //TODO: Consider a custom exception here.
        }

        if(targetFile == null)
            targetFile = new File("/MyProfile.json");

        JSONArray modulesList = new JSONArray();
        modulesList.addAll(moduleCodesToInclude);

        JSONObject profileObj = new JSONObject();
        profileObj.put("Operation", "0");
        profileObj.put("EnabledList", modulesList);

        //System.out.println(profileObj.toJSONString());
        Files.write(Paths.get(targetFile.getPath()), profileObj.toJSONString().getBytes());
    }

    /**
     * Reads a text file containing module codes to be included in the file.
     * @return an ArrayList of module codes to be included in the file.
     * @throws IOException - in the event of any errors reading the file.
     * @throws ListFormatException - if the provided text file is not formatted correctly.
     */
    private ArrayList<String> readModuleCodes() throws IOException, ListFormatException
    {
        BufferedReader listReader = new BufferedReader(new FileReader(moduleList));
        String currentLine = null;
        ArrayList<String> moduleCodes = new ArrayList<String>();

        while((currentLine = listReader.readLine()) != null)
        {//Continually reads lines of the file until
            if(!currentLine.startsWith("["))
            {//It is not checked if the module code is a legitimate module, as the mod selector manages this already.
                moduleCodes.add(currentLine);
            }
            else if(currentLine.contains("]"))
            {
                currentLine = currentLine.substring(1, currentLine.indexOf("]"));
                String[] modulesInLine = currentLine.split(",");
                moduleCodes.addAll(Arrays.asList(modulesInLine));
            }
            else
            {//If a line has a [ but no ], it has been formatted badly.
                throw new ListFormatException("Badly formatted module list file!");
            }
        }

        return moduleCodes;
    }
}
