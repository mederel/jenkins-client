package com.offbytwo.jenkins.utils;

import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

public class URLUtils {
  private URLUtils() {
    
  }
  
  public static String encode(String pathPart) {
    // jenkins doesn't like the + for space, use %20 instead
    return URLEncoder.encode(StringUtils.trim(pathPart)).replaceAll("\\+","%20");
  }

}
