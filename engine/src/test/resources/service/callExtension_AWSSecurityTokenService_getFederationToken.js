/*
 * personium.io
 * Copyright 2014 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function (request) {
  var extension = new dc.extension.AWSSecurityTokenService();

  var input = JSON.parse(request["input"].readAll());
  
  // 必須プロパティの指定
  extension.AccessKeyId = input.AccessKeyId;
  extension.SecretAccessKey = input.SecretAccessKey;

  // オプショナルプロパティの指定
  if(input.ProxyHost != null) {
  extension.ProxyHost = input.ProxyHost;
  }
  if(input.ProxyPort != null) {
  extension.ProxyPort = input.ProxyPort;
  }
  if(input.ProxyUser != null) {
  extension.ProxyUser = input.ProxyUser;
  }
  if(input.ProxyPassword != null) {
  extension.ProxyPassword = input.ProxyPassword;
  }
  
  var policy = {
          "Version": "2012-10-17",
          "Statement": [
             {
                 "Effect": "Allow",
                 "Action": "*",
                 "Resource": "*" 
             }]
      };

  try {
    var result;
    if(input.durationSeconds != null) {
      result = extension.getFederationToken("name", policy ,input.durationSeconds);
    } else {
      result = extension.getFederationToken("name", policy);
    }
    return {
      status: 200,
      headers: {"Content-Type":"application/json"},
      body: [JSON.stringify(result)]
    };
  } catch (e) {
    return {
      status: 418,
      headers: {"Content-Type":"text/plain"},
      body: ["Failed to get AWS Security Token. reason: " + e.message]
    };
  }
}
