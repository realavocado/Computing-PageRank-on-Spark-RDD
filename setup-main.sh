#!/bin/bash
export JAVA_HOME=/usr/local/openjdk-8/jre

####################################################################################
# DO NOT MODIFY THE BELOW ##########################################################

ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys

# DO NOT MODIFY THE ABOVE ##########################################################
####################################################################################

# Setup HDFS/Spark main here
# Configure core-site.xml
cat <<EOF > $HADOOP_HOME/etc/hadoop/core-site.xml
<configuration>
   <property>
      <name>fs.defaultFS</name>
      <value>hdfs://main:9000</value>
   </property>
</configuration>
EOF

# Configure hdfs-site.xml
cat <<EOF > $HADOOP_HOME/etc/hadoop/hdfs-site.xml
<configuration>
   <property>
      <name>dfs.namenode.name.dir</name>
      <value>file:///opt/hadoop/hdfs/namenode</value>
   </property>
   <property>
      <name>dfs.datanode.data.dir</name>
      <value>file:///opt/hadoop/hdfs/datanode</value>
   </property>
   <property>
      <name>dfs.replication</name>
      <value>3</value>
   </property>
</configuration>
EOF

# Format the Namenode
$HADOOP_HOME/bin/hdfs namenode -format

# Setup Spark Master
echo "export SPARK_MASTER_HOST=main" >> $SPARK_HOME/conf/spark-env.sh

# Start Spark Worker on main
echo "export SPARK_WORKER_CORES=2" >> $SPARK_HOME/conf/spark-env.sh
echo "export SPARK_WORKER_MEMORY=1g" >> $SPARK_HOME/conf/spark-env.sh