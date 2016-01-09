package cz.lidinsky.tools.xml;

/*
 *  Copyright 2015 Jiri Lidinsky
 *
 *  This file is part of control4j.
 *
 *  control4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  control4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with control4j.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 *  Interface through which the handler is notified about the
 *  start and end of XML parser processing. This interface must
 *  be implemented by the XML handlers.
 *
 *  @see XmlReader
 */
public interface IXMLHandler
{

  /**
   *  Through this method, the handler is notified about the
   *  processing start. It means, that the handler shoud expect
   *  incoming events from XML parser.
   *
   *  @param loader
   *             an XML loader; this object may be useful to
   *             the handler but may be ignored by the handler
   */
  void startProcessing();

  /**
   *  Notify the handler that parser reached the end element
   *  of the subtree which was processed by this handler.
   *  It means, that since now, there will be no other events
   *  from the XML parser.
   */
  void endProcessing();

  void setXMLReader(XMLReader reader);

}
