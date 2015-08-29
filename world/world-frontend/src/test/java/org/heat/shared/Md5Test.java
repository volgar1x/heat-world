package org.heat.shared;

import org.heat.world.controllers.utils.Md5;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class Md5Test {

    private Thread[] threads;
    private String[][] results;

    @Before
    public void setUp() throws Exception {
        this.threads = new Thread[50];
        this.results = new String[threads.length][1000];
    }

    @Test
    public void testSafety() throws InterruptedException {
        //given
        final int playerId = (int)(Math.random()*100000);
        final String secretAnswer = "test_" + playerId;
        final String givenResult = Md5.hash(playerId, secretAnswer);

        //when
        for(int i = 0; i < threads.length; i++) {
            final int tId = i;
            threads[i] = new Thread(() -> {
                for(int j =0; j < results[tId].length; j++) {
                    results[tId][j] = Md5.hash(playerId, secretAnswer);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        //then
        for (String[] tResult : results) {
            for (String result : tResult) {
                assertThat(result, equalTo(givenResult));
            }
        }
    }
}
