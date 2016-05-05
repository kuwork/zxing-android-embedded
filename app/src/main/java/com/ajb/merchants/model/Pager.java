package com.ajb.merchants.model;

import java.io.Serializable;
import java.util.List;

public class Pager<T> implements Serializable {
	public List<T> list;
	public int rowCount;
	public int pageCount;
	public int rows;
	public int page;
}
