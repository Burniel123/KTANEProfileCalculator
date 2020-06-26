# KTANEProfileCalculator
The KTANE Profile Calculator is a command-line tool designed to create and perform set operations on mod profiles in Keep Talking & Nobody Explodes. It has the ability to create a profile for a specific challenge bomb/mission using a list of modules in that mission, and to form profiles based on combinations of other profiles.

## What does the calculator do?
The KTANE Profile Calculator supports four main operations:

*CREATE* \[From module codes\] - creates a profile from scratch using a text file containing module codes (formatted with a module code on each line, or in the format given in the [Challenge Bombs Spreadsheet](https://docs.google.com/spreadsheets/d/1yQDBEpu0dO7-CFllakfURm4NGGdQl6tN-39m6O0Q_Ow/edit#gid=305025844)).

*CREATE FROM NAME* - creates a profile from scratch using a text file containing module names, as defined by the [KTANE Manual Repository](https://ktane.timwi.de/).

*UNION* - takes multiple profiles and creates a profile consisting of all modules in at least one of the given profiles.

*INTERSECTION* - takes multiple profiles and creates a profile consisting of all modules in every given profile.

*DIFFERENCE* - takes two profiles and creates a profile consisting of all modules in one profile but not the other.

## How is this useful?
This project originated from a difficulty in training for certain missions as a team, where one or all members may need to learn some new modules in order to complete the mission. For this, it is particularly helpful to run practice bombs, which may feature a mixture of all modules in the mission or perhaps all modules in the mission that at least one person in team are not familiar with. 

Currently, the only way to create profiles designed for these focused practice sessions, or even just wanting to gradually mix a set of new modules into a profile, is to manually create and manipulate profiles. This software aims to make this process significantly more efficient.

## What is it not useful for?
This software should not be used for day-to-day management of your main expert profile. By far the best tool for that is this [profile creator](https://ktane.timwi.de/More/Profile%20Editor.html). 

It is also **not** necessary to use this software to be able to play with people with differing profiles. This functionality is contained within the mod selector as always, and this software is only helpful for more advanced profile arithmetic. For example, it may prove useful for quickly working out which modules your teammate has on their profile that you don't have on yours and producing a "training profile" to help you catch them up.

## Usage
### Pre-Requisites
The current version of the software is only known to work on Windows with Java version 12 or later installed. It may also work on other Operating Systems or with older versions of Java, but this is not verified and behaviours may vary.

In order to make any use of the profiles created with this program, you will need a copy of Keep Talking and Nobody Explodes, enough modded modules installed to make profiles worthwhile, and the ModSelector mod to manage these in-game, although all these are assumed as the exact purpose of this software will likely only be clear to people who are already very familiar with these.

### Installation
The KTANE Profile Calculator is not yet publicly released. Installation will be simple: download the zip, unzip it wherever you like and run it by double-clicking the launcher batch file. It is recommended that you copy any profiles/lists you intend on using to the program's folder before use as you will need to reference these by their path, but this is not essential.

### General Usage
All operations are completed using text-based commands. These start with flags (eg "-c"), which define which operation should be completed. These are followed by one or more file names which describe which files to complete the operation on and (optionally) where to store the resulting profile.

The structure of a command looks as follows:
`-(c|u|i|d) [-verbose] fileOrDirectory1 [fileOrDirectory2] [destinationFile] `

All commands must start with a flag which indicates which operation to perform: -c (CREATE), -u (UNION), -i (INTERSECTION) or -d (DIFFERENCE). Each are explained below. The verbose flag may optionally be added after this to print out more details to the screen about the program's progress.

### CREATE \[from codes\]
The create operation creates a profile from a .txt file containing a list of modules. This file may be structured in one of two ways:
* A list of module codes, with one code per line and no commas or other characters added to each code.
* A list of module codes as formatted on the challenge bomb spreadsheet (eg `[BlindAlleyModule] Count: 1`), with one code per line and no commas or other characters added.

All CREATE operations start with -c or -c -verbose. The next part of the command is a path to the text file containing the list of modules to use. If this is in the same directory as the program's launcher, you may simply use the file's name (including its .txt extension). If this is elsewhere you must use a [relative or absolute path](https://en.wikipedia.org/wiki/Path_(computing)) to indicate the location of the file (ie you must detail the exact folders to go through to get to the file either from the program's folder or from the operating system's home). Multiple files must not be provided for this operation and doing so will cause an error or risk the loss of one of your profiles.

You may optionally specify where the created profile .json file should be saved. This does not have to be a file that already exists, but if it does anything in the file WILL be overwritten. Again, if you want to save this in the program's folder, simply specify the desired file name (including its .json extension). If you want to save it elsewhere, you'll to specify a path. If this is left unspecified, the profile will be saved to a file in the program's folder called calculated.json, which will be overwritten every time an operation is carried out.

#### Examples:
Turn a list of module codes in text file centurion.txt into a profile called centurion.json:
`-c centurion.txt centurion.json`

Turn a list of module codes in text file modules.txt into a profile called profile.json in a new subfolder called "profiles" with progress details:
`-c -verbose modules.txt profiles/profile.json`

The created profile can then be moved into your KTANE ModProfiles folder and will appear in-game ready to be used.


### CREATE FROM NAMES
This operation allows for profiles to be created using a list of names instead of in-game module codes. This can be helpful if you do not have a list of codes, but profile compilation will take slightly longer. The list of names MUST be a simple text file, with one module name per line. 

All CREATE FROM NAME operations start with -n or -n -verbose. Users are strongly encouraged to use -verbose with this operation, as this will output full details on which module names the program has managed to find a matching code for (any names which do not exactly match a module's "official name" will not be included in the profile). Otherwise, this operation works exactly the same as the standard CREATE operation: one text file must be specified followed by an optional destination file.

#### Examples:
Turn a list of module names in text file centurion.txt into a profile called centurion.json:
`-n -verbose centurion.txt centurion.json`

The created profile can then be moved into your KTANE ModProfiles folder and will appear in-game ready to be used.

### UNION/INTERSECTION
The union operation creates a single profile which enables all modules enabled in any one of two or more provided profiles. The intersection operation creates a single profile which enabled all modules enabled in every one of two or more provided profiles.

All UNION operations start with -u or -u -verbose. All INTERSECTION operations start with -i or -i -verbose. The next part of the command indicates where to find the profiles to use. If performing a union or intersection on exactly two profiles, their file names or relative/absolute paths may both be specified in the command (including the .json extension). If performing a union or intersection on more than two profiles, add all the profiles to a single subfolder which only contains these profiles and instead include the name or path to this folder in the command.

You may optionally specify where the created profile .json file should be saved. This does not have to be a file that already exists, but if it does anything in the file WILL be overwritten. Again, if you want to save this in the program's folder, simply specify the desired file name (including its .json extension). If you want to save it elsewhere, you'll to specify a path. If this is left unspecified, the profile will be saved to a file in the program's folder called calculated.json, which will be overwritten every time an operation is carried out.

#### Examples:
Create a profile called both.json which contains all modules from either mission1.json and/or mission2.json:
`-u mission1.json mission2.json both.json`

Create a profile, with progress output, called shared.json which contains only modules which are within all profiles in my friendsProfiles folder, which I have copied to this program's folder:
`-i -verbose friendsProfiles shared.json`

The created profile can then be moved into your KTANE ModProfiles folder and will appear in-game ready to be used.

### DIFFERENCE
The difference operation takes two profiles and creates a single profile which enables all modules which are enabled in the first profile but disabled in the second profile. 

All DIFFERENCE operations start with -d or -d -verbose. The next part of the command indicates where to find the profiles to use. A difference operation requires exactly two profiles to work with and no more. These are specified one by one in the command, using either their name (if they have been copied to the program's folder) or their path. Note that order matters in this operation: the second profile will be subtracted from the first, creating a different result than if the first were subtracted from the second.

You may optionally specify where the created profile .json file should be saved. This does not have to be a file that already exists, but if it does anything in the file WILL be overwritten. Again, if you want to save this in the program's folder, simply specify the desired file name (including its .json extension). If you want to save it elsewhere, you'll to specify a path. If this is left unspecified, the profile will be saved to a file in the program's folder called calculated.json, which will be overwritten every time an operation is carried out.

#### Examples:
Create a profile called newones.json, which contains all modules which are in harderMission.json but not in mission.json:
`-d harderMission.json mission.json newones.json`

Create a profile, with progress output, called catchup.json which contains only modules which are within my friend.json profile but not in my me.json profile:
`-d -verbose friend.json me.json catchup.json`

The created profile can then be moved into your KTANE ModProfiles folder and will appear in-game ready to be used.

### Combining Operations:
The program allows for a new command to be input as soon as one has finished. This allows commands to be used one after another to create more complex profiles. For example, if I wanted a profile to practise my "core responsibilities" (modules that I can do but none of my teammates do), I could first union all my friends' profiles together and then subtract the result from my profile. If I then wanted to see which of these are on a particular mission our team is interested in, I could create a profile for that mission and then intersect it with the core responsibilities profile I made. The resulting profile would be ideal for training up on these critical modules in preparation for a run.

### Errors & Issues
If a commmand is incorrectly formatted or if invalid/too many/too few files are specified, the program will alert you that it has been unable to complete the operation and will provide a reason. In the first instance, you are encouraged to double check your command and re-read these usage notes to try and identify the issue with the command. 

If you have good reason to believe that the program is incorrectly rejecting a command, or behaving incorrectly in any other way, you are more than welcome to report this. The best way to do so is by contacting me on Discord, where you can find me using Burniel#7129 (you may also see me around on the KTANE Discord server). 
