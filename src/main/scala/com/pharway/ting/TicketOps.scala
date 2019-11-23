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

import os._
import sys.process._
import java.nio.file.NoSuchFileException

import java.lang.ProcessBuilder
import java.io.File

import scala.util._

import TicketState._

object TicketOps
  
  def getTickets(options: List[String])(given config: RuntimeConfig): CommandError | String =
    Try(getCommandLineStateAndFlags(options)) match
    case Failure(err) => CommandError(err.getMessage)
    case Success(ticketState) =>
      readTickets() match
        case e: CommandError => e
        case tickets: List[Ticket] =>
          ticketState._2 match  
            case "-o" :: tail =>
              tickets.filterByState(ticketState._1).map(ticket => os.read(ticket.path)).mkString("---\n")
            case nil =>
              tickets.filterByState(ticketState._1).map(ticket => ticket.fileName).mkString("\n")
  
  def getTicket(options: List[String])(given config: RuntimeConfig): CommandError | String =
    options match 
      case Nil => CommandError("Missing ticket id")
      case ticketId :: tail => 
        readTickets() match 
          case tickets: List[Ticket] => 
            tickets.findByTicketId(ticketId.toInt) match 
              case None => CommandError(s"Ticket with id '$ticketId' not found.")
              case Some(x) => os.read(x.path)
          case c: CommandError => c

  def addTicket(options: List[String])(given config: RuntimeConfig): CommandError | String =
    options match
      case Nil =>
        CommandError("no ticket name specified.")
      case ticketName :: tail =>
        readTickets() match
          case e: CommandError  => e
          case tickets: List[Ticket] => 
            tickets.filterByState(Todo).find(ticket => ticket.name == ticketName) match
              case Some(x) => 
                CommandError(s"Ticket '$ticketName' does already exist!")
              case None =>
                val tickets    = ticketCount + 1
                val ticketPath = config.baseDirectory / Todo.toPath /  s"${"%04d".format(tickets)} - $ticketName"
                val templateFilePath = config.baseDirectory / Ticket.directory / Ticket.templateFile
                val fileContent = if os.isFile(templateFilePath) then
                  os.read(templateFilePath) 
                else 
                  "" 
                os.write(ticketPath, fileContent)
                if config.editTicket then 
                  Process(config.ticketEditor, Seq(ticketPath.toString())).run().exitValue()
                s"Added ticket '$ticketName'"
                  
  def editTicket(options: List[String])(given config: RuntimeConfig): CommandError | String =
    options match 
      case Nil => CommandError("Missing ticket id")
      case ticketId :: tail => 
        readTickets() match 
          case tickets: List[Ticket] => 
            tickets.findByTicketId(ticketId.toInt) match 
              case None => CommandError(s"Ticket with id '$ticketId' not found.")
              case Some(ticket) => 
                Process(config.ticketEditor, Seq(ticket.path.toString())).run.exitValue() match
                  case 0 =>
                    s"Edit ticket '${ticket.id}'"
                  case failure =>
                    CommandError(s"Failed to open file ${ticket.path.toString()}, exit code $failure.")                    
          case c: CommandError => c

  def startTicket(options: List[String])(given config: RuntimeConfig) : CommandError | String = 
    moveTicket(options,Todo,Current)

  def completeTicket(options: List[String])(given config: RuntimeConfig) : CommandError | String = 
    moveTicket(options,Current,Done)
    
  def restartTicket(options: List[String])(given config: RuntimeConfig) : CommandError | String = 
    moveTicket(options,Done,Current)


  private def getCommandLineStateAndFlags(options: List[String]) =
    options match       
      case state :: flags => 
        Try(TicketState.valueOf(state.capitalize) -> flags) match
          case Success(s) => s
          case Failure(f) => throw Exception(s"Please provide a valid state, '$state' is not a recognized state.")
      case _ => throw Exception(s"Please provide a valid state.")
        
  private def moveTicket(options: List[String], from: TicketState, to: TicketState)(given config: RuntimeConfig) : String | CommandError =
    options match 
      case Nil => CommandError("Missing ticket id")
      case ticketId :: tail => 
        readTickets() match 
          case tickets: List[Ticket] => 
            tickets.filterByState(from).findByTicketId(ticketId.toInt) match 
              case None => CommandError("Ticket with id ${ticketId.toInt} not found in '${from.toString}''")
              case Some(ticket) => 
                os.move(ticket.path,config.baseDirectory / to.toPath / ticket.fileName)
                s"Moved ticket ${ticketId.toInt} to $to"
          case c: CommandError => c    
  
  private def ticketCount(given config: RuntimeConfig): Int =
    TicketState.values.map(state => os.list(config.baseDirectory / state.toPath).filter(os.isFile).size).sum

  private def readTickets()(given config: RuntimeConfig): CommandError | List[Ticket] =
    Try(TicketState.values.flatMap( state => 
        os.list(config.baseDirectory / state.toPath)
          .filter(os.isFile)
          .map(path => 
            Ticket(state,path.last.take(path.last.indexOf("-") - 1).trim.toInt,path))
      ).toList) match
        case Success(tickets) => tickets
        case Failure(ex) => CommandError(ex.getMessage)
