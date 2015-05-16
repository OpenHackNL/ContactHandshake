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
public class HandshakeMessage {

    public static final String HANDSHAKE_MIME = "text/x-handshake-request";
    public static final String VCARD_MIME = "text/x-vcard";
    public static final String MAGIC_URI_PREFIX = "?";

    public static HandshakeMessage createDirectHandshake(String rawVCard){
        return new HandshakeMessage(rawVCard);
    }

    public static HandshakeMessage createOnlineHandshake(String brokerPostUrl, String senderId, String transactionKey, String rawVCard){
        //TODO: verify format of these inputs.
        return new HandshakeMessage(brokerPostUrl, senderId, transactionKey, rawVCard);
    }

    public static HandshakeMessage createFromNdefMessage(NdefMessage input)
            throws UnsupportedEncodingException, IllegalArgumentException {

        NdefRecord[] records = input.getRecords();
        NdefRecord record;

        //Record one: text/x-handshake-request
        record = records[0];
        if(!record.toMimeType().equals(HANDSHAKE_MIME)){
            throw new IllegalArgumentException("First record of handshake message must be of MIME: "+HANDSHAKE_MIME+".");
        }

        //Parse it's information.
        String brokerPostUrl = null;
        String senderId = null;
        String transactionKey = null;
        Uri decoded = Uri.parse(MAGIC_URI_PREFIX+new String(record.getPayload(), "UTF-8"));
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
            return HandshakeMessage.createDirectHandshake(rawVCard);
        }
        else{
            return HandshakeMessage.createOnlineHandshake(brokerPostUrl, senderId, transactionKey, rawVCard);
        }

    }

    protected String brokerPostUrl;
    protected String senderId;
    protected String transactionKey;
    protected Boolean isDirect;
    protected String rawVCard;

    public String getBrokerPostUrl() { return this.brokerPostUrl; }

    public String getSenderId() { return this.senderId; }

    public String getTransactionKey() { return this.transactionKey; }

    public Boolean isDirect() { return this.isDirect; }

    public String getRawVCard() {
        return this.rawVCard;
    }

    public VCard getVCard(){
        return Ezvcard.parse(this.rawVCard).first();
    }

    protected HandshakeMessage(String rawVCard){
        this.rawVCard = rawVCard;
        this.isDirect = true;
    }

    protected HandshakeMessage(String brokerPostUrl, String senderId, String transactionKey, String rawVCard){
        this.brokerPostUrl = brokerPostUrl;
        this.senderId = senderId;
        this.transactionKey = transactionKey;
        this.rawVCard = rawVCard;
        this.isDirect = false;
    }

    public String buildHandshakeBody(){
        Uri.Builder handshakeBody = new Uri.Builder();
        if(this.isDirect){
            handshakeBody.appendQueryParameter("direct", "1");
        }
        else{
            handshakeBody.appendQueryParameter("broker_post_url", this.brokerPostUrl);
            handshakeBody.appendQueryParameter("sender_id", this.senderId);
            handshakeBody.appendQueryParameter("transaction_key", this.transactionKey);
        }
        return handshakeBody.build().toString().substring(MAGIC_URI_PREFIX.length());
    }

    public NdefMessage toNdefMessage() throws UnsupportedEncodingException {
        return new NdefMessage(
                NdefRecord.createMime(HANDSHAKE_MIME, this.buildHandshakeBody().getBytes("UTF-8")),
                NdefRecord.createMime(VCARD_MIME, this.rawVCard.getBytes("UTF-8")),
                NdefRecord.createApplicationRecord(this.getClass().getPackage().getName())
        );
    }

}
