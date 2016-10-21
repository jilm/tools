package cz.lidinsky.tools.graph;

/*
 *  Copyright 2015 Jiri Lidinsky
 *
 *  This file is part of java tools library.
 *
 *  java tools is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  java tools library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with java tools library.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Graph<V>
{

  /**
   *  Takes a vertex v of some graph and traverses all of the vertices
   *  reachable from v. It uses depth first search algorithm.
   */
  public Iterator<V> DFS(IGraph<V> graph, V vertex)
  {
    HashSet<V> discovered = new HashSet<V>();
    LinkedList<V> stack = new LinkedList<V>();
    stack.push(vertex);
    while (stack.size() > 0)
    {
      vertex = stack.pop();
      if (!discovered.contains(vertex))
      {
	discovered.add(vertex);
	for (V v : graph.getDirectSuccessors(vertex))
	  stack.push(v);
      }
    }
    return discovered.iterator();
  }

  private HashSet<V> open;
  private HashSet<V> closed;

  /**
   *
   */
  public boolean isAcyclicDFS(IGraph<V> graph, V vertex)
  {
    open = new HashSet<V>();
    closed = new HashSet<V>();
    boolean result = isAcyclicDFSStep(graph, vertex);
    open = null;
    closed = null;
    return result;
  }

  private boolean isAcyclicDFSStep(IGraph<V> graph, V vertex)
  {
    boolean acyclic = true;
    open.add(vertex);
    for (V v : graph.getDirectSuccessors(vertex))
      if (!open.contains(v) && !closed.contains(v))
	acyclic = acyclic & isAcyclicDFSStep(graph, v); 
      else if (open.contains(v))
	return false;
    closed.add(vertex);
    return acyclic;
  }

}
