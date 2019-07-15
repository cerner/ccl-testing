# ccl-testing

## Contents
[Introduction](#introduction)  
[Component Versions](#component-versions)  
[Quick Start Guide](#quick-start-guide)  
[Recommendations](#recommendations)  
[Troubleshooting](#troubleshooting)  
[Release Schedule](#release-schedule)  
[Contributing](#contributing)  
[License](#license)  

## Introduction
[Cerner Command Language][CCL], aka CCL, aka Discern Explorer, is a database query and scripting language used with Cerner Millennium databases.  [CCL Unit][ccl_unit] 
is a unit testing framework for CCL.

The ***ccl-testing*** repository houses maven plugins to perform CCL Unit tests and analyses, to generate reports from the results, to perform static analyses,
and to generate code documentation. It also houses some dependencies for those maven plugins.  
See [Component Versions](#component-versions) for a list of the components.  


## Component Versions
The latest released versions of the ccl-testing components are listed below.  
Visit individuals components for details and usage instructions.  
Visit the [change log](CHANGELOG.md) for version details. 

* [**ccl-maven-plugin (3.2)**][ccl-maven-plugin] - Maven plugin for transferring resources, compiling CCL code and tests, executing the
tests, and retrieving the test and coverage results.
* [**cerreal-maven-plugin (2.1)**](cerreal-maven-plugin/README.md) - Maven reporting plugin to report test and coverage results.
* [**whitenoise-maven-plugin (2.5)**](whitenoise/whitenoise-maven-plugin/README.md) - Maven reporting plugin that identifies common CCL coding errors.
* [**cdoc-maven-plugin (1.2)**](cdoc/cdoc-maven-plugin/README.md) - Maven reporting plugin that generates code documentation from code comments.
* [**ecosystem**](ecosystem/README.md) - Eclipse preferences to ensure consistent formating and compiler settings. Import with [Workspace Mechanic][workspace_mechanic].
* [**ftp-util (2.0)**](ftp-util/README.md) - ccl-maven-plugin dependency used to ftp resource to/from the Cerner Millennium back end.
* [**j4ccl (3.1)**](j4ccl/README.md) - ccl-maven-plugin dependency defining   classes and interfaces for accessing a Cerner Millennium back end.
* [**j4ccl-ssh (4.2)**](j4ccl-ssh/README.md) - ccl-maven-plugin dependency leveraging JCraft SSH to implement the j4ccl interfaces.
* [**jsch-util (2.0.0)**](jsch-util/README.md) - ccl-maven-plugin dependency leveraging JCraft to provide back-end connections for j4ccl-ssh.
* [**cclunit-archetype (1.5)**][archetype usage] - archetype to generate a skeleton CCL project using the latest plugins.
* [**cclunit-maven-settings-check-archetype (1.2)**][archetype usage] - archetype to generate a CCL project to check maven settings.
  
## Quick Start Guide
If you are new to CCL Unit testing and want to get started writing and running tests quickly, [look here][ccl_unit_usage].  

Install and configure [Apache Maven](https://maven.apache.org/) as described [here](doc/CONFIGUREMAVEN.md).  
Use [the maven archetype][archetype usage] to generate a new mavenized CCL project.

## Recommendations
Configure the system so your password will not show up in log files. [Look here](ccl-maven-plugin/doc/PASSWORDLOGGING.md) for details.  
Use [the maven archetype][archetype usage] to generate new projects.  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;If you really want to do it manually, here is a [sample pom] (the same one the archetype creates).    
Become familiar with the [usage instructions][ccl-maven-plugin] and [configuration options][ccl-maven-plugin-configuration-options] for the unit testing plugin.  


## Troubleshooting  
Having issues? [Look here](ccl-maven-plugin/doc/BUILDISSUES.md) for some common problems and troubleshooting tips.


## Release Schedule

The release schedule will be driven by requests for and contributions of enhancements and corrections.  
See the [change log](CHANGELOG.md) for the contents of previous releases.

## Contributing

You are welcomed to contribute enhancements or fixes to the documention or code. Please read our [Contribution Guidelines][contibution_guidelines].  
Committers should follow the [Release Guidelines][release_guidelines].


## License

```markdown
Copyright 2017 Cerner Innovation, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


[CCL]: https://en.wikipedia.org/wiki/Cerner_CCL
[contibution_guidelines]: CONTRIBUTING.md#contributing
[release_guidelines]: RELEASING.md#releasing-ccl-testing
[ccl_unit]: https://github.com/cerner/cclunit-framework
[ccl_unit_usage]: https://github.com/cerner/cclunit-framework#cclunit-framework
[archetype usage]: ./archetype/ARCHETYPEUSAGE.md
[sample pom]: ccl-maven-plugin/doc/SAMPLEPOM.md
[ccl-maven-plugin]: ccl-maven-plugin/README.md
[ccl-maven-plugin-configuration-options]: ccl-maven-plugin/doc/CONFIGURATIONOPTIONS.md
[workspace_mechanic]: https://code.google.com/archive/a/eclipselabs.org/p/workspacemechanic 
