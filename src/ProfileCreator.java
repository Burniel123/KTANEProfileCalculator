import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
    private boolean useNames = false;

    private static final String CONFIG_FILE = "https://raw.githubusercontent.com/Burniel123/MakeMyManual/master/src/main/resources/modules-config-details.txt";

    /**
     * Creates a new ProfileCreator without a destination file. The created file will be dumped in the user's
     * current directory.
     * @param moduleList - .txt file containing a correctly-formatted list of modules.
     * @param verboseOutput - more process details are shown to the user if true.
     * @param useNames - true if module names are being supplied instead of module codes.
     */
    public ProfileCreator(File moduleList, boolean verboseOutput, boolean useNames)
    {
        this.moduleList = moduleList;
        this.verboseOutput = verboseOutput;
        this.useNames = useNames;
        targetFile = new File("calculated.json");
    }

    /**
     * Creates a new ProfileCreator with a destination file. The created file will be stored in this
     * destination where possible.
     * @param moduleList - .txt file containing a correctly-formatted list of modules.
     * @param targetFile - file location to store the produced profile.
     * @param verboseOutput - more process details are shown to the user if true.
     * @param useNames - true if module names are being supplied instead of module codes.
     */
    public ProfileCreator(File moduleList, File targetFile, boolean verboseOutput, boolean useNames)
    {
        this.moduleList = moduleList;
        this.targetFile = targetFile;
        this.verboseOutput = verboseOutput;
        this.useNames = useNames;
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
     * Creates a profile from the object's list file operand.
     * @throws IOException - in the event of a standard input error.
     * @throws ListFormatException - in the event the list file is badly formatted.
     */
    @SuppressWarnings("unchecked")
    public void createProfile() throws IOException, ListFormatException
    {
        ArrayList<String> moduleCodesToInclude = new ArrayList<String>();

        if(!useNames)
            moduleCodesToInclude = readModuleCodes();
        else
            moduleCodesToInclude = readModuleCodesFromModuleNames();

        if(verboseOutput)
            System.out.println("Module list read successfully.");

        if(targetFile != null && !targetFile.getPath().substring(targetFile.getPath().lastIndexOf(".") + 1).equals("json"))
        {//If the user has specified a non-json file for output, throw an exception.
            throw new IOException("If a destination file for your profile is specified, it must be a JSON file.");
        }

        JSONArray modulesList = new JSONArray();
        modulesList.addAll(moduleCodesToInclude);

        JSONObject profileObj = new JSONObject();
        profileObj.put("Operation", "0");
        profileObj.put("EnabledList", modulesList);

        if(verboseOutput)
            System.out.println("JSON objects created successfully.");

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
                if(verboseOutput)
                    System.out.println("Identified module: " + currentLine);
            }
            else if(currentLine.contains("]"))
            {
                currentLine = currentLine.substring(1, currentLine.indexOf("]"));
                String[] modulesInLine = currentLine.split(",");
                moduleCodes.addAll(Arrays.asList(modulesInLine));
                if(verboseOutput)
                {
                    for(String code : modulesInLine)
                        System.out.println("Identified module code: " + code);
                }
            }
            else
            {//If a line has a [ but no ], it has been formatted badly.
                throw new ListFormatException("Badly formatted module list file!");
            }

        }

        moduleCodes.remove("ALL_SOLVABLE");
        moduleCodes.remove("ALL_NEEDY");

        return moduleCodes;
    }

    /**
     * Reads a file containing module names and references a data repository to produce a list of module codes for the profile.
     * @return an ArrayList of module codes.
     * @throws IOException - in the event of a standard input error.
     * @throws ListFormatException - in the event that the user's list is poorly formatted.
     */
    private ArrayList<String> readModuleCodesFromModuleNames() throws IOException, ListFormatException
    {
        ArrayList<String> moduleNames = readModuleCodes();
        HashMap<String, String> codesTable = generateModuleCodesTable();
        ArrayList<String> moduleCodes = new ArrayList<String>();

        for(String name : moduleNames)
        {
            String code = codesTable.get(name);
            if(code != null)
            {
                if(verboseOutput)
                    System.out.println("Successfully converted module name " + name + " to code " + code);
                moduleCodes.add(code);
            }
            else if(verboseOutput)
                System.out.println("Unable to find match for module name: " + name);

        }

        return moduleCodes;

    }

    /**
     * References the config file from the repository and produces a mapping from module names to module codes.
     * @return a HashMap mapping module names to codes.
     * @throws IOException - in the event of a standard input error.
     */
    private HashMap<String, String> generateModuleCodesTable() throws IOException
    {
        HashMap<String, String> codesTable = new HashMap<String, String>();
        URL url = new URL(CONFIG_FILE);
        URLConnection connection = url.openConnection();

        BufferedReader configReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String configLine = null;

        while((configLine = configReader.readLine()) != null)
        {
            String[] lineContent = configLine.split("\t");

            codesTable.put(lineContent[0], lineContent[1]);
        }

        return codesTable;
    }
}
