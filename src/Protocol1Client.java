
//This is a simple client that connects to on ip "ipAddy" on  port "portNo" 
// and sends two strings.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.io.InputStream;

public class Protocol1Client {

	// ipAddy must be changed to the ip address of the server
	static String ipAddy = "127.0.0.1";
	static int portNo = 11337;
	static boolean debug = true;

	public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

		if (debug) System.out.println("opening socket");
		Socket socket = new Socket(ipAddy, portNo);

		// Create an input and output stream
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();
		
		//Protocol step 1: Write Connect Protocol 1 to server
		if (debug) System.out.println("Writing data");
		out.write(hexStringToByteArray("Connect Protocol 1"));
		
		//Protocol step 2: Recieve nonce from server
		byte[] serverNonce = new byte[16];
		in.read(serverNonce);
		if (debug) System.out.println("Server nonce: " + byteArrayToHexString(serverNonce));

		//Protocol step 3: Send back same nonce to server
		if (debug)
		System.out.println("Client Nonce: " + byteArrayToHexString(serverNonce));
		out.write(serverNonce);
		
		// Protocol step 4
		// We should receive the Client and Server Nonces encrypted with the session key
		// Because we sent back the same nonce, this should be an array of zeroes
		 byte[] message4 = new byte[32];
		 in.read(message4);
		 if (debug)
		 System.out.println("Message 4: " + byteArrayToHexString(message4));
		 
		 //Protocol step 5
		 // We should send the client and server nonces (same) encrypted with the
		 //session key
//		 byte[] message5pt = new byte[32];
//		 System.arraycopy(serverNonce, 0, message5pt, 0, 16);
//		 System.arraycopy(serverNonce, 0, message5pt, 16, 16);
//		
//		 byte[] keyBytes = xorBytes(serverNonce,serverNonce);
//		 SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
//		 Cipher decAEScipherSession = Cipher.getInstance("AES");
//		 decAEScipherSession.init(Cipher.DECRYPT_MODE, secretKeySpec);
//		 Cipher encAEScipherSession = Cipher.getInstance("AES");
//		 encAEScipherSession.init(Cipher.ENCRYPT_MODE, secretKeySpec);
//		
//		 byte[] cipherTextM5 = encAEScipherSession.doFinal(message5pt);
//		 if (debug) System.out.println("Sending M5pt:"+byteArrayToHexString(message5pt));
//		 if (debug) System.out.println("M5ct:"+byteArrayToHexString(cipherTextM5));
		 out.write(message4);

		 //Protocol step 6
		 // read the Secret Value sent by the server
		 byte[] encSecretMessage = new byte[100];
		 in.read(encSecretMessage);
		 //byte[] secretMessage = decAEScipherSession.doFinal(encSecretMessage);
		 if (debug) System.out.println(byteArrayToHexString(encSecretMessage));
		 
		if (debug) System.out.println("closing socket");
		socket.close();

		System.out.println("done");
	}
	
	private static byte[] xorBytes (byte[] one, byte[] two) {
		if (one.length!=two.length) {
		    return null;
		} else {
		    byte[] result = new byte[one.length];
		    for(int i=0;i<one.length;i++) {
			result[i] = (byte) (one[i]^two[i]);
		    }
		    return result;
		}
	    }
	    
	    private static String byteArrayToHexString(byte[] data) { 
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) { 
		    int halfbyte = (data[i] >>> 4) & 0x0F;
		    int two_halfs = 0;
		    do { 
			if ((0 <= halfbyte) && (halfbyte <= 9)) 
			    buf.append((char) ('0' + halfbyte));
			else 
			    buf.append((char) ('a' + (halfbyte - 10)));
			halfbyte = data[i] & 0x0F;
		    } while(two_halfs++ < 1);
		} 
		return buf.toString();
	    } 
	    
	    private static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
		    data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					  + Character.digit(s.charAt(i+1), 16));
		}
		return data;
	    }
}



// public void run() {
// OutputStream outStream;
// InputStream inStream;
// try {
// if (debug) System.out.println("Starting Protocol");
// outStream = myConnection.getOutputStream();
// inStream = myConnection.getInputStream();
//
// // Protocol Step 1
// // Write Connect Protocol 1 to server to start protocol
// PrintWriter out = new PrintWriter(myConnection.getOutputStream(), true);
// out.write("Connect Protocol 1");
//
// // Protocol Step 2
// // We should be sent the Nonce from the server
// System.out.println("Hello");
// byte[] serverNonce = new byte[16];
// inStream.read(serverNonce);
//
// // Protocol step 3
// // Send the server the same received nonce (still encrypted), which
// should make the session key an array of 0s
//
// System.out.println("Hello");
// if (debug)
// System.out.println("Client Nonce: " + byteArrayToHexString(serverNonce));
// outStream.write(serverNonce);
//
// // Protocol step 4
// // We should receive the Client and Server Nonces encrypted with the
// session key
// // Because we sent back the same nonce, this should be an array of zeroes
// byte[] message4 = new byte[32];
// inStream.read(message4);
// if (debug)
// System.out.println("Message 4: " + byteArrayToHexString(message4));
//
// // Protocol step 5
// // We should send the client and server nonces (same) encrypted with the
// session key
// byte[] message5pt = new byte[32];
// System.arraycopy(serverNonce, 0, message5pt, 0, 16);
// System.arraycopy(serverNonce, 0, message5pt, 16, 16);
//
// byte[] keyBytes = xorBytes(serverNonce,serverNonce);
// SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
// Cipher decAEScipherSession = Cipher.getInstance("AES");
// decAEScipherSession.init(Cipher.DECRYPT_MODE, secretKeySpec);
// Cipher encAEScipherSession = Cipher.getInstance("AES");
// encAEScipherSession.init(Cipher.ENCRYPT_MODE, secretKeySpec);
//
// byte[] cipherTextM5 = encAEScipherSession.doFinal(message5pt);
// if (debug) System.out.println("Sending M5
// pt:"+byteArrayToHexString(message5pt));
// if (debug) System.out.println(" M5
// ct:"+byteArrayToHexString(cipherTextM5));
// outStream.write(cipherTextM5);
//
// // Protocol step 6
// // read the Secret Value sent by the server
// byte[] encSecretMessage = null;
// inStream.read(encSecretMessage);
// byte[] secretMessage = decAEScipherSession.doFinal(encSecretMessage);
// if (debug) System.out.println(secretMessage);
//
// myConnection.close();
//
// } catch (Exception e) {
// e.printStackTrace();
// }
// }