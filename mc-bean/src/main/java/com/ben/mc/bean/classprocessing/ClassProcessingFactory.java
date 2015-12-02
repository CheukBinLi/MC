package com.ben.mc.bean.classprocessing;

import java.util.Set;

public interface ClassProcessingFactory<C> {

	public static final String Impl = "$MC_IMPL";

	public static final String FULL_NAME_BEAN = "$FULL_NAME_BEAN";
	public static final String NICK_NAME_BEAN = "$NICK_NAME_BEAN";

	public static final String SHORT_NAME_BEAN = "$SHORT_NAME_BEAN";

	public static final String REGISTER_CACHE = "REGISTER_CACHE";
	public static final String AUTO_LOAD_CACHE = "AUTO_LOAD_CACHE";
	public static final String NICK_NAME_CACHE = "NICK_NAME_CACHE";
	public static final String SHORT_NAME_CACHE = "SHORT_NAME_CACHE";
	public static final String CLASS_INFO_CACHE = "$CLASS_INFO_CACHE";
	public static final String XML_CONFIG_CACHE = "$XML_CONFIG_CACHE";

	C getCompleteClass(Set<String> clazzs, Object config) throws Throwable;

}
