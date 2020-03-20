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

object ProjectOpsTests {
  val tempDirectory   = os.temp.dir()
  @AfterClass 
  def afterAll() = {
    os.remove.all(tempDirectory)
  }
}

class ProjectOpsTests {

  import ProjectOpsTests._

  given config as RuntimeConfig = RuntimeConfig(tempDirectory,"")

  @After def afterEach() = {
    os.remove.all(tempDirectory / Ticket.directory)
  }

  @Test def InitProjectTest() = {
      assertTrue("Init project",CommandLine.parseArguments("init", "project").isInstanceOf[String])
    }
    
  @Test def DuplicateInitProjectTest() = {
    assertTrue("Init project",CommandLine.parseArguments("init", "project").isInstanceOf[String])
    assertTrue("Init project",CommandLine.parseArguments("init", "project").isInstanceOf[CommandError])
  }

  @Test def VerifyThatProjectIsInitialized() = {
    assertFalse(CommandLine.parseArguments("add", "ticket", "test").isInstanceOf[String])
  }
}
