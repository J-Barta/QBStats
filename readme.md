# QBStats
### A program to compile stats from [hsquizbowl.org](www.hsquizbowl.org)

## Downloading
The latest release can be downloaded from [here](https://github.com/J-Barta/QBStats/releases). Most users will want to use the `.exe` file included in the "Assets" section of the Release. If you don't have the Java SDK downloaded already, you should be prompted to download the Java 16 JDK (which can be found [here](https://www.oracle.com/java/technologies/javase/jdk16-archive-downloads.html)).

## Usage
The first step in using the program is identifying the "set id" of the set you'd like to analyze. This is a unique ID assigned by hsquizbowl.org to each question set. The ID can be found in the url of a specific set as shown in the image below. [Here](https://hsquizbowl.org/db/questionsets/search/?season=2023&hs=1) is the link for the 2022-2023 high school set list.

![image_2022-11-25_232539406](https://user-images.githubusercontent.com/53406552/204072198-3224a5e7-85e6-4c2c-a225-e588d85596ac.png)

After entering the set ID, the program will check each tournament using that set for stat sheets posted to that set. You can select as many stat sheets as you'd like per tournament, a feature useful for tournaments that don't provide a "Combined" or "Overall" stats sheet (i.e. [this one](https://hsquizbowl.org/db/tournaments/7670/)). They will display as shown in the image below. This issue can be handled by entering multiple numbers separated by a comma. The program will automatically collect all team data from each of the sheets you enter and combine them to produce tournmant-wide statistics for each team.

![image_2022-11-25_233706774](https://user-images.githubusercontent.com/53406552/204072449-7f268413-ca94-41f9-b6ba-61cab4100f07.png)

After each tournament in the question set has been run through, a `.csv` file will be generated in `C:\Users\{user}\Desktop\QBStats` with the stats of that set.

## Manual entry
By deafult, the program should be able to identify each column of the stats sheet. However, some tournaments incorrectly identify their columns (i.e. the [CAT XVIII](https://hsquizbowl.org/db/tournaments/7637/stats/all_games/) Scottie mirror). If this occurs in such a way where the program can identify that an issue has occured (i.e. it tries to apply a "4-0" WL record to PPG), it will alert you to the issue and have you manually enter the column of each stat, as shown in the image below.

![Screenshot 2022-11-25 234413](https://user-images.githubusercontent.com/53406552/204072783-5f7298af-6986-4596-8412-b9451f45b771.png)

## Troubleshooting
Since there can potentially be issues with the automated chrome browser not closing properly, some strange behavior could result from that. Therefore, the first step in troubleshooting should be restarting the machine and trying again. If this does not resolve your issue, feel free to report and issue [here](https://github.com/J-Barta/QBStats/issues).

## Feature Requests
If you have a feature idea you would like to see included in future versions of the program, please add them [here](https://github.com/J-Barta/QBStats/issues).
