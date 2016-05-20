package com.ajb.merchants.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by jerry on 16/5/20.
 */
public class PagerWithHeader implements Serializable {
    public Pager<Map<String, String>> pager;
    public List<Map<String, String>> headers;
}
