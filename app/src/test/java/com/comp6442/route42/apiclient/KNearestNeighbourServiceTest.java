package com.comp6442.route42.apiclient;

import com.comp6442.route42.api.KNearestNeighbourService;
import com.comp6442.route42.data.model.Post;

import org.junit.Test;

import java.util.List;

import timber.log.Timber;

public class KNearestNeighbourServiceTest {
    @Test
    public void test1(){
        KNearestNeighbourService kNearestNeighbourService = new KNearestNeighbourService(5, -35.25932077515105, 149.11459641897002);
        Timber.i(String.valueOf(kNearestNeighbourService));
        try {
            for(Post post : kNearestNeighbourService.call()){
                Timber.i(String.valueOf(post));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
