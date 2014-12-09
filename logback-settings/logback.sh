#!/bin/sh
#
# personium.io
# Copyright 2014 FUJITSU LIMITED
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


JAVA_HOME=/opt/jdk1.7.0_04
export JAVA_HOME  
PATH=$JAVA_HOME/bin:$PATH  
export PATH  
LOGBACK_HOME=/opt/logback/logback-1.0.3

java -cp  /fj/logback/dc1-logback.jar:$LOGBACK_HOME/logback-core-1.0.3.jar:$LOGBACK_HOME/logback-classic-1.0.3.jar:$LOGBACK_HOME/logback-access-1.0.3.jar:/opt/logback/slf4j-1.6.4/slf4j-api-1.6.4.jar ch.qos.logback.classic.net.SimpleSocketServer 6000 /opt/logback/logback.xml &
echo $! > /var/run/logback/logback.pid 

exit 0

