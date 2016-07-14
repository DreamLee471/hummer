/*
 * Copyright 2014 Dream.Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hummer.server.http.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A bogus key store which provides all the required information to
 * create an example SSL connection.
 *
 * To generate a bogus key store:
 * <pre>
 * keytool  -genkey -alias securechat -keysize 2048 -validity 36500
 *          -keyalg RSA -dname "CN=securechat"
 *          -keypass secret -storepass secret
 *          -keystore cert.jks
 * </pre>
 */
public final class SecureChatKeyStore {
    
    private static byte[] jks;
    
    static{
    	InputStream is = SecureChatKeyStore.class.getResourceAsStream("cert.jks");
    	try {
			jks=new byte[is.available()];
			is.read(jks);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
    }

    public static InputStream asInputStream() {
        return new ByteArrayInputStream(jks);
    }

    public static char[] getCertificatePassword() {
        return "secret".toCharArray();
    }

    public static char[] getKeyStorePassword() {
        return "secret".toCharArray();
    }

    private SecureChatKeyStore() {
        // Unused
    }
}
