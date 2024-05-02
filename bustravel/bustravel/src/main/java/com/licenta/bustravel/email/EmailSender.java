package com.licenta.bustravel.email;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class EmailSender {
    public static void sendEmail(String to, String subject, String body) {
        // Get email configuration
        Properties props = EmailConfig.getConfig();

        // Get session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //"travel.management.system2024@gmail.com", "Licentaapp2024"
                //"code.crushers.test@gmail.com", "zgqb uehm kyvn qpsi"
                return new PasswordAuthentication("travel.management.system2024@gmail.com", "ljcm pije gxyr rsih");
            }
        });
        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header
            message.setFrom(new InternetAddress("travel.management.system2024@gmail.com"));

            // Set To: header field of the header
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(body);

            // Send message
            Transport.send(message);

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}

