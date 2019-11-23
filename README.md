# Command-line driven ticket management in Scala 3
Ting is a small project I wrote to explore Scala 3 features in combination with GraalVM native image generation.

I hope that this project can be helpful to anyone that wants to explore Scala 3 and Graal AOT.

## Introduction

Ting is a command-line driven, file-based, ticket management system. 

A Ting project is initialized in the current folder by creating a base folder (`.ting`) and three sub-folders, one for each of the available ticket states.

 - When you add a new ticket, it's added to the `Todo` folder.
 - When you start a ticket, it's moved to the `Current` folder.
 - When you complete a ticket, it's moved to the `Done` folder.

A ticket template (default a YAML file packaged with Ting) is added to each tickets created.

If you want to use a different YAML template file, replace the `.template` file in the `.ting` base folder.

## The name

The name 'Ting' comes from an old Swedish/Scandinavian word for a meeting to resolve common issues.

## Hacking

Launch VS Code using Dotty LSP in the project directory.

    sbt launchIDE

More information about Dotty IDE support
https://dotty.epfl.ch/docs/usage/ide-support.html

Ting is intended to be built using Graal native-image using Graal 19.3.

    graalvm-native-image:packageBin

The output is located in `./target/graalvm-native-image/ting`

## Using Ting

### Configuration of Ting

Ting stores a small configuration file the first time its executed. 
The configuration path:

    ~/.ting/config.yaml

The configuration file currently contains the name of the editor used when editing tickets.

### Commands

	get ticket <id>                               - Display a ticket
	get ticket <todo | current | done> [-o]       - Display list of tickets, optionally print the content of the ticket.
	add ticket <title>                            - Adds a new ticket
	start ticket <id>                             - Starts ticket progress by moving it from 'todo' to 'current'
	edit ticket <id>                              - Edit a ticket using the pre-configured editor.
	complete ticket <id>                          - Completes a ticket by moving it from 'current' to 'done'
	restart ticket <id>                           - Restarts a ticket by moving it from 'done' to 'current'
	init project                                  - Initialize a new project in the current folder.
