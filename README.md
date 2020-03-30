# ProcessLif

Given a .lif image file, run EDOF on each series/channel.
### How to run it
```
java -Dconfig.file=lif.config -jar ProcessLif-assembly-0.0.1.jar
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

# Leave these commented out if you want do do the whole file
# Otherwise set them to the tiles/series you want to process
#tile.min = 0
#tile.max = 1 # non-inclusive

```
