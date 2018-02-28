#!/bin/bash

ssh -n <host> nohup java -jar <path>/ms2-server.jar 50000 ERROR &
