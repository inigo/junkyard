package net.surguy.junkyard.utils

import org.slf4j.LoggerFactory;

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 21, 2010 12:15:56 PM
 */
trait WithLog {
  lazy val log = LoggerFactory.getLogger(this.getClass.getName)
}
