package com.example.datastructure;

import com.example.reader.PeerToPeerReader;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentReaderSet implements ReaderSet {
    private static final Set<PeerToPeerReader> readers = ConcurrentHashMap.newKeySet();

    public PeerToPeerReader addReader(PeerToPeerReader reader) {
        readers.add(reader);
        return reader;
    }

    public void removeReader(String name) {
        readers.removeIf(t -> t.name().equals(name));
    }
}
