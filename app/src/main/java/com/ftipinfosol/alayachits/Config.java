package com.ftipinfosol.alayachits;

public class Config {

//    private static String BASE_URL = "http://alaya-stag-2008071.sf3.in/api/customers_api_v2/";
//    public static String DOWNLOAD_STATEMENT_URL = "http://alaya-stag-2008071.sf3.in/ledger_view_statement/";
      private static String BASE_URL = "http://192.168.1.8:8000/api/customers_api_v2/";
      public static String DOWNLOAD_STATEMENT_URL = "http://192.168.1.8:8000/ledger_view_statement/";
      private static String URL = "http://192.168.1.8:8000/";

//    private static String BASE_URL = "http://app.sreealayachits.com/api/customers_api_v2/";
//    public static String DOWNLOAD_STATEMENT_URL = "http://app.sreealayachits.com/ledger_view_statement/";
//    private static String URL = "http://app.sreealayachits.com/";

    public static String OTP_URL = BASE_URL+"get_otp";
    public static String VERIFY_URL = BASE_URL+"verify_otp";
    public static String TICKETS_URL = BASE_URL+"my_tickets";
    public static String LEDGER_URL = BASE_URL+"ledger";
    public static String REPORT_URL = BASE_URL+"report";
    public static String CHIT_URL = BASE_URL+"chit";
    public static String SCHEMES_URL = BASE_URL+"schemes";
    public static String SCHEME_DETAILS_URL = BASE_URL+"scheme_details/";
    public static String PAYMENT_REQUEST = BASE_URL+"payment_request";
    public static String NEW_CHIT_REQUEST = BASE_URL+"chit_request";
    public static String DOWNLOAD_STATEMENT = BASE_URL+"ledger_view/";
    public static String LEDGER_EXTRACT = BASE_URL+"ledger_extract/";


    public static String GET_PAYTM_CHECKSM = URL +"BlueInitiateTransaction.php";
    public static String PAYTM_COLLECTION = BASE_URL+"savePaytmCollection";

}
