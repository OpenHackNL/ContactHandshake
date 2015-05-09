package nl.openhack.mutualcontactexchange;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.nio.charset.Charset;


public class SendCardDialog extends ActionBarActivity implements
        NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    NfcAdapter mNfcAdapter;

    public static String VCARD = "BEGIN:VCARD\n" +
            "VERSION:2.1\n" +
            "N:van Boven;Robin;;;\n" +
            "FN:Robin van Boven\n" +
            "TEL;CELL:+31682057903\n" +
            "EMAIL;WORK:info@robin-it.com\n" +
            "URL:robin-it.com\n" +
            "END:VCARD\n"; // Not final because it can change during runtime.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_card_dialog);

        // Get the adapter we will use.
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Register callback to set NDEF message
        mNfcAdapter.setNdefPushMessageCallback(this, this);

        // Register callback to listen for message-sent success
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

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
        return new NdefMessage(new NdefRecord[] { getContactRecord() });
    }

    /**
     * Generates an NdefRecord corresponding to the current contact, as defined in
     * the Beam.VCARD static variable.
     * @return NdefRecord containing the contact info to beam
     */
    private NdefRecord getContactRecord()
    {
        if (VCARD.isEmpty())  // If no contact is set...
        {
            // Send an "empty" contact (vcard).
            // On receiving device, user will be prompted create a new contact.
            return null;
        }
        else  // Send the set contact.
        {
            byte[] payload = VCARD.getBytes(Charset.forName("UTF-8"));
            NdefRecord nfcRecord = NdefRecord.createMime("text/vcard", payload);
            return nfcRecord;
        }
    }

    @Override
    public void onNdefPushComplete(NfcEvent arg0)
    {
        toast("Sent?!");
    }

    /**
     * Toast simplification.
     * @param message
     */
    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
