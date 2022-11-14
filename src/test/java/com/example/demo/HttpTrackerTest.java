package com.example.demo;


import com.example.demo.announce.TrackerClient;
import com.example.demo.common.MetaData;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HttpTrackerTest {

    @Test
    public void getPeersTest() throws IOException, URISyntaxException {

        MetaData metaData = new MetaData("src/test/resources/torrent/aqgy.torrent");

        String url = "https://cdn.staticaly.com/gh/XIU2/TrackersListCollection/master/all.txt";
        String s = IOUtils.toString(new URI(url), StandardCharsets.UTF_8);
        String[] array = s.split("\n\n");

        metaData.addAnnounceList(Arrays.asList(array));

        TrackerClient client = new TrackerClient(metaData);

    }
}
