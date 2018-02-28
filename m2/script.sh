#!/bin/bash

ssh -n <host> nohup java -jar <path>/m2-server.jar 50000 ERROR &
