#!/bin/bash

####################################################################################
# DO NOT MODIFY THE BELOW ##########################################################

/etc/init.d/ssh start
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/shared_rsa

# DO NOT MODIFY THE ABOVE ##########################################################
####################################################################################

# Start HDFS/Spark worker here
$HADOOP_HOME/sbin/hadoop-daemon.sh start datanode

# Start Spark Worker and connect to Spark Master
$SPARK_HOME/sbin/start-worker.sh spark://main:7077

bash