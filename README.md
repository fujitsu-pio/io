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

Documentaion including installation and development can be found at [personium.io wiki](https://github.com/personium/io/wiki).


## Community

Please join and discuss at our [google group](https://groups.google.com/forum/?hl=en#!forum/personium-io).



## Note

This  project started as a proprietary service platform to accelerate Fujitsu's social innovation.
   On Dec. 2014, we made this project open source to share,collaborate and make innovation with comunity.









## License

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
