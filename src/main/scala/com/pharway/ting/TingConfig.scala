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

import net.jcazevedo.moultingyaml._
import net.jcazevedo.moultingyaml.DefaultYamlProtocol._

case class Config(ticketEditor: String = "xdg-open")

object ConfigFactory extends DefaultYamlProtocol:
  given YamlFormat[Config] = yamlFormat1(Config.apply)

  final case class ConfigurationNotFound(message: String = "") extends Exception(message)

  private def configDirectory = os.home / RelPath(".ting")
  private val configFile = RelPath("config.yaml")

  def init = 
    if !os.isFile(configDirectory / configFile) then 
      val config = Config()
      os.makeDir.all(configDirectory)
      os.write.over(configDirectory / configFile, config.toYaml.prettyPrint)
      config
    else
      load

  def load: Config =
    if os.isFile(configDirectory / configFile) then
      Try(os.read(configDirectory / configFile).parseYaml.convertTo[Config]) match 
        case Failure(x) => 
          s"Unable to read configuration file `${(configDirectory / configFile).toString}`, remove the configuration file and try again. Using default configuration.".logError
          Config()
        case Success(c) => c
      
    else
      throw new ConfigurationNotFound("This is not a Ting project.")

case class RuntimeConfig(baseDirectory: Path, ticketEditor: String):
  val editTicket = ticketEditor.nonEmpty

object RuntimeConfig:
  def apply(config: Config) : RuntimeConfig = RuntimeConfig(Path(System.getProperty("user.dir")),config)
  def apply(baseDirectory: Path, config: Config) : RuntimeConfig = RuntimeConfig(baseDirectory,config.ticketEditor)

