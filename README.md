personium.io
====

An interconnectable open source BaaS(Backend as a Service) / PDS (Personal Data Store) server.

http://personium.io/

## Components

	core                    - Core module of personium.io
	engine                  - Module to enable server-side JS execution.
	cell-sweeper            - A batch program to delete the cells that are marked to be deleted.
	logback                 - A logback customization to rotate the event logs.
	logback-settings        - A shell command to run logback as a daemon process .
	es-api-1.2              - Abstraction layer to absorb version incompatibilities of ElasticSearch.
	common                  - Common modules used in the above modules.
	engine-extension-common - Common modules for implementing Engine Extension.
	client-java             - Java client. Engine internally uses it.
	client-js               - JavaScript client for web browsers.

## Documentation

http://personium.io/docs/

## Note

	This project started as a proprietary service platform development 
	to accelerate Fujitsu's social innovations business. 

	As of Dec. 2014, this project is still on the way from properietary 
	to open source, and thus this product is not ready to be comfortably 
	used by everyone yet.
	
	We are going to prepare missing parts, e.g. documents, tools, etc., 
	to resolve this situation as soon as possible.

## Set up 

Please look at set up page. ([set up page](https://github.com/personium/io-vagrant-ansible))

## Licence

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

	Copyright 2014 FUJITSU LIMITED

