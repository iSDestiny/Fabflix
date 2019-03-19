# cs122b-winter19-team-71

## Members
    Jason Bugalon, 85806059, jbugallo
    Kulraj Dhaliwal, 18833491, kulrajd

## How to use the script to get averages
The log parsing script can be found in the root of the repository, it is named getAveragesScript.py. This script was created and tested with Python 3.7.2. To use it you must call it in the command line and supply it a single log file. It doesn't have to be in the same folder as the log file, as long as you give it the path to the log file, that will suffice. The script will return both the average servlet time and the average JDBC time in the format of (ts: xxxx, tj: xxxx), where xxxx is the result for each average. 

### Example Usage of Script
>python getAveragesScript.py LOG_FILE
