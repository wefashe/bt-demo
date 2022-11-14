package com.example.demo.announce;

import com.example.demo.common.MetaData;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Set;

public class TrackerClient {

    // protected final URI tracker;

    public TrackerClient(MetaData metaData) throws URISyntaxException {
        System.out.println(LocalDateTime.now() + " 连接Tracker ");
        Set<String> announces = metaData.getAnnounces();
        for (String announce : announces) {
            System.out.println(LocalDateTime.now() + " 连接Tracker " + announce);
        }
    }

    // protected abstract void announce(RequestEvent event);

}
