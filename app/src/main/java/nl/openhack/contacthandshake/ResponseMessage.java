package nl.openhack.contacthandshake;

import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.io.UnsupportedEncodingException;

import ezvcard.Ezvcard;
import ezvcard.VCard;

/**
 * Created by beanow on 15/05/15.
 */
public class ResponseMessage {

    public static final String RESPONSE_MIME = "text/x-handshake-response";
    public static final String VCARD_MIME = "text/x-vcard";
    public static final String MAGIC_URI_PREFIX = "?";

    public static ResponseMessage createDirectResponse(String rawVCard){
        return new ResponseMessage(rawVCard);
    }

    public static ResponseMessage createFromNdefMessage(NdefMessage input)
            throws UnsupportedEncodingException, IllegalArgumentException {

        NdefRecord[] records = input.getRecords();
        NdefRecord record;

        //Record one: text/x-handshake-response
        record = records[0];
        if(!record.toMimeType().equals(RESPONSE_MIME)){
            throw new IllegalArgumentException("First record of response message must be of MIME: "+RESPONSE_MIME+".");
        }

        //Parse it's information.
        Uri decoded = Uri.parse(MAGIC_URI_PREFIX+new String(record.getPayload(), "UTF-8"));
        Boolean isDirect = decoded.getBooleanQueryParameter("direct", false);

        //Next up: text/x-vcard
        record = records[1];
        String rawVCard = new String(record.getPayload(), "UTF-8");

        return ResponseMessage.createDirectResponse(rawVCard);

    }

    protected String rawVCard;

    public String getRawVCard() {
        return this.rawVCard;
    }

    public VCard getVCard(){
        return Ezvcard.parse(this.rawVCard).first();
    }

    protected ResponseMessage(String rawVCard){
        this.rawVCard = rawVCard;
    }

    public String buildHandshakeBody(){
        Uri.Builder handshakeBody = new Uri.Builder();
        handshakeBody.appendQueryParameter("direct", "1");
        return handshakeBody.build().toString().substring(MAGIC_URI_PREFIX.length());
    }

    public NdefMessage toNdefMessage() throws UnsupportedEncodingException {
        return new NdefMessage(
                NdefRecord.createMime(RESPONSE_MIME, this.buildHandshakeBody().getBytes("UTF-8")),
                NdefRecord.createMime(VCARD_MIME, this.rawVCard.getBytes("UTF-8")),
                NdefRecord.createApplicationRecord(this.getClass().getPackage().getName())
        );
    }

}
