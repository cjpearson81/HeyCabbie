package com.bryanreinero.bitcoin.inflight;

import com.bryanreinero.firehose.metrics.Interval;
import com.bryanreinero.firehose.metrics.SampleSet;
import com.bryanreinero.lambda.LogReplayer;
import com.bryanreinero.lambda.ViewBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by brein on 5/28/2016.
 */
public class TestCoinInflightAggregator {

    private final MongoDatabase bitcoinDB;
    private final MongoCollection<Document> transactions;

    private final SampleSet stats = new SampleSet();
    private final LogReplayer<Document> replay;

    public TestCoinInflightAggregator(MongoClient mongo) {
        bitcoinDB = mongo.getDatabase( "bitcoin" );
        this.transactions = bitcoinDB.getCollection( "transactions" );
        this.replay = new LogReplayer<>( transactions, new Document() );


    }


    public void getAllCoinInFlight () {

        Long time = System.currentTimeMillis() / 1000L;
        final ViewBuilder<Document, String> aggregator =  new EmcumberancesInFlightAggegator() ;
        replay.addBuilder(aggregator);
        try (  Interval t = stats.set("logReplay") ) {
            replay.replayLogs( 0, time.intValue() );
        }
        System.out.println ( aggregator.getView() );
    }


    public static void main( String[] args ) {

        MongoClient client = new MongoClient();
        TestCoinInflightAggregator test = new TestCoinInflightAggregator( client );
        test.getAllCoinInFlight();

    }
}
