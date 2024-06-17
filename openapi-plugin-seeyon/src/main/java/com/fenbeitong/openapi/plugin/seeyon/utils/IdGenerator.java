package com.fenbeitong.openapi.plugin.seeyon.utils;

import com.fenbeitong.openapi.plugin.seeyon.helper.Jsr310DateHelper;
import org.bson.types.ObjectId;

import java.util.UUID;

/**
 * IdGenerator
 *
 * <p>Id生成类
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/4/19 - 10:50 AM.
 */
public class IdGenerator {

  public static String getId32bit() {

    return UUID.randomUUID().toString().replace("-", "").toLowerCase();
  }

  public static String getId32bitTime() {
    ObjectId objectId = new ObjectId();
    String dtm = Jsr310DateHelper.getDateTime8Length();
    return dtm + objectId.toString();
  }

  public static String getId24bit() {
    ObjectId objectId = new ObjectId();
    return objectId.toString();
  }

  public static void main(String args[]) {
    System.out.println(getId32bitTime());
  }
}
