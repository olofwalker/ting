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

import org.junit.Test
import org.junit.Assert._
import org.junit._

import os._

import java.io._
import java.nio._
import java.nio.file._

object TicketOpsTests {
  val tempDirectory   = os.temp.dir()
  @AfterClass 
  def afterAll() = {
    os.remove.all(tempDirectory)
  }
}

class TicketOpsTests {

  import TicketOpsTests._

  given config : RuntimeConfig = RuntimeConfig(tempDirectory,"")

  @Before def beforeEach() : Unit = {
    CommandLine.parseArguments("init", "project")
  }

  @After def afterEach() : Unit = {
    os.remove.all(tempDirectory / Ticket.directory)
  }

  @Test def AddTicket(): Unit = {
    assertTrue("Add ticket",CommandLine.parseArguments("add", "ticket", "test").isInstanceOf[String])
  }

  @Test def DoNotAddDuplicateTickets(): Unit = {
    val rightResult = CommandLine.parseArguments("add", "ticket", "test")
    assertTrue("Add ticket",rightResult.isInstanceOf[String])
    val leftResult = CommandLine.parseArguments("add", "ticket", "test")
    assertTrue("Add the same ticket again should fail",leftResult.isInstanceOf[CommandError])
  }

  @Test def returnAllTodoTickets(): Unit = {
    assertTrue("Add ticket",CommandLine.parseArguments("add", "ticket", "test").isInstanceOf[String])
    assertTrue("Add ticket",CommandLine.parseArguments("add", "ticket", "test2").isInstanceOf[String])
    assertTrue("Add ticket",CommandLine.parseArguments("add", "ticket", "test3").isInstanceOf[String])

    val correctReturnValue = "0001 - test\n0002 - test2\n0003 - test3"
    assertTrue("Get all tickets",CommandLine.parseArguments("get", "tickets","todo").asInstanceOf[String] == correctReturnValue)
  }

  @Test def returnAllCurrentTickets(): Unit = {
    assertTrue("Add ticket",CommandLine.parseArguments("add", "ticket", "test").isInstanceOf[String])
    assertTrue("Add ticket",CommandLine.parseArguments("add", "ticket", "test2").isInstanceOf[String])
    assertTrue("Add ticket",CommandLine.parseArguments("add", "ticket", "test3").isInstanceOf[String])

    assertTrue("Start ticket 1",CommandLine.parseArguments("start", "ticket","1").isInstanceOf[String])
    assertTrue("Start ticket 2",CommandLine.parseArguments("start", "ticket","2").isInstanceOf[String])
    assertTrue("Start ticket 33",CommandLine.parseArguments("start", "ticket","3").isInstanceOf[String])

    val correctReturnValue = "0001 - test\n0002 - test2\n0003 - test3"
    assertTrue("Get all tickets",CommandLine.parseArguments("get", "tickets","current").asInstanceOf[String] == correctReturnValue)
  }

  @Test def returnAllDoneTickets(): Unit = {
    assertTrue("Add ticket 1",CommandLine.parseArguments("add", "ticket", "test").isInstanceOf[String])
    assertTrue("Add ticket 2",CommandLine.parseArguments("add", "ticket", "test2").isInstanceOf[String])
    assertTrue("Add ticket 3",CommandLine.parseArguments("add", "ticket", "test3").isInstanceOf[String])

    assertTrue("Start ticket 1",CommandLine.parseArguments("start", "ticket","1").isInstanceOf[String])
    assertTrue("Start ticket 2",CommandLine.parseArguments("start", "ticket","2").isInstanceOf[String])
    assertTrue("Start ticket 33",CommandLine.parseArguments("start", "ticket","3").isInstanceOf[String])

    assertTrue("Complete ticket 1",CommandLine.parseArguments("complete", "ticket","1").isInstanceOf[String])
    assertTrue("Complete ticket 2",CommandLine.parseArguments("complete", "ticket","2").isInstanceOf[String])
    assertTrue("Complete ticket 33",CommandLine.parseArguments("complete", "ticket","3").isInstanceOf[String])

    val correctReturnValue = "0001 - test\n0002 - test2\n0003 - test3"
    assertTrue("Get all tickets",CommandLine.parseArguments("get", "tickets","done").asInstanceOf[String] == correctReturnValue)
  }

  @Test def restartADoneTicket(): Unit = {
    assertTrue("Add ticket",CommandLine.parseArguments("add", "ticket", "test").isInstanceOf[String])
    assertTrue("Start ticket 1",CommandLine.parseArguments("start", "ticket","1").isInstanceOf[String])
    assertTrue("Complete ticket 1",CommandLine.parseArguments("complete", "ticket","1").isInstanceOf[String])
    assertTrue("Restart ticket 1",CommandLine.parseArguments("restart", "ticket","1").isInstanceOf[String])
  }
}
