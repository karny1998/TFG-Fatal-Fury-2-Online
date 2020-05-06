package lib.utils;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

public class crashReport {

    private static String from ="ff2crashreports@gmail.com";
    private static String to ="ff2crashreports@gmail.com";
    private static String password = "FatalFury2";

    private static String thread;
    private static String trace;
    private static JTextField cause;
    private static JLabel res;

    private static boolean activeWindow = false;

    public crashReport(){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                if (!activeWindow){
                    thread = t.getName();
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    trace = sw.toString();

                    createWindow();
                    activeWindow = true;
                }
            }
        });
    }

    private void createWindow(){
        JFrame frame = new JFrame("Crash report");

        frame.add(new Ventana());
        frame.setSize(500, 125);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }



    private static void sendMail(String content){
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        //get Session
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from,password);
                    }
                });
        //Compose the message
        try {
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            message.setSubject(content);
            message.setText("thread: " + thread +"\ntrace: " + trace);

            //send the message
            Transport.send(message);

            System.out.println("message sent successfully...");

        } catch (MessagingException e) {e.printStackTrace();}
    }



    private static class Ventana extends JPanel {
        private Ventana() {
            setLayout(new BorderLayout());

            JPanel northPanel = new JPanel();
            northPanel.setLayout(new FlowLayout());
            JLabel label = new JLabel("Please explain in detail the cause of the crash.");
            northPanel.add(label);
            add(northPanel, BorderLayout.NORTH);


            JPanel centerPanel = new JPanel();
            cause = new JTextField(40);
            centerPanel.add(cause);
            add(centerPanel, BorderLayout.CENTER);


            JPanel southPanel = new JPanel();
            JButton btn = new JButton("Send report");
            btn.addActionListener(new BtnListener());
            southPanel.add(btn);

            res = new JLabel();
            res.setVisible(false);
            southPanel.add(res);

            add(southPanel, BorderLayout.SOUTH);
        }
    }

    private static class BtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String content = cause.getText();
            crashReport.sendMail(content);
            System.exit(1);
        }
    }
}
