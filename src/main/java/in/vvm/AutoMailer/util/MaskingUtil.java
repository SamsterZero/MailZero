package in.vvm.AutoMailer.util;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MaskingUtil {

    /**
     * Masks an email address to protect PII.
     * vne.vvm@gmail.com -> v***m@gmail.com
     * me@vvm.in -> ***@vvm.in
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email;
        }
        
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);
        
        if (local.length() <= 2) {
            return "***@" + domain;
        }
        
        return local.charAt(0) + "***" + local.charAt(local.length() - 1) + "@" + domain;
    }

    /**
     * Masks a list of email addresses.
     */
    public static List<String> maskEmails(List<String> emails) {
        if (emails == null) {
            return Collections.emptyList();
        }
        return emails.stream()
                .map(MaskingUtil::maskEmail)
                .collect(Collectors.toList());
    }
}
