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


object CommandLine

  def parseArguments(args: String*)(given config: RuntimeConfig) : CommandError | String =

    def commands = List(
      Command(Get,Ticket,"get ticket <id>", "Display a ticket",TicketOps.getTicket),
      Command(Get,Tickets,"get ticket <todo | current | done> [-o]","Display list of tickets, optionally print the content of the ticket.",TicketOps.getTickets),
      Command(Add,Ticket,"add ticket <title>","Adds a new ticket",TicketOps.addTicket),
      Command(Start,Ticket,"start ticket <id>","Starts ticket progress by moving it from 'todo' to 'current'",TicketOps.startTicket),
      Command(Edit,Ticket,"edit ticket <id>","Edit a ticket using the pre-configured editor.",TicketOps.editTicket),
      Command(Complete,Ticket,"complete ticket <id>","Completes a ticket by moving it from 'current' to 'done'",TicketOps.completeTicket),
      Command(Restart,Ticket,"restart ticket <id>","Restarts a ticket by moving it from 'done' to 'current'",TicketOps.restartTicket),
      Command(Init,Project,"init project","Initialize a new project in the current folder.",ProjectOps.init)
    )
  
    lazy val helpText =  
      s"\nTing help:\n" ++
      commands.map(cmd => 
        f"${cmd.syntax}%-45s - ${cmd.description}"
        ).mkString("\n")
  
    args.toList match 
      case verb :: subject :: options =>
        val res = Try{commands
            .find(_.compare(Verb.valueOf(verb.capitalize),Subject.valueOf(subject.capitalize)))}
        res match 
          case Success(cmd) => cmd match
              case Some(x) => 
                if x.verb != Init && x.subject != Project
                  if(ProjectOps.validProject())
                    x.func(options) 
                  else
                    CommandError("Not a valid project.")
                else
                  x.func(options)
              case None => helpText
          case Failure(ex) => helpText
      case _ => helpText
