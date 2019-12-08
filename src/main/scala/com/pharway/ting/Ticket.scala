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

case class Ticket(state: TicketState,id: Int, path: Path)
  val fileName = RelPath(path.last)
  val name = fileName.toString.takeRight(fileName.toString.length - fileName.toString.indexOf("-") - 1).trim


object Ticket
  val directory = RelPath(".ting-project")
  def apply(state: TicketState, path: Path) : Ticket = 
    val id = path.last.take(path.last.indexOf("-") - 1).trim.toInt
    Ticket(state,id,path)
  val templateFile = ".template"

def (list: List[Ticket]) findByTicketId(id: Int) : Option[Ticket] = 
    list.find(_.id == id)
def (list: List[Ticket]) filterByState(state: TicketState) : List[Ticket] = 
  list.filter(_.state == state)  


enum TicketState
  case Todo,Current,Done

given ticketStateOps: with
  def (state: TicketState) toPath =
    Ticket.directory / RelPath(state.toString.toLowerCase)

