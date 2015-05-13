package nl.openhack.contacthandshake;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.io.UnsupportedEncodingException;
import android.net.Uri;
import ezvcard.Ezvcard;
import ezvcard.VCard;

/**
 * Created by beanow on 09/05/15.
 */
public class NdefHandshakeMessage {

    public static NdefHandshakeMessage createDirectHandshake(String rawVCard){
        return new NdefHandshakeMessage(rawVCard);
    }

    public static NdefHandshakeMessage createOnlineHandshake(String brokerPostUrl, String senderId, String transactionKey, String rawVCard){
        //TODO: verify format of these inputs.
        return new NdefHandshakeMessage(brokerPostUrl, senderId, transactionKey, rawVCard);
    }

    public static NdefHandshakeMessage createFromNdefMessage(NdefMessage input)
            throws UnsupportedEncodingException, IllegalArgumentException {
        NdefRecord[] records = input.getRecords();
        NdefRecord record;

        //Record one: text/x-handshake-request
        record = records[0];
        if(!record.toMimeType().equals("text/x-handshake-request")){
            throw new IllegalArgumentException("First record of handshake message must be of MIME: text/x-handshake-request.");
        }

        //Parse it's information.
        String brokerPostUrl = null;
        String senderId = null;
        String transactionKey = null;
        Uri decoded = Uri.parse(new String(record.getPayload(), "UTF-8"));
        Boolean isDirect = decoded.getBooleanQueryParameter("direct", false);
        if(!isDirect){
            brokerPostUrl = decoded.getQueryParameter("broker_post_url");
            senderId = decoded.getQueryParameter("sender_id");
            transactionKey = decoded.getQueryParameter("transaction_key");
        }

        //Next up: text/x-vcard
        record = records[1];
        String rawVCard = new String(record.getPayload(), "UTF-8");

        if(isDirect){
            return NdefHandshakeMessage.createDirectHandshake(rawVCard);
        }
        else{
            return NdefHandshakeMessage.createOnlineHandshake(brokerPostUrl, senderId, transactionKey, rawVCard);
        }

    }

    protected String brokerPostUrl;
    protected String senderId;
    protected String transactionKey;
    protected Boolean isDirect;
    protected String rawVCard;

    public String getBrokerPostUrl() {
        return brokerPostUrl;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getTransactionKey() {
        return transactionKey;
    }

    public Boolean isDirect() {
        return isDirect;
    }

    public String getRawVCard() {
        return rawVCard;
    }

    public VCard getVCard(){
        return Ezvcard.parse(rawVCard).first();
    }

    protected NdefHandshakeMessage(String rawVCard){
        this.rawVCard = rawVCard;
        this.isDirect = true;
    }

    protected NdefHandshakeMessage(String brokerPostUrl, String senderId, String transactionKey, String rawVCard){
        this.brokerPostUrl = brokerPostUrl;
        this.senderId = senderId;
        this.transactionKey = transactionKey;
        this.rawVCard = rawVCard;
        this.isDirect = false;
    }

}
