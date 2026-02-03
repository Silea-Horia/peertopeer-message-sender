package com.example.datastructure;

import com.example.reader.PeerToPeerReader;

public interface ReaderSet {
    PeerToPeerReader addReader(PeerToPeerReader reader);

    void removeReader(String name);
}
