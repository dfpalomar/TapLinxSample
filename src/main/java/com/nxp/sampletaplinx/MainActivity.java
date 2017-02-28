/*
 * =============================================================================
 *
 *                       Copyright (c), NXP Semiconductors
 *
 *                        (C)NXP Electronics N.V.2013
 *         All rights are reserved. Reproduction in whole or in part is
 *        prohibited without the written consent of the copyright owner.
 *    NXP reserves the right to make changes without notice at any time.
 *   NXP makes no warranty, expressed, implied or statutory, including but
 *   not limited to any implied warranty of merchantability or fitness for any
 *  particular purpose, or that the use will not infringe any third party patent,
 *   copyright or trademark. NXP must not be liable for any loss or damage
 *                            arising from its use.
 *
 * =============================================================================
 */

package com.nxp.sampletaplinx;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.nxp.nfclib.CardType;
import com.nxp.nfclib.CustomModules;
import com.nxp.nfclib.KeyType;
import com.nxp.nfclib.NxpNfcLib;
import com.nxp.nfclib.classic.ClassicFactory;
import com.nxp.nfclib.classic.IMFClassic;
import com.nxp.nfclib.classic.IMFClassicEV1;
import com.nxp.nfclib.defaultimpl.KeyData;
import com.nxp.nfclib.desfire.DESFireFactory;
import com.nxp.nfclib.desfire.DESFireFile;
import com.nxp.nfclib.desfire.EV1ApplicationKeySettings;
import com.nxp.nfclib.desfire.EV2ApplicationKeySettings;
import com.nxp.nfclib.desfire.EV2PICCConfigurationSettings;
import com.nxp.nfclib.desfire.IDESFireEV1;
import com.nxp.nfclib.desfire.IDESFireEV2;
import com.nxp.nfclib.exceptions.NxpNfcLibException;
import com.nxp.nfclib.icode.ICode;
import com.nxp.nfclib.icode.ICodeFactory;
import com.nxp.nfclib.icode.IICodeDNA;
import com.nxp.nfclib.icode.IICodeSLI;
import com.nxp.nfclib.icode.IICodeSLIL;
import com.nxp.nfclib.icode.IICodeSLIS;
import com.nxp.nfclib.icode.IICodeSLIX;
import com.nxp.nfclib.icode.IICodeSLIX2;
import com.nxp.nfclib.icode.IICodeSLIXL;
import com.nxp.nfclib.icode.IICodeSLIXS;
import com.nxp.nfclib.interfaces.IKeyData;
import com.nxp.nfclib.ndef.NdefMessageWrapper;
import com.nxp.nfclib.ndef.NdefRecordWrapper;
import com.nxp.nfclib.ntag.INTAGI2Cplus;
import com.nxp.nfclib.ntag.INTag;
import com.nxp.nfclib.ntag.INTag203x;
import com.nxp.nfclib.ntag.INTag210;
import com.nxp.nfclib.ntag.INTag210u;
import com.nxp.nfclib.ntag.INTag213215216;
import com.nxp.nfclib.ntag.INTag213F216F;
import com.nxp.nfclib.ntag.INTagI2C;
import com.nxp.nfclib.ntag.NTagFactory;
import com.nxp.nfclib.plus.IPlus;
import com.nxp.nfclib.plus.IPlusEV1SL0;
import com.nxp.nfclib.plus.IPlusEV1SL1;
import com.nxp.nfclib.plus.IPlusEV1SL3;
import com.nxp.nfclib.plus.IPlusSL0;
import com.nxp.nfclib.plus.IPlusSL1;
import com.nxp.nfclib.plus.IPlusSL3;
import com.nxp.nfclib.plus.PlusFactory;
import com.nxp.nfclib.plus.PlusSL1Factory;
import com.nxp.nfclib.plus.ValueBlockInfo;
import com.nxp.nfclib.ultralight.IUltralight;
import com.nxp.nfclib.ultralight.IUltralightC;
import com.nxp.nfclib.ultralight.IUltralightEV1;
import com.nxp.nfclib.ultralight.IUltralightNano;
import com.nxp.nfclib.ultralight.UltralightFactory;
import com.nxp.nfclib.utils.NxpLogUtils;
import com.nxp.nfclib.utils.Utilities;
import com.yoti.nxp.test.R;
import java.io.File;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author nxp70496 Main start activity.
 */
public class MainActivity extends Activity {

    public static final String TAG = "SampleTapLinx";

    private IKeyData objKEY_2KTDES_ULC = null;
    private IKeyData objKEY_2KTDES = null;
    private IKeyData objKEY_AES128 = null;
    private byte[] default_ff_key = null;
    private IKeyData default_zeroes_key = null;

    private static final String ALIAS_KEY_AES128 = "key_aes_128";
    private static final String ALIAS_KEY_2KTDES = "key_2ktdes";
    private static final String ALIAS_KEY_2KTDES_ULC = "key_2ktdes_ulc";
    private static final String ALIAS_DEFAULT_FF = "alias_default_ff";
    private static final String ALIAS_KEY_AES128_ZEROES = "alias_default_00";
    private static final String EXTRA_KEYS_STORED_FLAG = "keys_stored_flag";

    /**
     * NDEF MESSAGE DATA !!
     */

    static String ndefData = "Mifare";

    static String ndefDataslix2 = "MifareSDKTeamMifareSDKTeamMifareSDKTeamMifareSDKTeamMifareSDKTeamMifareSDKTeamMifareSDKTeamMifareSDKTeamMifareSDKTeamMifareSDKTeam" +
            "MifareSDKTeamMifar";


    /**
     * Package Key.
     */
    static String packageKey = "a81f56f88e23e83d4617c23686cd9d22";

    /**
     * KEY_APP_MASTER key used for encrypt data.
     */
    private static final String KEY_APP_MASTER = "This is my key  ";
    /**
     *
     */
    private static final String DATA = "This is the data";
    /**
     * Classic sector number set to 6.
     */
    private static final int DEFAULT_SECTOR_CLASSIC = 6;
    /**
     * Ultralight First User Memory Page Number.
     */
    private static final int DEFAULT_PAGENO_ULTRALIGHT = 4;
    /**
     * NxpNfclib instance.
     */
    private NxpNfcLib libInstance = null;
    /**
     * bytes key.
     */
    private byte[] bytesKey = null;
    /**
     * Cipher instance.
     */
    private Cipher cipher = null;
    /**
     * Iv.
     */
    private IvParameterSpec iv = null;
    /**
     * text view instance.
     */
    private TextView tv = null;
    /**
     * Image view inastance.
     */
    private ImageView mImageView = null;
    /**
     * byte array.
     */
    byte[] data;

    /**
     * Desfire card object.
     */
    private IDESFireEV1 desFireEV1;

    private IDESFireEV2 desFireEV2;
    /**
     * MFClassic card object.
     */
    private IMFClassic mifareClassic;

    /**
     * MFClassic EV1 Card
     */
    private IMFClassicEV1 mifareClassicEv1 = null;


    /**
     * Ultralight card object.
     */
    private IUltralight ultralightBase;
    /**
     * UltralightC card object.
     */
    private IUltralightC ultralightC;
    /**
     * UltralightEV1 card object.
     */
    private IUltralightEV1 ultralightEV1;
    /**
     * NTag203x card object.
     */
    private INTag203x nTAG203x;
    /**
     * NTag210 card object.
     */
    private INTag210 nTAG210;
    /**
     * NTag210u card object.
     */
    private INTag210u nTAG210u;
    /**
     * NTagI2C card object.
     */
    private INTagI2C nTAGI2C;
    /**
     * NTagI2C Plus card object.
     */
    private INTAGI2Cplus nTAGI2CPlus;
    /**
     * NTag213215216 card object.
     */
    private INTag213215216 nTAG213215216;
    /**
     * NTag213F216F card object.
     */
    private INTag213F216F nTAG213F216F;

    /**
     * ICodeSLI card object.
     */
    private IICodeSLI icodeSLI;
    /**
     * ICodeSLIS card object.
     */
    private IICodeSLIS icodeSLIS;
    /**
     * ICodeSLIL card object.
     */
    private IICodeSLIL icodeSLIL;
    /**
     * ICodeSLIX card object.
     */
    private IICodeSLIX icodeSLIX;
    /**
     * ICodeSLIXS card object.
     */
    private IICodeSLIXS icodeSLIXS;
    /**
     * ICodeSLIXL card object.
     */
    private IICodeSLIXL icodeSLIXL;
    /**
     * ICodeSLIX2 card object.
     */
    private IICodeSLIX2 icodeSLIX2;
    /**
     * ICodeDNA card object.
     */
    private IICodeDNA icodeDNA;

    private IUltralightNano ultralightNano;

    private IPlusSL0 plusSL0 = null;

    private IPlusSL1 plusSL1 = null;


    private IPlusEV1SL3 plusEV1SL3;

    private IPlusEV1SL0 plusEV1SL0;

    private IPlusEV1SL1 plusEV1SL1;

    private IPlusSL3 plusSL3 = null;
    /**
     * Checkbox for write select
     */
    CheckBox mCheckToWrite;
    /**
     * Constant for permission
     */
    private static final int STORAGE_PERMISSION_WRITE = 113;

    /**
     * Android Handler for handling messages from the threads.
     */
    private static Handler mHandler;

    private boolean bWriteAllowed = false;

    private boolean mIsPerformingCardOperations = false;


