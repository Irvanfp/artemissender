package com.irvan.ArtemisMassSender.model;

import lombok.Data;

@Data
public class Watosmscontroller {
    String trxid;
    String senderid;
    String msisdn;
    int error_code;
    String error_details;
    String error_title;
    long timestamp;
    String messageId;
    String client_id;
    String division_id;
    String waproviderTrxId;
}
