package nl.openhack.contacthandshake;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.UnsupportedEncodingException;


public class SendHandshakeDialog extends ActionBarActivity implements
        NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    NfcAdapter mNfcAdapter;
    Button sendButton;
    protected final Context alertContext = this;

    public static final String VCARD = "BEGIN:VCARD\n" +
            "VERSION:2.1\n" +
            "N:van Boven;Robin;;;\n" +
            "FN:Robin van Boven\n" +
            "TEL;CELL:+31682057903\n" +
            "EMAIL;WORK:info@robin-it.com\n" +
            "URL:robin-it.com\n" +
            "END:VCARD\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_card_dialog);
        sendButton = (Button)findViewById(R.id.button3);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get the adapter we will use.
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check NFC and Android Beam is enabled.
        if(!mNfcAdapter.isEnabled() || !mNfcAdapter.isNdefPushEnabled()){
            showNFCSettingsAlert();
        }

        // Register callback to set NDEF message
        mNfcAdapter.setNdefPushMessageCallback(this, this);

        // Register callback to listen for message-sent success
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

        if(Build.VERSION.SDK_INT >= 21){
            sendButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_card_dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        HandshakeMessage handshakeMessage = HandshakeMessage.createDirectHandshake(VCARD);
        try {
            return handshakeMessage.toNdefMessage();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onNdefPushComplete(NfcEvent arg0)
    {
        startActivity(new Intent(this, WaitingForResponse.class));
    }

    protected void showNFCSettingsAlert(){
        new AlertDialog.Builder(this)
                .setTitle("NFC is not enabled")
                .setMessage("Please enable both NFC and Android Beam for this app.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(alertContext, HomePage.class));
                    }
                })
                .show();
    }

    @TargetApi(21)
    public void sendNow(View view){
        mNfcAdapter.invokeBeam(this);
    }

}
