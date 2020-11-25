/*
    Ting - Command-line driven project management in Scala 3
    Copyright (C) <year>  <name of author>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.pharway.ting

import verbs._
import subjects._

import verbs.Verb._
import subjects.Subject._

import scala.util.{Try, Success, Failure}


object CommandLine:
  def commands()(using RuntimeConfig) : List[Command] = List(
    Command(Get,Ticket,"get ticket <id>", "Display a ticket",TicketOps.getTicket),
    Command(Get,Tickets,"get tickets <todo | current | done> [-o]","Display list of tickets, optionally print the content of the ticket.",TicketOps.getTickets),
    Command(Add,Ticket,"add ticket <title>","Adds a new ticket",TicketOps.addTicket),
    Command(Start,Ticket,"start ticket <id>","Starts ticket progress by moving it from 'todo' to 'current'",TicketOps.startTicket),
    Command(Edit,Ticket,"edit ticket <id>","Edit a ticket using the pre-configured editor.",TicketOps.editTicket),
    Command(Complete,Ticket,"complete ticket <id>","Completes a ticket by moving it from 'current' to 'done'",TicketOps.completeTicket),
    Command(Restart,Ticket,"restart ticket <id>","Restarts a ticket by moving it from 'done' to 'current'",TicketOps.restartTicket),
    Command(Init,Project,"init project","Initialize a new project in the current folder.",ProjectOps.init)
  )

  private def synthesize(verb: String, subject: String): Try[(Verb, Subject)] = 
    Try((Verb.valueOf(verb.capitalize), Subject.valueOf(subject.capitalize)))
  
  type ExecutableCommand = RuntimeConfig ?=> CommandError | String //Context Function

  def parseArguments(args: String*): ExecutableCommand =

    lazy val helpText =  
      s"\nTing help:\n" ++
      commands().map(cmd => 
        f"${cmd.syntax}%-45s - ${cmd.description}"
        ).mkString("\n")

    def find(verb: Verb, subject: Subject): Try[Command] = 
      commands().find(_.compare(verb, subject)) match
        case Some(command) => Success(command)
        case None => Failure(Exception(s"'${verb.toString} ${subject.toString}' is not a recognized command"))
      
    args.toList match 
      case verb :: subject :: options =>
        val commandOrError: Try[Command] = 
          for 
            (v, s) <- synthesize(verb, subject)
            command <- find(v, s)
          yield 
            command  
          
        commandOrError match 
          case Success(cmd) =>
            if cmd.verb != Init && cmd.subject != Project
              if(ProjectOps.validProject())
                cmd.func(options) 
              else
                CommandError("The current folder does not contain a valid project. You can initialize a project in this folder using the command `ting init project`.")
            else
              cmd.func(options)
          case Failure(_) => helpText
      case _ => helpText