    private CardType mCardType = CardType.UnknownCard;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        boolean readPermission = (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (!readPermission) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_WRITE
            );
        }

        /* Initialize the library and register to this activity */
        initializeLibrary();

        initializeKeys();

		/* Initialize the Cipher and init vector of 16 bytes with 0xCD */
        initializeCipherinitVector();

		/* Get text view handle to be used further */
        initializeView();

		/* Set the UI handler */
        initializeUIhandler();

        /* Setup the About buttton handler */
        readMeHelpAbout();

		/* Show About dialog */
//            showDisclaimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initializeKeys() {
        KeyInfoProvider infoProvider = KeyInfoProvider.getInstance(getApplicationContext());

        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        boolean keysStoredFlag = sharedPrefs.getBoolean(EXTRA_KEYS_STORED_FLAG, false);
        if (!keysStoredFlag) {
            //Set Key stores the key in persistent storage, this method can be called only once if key for a given alias does not change.
            byte[] ulc24Keys = new byte[24];
            System.arraycopy(SampleAppKeys.KEY_2KTDES_ULC, 0, ulc24Keys, 0, SampleAppKeys.KEY_2KTDES_ULC.length);
            System.arraycopy(SampleAppKeys.KEY_2KTDES_ULC, 0, ulc24Keys, SampleAppKeys.KEY_2KTDES_ULC.length, 8);
            infoProvider.setKey(ALIAS_KEY_2KTDES_ULC, SampleAppKeys.EnumKeyType.EnumDESKey, ulc24Keys);

            infoProvider.setKey(ALIAS_KEY_2KTDES, SampleAppKeys.EnumKeyType.EnumDESKey, SampleAppKeys.KEY_2KTDES);
            infoProvider.setKey(ALIAS_KEY_AES128, SampleAppKeys.EnumKeyType.EnumAESKey, SampleAppKeys.KEY_AES128);
            infoProvider.setKey(ALIAS_KEY_AES128_ZEROES, SampleAppKeys.EnumKeyType.EnumAESKey, SampleAppKeys.KEY_AES128_ZEROS);
            infoProvider.setKey(ALIAS_DEFAULT_FF, SampleAppKeys.EnumKeyType.EnumMifareKey, SampleAppKeys.KEY_DEFAULT_FF);

            sharedPrefs.edit().putBoolean(EXTRA_KEYS_STORED_FLAG, true).commit();
            //If you want to store a new key after key initialization above, kindly reset the flag EXTRA_KEYS_STORED_FLAG to false in shared preferences.
        }


        objKEY_2KTDES_ULC = infoProvider.getKey(ALIAS_KEY_2KTDES_ULC, SampleAppKeys.EnumKeyType.EnumDESKey);
        objKEY_2KTDES = infoProvider.getKey(ALIAS_KEY_2KTDES, SampleAppKeys.EnumKeyType.EnumDESKey);
        objKEY_AES128 = infoProvider.getKey(ALIAS_KEY_AES128, SampleAppKeys.EnumKeyType.EnumAESKey);
        default_zeroes_key = infoProvider.getKey(ALIAS_KEY_AES128_ZEROES, SampleAppKeys.EnumKeyType.EnumAESKey);
        default_ff_key = infoProvider.getMifareKey(ALIAS_DEFAULT_FF);
    }

    /**
     * Initializing the UI thread.
     */
    private void initializeUIhandler() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mImageView.getLayoutParams().width = (size.x * 2) / 3;
        mImageView.getLayoutParams().height = size.y / 3;

        mHandler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                Log.i(TAG, "inside handleMessage, msg.what is " + msg.what);
                String messageResponse = msg.getData().getString("message");
                char where = msg.getData().getChar("where");
                if (messageResponse != null) {
                    showMessage(messageResponse, where);
                }
            }
        };

        mImageView.setImageResource(R.drawable.product_overview);
    }


    /**
     * Initializing the widget, and Get text view handle to be used further.
     */
    private void initializeView() {

		/* Get text view handle to be used further */
        tv = (TextView) findViewById(R.id.tvLog);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(R.string.info_string);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/AvenirNextLTPro-MediumCn.otf");
        tv.setTypeface(face);
        tv.setTextColor(Color.BLACK);

		/* Get image view handle to be used further */
        mImageView = (ImageView) findViewById(R.id.cardSnap);

        mCheckToWrite = (CheckBox) findViewById(R.id.checkToWrite);

        mCheckToWrite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!bWriteAllowed) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            MainActivity.this);
                    alertDialog.setCancelable(false);

                    // Setting Dialog Title
                    alertDialog.setTitle(R.string.write_ops_title);

                    // Setting Dialog Message
                    alertDialog.setMessage(getString(R.string.write_ops_message));

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.nxp_logo);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("Allow",
                            new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog,
                                                    final int which) {

                                    bWriteAllowed = true;

                                }

                            });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog,
                                                    final int which) {
                                    bWriteAllowed = false;
                                    // Write your code here to invoke NO event
                                    mCheckToWrite.setChecked(false);
                                    dialog.cancel();
                                }
                            });

                    // Showing Alert Message
                    alertDialog.show();

                } else {
                    bWriteAllowed = false;
                }
            }
        });


    }

    /**
     * Initialize the library and register to this activity.
     */
    @TargetApi(19)
    private void initializeLibrary() {
        libInstance = NxpNfcLib.getInstance();
        try {
            libInstance.registerActivity(this, packageKey);
        } catch (NxpNfcLibException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initialize the Cipher and init vector of 16 bytes with 0xCD.
     */

    private void initializeCipherinitVector() {

		/* Initialize the Cipher */
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

		/* set Application Master Key */
        bytesKey = KEY_APP_MASTER.getBytes();

		/* Initialize init vector of 16 bytes with 0xCD. It could be anything */
        byte[] ivSpec = new byte[16];
        Arrays.fill(ivSpec, (byte) 0xCD);
        iv = new IvParameterSpec(ivSpec);

    }

    /**
     * (non-Javadoc).
     *
     * @param intent NFC intent from the android framework.
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    public void onNewIntent(final Intent intent) {
        cardLogic(intent);
        super.onNewIntent(intent);
    }


    private void cardLogic(final Intent intent) {
        CardType type = CardType.UnknownCard;
        try {
            type = libInstance.getCardType(intent);
        } catch (NxpNfcLibException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        switch (type) {

            case MIFAREClassic: {
                if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    if (tag != null) {
                        mifareClassic = ClassicFactory.getInstance().getClassic(MifareClassic.get(tag));
                        mCardType = CardType.MIFAREClassic;
                        classicCardLogic();
                    }
                }
                break;
            }

            case MIFAREClassicEV1: {
                if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    if (tag != null) {
                        mifareClassicEv1 = ClassicFactory.getInstance().getClassicEV1(MifareClassic.get(tag));
                        mCardType = CardType.MIFAREClassicEV1;
                        classicCardEV1Logic();
                    }
                }
                break;
            }


            case Ultralight:
                ultralightBase = UltralightFactory.getInstance().getUltralight(libInstance.getCustomModules());
                mCardType = CardType.Ultralight;
                try {
                    ultralightBase.getReader().connect();
                    ultralightCardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case UltralightEV1_11:
                mCardType = CardType.UltralightEV1_11;
                ultralightEV1 = UltralightFactory.getInstance().getUltralightEV1(libInstance.getCustomModules());
                try {
                    ultralightEV1.getReader().connect();
                    ultralightEV1CardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case UltralightEV1_21:
                mCardType = CardType.UltralightEV1_21;
                ultralightEV1 = UltralightFactory.getInstance().getUltralightEV1(libInstance.getCustomModules());
                try {
                    ultralightEV1.getReader().connect();
                    ultralightEV1CardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case UltralightC:
                mCardType = CardType.UltralightC;
                ultralightC = UltralightFactory.getInstance().getUltralightC(libInstance.getCustomModules());
                try {
                    ultralightC.getReader().connect();
                    ultralightcCardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTag203X:
                mCardType = CardType.NTag203X;
                nTAG203x = NTagFactory.getInstance().getNTAG203x(libInstance.getCustomModules());
                try {
                    nTAG203x.getReader().connect();
                    ntagCardLogic(nTAG203x);
                } catch (Throwable t) {

                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTag210:
                mCardType = CardType.NTag210;
                nTAG210 = NTagFactory.getInstance().getNTAG210(libInstance.getCustomModules());
                try {
                    nTAG210.getReader().connect();
                    ntagCardLogic(nTAG210);
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTag213:
                mCardType = CardType.NTag213;
                nTAG213215216 = NTagFactory.getInstance().getNTAG213(libInstance.getCustomModules());
                try {
                    nTAG213215216.getReader().connect();
                    ntagCardLogic(nTAG213215216);
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTag215:
                mCardType = CardType.NTag215;
                nTAG213215216 = NTagFactory.getInstance().getNTAG215(libInstance.getCustomModules());
                try {
                    nTAG213215216.getReader().connect();
                    ntagCardLogic(nTAG213215216);
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTag216:
                mCardType = CardType.NTag216;
                nTAG213215216 = NTagFactory.getInstance().getNTAG216(libInstance.getCustomModules());
                try {
                    nTAG213215216.getReader().connect();
                    ntagCardLogic(nTAG213215216);
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTag213F:
                mCardType = CardType.NTag213F;
                nTAG213F216F = NTagFactory.getInstance().getNTAG213F(libInstance.getCustomModules());

                try {
                    nTAG213F216F.getReader().connect();
                    ntagCardLogic(nTAG213F216F);
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTag216F:
                mCardType = CardType.NTag216F;
                nTAG213F216F = NTagFactory.getInstance().getNTAG216F(libInstance.getCustomModules());

                try {
                    nTAG213F216F.getReader().connect();
                    ntagCardLogic(nTAG213F216F);
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTagI2C1K:
                mCardType = CardType.NTagI2C1K;
                nTAGI2C = NTagFactory.getInstance().getNTAGI2C1K(libInstance.getCustomModules());
                try {
                    nTAGI2C.getReader().connect();
                    ntagCardLogic(nTAGI2C);
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTagI2C2K:
                mCardType = CardType.NTagI2C2K;
                nTAGI2C = NTagFactory.getInstance().getNTAGI2C2K(libInstance.getCustomModules());
                try {
                    nTAGI2C.getReader().connect();
                    ntagCardLogic(nTAGI2C);
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTagI2CPlus1K:
                mCardType = CardType.NTagI2CPlus1K;
                nTAGI2CPlus = NTagFactory.getInstance().getNTAGI2CPlus1K(libInstance.getCustomModules());
                try {
                    nTAGI2CPlus.getReader().connect();
                    ntagCardLogic(nTAGI2CPlus);
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTagI2CPlus2K:
                mCardType = CardType.NTagI2CPlus2K;
                nTAGI2CPlus = NTagFactory.getInstance().getNTAGI2CPlus2K(libInstance.getCustomModules());
                try {
                    nTAGI2CPlus.getReader().connect();
                    ntagCardLogic(nTAGI2CPlus);
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case NTag210u:
                mCardType = CardType.NTag210u;
                nTAG210u = NTagFactory.getInstance().getNTAG210u(libInstance.getCustomModules());
                try {
                    nTAG210u.getReader().connect();
                    ntagCardLogic(nTAG210u);
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case ICodeSLI:
                mCardType = CardType.ICodeSLI;
                icodeSLI = ICodeFactory.getInstance().getICodeSLI(libInstance.getCustomModules());
                try {
                    icodeSLI.getReader().connect();
                    iCodeSLICardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case ICodeSLIS:
                mCardType = CardType.ICodeSLIS;
                icodeSLIS = ICodeFactory.getInstance().getICodeSLIS(libInstance.getCustomModules());
                try {
                    if (!icodeSLIS.getReader().isConnected())
                        icodeSLIS.getReader().connect();
                    iCodeSLISCardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case ICodeSLIL:
                mCardType = CardType.ICodeSLIL;
                icodeSLIL = ICodeFactory.getInstance().getICodeSLIL(libInstance.getCustomModules());
                try {
                    icodeSLIL.getReader().connect();
                    iCodeSLILCardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case ICodeSLIX:
                mCardType = CardType.ICodeSLIX;
                icodeSLIX = ICodeFactory.getInstance().getICodeSLIX(libInstance.getCustomModules());
                try {
                    icodeSLIX.getReader().connect();
                    iCodeSLIXCardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case ICodeSLIXS:
                mCardType = CardType.ICodeSLIXS;
                icodeSLIXS = ICodeFactory.getInstance().getICodeSLIXS(libInstance.getCustomModules());
                try {
                    icodeSLIXS.getReader().connect();
                    iCodeSLIXSCardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case ICodeSLIXL:
                mCardType = CardType.ICodeSLIXL;
                icodeSLIXL = ICodeFactory.getInstance().getICodeSLIXL(libInstance.getCustomModules());
                try {
                    icodeSLIXL.getReader().connect();
                    iCodeSLIXLCardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case ICodeSLIX2:
                mCardType = CardType.ICodeSLIX2;
                icodeSLIX2 = ICodeFactory.getInstance().getICodeSLIX2(libInstance.getCustomModules());

                try {
                    icodeSLIX2.getReader().connect();
                    iCodeSLIX2CardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');

                    mIsPerformingCardOperations = false;
                }
                break;
            case ICodeDNA:
                mCardType = CardType.ICodeDNA;
                icodeDNA = ICodeFactory.getInstance().getICodeDNA(libInstance.getCustomModules());

                try {
                    if (!icodeDNA.getReader().isConnected())
                        icodeDNA.getReader().connect();
                    iCodeDNACardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');

                    mIsPerformingCardOperations = false;
                }
                break;
            case DESFireEV1:
                mCardType = CardType.DESFireEV1;
                desFireEV1 = DESFireFactory.getInstance().getDESFire(libInstance.getCustomModules());
                try {

                    desFireEV1.getReader().connect();
                    desFireEV1.getReader().setTimeout(2000);
                    desfireEV1CardLogic();

                } catch (Throwable t) {
                    t.printStackTrace();
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case DESFireEV2:
                mCardType = CardType.DESFireEV2;
                showMessage("DESFireEV2 Card detected.", 't');
                tv.setText(" ");
                showImageSnap(R.drawable.desfire_ev2);
                showMessage("Card Detected : DESFireEV2", 'n');
                desFireEV2 = DESFireFactory.getInstance().getDESFireEV2(libInstance.getCustomModules());
                try {
                    desFireEV2.getReader().connect();
                    //desfireEV2CardLogic();
                    desfireEV2CardLogicCustom();

                } catch (Throwable t) {
                    t.printStackTrace();
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case PlusSL0:
                mCardType = CardType.PlusSL0;
                showMessage("Plus SL0 Card detected.", 't');
                tv.setText(" ");
                plusSL0 = PlusFactory.getInstance().getPlusSL0(libInstance.getCustomModules());

                showMessage("Card Detected :" + plusSL0.getType().getTagName(), 'n');
                showMessage("Sub Type :" + plusSL0.getPlusType(), 'n');

                if (plusSL0.getPlusType() == IPlus.SubType.PLUS_SE) {
                    showImageSnap(R.drawable.plusse);
                } else {
                    showImageSnap(R.drawable.plus);
                }
                // code commented because the operations are irreversible.
                //plusSL0.writePerso(0x9000,default_ff_key); // similarly fill all the mandatory keys.
                //plusSL0.commitPerso();
                showMessage("No operations are executed on a Plus SL0 card", 'n');
                break;
            case PlusSL1:
                mCardType = CardType.PlusSL1;
                showMessage("Plus SL1 Card detected.", 't');
                tv.setText(" ");
                showImageSnap(R.drawable.plus);
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                MifareClassic obj = MifareClassic.get(tag);
                if (obj != null) {
                    plusSL1 = PlusSL1Factory.getInstance().getPlusSL1(libInstance.getCustomModules(), obj);
                    plusSL1CardLogic();
                } else {
                    plusSL1 = PlusSL1Factory.getInstance().getPlusSL1(libInstance.getCustomModules());
                    showImageSnap(R.drawable.plus);
                    tv.setText(" ");
                    showMessage("Card Detected : " + plusSL1.getType().getTagName(), 'n');
                    showMessage("Plus SL1 card's Classic compatible methods not available on this device!", 'n');
                    //sample code to switch sector to security level 3. commented because changes are irreversible.
                    //plusSL1.switchToSL3(objKEY_AES128);
                }
                break;
            case PlusSL3:
                mCardType = CardType.PlusSL3;
                showMessage("Plus SL3 Card detected.", 't');
                tv.setText(" ");

                showMessage("Card Detected : Plus", 'n');
                plusSL3 = PlusFactory.getInstance().getPlusSL3(libInstance.getCustomModules());


                try {
                    plusSL3.getReader().connect();
                    plusSL3CardLogic();

                } catch (Throwable t) {
                    t.printStackTrace();
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;

            case PlusEV1SL0:
                mCardType = CardType.PlusSL0;
                showMessage("Plus EV1 SL0 Card detected.", 't');
                tv.setText(" ");
                showImageSnap(R.drawable.plusev1);

                plusEV1SL0 = PlusFactory.getInstance().getPlusEV1SL0(libInstance.getCustomModules());
                try {
                    plusEV1SL0.getReader().connect();
                    // code commented because the operations are irreversible.
                    //plusEV1SL0.writePerso(0x9000,default_ff_key); // similarly fill all the mandatory keys.
                    //plusEV1SL0.commitPerso(true);
                    showMessage("Card Detected :" + plusEV1SL0.getType().getTagName(), 'n');
                    showMessage("No operations are executed on a Plus EV1 SL0 card", 'n');
                } catch (Throwable t) {
                    t.printStackTrace();
                    showMessage("Unknown Error Tap Again!", 't');
                }

                break;
            case PlusEV1SL1:
                mCardType = CardType.PlusEV1SL1;
                showMessage("Plus EV1 SL1 Card detected.", 't');
                tv.setText(" ");
                showImageSnap(R.drawable.plusev1);
                tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                obj = MifareClassic.get(tag);
                if (obj != null) {
                    plusEV1SL1 = PlusSL1Factory.getInstance().getPlusEV1SL1(libInstance.getCustomModules(), obj);
                    plusEV1SL1CardLogic();
                } else {
                    plusEV1SL1 = PlusSL1Factory.getInstance().getPlusEV1SL1(libInstance.getCustomModules());
                    showImageSnap(R.drawable.plus);
                    tv.setText(" ");
                    showMessage("Card Detected : " + plusEV1SL1.getType().getTagName(), 'n');
                    showMessage("Plus SL1 card's Classic compatible methods not available on this device!", 'n');
                    //sample code to switch sector to security level 3. commented because changes are irreversible.
                    //plusEV1SL1.switchToSL3(objKEY_AES128);
                }
                break;
            case PlusEV1SL3:
                mCardType = CardType.PlusEV1SL3;
                plusEV1SL3 = PlusFactory.getInstance().getPlusEV1SL3(libInstance.getCustomModules());
                showMessage("Card Detected: Plus EV1 SL3", 't');
                try {
                    if (!plusEV1SL3.getReader().isConnected())
                        plusEV1SL3.getReader().connect();
                    plusEV1SL3CardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;
            case UltralightNano_40:
                mCardType = CardType.UltralightNano_40;
                ultralightNano = UltralightFactory.getInstance().getUltralightNano(libInstance.getCustomModules());
                showMessage("Card Detected: Ultra Light Nano 40", 't');
                try {
                    if (!ultralightNano.getReader().isConnected())
                        ultralightNano.getReader().connect();
                    ultralightNanoCardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;

            case UltralightNano_48:
                mCardType = CardType.UltralightNano_48;
                ultralightNano = UltralightFactory.getInstance().getUltralightNano(libInstance.getCustomModules());
                showMessage("Card Detected: Ultra Light Nano 48", 't');
                try {
                    if (!ultralightNano.getReader().isConnected())
                        ultralightNano.getReader().connect();
                    ultralightNanoCardLogic();
                } catch (Throwable t) {
                    showMessage("Unknown Error Tap Again!", 't');
                }
                break;


        }
    }


    private void onCardNotSupported(Tag tag) {

    }


    /**
     * MIFARE Plus Pre-condition.
     * <p/>
     * - PICC should be SL3. AuthenticateSL3 API requires block number to be
     * authenticated with AES128 key. Default key -
     * 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF, KeyNo - specify(0-9) during
     * set/getkey, KeyVersion - specify(0-2) Diversification input is null,
     * pcdCap2Out/pdCap2/pcdCap2In is a byte array.
     * <p/>
     * <p/>
     * ReadValue API require parameters(byte encrypted, byte readMACed, byte
     * macOnCmd, int blockNo, byte dstBlock).Result will print read data from
     * corresponding block(4 bytes).
     */
    private void plusSL3CardLogic() {

        byte[] divInput = null;
        byte[] pcdCap2In = new byte[0];
        ValueBlockInfo valueResult;
        byte[] result = null;

        byte[] dataWrite = new byte[]{(byte) 0x16, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0xE9, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0x16, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x04, (byte) 0xFB, (byte) 0x04,
                (byte) 0xFB, (byte) 0x21, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0xDE, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0x21, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x05, (byte) 0xFA, (byte) 0x05,
                (byte) 0xFA, (byte) 0x2C, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0xD3, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0x2C, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x06, (byte) 0xF9, (byte) 0x06, (byte) 0x00};

        tv.setText(" ");

        showMessage("Card Detected : " + plusSL3.getType().getTagName(), 'n');
        showMessage("Sub Type : " + plusSL3.getPlusType(), 'n');
        if (plusSL3.getPlusType() == IPlus.SubType.PLUS_SE) {
            showImageSnap(R.drawable.plusse);
        } else {
            showImageSnap(R.drawable.plus);
        }

        if (plusSL3.getCardDetails().securityLevel.equals("Security Level 3")) {
            try {

                /** ALL WORK RELATED TO MIFARE PLUS SL3 card. */
                plusSL3.authenticateFirst(0x4004, objKEY_AES128, pcdCap2In);


                sendMessageToHandler("Authentication status: true", 'n');
            /* Do the following only if write checkbox is selected */
                if (bWriteAllowed) {
                    /** Write Plain, MAC on response, MAC on command. This works on all variants of Plus - S, SE and X*/
                    plusSL3.writeValue(IPlusSL3.WriteMode.Plain_ResponseMACed, 12, 999, (byte) 0x09);

                    /** Read plain,  MAC on response, MAC on command. This works on all variants of Plus - S, SE and X*/
                    valueResult = plusSL3.readValue(IPlusSL3.ReadMode.Plain_ResponseMACed_CommandMACed, 12);
                    sendMessageToHandler(
                            "Read Value From Block-9 :" + valueResult.getDataValue(),
                            'd');

                    plusSL3.multiBlockWrite(IPlusSL3.WriteMode.Plain_ResponseMACed, (byte) 12, 3, dataWrite);

                    sendMessageToHandler("Multiblock write is true:", 'n');
                }
                result = plusSL3.multiBlockRead(IPlusSL3.ReadMode.Plain_ResponseMACed_CommandMACed, 12, 3);

                sendMessageToHandler(
                        "Multiblock read is :" + Utilities.dumpBytes(result), 'n');
                NxpLogUtils.save();
            } catch (Exception e) {
                showMessage(e.getMessage(), 'n');
                showMessage("Exception occurred... check LogCat", 't');
                e.printStackTrace();
            }
        } else {
            showMessage("No operations done since card is in secuirty level 0", 'n');
        }


    }

    private void plusEV1SL3CardLogic() {
        //pcdCap2In ensures the usage of PlusEV1 Secure messaging
        byte[] pcdCap2In = new byte[]{0x01};
        ValueBlockInfo valueResult;
        byte[] result = null;

        byte[] dataWrite = new byte[]{(byte) 0x16, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0xE9, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0x16, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x04, (byte) 0xFB, (byte) 0x04,
                (byte) 0xFB, (byte) 0x21, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0xDE, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0x21, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x05, (byte) 0xFA, (byte) 0x05,
                (byte) 0xFA, (byte) 0x2C, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0xD3, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0x2C, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x06, (byte) 0xF9, (byte) 0x06, (byte) 0x00};

        tv.setText(" ");
        showImageSnap(R.drawable.plusev1);
        showMessage("Card Detected : " + plusEV1SL3.getType().getTagName(), 'n');
        try {

            /** ALL WORK RELATED TO MIFARE PLUSEV1 SL3 card. using EV1 secure messaging*/
            plusEV1SL3.authenticateFirst(0x4006, objKEY_AES128, pcdCap2In);


            sendMessageToHandler("Authentication status: true", 'n');
            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                /** Write Plain, MAC on response, MAC on command. This works on all variants of Plus - S, SE and X*/
                plusEV1SL3.writeValue(IPlusSL3.WriteMode.Plain_ResponseMACed, 12, 999, (byte) 0x09);

                /** Write Plain, MAC on response, MAC on command. This works on all variants of Plus - S, SE and X*/
                valueResult = plusEV1SL3.readValue(IPlusSL3.ReadMode.Plain_ResponseMACed_CommandMACed, 12);
                sendMessageToHandler(
                        "Read Value From Block-9 :" + valueResult.getDataValue(),
                        'd');

                plusEV1SL3.multiBlockWrite(IPlusSL3.WriteMode.Plain_ResponseMACed, (byte) 12, 3, dataWrite);

                sendMessageToHandler("Multiblock write is true:", 'n');
            }
            result = plusEV1SL3.multiBlockRead(IPlusSL3.ReadMode.Plain_ResponseMACed_CommandMACed, 12, 3);

            sendMessageToHandler(
                    "Multiblock read is :" + Utilities.dumpBytes(result), 'n');
            NxpLogUtils.save();
        } catch (Exception e) {
            showMessage(e.getMessage(), 'l');
            showMessage("Exception occurred... check LogCat", 't');
            e.printStackTrace();
        }


    }


    /**
     * MIFARE Ultralight EV1 CardLogic.
     */
    private void ultralightEV1CardLogic() {


        tv.setText(" ");
        showImageSnap(R.drawable.ultralight_ev1);
        showMessage("Card Detected : " + ultralightEV1.getType().getTagName(), 'n');


        try {
            /** connect to card, authenticate and read data */

            showMessage(
                    "Ultralight-EV1 UID : "
                            + Utilities.dumpBytes(ultralightEV1.getUID()), 'd');
            data = ultralightEV1.readAll();
            data = ultralightEV1.read(DEFAULT_PAGENO_ULTRALIGHT);

            String str = Utilities.dumpBytes(data);
            showMessage("Data read from card @ " + "page "
                    + DEFAULT_PAGENO_ULTRALIGHT + " is " + str, 'd');
            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                byte[] bytesData = DATA.getBytes();
                String s1 = new String(bytesData);
                showMessage("Input String is " + s1, 'd');
                byte[] bytesEncData = encryptAESData(bytesData, bytesKey);
                str = "Encrypted string is " + Utilities.dumpBytes(bytesEncData);
                showMessage(str, 'd');

                ultralightEV1.write(4, Arrays.copyOfRange(bytesEncData, 0, 4));
                ultralightEV1.write(5, Arrays.copyOfRange(bytesEncData, 4, 8));
                ultralightEV1.write(6, Arrays.copyOfRange(bytesEncData, 8, 12));
                ultralightEV1.write(7, Arrays.copyOfRange(bytesEncData, 12, 16));

                byte[] bytesDecData = decryptAESData(data, bytesKey);
                String s = new String(bytesDecData);
                str = "Decrypted string is " + s;
                showMessage(str, 'd');

                if (Arrays.equals(bytesData, bytesDecData)) {
                    showMessage("Matches", 't');
                }
            }
        } catch (Exception e) {
            showMessage(e.getMessage(), 'l');
            showMessage("Exception occurred... check LogCat", 't');
            e.printStackTrace();
        }
        /* Save the logs in \sdcard\NxpLogDump\logdump.xml */
        NxpLogUtils.save();
    }

    /**
     * MIFARE Ultralight-C Card Logic.
     */
    private void ultralightcCardLogic() {

        tv.setText(" ");
        showImageSnap(R.drawable.ultralight_c);
        showMessage("Card Detected : " + ultralightC.getType().getTagName(), 'n');

        try {
            // ultralightC.connect();
            ultralightC.authenticate(objKEY_2KTDES_ULC);
            showMessage(
                    "Ultralight-C UID : "
                            + Utilities.dumpBytes(ultralightC.getUID()), 'd');
            showMessage("Authentication status is : true", 'd');
            data = ultralightC.readAll();
            showMessage("Read All o/p is : " + Utilities.dumpBytes(data),
                    'd');

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(e.getMessage(), 'l');
            showMessage("IOException occurred... check LogCat", 't');

        }


    }

    /**
     * Ultralight Card Logic.
     */
    private void ultralightCardLogic() {

        tv.setText(" ");
        showImageSnap(R.drawable.ultralight);
        showMessage("Card Detected : " + ultralightBase.getType().getTagName(), 'n');

        try {


            data = ultralightBase.readAll();
            showMessage("Read All o/p is : " + Utilities.dumpBytes(data), 'd');
            showMessage(
                    "Ultralight UID : "
                            + Utilities.dumpBytes(ultralightBase.getUID()), 'd');

        } catch (Exception e) {
            showMessage(e.getMessage(), 'l');
            showMessage("IOException occurred... check LogCat", 't');
            e.printStackTrace();
        }
    }

    private void ultralightNanoCardLogic() {
        tv.setText(" ");
        showImageSnap(R.drawable.ultralight_nano);
        showMessage("Card Detected : " + ultralightNano.getType().getTagName(), 'n');

        try {

            data = ultralightNano.readAll();
            showMessage("Read All o/p is : " + Utilities.dumpBytes(data), 'd');
            showMessage(
                    "Ultralight Nano UID : "
                            + Utilities.dumpBytes(ultralightNano.getUID()), 'd');

        } catch (Exception e) {
            showMessage(e.getMessage(), 'l');
            showMessage("IOException occurred... check LogCat", 't');
            e.printStackTrace();
        }
    }

    /**
     * MIFARE Plus SL1 Card Logic.
     */
    private void plusSL1CardLogic() {
        showImageSnap(R.drawable.plus);
        tv.setText(" ");
        showMessage("Card Detected : " + plusSL1.getType().getTagName(), 'n');
        // ******* Note that all the classic APIs work well with Plus Security
        // Level 1 *******
        int blockTorw = DEFAULT_SECTOR_CLASSIC;
        int sectorOfBlock = 0;

        if (!plusSL1.getReader().isConnected())
            plusSL1.getReader().connect();

        try {
            sectorOfBlock = plusSL1.blockToSector(blockTorw);

            plusSL1.authenticateSectorWithKeyA(sectorOfBlock, default_ff_key);

            data = plusSL1.readBlock(blockTorw);
            String str = Utilities.dumpBytes(data);
            showMessage("Data read from card @ block " + blockTorw + " is "
                    + str, 'd');

			/* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {


                /** write data to tag */
                showMessage("Data to write: " + DATA, 'd');
                plusSL1.writeBlock(blockTorw, DATA.getBytes());


            }


        } catch (Exception e) {
            showMessage("Exception occurred... check LogCat", 't');
            e.printStackTrace();
        }
    }


    private void plusEV1SL1CardLogic() {
        showImageSnap(R.drawable.plus);
        tv.setText(" ");


        try {
            showMessage("Card Detected :" + plusEV1SL1.getType().getTagName(), 'n');
            plusEV1SL1.getReader().setTimeout(1000);
            if (!plusEV1SL1.getReader().isConnected()) {
                plusEV1SL1.getReader().connect();
            }

            plusEV1SL1.activateLayer4();
            plusEV1SL1.getSL3SectorHelper().authenticateFirst(0x4004, default_zeroes_key, null);
            showMessage("SL3 sector authenticated :" + plusEV1SL1.getType().getTagName(), 'n');
            byte[] readData = plusEV1SL1.getSL3SectorHelper().read(IPlusSL3.ReadMode.Encrypted_ResponseMACed_CommandMACed, 8);
            showMessage("Read data:" + CustomModules.getUtility().dumpBytes(readData), 'n');
//            Map<Integer,IKeyData> switchMap = new ArrayMap<>(1);
//            switchMap.put(0x4005,default_zeroes_key);
//            plusEV1SL1.sectorSwitchToSL3(objKEY_AES128,0x4005, switchMap);
        } catch (Exception e) {
            showMessage("Exception occurred... check LogCat", 't');
            e.printStackTrace();
        }
    }

    /**
     * MIFARE classic Card Logic.
     */
    protected void classicCardLogic() {

        tv.setText(" ");
        showImageSnap(R.drawable.classic);
        showMessage("Card Detected : " + mifareClassic.getType().getTagName(), 'n');

        int blockTorw = DEFAULT_SECTOR_CLASSIC;
        int sectorOfBlock = 0;

        try {
            //Call connect first is the Reader is not connected
            if (!mifareClassic.getReader().isConnected())
                mifareClassic.getReader().connect();


            sectorOfBlock = mifareClassic.blockToSector(blockTorw);

            mifareClassic.authenticateSectorWithKeyA(sectorOfBlock, default_ff_key);


            data = mifareClassic.readBlock(blockTorw);
            String str = Utilities.dumpBytes(data);
            showMessage("Data read from card @ block " + blockTorw + " is "
                    + str, 'd');

			/* Do the following only if checkbox is selected */
            if (bWriteAllowed) {


                /** write data to tag */
                showMessage("Data to write: " + DATA, 'd');
                mifareClassic.writeBlock(blockTorw, DATA.getBytes());

            }
            mifareClassic.authenticateSectorWithKeyA(0, default_ff_key);

        } catch (Exception e) {

            showMessage("Exception occurred... check LogCat", 't');

            e.printStackTrace();
        }
    }


    /**
     * MIFARE classic EV1 Card Logic.
     */
    protected void classicCardEV1Logic() {

        tv.setText(" ");
        showImageSnap(R.drawable.classicev1);
        showMessage("Card Detected : " + mifareClassicEv1.getType().getTagName(), 'n');


        int blockTorw = DEFAULT_SECTOR_CLASSIC;
        int sectorOfBlock = 0;

        try {
            //Call connect first is the Reader is not connected
            if (!mifareClassicEv1.getReader().isConnected())
                mifareClassicEv1.getReader().connect();


            sectorOfBlock = mifareClassicEv1.blockToSector(blockTorw);

            mifareClassicEv1.authenticateSectorWithKeyA(sectorOfBlock,
                    default_ff_key);

            data = mifareClassicEv1.readBlock(blockTorw);
            String str = Utilities.dumpBytes(data);
            showMessage("Data read from card @ block " + blockTorw + " is "
                    + str, 'd');

            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                /** write data to tag */
                showMessage("Data to write: " + DATA, 'd');
                mifareClassicEv1.writeBlock(blockTorw, DATA.getBytes());

            }
            mifareClassicEv1.authenticateSectorWithKeyA(0, default_ff_key);


            //Originality Check
            if (mifareClassicEv1.getCardDetails().totalMemory == 1024) {
                boolean isSuccess = mifareClassicEv1.doOriginalityCheck();
                showMessage("doOriginalityCheck API status is " + isSuccess, 'd');
            }

            //Closing this is Mandatory...
            mifareClassicEv1.getReader().close();
        } catch (Exception e) {
            showMessage(e.getMessage(), 't');
            showMessage("Exception ... check LogCat", 't');
            e.printStackTrace();
        }
    }


    /**
     * DESFire Pre Conditions.
     * <p/>
     * PICC Master key should be factory default settings, (ie 16 byte All zero
     * Key ).
     * <p/>
     */
    private void desfireEV1CardLogic() {

        byte[] appId = new byte[]{0x12, 0x00, 0x00};
        int fileSize = 100;
        byte[] data = new byte[]{0x11, 0x11, 0x11, 0x11,
                0x11};
        int timeOut = 2000;
        int fileNo = 0;

        tv.setText(" ");
        showImageSnap(R.drawable.desfire_ev1);
        showMessage("Card Detected : " + desFireEV1.getType().getTagName(), 'n');

        try {
            desFireEV1.getReader().setTimeout(timeOut);
            showMessage(
                    "Version of the Card : "
                            + Utilities.dumpBytes(desFireEV1.getVersion()),
                    'd');
            showMessage(
                    "Existing Applications Ids : " + Arrays.toString(desFireEV1.getApplicationIDs()),
                    'd');

            desFireEV1.selectApplication(0);

            desFireEV1.authenticate(0, IDESFireEV1.AuthType.Native, KeyType.THREEDES, objKEY_2KTDES);

            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                desFireEV1.authenticate(0, IDESFireEV1.AuthType.Native, KeyType.THREEDES, objKEY_2KTDES);
                desFireEV1.format();
                EV1ApplicationKeySettings.Builder appsetbuilder = new EV1ApplicationKeySettings.Builder();

                EV1ApplicationKeySettings appsettings = appsetbuilder.setAppKeySettingsChangeable(true)
                        .setAppMasterKeyChangeable(true)
                        .setAuthenticationRequiredForFileManagement(false)
                        .setAuthenticationRequiredForDirectoryConfigurationData(false)
                        .setKeyTypeOfApplicationKeys(KeyType.TWO_KEY_THREEDES).build();

                desFireEV1.createApplication(appId, appsettings);
                desFireEV1.selectApplication(appId);

                desFireEV1.createFile(fileNo, new DESFireFile.StdDataFileSettings(
                        IDESFireEV1.CommunicationType.Plain, (byte) 0, (byte) 0, (byte) 0, (byte) 0, fileSize));

                desFireEV1.authenticate(0, IDESFireEV1.AuthType.Native, KeyType.TWO_KEY_THREEDES, objKEY_2KTDES);

                desFireEV1.writeData(0, 0, data);
                showMessage(
                        "Data Read from the card : "
                                + Utilities.dumpBytes(desFireEV1.readData(0, 0,
                                5)), 'd');
                showMessage(
                        "Free Memory of the Card : " + desFireEV1.getFreeMemory(),
                        'd');
            }

            desFireEV1.getReader().close();

            // Set the custom path where logs will get stored, here we are setting the log folder DESFireLogs under
            // external storage.
            String spath = Environment.getExternalStorageDirectory().getPath() + File.separator + "DESFireLogs";
            NxpLogUtils.setLogFilePath(spath);
            // if you don't call save as below , logs will not be saved.
            NxpLogUtils.save();

        } catch (Exception e) {
            showMessage("IOException occurred... check LogCat", 't');
            e.printStackTrace();
        }

    }

    private void desfireEV2CardLogic() {
        byte[] appId = new byte[]{0x12, 0x00, 0x00};
        int fileSize = 100;
        byte[] data = new byte[]{0x11, 0x11, 0x11, 0x11, 0x11};
        int timeOut = 2000;
        int fileNo = 0;

        tv.setText(" ");
        showImageSnap(R.drawable.desfire_ev2);
        showMessage("Card Detected : " + desFireEV2.getType().getTagName(), 'n');

        try {
            desFireEV2.getReader().setTimeout(timeOut);
            showMessage(
                    "Version of the Card : "
                            + Utilities.dumpBytes(desFireEV2.getVersion()),
                    'd');
            showMessage(
                    "Existing Applications Ids : " + Arrays.toString(desFireEV2.getApplicationIDs()),
                    'd');


            desFireEV2.selectApplication(0);

            desFireEV2.authenticate(0, IDESFireEV2.AuthType.Native, KeyType.THREEDES, objKEY_2KTDES);

            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                desFireEV2.authenticate(0, IDESFireEV2.AuthType.Native, KeyType.THREEDES, objKEY_2KTDES);

                desFireEV2.getReader().setTimeout(timeOut);
                desFireEV2.format();

                EV2ApplicationKeySettings.Builder appsetbuilder = new EV2ApplicationKeySettings.Builder();
                appsetbuilder
                        .setAppKeySettingsChangeable(true)
                        .setAppMasterKeyChangeable(true)
                        .setAuthenticationRequiredForFileManagement(false)
                        .setAuthenticationRequiredForDirectoryConfigurationData(false)
                        .setKeyTypeOfApplicationKeys(KeyType.TWO_KEY_THREEDES);

                EV2ApplicationKeySettings appsettings = appsetbuilder.build();

                desFireEV2.createApplication(appId, appsettings);
                desFireEV2.selectApplication(appId);

                desFireEV2.createFile(fileNo, new DESFireFile.StdDataFileSettings(
                        IDESFireEV2.CommunicationType.Plain, (byte) 0, (byte) 0, (byte) 0, (byte) 0, fileSize));

                desFireEV2.authenticate(0, IDESFireEV2.AuthType.Native, KeyType.TWO_KEY_THREEDES, objKEY_2KTDES);
                desFireEV2.writeData(0, 0, data);
                showMessage(
                        "Data Read from the card : "
                                + Utilities.dumpBytes(desFireEV2.readData(0, 0,
                                5)), 'd');
                showMessage(
                        "Free Memory of the Card : " + desFireEV2.getFreeMemory(),
                        'd');
                desFireEV2.getReader().close();
            }

            // Set the custom path where logs will get stored, here we are setting the log folder DESFireLogs under
            // external storage.
            String spath = Environment.getExternalStorageDirectory().getPath() + File.separator + "DESFireLogs";
            Log.i("LogNXP", "path to logs :" + spath);

            NxpLogUtils.setLogFilePath(spath);
            // if you don't call save as below , logs will not be saved.
            NxpLogUtils.save();

        } catch (Exception e) {
            showMessage("IOException occurred... check LogCat", 't');
            e.printStackTrace();
        }
    }

    private void desfireEV2CardLogicCustom(){
        format(true, SampleAppKeys.KEY_AES128_PICC_MASTER);
        initPiccAndChangeMasterKey(SampleAppKeys.KEY_2KTDES, SampleAppKeys.KEY_AES128_PICC_MASTER, true);
        authenticateMasterKeyAndecureMessaging(SampleAppKeys.KEY_AES128_PICC_MASTER);
    }

    private EV2ApplicationKeySettings getApplicationSettings(byte[] appId) {
        Log.i(TAG, "Creating application Settings");

        EV2ApplicationKeySettings.Builder appsetbuilder = new EV2ApplicationKeySettings.Builder();
        appsetbuilder
                .setAppKeySettingsChangeable(true)
                .setAppMasterKeyChangeable(true)
                .setAuthenticationRequiredForFileManagement(true)
                .setAuthenticationRequiredForDirectoryConfigurationData(true)
                .setKeyTypeOfApplicationKeys(KeyType.AES128);

        EV2ApplicationKeySettings appsettings = appsetbuilder.build();

        return appsettings;
    }

    private void createAndSelectApplication(byte[] appId, EV2ApplicationKeySettings applicationKeySettings) {
        Log.i(TAG, "Creating application new application");

        desFireEV2.createApplication(appId, applicationKeySettings);
        desFireEV2.selectApplication(appId);
    }

    private void setApplicationKey(byte[] appId){

    }

    private void authenticateMasterKeyAndecureMessaging(byte[] keyToAuthenticate) {
        Log.i(TAG, "Authenticating with AES master key and starting EV2Auth");

        int timeOut = 2000;
        desFireEV2.getReader().setTimeout(timeOut);
        desFireEV2.selectApplication(0);

        newAESAuthEV2Auth(SampleAppKeys.KEY_AES128);
    }

    private void format(boolean useNewAESAuth, byte[] keyToAuthenticate) {
        Log.i(TAG, "Formatting ?");
        desFireEV2.selectApplication(0);

        if (bWriteAllowed) {
            Log.i(TAG, "Formatting ALLOWED");
            if(useNewAESAuth){
                newAESAuthEV2Auth(keyToAuthenticate);
            }else {
                oldThreeDesAuth(keyToAuthenticate);
            }
            desFireEV2.getReader().setTimeout(2000);
            desFireEV2.format();
        }
    }

    private void newAESAuthEV2Auth(byte[] keyToAuthenticate){
        Log.i(TAG, "New AES auth and secure messaging");

        SecretKey originalKey = new SecretKeySpec(keyToAuthenticate, 0, keyToAuthenticate.length, "AES");
        KeyData keyData = new KeyData();
        keyData.setKey(originalKey);
        desFireEV2.authenticateEV2First(0, keyData, new byte[]{0, 0, 0, 0, 0, 0});
        desFireEV2.getFreeMemory();
    }

    private void oldThreeDesAuth(byte[] keyToAuthenticate){
        Log.i(TAG, "Old 3DES auth");

        SecretKey originalKey = new SecretKeySpec(keyToAuthenticate, 0, keyToAuthenticate.length, "DESede");
        KeyData keyData = new KeyData();
        keyData.setKey(originalKey);

        desFireEV2.authenticate(0, IDESFireEV2.AuthType.Native, KeyType.THREEDES, keyData);
    }

    private void initPiccAndChangeMasterKey(byte[] oldKey, byte[] newKey, boolean useAES) {
        Log.i(TAG, "Changing master keys");

        desFireEV2.selectApplication(0);

        EV2PICCConfigurationSettings piccKeySettings = new EV2PICCConfigurationSettings();
        piccKeySettings.setAppDefaultKey(SampleAppKeys.KEY_AES128_PICC_APP_DEFAULT_KEY, (byte)1);
        //piccKeySettings.setVCAndPICConfigurations(/*auth for virtual card selection*/false, /*proximity check*/true);

        if(useAES){
            newAESAuthEV2Auth(oldKey);
        }else {
            oldThreeDesAuth(oldKey);
        }

        desFireEV2.setConfigurationByte(piccKeySettings);
        desFireEV2.changeKey(0, KeyType.AES128, oldKey, newKey, (byte) 1);
    }

    private void changePiccKeys(){

    }


    private void authenticateOldApplicationKey() {

    }

    /**
     * Ntag IO Operations.
     *
     * @param tag object
     */
    private void ntagCardLogic(final INTag tag) {

        tv.setText(" ");
        showImageSnap(R.drawable.ntag_p);
        showMessage("Card Detected : " + tag.getType().getTagName(), 'n');

        try {

            showMessage("UID of the Tag: " + Utilities.dumpBytes(tag.getUID()),
                    'd');
            /* Do the following only if write checkbox is selected */
            for (int idx = tag.getFirstUserpage(); idx <= 5; idx++) {
                if (bWriteAllowed) {
                    byte[] dataWrite = new byte[]{(byte) idx, (byte) idx, (byte) idx,
                            (byte) idx};
                    tag.write(idx, dataWrite);

                    showMessage("Written 4 Bytes of Data at page No= " + idx + " "
                            + Utilities.dumpBytes(dataWrite), 'd');
                }
                showMessage("Read 16 Bytes from page No= " + idx + " "
                        + Utilities.dumpBytes(tag.read(idx)), 'd');
            }
            // NTAG I2C 1K and 2K Operation
            if (tag.getType() == (CardType.NTagI2C2K)
                    || tag.getType() == (CardType.NTagI2C1K)) {

                showMessage(
                        "Read Session Bytes: "
                                + Utilities.dumpBytes(nTAGI2C
                                .getSessionBytes()), 'd');
                showMessage(
                        "Read Config Bytes: "
                                + Utilities.dumpBytes(nTAGI2C
                                .getConfigBytes()), 'd');


            }
            // NTAG I2C and NTAG I2C Plus Variant 1K and 2K Operation
            if (tag.getType() == (CardType.NTagI2CPlus2K)
                    || tag.getType() == (CardType.NTagI2CPlus1K)) {


                showMessage(
                        "Get Version bytes: "
                                + Utilities.dumpBytes(nTAGI2CPlus
                                .getVersion()), 'd');
                showMessage(
                        "Read Session Bytes: "
                                + Utilities.dumpBytes(nTAGI2CPlus
                                .getSessionBytes()), 'd');
                showMessage(
                        "Read Config Bytes: "
                                + Utilities.dumpBytes(nTAGI2CPlus
                                .getConfigBytes()), 'd');

            }

            // showCardDetails(tag.getCardDetails());
            tag.getReader().close();
        } catch (Exception e) {
            showMessage("Exception -  Check logcat!", 't');
            e.printStackTrace();
        }
    }

    /**
     * ICODE SLI card logic.
     */
    private void iCodeSLICardLogic() {
        byte[] out = null;

        tv.setText(" ");
        showImageSnap(R.drawable.icode_p);
        showMessage("Card Detected : " + icodeSLI.getType().getTagName(), 'n');
        try {

            showMessage("Uid: " + Utilities.dumpBytes(icodeSLI.getUID()),
                    'd');
            out = icodeSLI.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                    (byte) 0x05);
            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                NdefMessageWrapper msg = new NdefMessageWrapper(createTextRecord(ndefData,
                        Locale.ENGLISH, false));
                icodeSLI.formatT5T();

                icodeSLI.writeNDEF(msg);
                showMessage(" Text Record NDEF msg Written successfully !", 'd');

                // write single block
                byte[] data = new byte[]{(byte) 0x42, (byte) 0x43, (byte) 0x44,
                        (byte) 0x45};
                icodeSLI.writeSingleBlock(ICode.NFCV_FLAG_ADDRESS, (byte) 0x05,
                        data);
                int nMblocks = icodeSLI.getNumBlocks();
                showMessage(
                        "no of blocks: "
                                + nMblocks, 'd');
                showMessage(
                        "Written 4 Bytes Data at page No 5: "
                                + Utilities.dumpBytes(data), 'd');
                out = icodeSLI.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05);

            }
        } catch (Exception e) {
            showMessage("IO Exception -  Check logcat!", 't');
            e.printStackTrace();
        }
        if (null != out) {
            showMessage(
                    "Read 4 Bytes of Data from page No 5: "
                            + Utilities.dumpBytes(out), 'd');
        }

    }

    /**
     * ICODE SLIS card logic.
     */
    private void iCodeSLISCardLogic() {
        byte[] out = null;

        tv.setText(" ");
        showImageSnap(R.drawable.icode_p);
        showMessage("Card Detected : " + icodeSLIS.getType().getTagName(), 'n');
        showMessage("Uid: " + Utilities.dumpBytes(icodeSLIS.getUID()), 'd');
        out = icodeSLIS.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                (byte) 0x05);
        try {
            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                NdefMessageWrapper msg = new NdefMessageWrapper(createTextRecord(ndefData,
                        Locale.ENGLISH, false));
                icodeSLIS.formatT5T();

                icodeSLIS.writeNDEF(msg);
                showMessage(" Text Record NDEF msg Written successfully !", 'd');

                byte[] data = new byte[]{(byte) 0x42, (byte) 0x43, (byte) 0x44,
                        (byte) 0x45};
                icodeSLIS.writeSingleBlock(ICode.NFCV_FLAG_ADDRESS, (byte) 0x05,
                        data);
                int nMblocks = icodeSLIS.getNumBlocks();
                showMessage(
                        "no of blocks: "
                                + nMblocks, 'd');
                showMessage(
                        "Written 4 Bytes Data at page No 5: "
                                + Utilities.dumpBytes(data), 'd');
                out = icodeSLIS.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05);
            }
        } catch (Exception e) {
            showMessage("Exception -  Check logcat!", 't');
            e.printStackTrace();
        }

        if (null != out) {
            showMessage(
                    "Read 4 Bytes of Data from page No 5: "
                            + Utilities.dumpBytes(out), 'd');
        }
    }

    /**
     * ICODE SLIL card logic.
     */
    private void iCodeSLILCardLogic() {
        byte[] out = null;

        tv.setText(" ");
        showImageSnap(R.drawable.icode_p);
        showMessage("Card Detected : " + icodeSLIL.getType().getTagName(), 'n');
        try {

            showMessage("Uid: " + Utilities.dumpBytes(icodeSLIL.getUID()),
                    'd');
            out = icodeSLIL.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                    (byte) 0x05);
            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                NdefMessageWrapper msg = new NdefMessageWrapper(createTextRecord(ndefData,
                        Locale.ENGLISH, false));
                icodeSLIL.formatT5T();
                icodeSLIL.writeNDEF(msg);
                showMessage(" Text Record NDEF msg Written successfully !", 'd');
                int nMblocks = icodeSLIL.getNumBlocks();
                showMessage(
                        "no of blocks: "
                                + nMblocks, 'd');

                byte[] data = new byte[]{(byte) 0x42, (byte) 0x43, (byte) 0x44,
                        (byte) 0x45};
                icodeSLIL.writeSingleBlock(ICode.NFCV_FLAG_ADDRESS, (byte) 0x05,
                        data);
                showMessage(
                        "Written 4 Bytes Data at page No 5: "
                                + Utilities.dumpBytes(data), 'd');

                out = icodeSLIL.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05);
            }
        } catch (Exception e) {
            showMessage("IO Exception -  Check logcat!", 't');
            e.printStackTrace();
        }
        if (null != out) {
            showMessage(
                    "Read 4 Bytes of Data from page No 5: "
                            + Utilities.dumpBytes(out), 'd');
        }
    }

    /**
     * ICODE SLIX card logic.
     */
    private void iCodeSLIXCardLogic() {
        byte[] out = null;

        tv.setText(" ");
        showImageSnap(R.drawable.icode_p);
        showMessage("Card Detected : " + icodeSLIX.getType().getTagName(), 'n');
        try {

            showMessage("Uid: " + Utilities.dumpBytes(icodeSLIX.getUID()),
                    'd');
            out = icodeSLIX.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                    (byte) 0x05);
            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                NdefMessageWrapper msg = new NdefMessageWrapper(createTextRecord(ndefData,
                        Locale.ENGLISH, false));
                icodeSLIX.formatT5T();
                icodeSLIX.writeNDEF(msg);
                showMessage(" Text Record NDEF msg Written successfully !", 'd');

                byte[] data = new byte[]{(byte) 0x42, (byte) 0x43, (byte) 0x44,
                        (byte) 0x45};
                icodeSLIX.writeSingleBlock(ICode.NFCV_FLAG_ADDRESS, (byte) 0x05,
                        data);
                int nMblocks = icodeSLIX.getNumBlocks();
                showMessage(
                        "no of blocks: "
                                + nMblocks, 'd');
                showMessage(
                        "Written 4 Bytes Data at page No 5: "
                                + Utilities.dumpBytes(data), 'd');

                out = icodeSLIX.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05);

            }
        } catch (Exception e) {
            showMessage("IO Exception -  Check logcat!", 't');
            e.printStackTrace();
        }
        if (null != out) {
            showMessage(
                    "Read 4 Bytes of Data from page No 5: "
                            + Utilities.dumpBytes(out), 'd');
        }

    }

    /**
     * ICODE SLIXS card logic.
     */
    private void iCodeSLIXSCardLogic() {
        byte[] out = null;

        tv.setText(" ");
        showImageSnap(R.drawable.icode_p);
        showMessage("Card Detected : " + icodeSLIXS.getType().getTagName(), 'n');
        try {

            showMessage("Uid: " + Utilities.dumpBytes(icodeSLIXS.getUID()),
                    'd');
            out = icodeSLIXS.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                    (byte) 0x05);

            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                NdefMessageWrapper msg = new NdefMessageWrapper(createTextRecord(ndefData,
                        Locale.ENGLISH, false));
                icodeSLIXS.formatT5T();
                icodeSLIXS.writeNDEF(msg);
                showMessage(" Text Record NDEF msg Written successfully !", 'd');

                byte[] data = new byte[]{(byte) 0x42, (byte) 0x43, (byte) 0x44,
                        (byte) 0x45};
                icodeSLIXS.writeSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05, data);
                int nMblocks = icodeSLIXS.getNumBlocks();
                showMessage(
                        "no of blocks: "
                                + nMblocks, 'd');
                showMessage(
                        "Written 4 Bytes Data at page No 5: "
                                + Utilities.dumpBytes(data), 'd');

                out = icodeSLIXS.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05);

            }
        } catch (Exception e) {
            showMessage("IO Exception -  Check logcat!", 't');
            e.printStackTrace();
        }
        if (null != out) {
            showMessage(
                    "Read 4 Bytes of Data from page No 5: "
                            + Utilities.dumpBytes(out), 'd');
        }
    }

    /**
     * ICODE SLIXL card logic.
     */
    private void iCodeSLIXLCardLogic() {
        byte[] out = null;

        tv.setText(" ");
        showImageSnap(R.drawable.icode_p);
        showMessage("Card Detected : " + icodeSLIXL.getType().getTagName(), 'n');
        try {

            // UID
            showMessage("Uid: " + Utilities.dumpBytes(icodeSLIXL.getUID()),
                    'd');
            out = icodeSLIXL.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                    (byte) 0x05);
            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                NdefMessageWrapper msg = new NdefMessageWrapper(createTextRecord(ndefData,
                        Locale.ENGLISH, false));
                icodeSLIXL.formatT5T();
                icodeSLIXL.writeNDEF(msg);
                showMessage(" Text Record NDEF msg Written successfully !", 'd');

                byte[] data = new byte[]{(byte) 0x42, (byte) 0x43, (byte) 0x44,
                        (byte) 0x45};
                icodeSLIXL.writeSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05, data);
                int nMblocks = icodeSLIXL.getNumBlocks();
                showMessage(
                        "no of blocks: "
                                + nMblocks, 'd');
                showMessage(
                        "Written 4 Bytes Data at page No 5: "
                                + Utilities.dumpBytes(data), 'd');
                out = icodeSLIXL.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05);
            }
        } catch (Exception e) {
            showMessage("IO Exception -  Check logcat!", 't');
            e.printStackTrace();
        }
        if (null != out) {
            showMessage(
                    "Read 4 Bytes of Data from page No 5: "
                            + Utilities.dumpBytes(out), 'd');
        }

    }

    /**
     * ICODE SLIX2 card logic.
     */
    private void iCodeSLIX2CardLogic() {
        byte[] out = null;
        tv.setText(" ");
        showImageSnap(R.drawable.icode_p);
        showMessage("Card Detected : " + icodeSLIX2.getType().getTagName(), 'n');
        try {
            // UID
            showMessage("Uid: " + Utilities.dumpBytes(icodeSLIX2.getUID()),
                    'd');
            out = icodeSLIX2.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                    (byte) 0x05);
            if (bWriteAllowed) {
                NdefMessageWrapper msg = new NdefMessageWrapper(createTextRecord(ndefDataslix2,
                        Locale.ENGLISH, false));
                icodeSLIX2.formatT5T();
                icodeSLIX2.writeNDEF(msg);

                showMessage(" Text Record NDEF msg Written successfully !", 'd');

                byte[] data = new byte[]{(byte) 0x42, (byte) 0x43, (byte) 0x44,
                        (byte) 0x45};
                icodeSLIX2.writeSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05, data);
                int nMblocks = icodeSLIX2.getNumBlocks();
                showMessage(
                        "no of blocks: "
                                + nMblocks, 'd');
                showMessage(
                        "Written 4 Bytes Data at page No 5: "
                                + Utilities.dumpBytes(data), 'd');
                out = icodeSLIX2.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05);


            }
        } catch (Exception e) {
            showMessage("IO Exception -  Check logcat!", 't');
            e.printStackTrace();
        }
        if (null != out) {
            showMessage(
                    "Read 4 Bytes of Data from page No 5: "
                            + Utilities.dumpBytes(out), 'd');
        }

    }

    private void iCodeDNACardLogic() {
        byte[] out = null;
        byte[] bytes = null;
        tv.setText(" ");
        showImageSnap(R.drawable.icode_p);
        showMessage("Card Detected : " + icodeDNA.getType().getTagName(), 'n');
        try {
            // UID
            showMessage("Uid: " + Utilities.dumpBytes(icodeDNA.getUID()),
                    'd');
            out = icodeDNA.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                    (byte) 0x05);
            /* Do the following only if write checkbox is selected */
            if (bWriteAllowed) {
                NdefMessageWrapper msg = new NdefMessageWrapper(createTextRecord(ndefData,
                        Locale.ENGLISH, false));
                icodeDNA.formatT5T();
                icodeDNA.writeNDEF(msg);
                showMessage(" Text Record NDEF msg Written successfully !", 'd');
                byte[] data = new byte[]{(byte) 0x42, (byte) 0x43, (byte) 0x44,
                        (byte) 0x45};
                icodeDNA.writeSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05, data);
                showMessage(
                        "Written 4 Bytes Data at page No 5: "
                                + Utilities.dumpBytes(data), 'd');
                out = icodeDNA.readSingleBlock(ICode.NFCV_FLAG_ADDRESS,
                        (byte) 0x05);
                int nMblocks = icodeDNA.getNumBlocks();


                showMessage(
                        "no of blocks: "
                                + nMblocks, 'd');

            }
        } catch (Exception e) {
            showMessage("SmartCard Exception - Check logcat!", 't');
            e.printStackTrace();
        }
        if (null != out) {
            showMessage(
                    "Read 4 Bytes of Data from page No 5: "
                            + Utilities.dumpBytes(out), 'd');
        }
        if (null != bytes) {
            showMessage(
                    "Read congfiguration block block from 0 and 9: "
                            + Utilities.dumpBytes(bytes), 'd');
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        libInstance.stopForeGroundDispatch();
    }


    @Override
    protected void onResume() {
        super.onResume();
        libInstance.startForeGroundDispatch();
    }

    /**
     * Encrypt the supplied data with key provided.
     *
     * @param data data bytes to be encrypted
     * @param key  Key to encrypt the buffer
     * @return encrypted data bytes
     * @throws NoSuchAlgorithmException           NoSuchAlgorithmException
     * @throws NoSuchPaddingException             NoSuchPaddingException
     * @throws InvalidKeyException                InvalidKeyException
     * @throws IllegalBlockSizeException          IllegalBlockSizeException
     * @throws BadPaddingException                eption BadPaddingException
     * @throws InvalidAlgorithmParameterException InvalidAlgorithmParameterException
     */
    protected byte[] encryptAESData(final byte[] data, final byte[] key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException {
        final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        byte[] encdata = cipher.doFinal(data);
        return encdata;
    }

    /**
     * @param encdata Encrypted input buffer.
     * @param key     Key to decrypt the buffer.
     * @return byte array.
     * @throws NoSuchAlgorithmException           No such algorithm exce.
     * @throws NoSuchPaddingException             NoSuchPaddingException.
     * @throws InvalidKeyException                if key is invalid.
     * @throws IllegalBlockSizeException          if block size is illegal.
     * @throws BadPaddingException                if padding is bad.
     * @throws InvalidAlgorithmParameterException if algo. is not avaliable or not present.
     */
    protected byte[] decryptAESData(final byte[] encdata, final byte[] key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException {
        final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
        byte[] decdata = cipher.doFinal(encdata);
        return decdata;
    }

    /**
     * Update the card image on the screen.
     *
     * @param cardTypeId resource image id of the card image
     */

    private void showImageSnap(final int cardTypeId) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mImageView.getLayoutParams().width = (size.x * 2) / 3;
        mImageView.getLayoutParams().height = size.y / 3;
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                mImageView.setImageResource(cardTypeId);
                mImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomnrotate));
            }
        }, 1250);
        mImageView.setImageResource(R.drawable.product_overview);
        mImageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
    }

    /**
     * This will display message in toast or logcat or on screen or all three.
     *
     * @param str   String to be logged or displayed
     * @param where 't' for Toast; 'l' for Logcat; 'd' for Display in UI; 'n' for
     *              logcat and textview 'a' for All
     */
    protected void showMessage(final String str, final char where) {

        switch (where) {

            case 't':
                Toast.makeText(MainActivity.this, "\n" + str, Toast.LENGTH_SHORT)
                        .show();
                break;
            case 'l':
                NxpLogUtils.i(TAG, "\n" + str);
                break;
            case 'd':
                tv.setText(tv.getText() + "\n-----------------------------------\n"
                        + str);
                break;
            case 'a':
                Toast.makeText(MainActivity.this, "\n" + str, Toast.LENGTH_SHORT)
                        .show();
                NxpLogUtils.i(TAG, "\n" + str);
                tv.setText(tv.getText() + "\n-----------------------------------\n"
                        + str);
                break;
            case 'n':
                NxpLogUtils.i(TAG, "Dump Data: " + str);
                tv.setText(tv.getText() + "\n-----------------------------------\n"
                        + str);
                break;
            default:
                break;
        }

        Log.i("NXPLog", str);
        return;
    }


    /**
     * This will send the message to the handler with required String and
     * character.
     *
     * @param stringMessage message to be send
     * @param codeLetter    't' for Toast; 'l' for Logcat; 'd' for Display in UI; 'a' for
     *                      All
     */
    protected void sendMessageToHandler(final String stringMessage,
                                        final char codeLetter) {
        Bundle b = new Bundle();
        b.putString("message", stringMessage);
        b.putChar("where", codeLetter);
        Message msg = mHandler.obtainMessage();
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    /**
     * Read Me section contain Help and About product.
     */
    private void readMeHelpAbout() {

        ((TextView) findViewById(R.id.tvAbout))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        showDisclaimer();
                    }
                });
    }


    private String getApplicationVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Disclaimer Section contain Details About product.
     */
    private void showDisclaimer() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("About");
        alert.setCancelable(false);
        String[] cards = libInstance.getSupportedCards();
        showMessage("Supported Cards", 'l');
        String message = getString(R.string.about_text);

        String alertMessage = message + "\n";

        alertMessage += "\n";
        String appVer = getApplicationVersion();
        if (appVer != null)
            alertMessage += "Application version is: " + appVer + "\n";

        alertMessage += "\n\n" + "Supported Cards: "
                + Arrays.toString(cards) + "\n";

        alert.setMessage(alertMessage);

        alert.setIcon(R.drawable.product_overview);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog,
                                final int whichButton) {

            }
        });

        alert.show();
    }

    public NdefRecordWrapper createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {

        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");

        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];

        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecordWrapper record = new NdefRecordWrapper(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);

        return record;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION_WRITE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Requested permission granted", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }
}
