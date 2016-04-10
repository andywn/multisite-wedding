#!/bin/bash

## Compile using ./activator universal:packageZipTarball
## Deploy, and then start using this script. 
nohup ./bin/wedding-bootstrap -J-Xms128M -J-Xmx512m -J-server -Dhttps.port=9443 &