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

  given config : RuntimeConfig = RuntimeConfig(tempDirectory,"")

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
