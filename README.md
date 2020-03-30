# ProcessLif

Given a .lif image file, run EDOF on each series/channel.
### How to run it
```
java -Dconfig.file=lif.config -jar ProcessLif-assembly-0.0.1.jar
```
If you're happy with the default config shown below
(all series and those channels with thresholds) then you can use
-D options to set the input/output configs
```
java -DinputFile="F:\\path\to\input.lif" -DoutputDirectory="F:\\path\to\output" -DidCode=3470LC -jar ProcessLif-assembly-0.0.1.jar
```
To run a bunch of them, you can put a series of the above lines in a text .bat file and double-click it
```
java -DinputFile="F:\\path\to\input.lif" -DoutputDirectory="F:\\path\to\output" -DidCode=3470LC -jar ProcessLif-assembly-0.0.1.jar
java -DinputFile="F:\\path\to\input2.lif" -DoutputDirectory="F:\\path\to\output2" -DidCode=MLAD -jar ProcessLif-assembly-0.0.1.jar
...
```

### Sample config file
```
inputFile = "/Users/Britt/lifFiles/bigOne.lif"
outputDirectory = "/tmp/moreOutput"
idCode = "3470LC"

channels = [
 { number = 0, scale = [100, 2000] },
 { number = 1, scale = [100, 1000] },
 { number = 3, scale = [100, 2000] }
]
```
