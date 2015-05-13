package nl.openhack.contacthandshake;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import ezvcard.VCard;


public class IncomingHandshakeRequest extends ActionBarActivity {

    NdefMessage[] msgs;
    Intent intent;
    TextView vcardName;
    TextView vcardSummary;

    public void onResume() {
        super.onResume();
        intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            vcardName = (TextView)findViewById(R.id.textView6);
            vcardSummary = (TextView)findViewById(R.id.textView7);
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    try {
                        NdefHandshakeMessage handshakeMessage = NdefHandshakeMessage.createFromNdefMessage(msgs[i]);
                        vcardName.setText(handshakeMessage.getVCard().getFormattedName().getValue());
                        String summary = handshakeMessage.isDirect() ? "Response: direct mode" : "Response: direct mode, online mode";
                        vcardSummary.setText(summary);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_handshake_request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_incomming_handshake_request, menu);
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

}
