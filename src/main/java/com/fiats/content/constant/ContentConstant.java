package com.fiats.content.constant;

public class ContentConstant {

    public final static String REGEX_TO_DETECT_TABLE = "(?s)(.*?<\\/tr>.*?(?:#foreach\\(.*?\\))?)(.*?\\<(?:td|tr|\\/td|\\/tr).*?\\$(?:Table\\.)?(?:{0}).*?\\/tr>)((?:#end)?.*)";

    public final static String REGEX_TO_REMOVE_VELOCITY_FOREACH = "(?:\\#foreach\\(.*?\\)|\\#end|Table\\.)";
    public final static String TABLE_TEMPORARY_KEY = "$Table";
    public final static String FOREACH_START_FORMAT = "#foreach( " + TABLE_TEMPORARY_KEY + " in ${0} )";
    public final static String FOREACH_END_FORMAT = "#end";

    public final static String TABLE_HTML_END_TAG = "</table>";

    public final static String CACHE_URI = "/actuator/caches";

}
