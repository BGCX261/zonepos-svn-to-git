package com.soulstrewn.zonepos.Utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

/**
 * User: jeff
 * Date: Mar 5, 2009
 * Time: 1:34:16 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public class CreditCard {

    public static boolean runCard(boolean swiped, String track1, String track2, String number, String expdate, int amount )
    {
    try{
      // standard variables for basic Java AIM test
      // use your own values where appropriate

      StringBuffer sb = new StringBuffer();

      // mandatory name/value pairs for all AIM CC transactions
      // as well as some "good to have" values
      sb.append("x_login=XXXXXXXXXXXX&");             // replace with your own
      sb.append("x_tran_key=XXXXXXXXXXXX&");     // replace with your own
      sb.append("x_cpversion=1.0&");
      sb.append("x_test_request=FALSE&");             // for testing
        sb.append("x_market_type=2&");
        sb.append("x_device_type=5&");             // for testing

      sb.append("x_amount=").append(String.format("%.2f", (float)amount/100)).append("&");
      sb.append("x_response_format=1&");
      sb.append("x_delim_char=|&");
      sb.append("x_relay_response=FALSE&");

      if(swiped)
      {
          if(track1 != null)
            sb.append("x_track1=").append(track1).append("&");
          if(track2 != null)
            sb.append("x_track2=").append(track2).append("&");
       } else {
      // CC information
      sb.append("x_card_num=").append(number).append("&");
      sb.append("x_exp_date=").append(expdate).append("&");
      }
      // not required...but my test account is set up to require it
      sb.append("x_user_ref=ZonePOS Transaction&");


      // open secure connection
      URL url = new URL(
          "https://test.authorize.net/gateway/transact.dll");
     //  Uncomment the line ABOVE for test accounts or BELOW for live merchant accounts
     //   https://secure.authorize.net/gateway/transact.dll

      /* NOTE: If you want to use SSL-specific features,change to:
          HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      */

     URLConnection connection = url.openConnection();
     connection.setDoOutput(true);
     connection.setUseCaches(false);

     // not necessarily required but fixes a bug with some servers
     connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
     
     // POST the data in the string buffer
     DataOutputStream out = new DataOutputStream( connection.getOutputStream() );
     out.write(sb.toString().getBytes());
     out.flush();
     out.close();

     // process and read the gateway response
     BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
     String line;
     line = in.readLine();
     in.close();	                     // no more data
     System.err.println(line);



     // ONLY FOR THOSE WHO WANT TO CAPTURE GATEWAY RESPONSE INFORMATION
     // make the reply readable (be sure to use the x_delim_char for the split operation)
     Vector ccrep = split("|", line);

     System.out.print("Response Code: ");
     System.out.println(ccrep.elementAt(0));
     System.out.print("Human Readable Response Code: ");
     System.out.println(ccrep.elementAt(3));
     System.out.print("Approval Code: ");
     System.out.println(ccrep.elementAt(4));
     System.out.print("Trans ID: ");
     System.out.println(ccrep.elementAt(6));
     if(ccrep.size() > 10)
     {
     System.out.print("MD5 Hash Server: ");
     System.out.println(ccrep.elementAt(37));
     }
        return ccrep.elementAt(1).equals("1");

    }catch(Exception e){
      e.printStackTrace();
    }
     return false;   
 }
  // utility functions
  public static Vector split(String pattern, String in){
          int s1=0, s2=-1;
          Vector out = new Vector(30);
          while(true){
                  s2 = in.indexOf(pattern, s1);
                  if(s2 != -1){
                          out.addElement(in.substring(s1, s2));
                  }else{
                          //the end part of the string (string not pattern terminated)
                          String _ = in.substring(s1);
                          if(_ != null && !_.equals("")){
                                  out.addElement(_);
                          }
                          break;
                  }
                  s1 = s2;
                  s1 += pattern.length();
          }
          return out;
  }

  // by Roedy Green (c)1996-2003 Canadian Mind Products
  public static String toHexString ( byte[] b ){
          StringBuffer sb = new StringBuffer( b.length * 2 );
          for ( int i=0 ; i<b.length ; i++ )
          {
                  // look up high nibble char
                  sb.append( hexChar [ ( b[ i] & 0xf0 ) >>> 4 ] ) ;

                  // look up low nibble char
                  sb.append( hexChar [ b[ i] & 0x0f ] ) ;
          }
          return sb.toString() ;
 }

// table to convert a nibble to a hex character
static char[] hexChar = {
'0' , '1' , '2' , '3' ,
'4' , '5' , '6' , '7' ,
'8' , '9' , 'A' , 'B' ,
'C' , 'D' , 'E' , 'F' }
;
}

