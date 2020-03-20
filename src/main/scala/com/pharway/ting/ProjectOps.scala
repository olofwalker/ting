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
import scala.util._

object ProjectOps:
  def validProject()(using config: RuntimeConfig): Boolean =
    Try(
      TicketState.values.map(_.toPath)
      .map(dir => os.isDir(config.baseDirectory / dir))      
      .foldLeft(true)(_ && _)
    ) 
    match 
      case Success(b) => b
      case Failure(_) => false
      
  def init(options: List[String])(using config: RuntimeConfig): String | CommandError =
    if validProject() then
      CommandError(s"There is already a project in the current path!")
    else 
      try
        TicketState.values.foreach(state => os.makeDir.all(config.baseDirectory / state.toPath))
        val contents = os.read(os.resource/ "template.yaml")
        os.write(config.baseDirectory / Ticket.directory / Ticket.templateFile, contents)
        "Project initialized"
      catch
        case e: Exception => CommandError(s"Could not initialize the project, ${e.getMessage()}")