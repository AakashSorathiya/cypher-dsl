/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypherdsl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.test.GraphDescription;
import org.neo4j.test.GraphHolder;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.test.TestData;

import java.io.IOException;
import java.util.Map;

import static org.neo4j.cypherdsl.query.MatchExpression.Direction.OUTGOING;
import static org.neo4j.cypherdsl.query.MatchExpression.path;

/**
 * Set up a query using the CypherQuery builder, and then use it to execute a query to a test database.
 */
public class CypherExecutionTest
    implements GraphHolder
{
    public @Rule
    TestData<Map<String, Node>> data = TestData.producedThrough( GraphDescription.createGraphFor(
        this, true ) );

    private ImpermanentGraphDatabase graphdb;
    private ExecutionEngine engine;
    private CypherParser parser;

    @Test
    @GraphDescription.Graph( value = {
        "John friend Sara", "John friend Joe",
        "Sara friend Maria", "Joe friend Steve"
    }, autoIndexNodes = true )
    public void testCypherExecution()
        throws Exception
    {
        data.get();

        String query = CypherQuery.newQuery()
            .nodesLookup( "john", "node_auto_index", "name", "John" )
            .match( path( "john", OUTGOING, "" ).relationship( "friend" ).path( OUTGOING, null, "friend", "fof" ) )
            .returnProperty( "john.name", "fof.name" )
            .toString();

        System.out.println(query);
        Query parsedQuery = parser.parse( query );
        System.out.println( engine.execute( parsedQuery ).toString() );
    }

    @Before
    public void setup()
        throws IOException
    {
        graphdb = new ImpermanentGraphDatabase();
        graphdb.cleanContent( false );

        parser = new CypherParser();
        engine = new ExecutionEngine( graphdb );
    }

    @Override
    public GraphDatabaseService graphdb()
    {
        return graphdb;
    }
}
