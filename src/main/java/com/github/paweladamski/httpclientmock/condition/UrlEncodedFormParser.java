package com.github.paweladamski.httpclientmock.condition;

import com.github.paweladamski.httpclientmock.Request;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URLEncodedUtils;

public class UrlEncodedFormParser {

  List<NameValuePair> parse(Request request) {
    if (!requestHasBody(request)) {
      return Collections.emptyList();
    }

    HttpEntityContainer httpRequest = (HttpEntityContainer) request.getHttpRequest();
    HttpEntity entity = httpRequest.getEntity();
    return parse(entity);
  }

  public List<NameValuePair> parse(HttpEntity entity) {
    if (entity == null) {
      return Collections.emptyList();
    }

    try {
      /*
       * The method below returns an empty list if the Content-Type of the
       * request is not "application/x-www-form-urlencoded". So, requests with
       * other kinds of data in the body will correctly be ignored here.
       */
      if (!entity.getContentType().contains(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
        return Collections.emptyList();
      }
      String entityContent = EntityUtils.toString(entity);
      return URLEncodedUtils.parse(entityContent, StandardCharsets.UTF_8);
    } catch (IOException | ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean requestHasBody(Request r) {
    return (r.getHttpRequest() instanceof HttpEntityContainer);
  }
}
