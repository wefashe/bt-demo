package com.example.demo.common;

import com.example.demo.bencode.BDecoder;
import com.example.demo.bencode.BEncoder;
import lombok.Data;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Data
public class MetaData {

    private Map<String, Object> metaData;
    private byte[] infoHash;
    private String hexHash;

    private String name;
    private long totalLength;
    private List<TorrentFile> files;

    private String creationDate;
    private String createdBy;
    private String comment;

    private long pieceLength;
    private long pieceCount;

    private boolean isPrivate;
    private String source;

    private Set<String> announces;

    public MetaData(String torrentPath) throws IOException {
        System.out.println(LocalDateTime.now() + " 开始分析种子文件...");
        byte[] data = Files.readAllBytes(Paths.get(torrentPath));
        this.metaData = (Map<String, Object>) BDecoder.decodeFromByte(data);

        Map<String,Object> infoMap = (Map<String, Object>) metaData.get("info");
        this.infoHash = DigestUtils.sha1(BEncoder.encodeToByte(infoMap));
        this.hexHash = Hex.encodeHexString(this.infoHash);

        this.name = (String) infoMap.get("name");
        this.name = new String(name.getBytes(Constants.BYTE_ENCODING), StandardCharsets.UTF_8);

        List<Object> fileMaps = (List<Object>) infoMap.get("files");

        this.files = new ArrayList<>();
        if (Objects.isNull(fileMaps)) {
            long length = (Long) infoMap.get("length");
            this.files.add( new TorrentFile(this.name, length));
            this.totalLength = length;
        } else {
            for (Object fileMapObj : fileMaps) {
                Map<String, Object> fileMap = (Map<String, Object>) fileMapObj;
                List<String> path = (List<String>) fileMap.get("path");
                long length = (Long) fileMap.get("length");
                this.files.add(new TorrentFile(this.name, path, length));
                this.totalLength += length;
            }
        }

        this.creationDate = timeToString((Long) this.metaData.get("creation date"));
        this.createdBy = (String) this.metaData.get("created by");
        this.comment = (String) this.metaData.get("comment");

        this.pieceLength = (Long) infoMap.get("piece length");

        byte[] pieces = BEncoder.encodeToByte(infoMap.get("pieces"));
        // 也可以总大小除分块大小，除不尽则加一
        this.pieceCount = pieces.length / Constants.PIECE_HASH_SIZE;

        Object privateObj = infoMap.get("private");
        this.isPrivate = !Objects.isNull(privateObj) && 1 == (Long) privateObj;

        this.source = (String) infoMap.get("source");

        this.announces = new HashSet<>();
        Object announce = this.metaData.get("announce");
        if (announce instanceof List) {
            this.announces.addAll((List) announce);
        } else {
            this.announces.add((String) announce);
        }
        List<Object> announceList = (List<Object>) this.metaData.get("announce-list");
        if (!Objects.isNull(announceList)) {
            for (Object announceObj : announceList) {
                if (announceObj instanceof List) {
                    this.announces.addAll((List) announceObj);
                } else {
                    this.announces.add((String) announceObj);
                }
            }
        }
        System.out.println(LocalDateTime.now() + " 分析种子文件成功");
    }

    private String timeToString(long time) {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 时间戳(秒)转化成LocalDateTime 毫秒使用ofEpochMilli
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault()));
    }

    public String lengthFormat(long length){
        BigDecimal pieceLengthBFormat = new BigDecimal(length);
        pieceLengthBFormat = pieceLengthBFormat.setScale(0, RoundingMode.DOWN);
        BigDecimal pieceLengthKBFormat = pieceLengthBFormat.divide(new BigDecimal(1024));
        pieceLengthKBFormat = pieceLengthKBFormat.setScale(1, RoundingMode.DOWN);
        BigDecimal pieceLengthMBFormat = pieceLengthKBFormat.divide(new BigDecimal(1024));
        pieceLengthMBFormat = pieceLengthMBFormat.setScale(2, RoundingMode.DOWN);
        BigDecimal pieceLengthGBFormat = pieceLengthMBFormat.divide(new BigDecimal(1024));
        pieceLengthGBFormat = pieceLengthGBFormat.setScale(2, RoundingMode.DOWN);
        if (pieceLengthGBFormat.compareTo(new BigDecimal(1)) > 0) {
            return pieceLengthGBFormat + " GB";
        } else if (pieceLengthMBFormat.compareTo(new BigDecimal(1)) > 0) {
            return pieceLengthMBFormat + " MB";
        } else if (pieceLengthKBFormat.compareTo(new BigDecimal(1)) > 0) {
            return pieceLengthKBFormat + " KB";
        } else {
            return pieceLengthBFormat + " B";
        }
    }

    public boolean addAnnounce(String announce) {
        return this.announces.add(announce);
    }

    public boolean addAnnounceList(Collection announces){
        return this.announces.addAll(announces);
    }
}
