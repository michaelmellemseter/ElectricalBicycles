import java.security.*;
import javax.crypto.*;
import java.math.*;
import javax.crypto.spec.*;
import java.security.spec.InvalidKeySpecException;

/*
Hash and check code is based upon Lokesh Gupta original code
which can be found here:
https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
Date: 27.March.2018
*/

class Hashing {

    Hashing(){

    }

    public String Hash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
        char[] charPass = password.toCharArray();
        byte[] salt = new byte[16];
        SecureRandom secure = new SecureRandom();
        secure.getInstance("SHA1PRNG");
        secure.nextBytes(salt);

        //Creating the hash
        PBEKeySpec keySpec = new PBEKeySpec(charPass, salt, 800, 64*8);
        SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = key.generateSecret(keySpec).getEncoded();

        //Convert the newly created hash bytearray to readable hash-string
        BigInteger big1 = new BigInteger(1, salt);
        String hexSalt = big1.toString(16);
        int lengdeSalt = (salt.length * 2) - hexSalt.length();
        String saltString = "";
        if(lengdeSalt > 0){
            saltString += String.format("%0"  +lengdeSalt + "d", 0) + hexSalt;
        } else {
            saltString = hexSalt;
        }

        BigInteger big2 = new BigInteger(1, hash);
        String hexHash = big2.toString(16);
        int lengdeHash = (hash.length * 2) - hexHash.length();
        String hashString = "";
        if(lengdeHash > 0){
            hashString += String.format("%0"  +lengdeHash + "d", 0) + hexHash;
        } else {
            hashString = hexHash;
        }
        //returns salt and hash as string object, easily splitable because of the colon seperating them
        return saltString + ":" + hashString;
    }

    public boolean check(String salt, String password, String pass) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] charPass = password.toCharArray();
        byte[] s = hexStringToByteArray(salt);
        byte[] hash = hexStringToByteArray(pass);
        PBEKeySpec keySpec = new PBEKeySpec(charPass, s, 800, 64*8);
        SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] test = key.generateSecret(keySpec).getEncoded();
        //testing to see if both hashes are of equal length, which they should be if the password is correct
        int diff = hash.length ^ test.length;
        //Checking each character in the string for differences
        for(int i = 0; i < hash.length && i < test.length; i++){
            diff |= hash[i] ^ test[i];
        }
        //return true if there are no differences
        return diff == 0;
    }

    /*
    hexStringToByteArray code was created by Dave L. Original code
    which can be found here:
    https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    Date: 15.April.2018
    */

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}