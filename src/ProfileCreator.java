import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
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
    private boolean useNames = false;

    private static final String JSON_URL = "https://ktane.timwi.de/json/raw";

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
     * @throws ParseException - in the event that the json source used to map module names to module codes is badly formatted.
     */
    @SuppressWarnings("unchecked")
    public void createProfile() throws IOException, ListFormatException, ParseException
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
        profileObj.put("Operation", 0);
        profileObj.put("EnabledList", modulesList);
        profileObj.put("DisabledList", new JSONArray());

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

            if(!containsRelevantChars(currentLine))
                continue;
            else if(!currentLine.startsWith("["))
            {//It is not checked if the module code is a legitimate module, as the mod selector manages this already.
                moduleCodes.add(currentLine);
                if(verboseOutput)
                    System.out.println("Identified module: " + currentLine);
            }
            else if(currentLine.contains("]"))
            {
                currentLine = currentLine.substring(1, currentLine.indexOf("]"));
                String[] modulesInLine = currentLine.split(", ");
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
     * Establishes if a line in the file actually contains relevant characters.
     * @param line - String file line to check.
     * @return true if contains useful characters, false if it's just a newline or similar.
     */
    private boolean containsRelevantChars(String line)
    {
        for(char c : line.toCharArray())
        {
            if((int) c >= 48 && (int) c <= 122)
                return true;
        }

        return false;
    }

    /**
     * Reads a file containing module names and references the KTANE manual repository to produce a list of module codes for the profile.
     * @return an ArrayList of module codes.
     * @throws IOException - in the event of a standard input error.
     * @throws ListFormatException - in the event that the user's list is poorly formatted.
     * @throws ParseException - in the event that
     */
    private ArrayList<String> readModuleCodesFromModuleNames() throws IOException, ListFormatException, ParseException
    {
        ArrayList<String> moduleNames = readModuleCodes();
        ArrayList<String> moduleCodes = new ArrayList<String>();

        JSONArray moduleInfo = obtainModuleMappings();
        boolean codeFound = false;

        for(String name : moduleNames)
        {
            codeFound = false;

            for(Object o : moduleInfo)
            {
                String code = ((String) ((JSONObject) o).get("ModuleID")).toLowerCase();

                if (((String) ((JSONObject) o).get("Name")).toLowerCase().equals(name.toLowerCase())) {
                    if (verboseOutput)
                        System.out.println("Successfully converted module name " + name + " to code " + code);
                    moduleCodes.add(code);
                    codeFound = true;
                    break;
                }
            }

            if(!codeFound)
            {
                if(verboseOutput)
                    System.out.println("Unable to find match for module name: " + name);
            }
        }

        return moduleCodes;
    }

    /**
     * Obtains the raw JSON from the KTANE manual repository and stores it in
     * @return JSONObject containing information on all modded modules known to the KTANE Manual Repo.
     * @throws IOException - in the event of a standard input error.
     * @throws ParseException - in the event that the json downloaded is improperly formatted.
     */
    @SuppressWarnings("unchecked")
    private JSONArray obtainModuleMappings() throws IOException, ParseException
    {
        URL url = new URL(JSON_URL);
        File mappingsFile = new File("codes.txt");
        InputStreamReader in = new InputStreamReader(url.openStream());
        JSONParser parser = new JSONParser();

        return (JSONArray) ((JSONObject)parser.parse(in)).get("KtaneModules");
    }
}
