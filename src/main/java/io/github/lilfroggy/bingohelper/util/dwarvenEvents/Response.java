package io.github.lilfroggy.bingohelper.util.dwarvenEvents;

import java.util.Map;

public class Response {
    public Data data;

    public class Data {
        public EventDatas event_datas;

        public class EventDatas {
            public Map<String, Event> DWARVEN_MINES;
        }
    }
}