# Command-line based ticket system written in Scala 3
Ting is a small project I wrote to explore [Scala 3](https://github.com/lampepfl/dotty) features in combination with [GraalVM](https://www.graalvm.org) native image generation.

I hope that this project can be helpful to anyone that wants to explore Scala 3 and Graal AOT, and/or is looking for a simple way of organizing their work.

## Introduction

Ting is a proof-of-concept, command-line driven, file-based, ticket management system. 

Before you can add any tickets, you first need to initialize a project. 

`ting init project`

Initializing creates the Ting directory structure in the current directory, the project directory is called `.ting-project`.

```
ls .ting-project 
drwxr-xr-x 2 user user 4,0K dec  8 11:02 current
drwxr-xr-x 2 user user 4,0K dec  8 11:02 done
-rw-r--r-- 1 user user   59 dec  8 11:02 .template
drwxr-xr-x 2 user user 4,0K dec  8 11:02 todo
```

Each of the sub-directories in the project directory defines a ticket state, the ticket file will be placed in the `todo` directory when created. When starting a ticket, the ticket file will be moved from `todo` to `current`, when resolving a ticket it will be moved from `current` to `done`.

A ticket template called `.template`, is also written to the project directory when initializing, it is used when creating new tickets. This template file  can be customized, by default it contains the following YAML:

```
createdBy:
assignedTo:
description: >
```

To add your first ticket you:

`ting add ticket my-first-ticket`

Adding a ticket will create a file in the `todo` directory using the above mentioned template and then open up your current selected editor to edit the file. The editor used can be configured in `~/.ting/config.yaml`.

Each added ticket will have a filename with a unique id number prefixed to the selected ticket name. This id number is later used when referencing the ticket using other commands.

Example:
```
> ting add ticket test
Added ticket 'test'
> ting get tickets todo
0001 - test
> ting get ticket 1
createdBy: someone
assignedTo: someother
description: > 
This is a test ticket
```

Here follows a summary of all `Ting` commands:

	get ticket <id>                               - Display a ticket
	get tickets <todo | current | done> [-o]      - Display list of tickets, optionally print the content of the ticket.
	add ticket <title>                            - Adds a new ticket.
	start ticket <id>                             - Starts ticket progress by moving it from 'todo' to 'current'
	edit ticket <id>                              - Edit a ticket using the pre-configured editor.
	complete ticket <id>                          - Completes a ticket by moving it from 'current' to 'done'
	restart ticket <id>                           - Restarts a ticket by moving it from 'done' to 'current'
	init project                                  - Initialize a new project in the current folder.

## Building Ting

Ting is intended to be built using Graal native-image using [Graal 19.3.1](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-19.3.1).

    graalvm-native-image:packageBin

The output is located in `./target/graalvm-native-image/ting`

Some parts of Ting uses reflection, for example, serialization of the configuration, if this part of the program is changed, chances are that you have to re-generate the reflection configuration used by `native-image` while compiling.

Generation of reflection configuration:

1. First build a fat jar using `assembly`

2. Run the tracing agent:

`java -jar -agentlib:native-image-agent=config-output-dir=graal target/scala-0.20/ting-assembly-0.1.0.jar`

3. Recompile the binary using `graalvm-native-image:packageBin`

## Hacking Ting

Launch VS Code using Dotty LSP in the project directory.

    sbt launchIDE

More information about Dotty IDE support
https://dotty.epfl.ch/docs/usage/ide-support.html

## Configuring Ting

Ting stores a small configuration file the first time its executed. 
The configuration path:

    ~/.ting/config.yaml

The configuration file currently contains the name of the editor used when editing tickets.

## The name

The name 'Ting' comes from an old Swedish/Scandinavian word for a meeting to resolve common issues.
