package com.fenbeitong.openapi.plugin.seeyon.constant;

/**
 * SignatureConstants
 *
 * <p>OpenApi 签名常量类
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/7/19 - 4:01 PM.
 */
public interface SignatureConstants {
  /* Validating Sign Ref. */
  /** Valid Timestamp Gap Compare to Current */
  Long TIMESTAMP_VALID_GAP = 60 * 60 * 1000L;
  /** Calculation Append */
  String CALCULATE_SIGN_TIMESTAMP = "timestamp=";
  /** Calculation Append */
  String CALCULATE_SIGN_DATA = "&data=";
  /** Calculation Append */
  String CALCULATE_SIGN_SIGNKEY = "&sign_key=";
}
