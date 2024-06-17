package com.fenbeitong.openapi.plugin.feishu.common.constant;

public interface FeiShuPlugIn {

    interface field {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
        public static final String INTERVAL = "interval";
        public static final String END = "end";
        public static final String START = "start";
    }

    interface type {
        public static final String textarea = "textarea";
        public static final String dateInterval = "dateInterval";
        public static final String address = "address";
    }

}
