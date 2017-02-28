package com.nxp.sampletaplinx;

import android.content.Context;

import com.nxp.nfclib.defaultimpl.KeyData;
import com.nxp.nfclib.interfaces.IKeyData;
import com.nxp.sampletaplinx.SampleAppKeys.EnumKeyType;

import java.security.Key;

/**
 * KeyInfoProvider is used to store and retrieve the keys required by the sample application.
 * Created by NXP on 7/25/2016.
 *
 * The TapLinx SDK supports third-party key stores. The sample app shows the implementation of the
 * Spongy Castle library as key store. You are free to use any other key store you want.
 */

public class KeyInfoProvider {

    /**
     * We shall be using Spongy Castle(Bouncy Castle for Android) to securely store and retrieve keys used in the application.
     *
     * @see SpongyCastleKeystoreHelper
     */
    private SpongyCastleKeystoreHelper mKeystoreHelper = null;

    private static KeyInfoProvider mSelf = null;

    /**
     * Private Constructor.
     * @param context
     */
    private KeyInfoProvider(Context context){

        /**
         * Necessary step to use provider of Spongy Castle.
         */
        SpongyCastleKeystoreHelper.initProvider();

        /**
         * Initialize the Keystore helper that helps store and retrieve keys
         */
        mKeystoreHelper = new SpongyCastleKeystoreHelper(context);
    }

    /**
     * Returns Singleton instance of KeyInfoProvider.
     * @param context
     * @return KeyInfoProvider
     */
    public synchronized static  KeyInfoProvider getInstance(Context context){
        if(mSelf == null)
            mSelf = new KeyInfoProvider(context);
        return mSelf;
    }


    /**
     * Stores the Key to the underlying Keystore.
     * @param alias
     * @param keyType
     * @param key
     */
    public void setKey(final String alias, final EnumKeyType keyType, final byte[] key){
        if(alias != null && key != null)
            mKeystoreHelper.storeKey(key, alias, keyType);
    }


    /**
     * Retrieves the Key data from Underlying Keystore.
     * @param alias
     * @param keyType
     * @return IKeyData
     */
    public IKeyData getKey(final String alias, final EnumKeyType keyType){
        /**
         * MIFARE Keys are custom keys, they are not supported by SpongyCastle based keystore and hence cannot be retrieved from SpongyCastle Keystore without compromising the key material.
         * You can use the  method getMifareKey() to fetch Mifare Key bytes.
         */
        if(keyType == EnumKeyType.EnumMifareKey)
            return null;

        Key storedKey =  mKeystoreHelper.getKey(alias);
        if (storedKey != null) {
            KeyData keyDataObj = new KeyData();
            keyDataObj.setKey(storedKey);
            return keyDataObj;
        }
        return null;
    }


    /**
     * Returns the bytes of Mifare type key.
     * @param alias
     * @return byte[]
     */
    public byte[] getMifareKey(final String alias){
        return mKeystoreHelper.getMifareKey(alias);
        //MIFARE Keys are custom keys, they are not supported by SpongyCastle based keystore and hence cannot be retrieved from SpongyCastle Keystore without compromising the key material.
    }
}
