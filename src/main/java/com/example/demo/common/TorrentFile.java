package com.example.demo.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TorrentFile {
    private String root;
    private String parent;
    private String name;
    private long length;
    private List<String> names = new ArrayList<>();

    public TorrentFile(String name, long length) {
        this.names.add(name);
        this.name = name;
        this.length = length;
    }

    public TorrentFile(String parent, List<String> names, long length) {
        this.parent = parent;
        this.names.addAll(names);
        this.name = names.get(0);
        this.length = length;
    }
}
